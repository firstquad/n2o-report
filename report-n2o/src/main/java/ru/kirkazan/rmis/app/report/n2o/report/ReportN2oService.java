package ru.kirkazan.rmis.app.report.n2o.report;

import net.n2oapp.framework.api.metadata.global.view.N2oPage;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oForm;
import net.n2oapp.framework.config.persister.MetadataPersister;
import net.n2oapp.framework.config.register.ConfigRegister;
import net.n2oapp.framework.config.register.Origin;
import net.n2oapp.framework.config.service.GlobalMetadataStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import ru.kirkazan.rmis.app.report.n2o.api.service.ReportDAO;
import ru.kirkazan.rmis.app.report.n2o.form.ReportFormService;
import ru.kirkazan.rmis.app.report.n2o.impl.parser.ReportParam;
import ru.kirkazan.rmis.app.report.n2o.impl.parser.ReportParser;
import ru.kirkazan.rmis.app.report.n2o.place.model.N2oReportPlace;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static ru.kirkazan.rmis.app.report.n2o.form.ReportFormService.retrieveForm;
import static ru.kirkazan.rmis.app.report.n2o.place.util.ReportPlaceUtil.retrieveAllPlaceFormIds;
import static ru.kirkazan.rmis.app.report.n2o.util.ConfigRegisterUtil.remove;

/**
 * Created by dfirstov on 25.09.2014.
 */
public class ReportN2oService {
    private Properties properties;
    private ReportDAO reportDAO;
    private ReportFormService formService;
    private static final Logger logger = LoggerFactory.getLogger(ReportN2oService.class);

    public static enum CallMode {
        CUSTOM("По умолчанию"),
        WITHOUT_FORM("Без формы"),
        DEFAULT("По умолчанию");

        public String value;

        CallMode(String value) {
            this.value = value;
        }
    }

    public Integer createReport(String reportName, String fileName, String path, String note, Boolean inModule, Integer groupId, String customFormId, String formMode) {
        List<ReportParam> reportParams = ReportParser.getInstance().parseParams(new File(path));
        Boolean isHideForm = (formMode != null && formMode.equals("notForm")) || reportParams == null;
        Boolean isGeneratedReport = customFormId == null && !isHideForm;
        String nameForReport = (reportParams != null && !isHideForm) ? null : reportName;
        reportDAO.insertReport(nameForReport, fileName, note, inModule, groupId, isGeneratedReport, isHideForm ? true : null);
        Integer newReportId = createForm(reportName, fileName, customFormId, reportParams, isHideForm);
        insertToCmnReport(fileName, reportName);
        return newReportId;
    }

    private Integer createForm(String reportName, String fileName, String customFormId, List<ReportParam> reportParams, Boolean isHideForm) {
        Integer newReportId = reportDAO.getMaxReportId(fileName);
        if (reportParams != null && !isHideForm) {
            String newFormId = customFormId;
            if (customFormId == null) {
                newFormId = getFormId(fileName, newReportId);
                formService.createFormByReport(reportParams, newFormId, fileName, reportName, processedFormLocation());
            }
            reportDAO.updateReportForm(newFormId, newReportId);
        }
        return newReportId;
    }

    private String getFormId(String fileName, Integer newReportId) {
        String[] fileNameSplit = fileName.split("/");
        return getN2oId(fileNameSplit[fileNameSplit.length - 1]) + "_" + newReportId;
    }

    private void insertToCmnReport(String fileName, String nameForReport) {
        String code = fileName.replace(".rptdesign", "");
        String nameForCmnReport = (nameForReport == null || nameForReport.equals("") ? fileName : nameForReport);
        reportDAO.addToCmnReportDef(nameForCmnReport, code);
        reportDAO.addToCmnReport(nameForCmnReport, code);
    }

    public static String generateFormId(String fileName, Integer newReportId, String customFormId) {
        if (customFormId != null)
            return customFormId;
        String[] fileNameSplit = fileName.split("/");
        return getN2oId(fileNameSplit[fileNameSplit.length - 1]) + "_" + newReportId;
    }

    private static String getN2oId(String fileName) {
        fileName = "report_" + fileName.substring(0, fileName.lastIndexOf('.')).replace("-", "_").replace(".", "");
        return fileName;
    }

    public Integer multiCreateReport(String fileName, String path, String formId, String reportName) {
        String formMode = null;
        if (formId == null) {
            formMode = "notForm";
            reportName = (reportName == null
                    || ReportN2oService.CallMode.DEFAULT.value.equals(reportName)
                    || CallMode.WITHOUT_FORM.value.equals(reportName)) ? fileName : reportName;
        }
        return createReport(reportName, fileName, path, "", false, null, formId, formMode);
    }

    @Transactional
    public void deleteReport(Integer reportId, String formId, Boolean isGeneratedForm) {
        Boolean toDelete = true;
        if (isGeneratedForm != null && isGeneratedForm) {
            String config = properties.getProperty("n2o.config.path");
            if (config.endsWith("/"))
                config = config.substring(0, config.length() - 1);
            String formPath = getFormLocation();
            if (!formPath.endsWith("/"))
                formPath += "/";
            String path = config + formPath + formId + ".widget.xml";
            File file = new File(path);
            toDelete = !file.exists() || file.delete();
            remove(formId, N2oForm.class);
        }
        if (toDelete) {
            reportDAO.prepareReportOrderForDelete(reportId);
            reportDAO.deleteCmnReportDef(reportId);
            reportDAO.deleteReport(reportId);
        }
    }

    public String addToPlace(String pageId, String containerId, String fileName, String formId) {
        if (pageId == null)
            return null;
        if (formId == null && fileName == null)
            return null;
        N2oReportPlace.Report report = new N2oReportPlace.Report();
        String queryId = pageId + "_";
        if (formId != null) {
            report.setFormId(formId);
            queryId +=  prepareId(formId) + "_";
        } else {
            report.setCode(fileName);
            queryId +=  prepareId(fileName) + "_";
        }
        N2oReportPlace.Report[] newReports = new N2oReportPlace.Report[]{report};
        N2oReportPlace place = new N2oReportPlace();
        ConfigRegister configRegister = ConfigRegister.getInstance();
        String realPageId = configRegister.get(pageId, N2oPage.class).getContext().getRealId();
        if (configRegister.contains(realPageId, N2oReportPlace.class)) {
            place = addToExistsPlace(realPageId, containerId, newReports);
        } else {
            fillNewPlace(place, realPageId, containerId, newReports);
        }
        String formPath = processedFormLocation();
        MetadataPersister.getInstance().persist(place, formPath);
        return resolveQueryId(containerId, queryId, place);
    }

    private String resolveQueryId(String containerId, String queryId, N2oReportPlace place) {
        try {
            if (containerId == null) {
                queryId += place.getReportsElement().getReports().length;
            } else {
                queryId += containerId + "_" +
                        Arrays.asList(place.getContainerElements()).stream().filter(c -> c.getId().equals(containerId)).collect(Collectors.toList()).get(0)
                                .getReportsElement().getReports().length;
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            return queryId;
        }
        return queryId;
    }


    public static String prepareId(String formId) {
        return formId.replace("-", "_").replace(".", "_");
    }

    private void fillNewPlace(N2oReportPlace place, String pageId, String containerId, N2oReportPlace.Report[] newReports) {
        place.setId(pageId);
        N2oReportPlace.ReportsElement reportsElement = new N2oReportPlace.ReportsElement();
        reportsElement.setReports(newReports);
        if (containerId != null) {
            N2oReportPlace.ContainerElement containerElement = new N2oReportPlace.ContainerElement();
            containerElement.setId(containerId);
            containerElement.setReportsElement(reportsElement);
            N2oReportPlace.ContainerElement[] containerElements = new N2oReportPlace.ContainerElement[]{containerElement};
            place.setContainerElements(containerElements);
        } else {
            place.setReportsElement(reportsElement);
        }
    }

    private N2oReportPlace addToExistsPlace(String pageId, String containerId, N2oReportPlace.Report[] newReports) {
        N2oReportPlace place = GlobalMetadataStorage.getInstance().get(pageId, N2oReportPlace.class);
        N2oReportPlace.ReportsElement reportsElement = new N2oReportPlace.ReportsElement();
        reportsElement.setReports(newReports);
        N2oReportPlace.ContainerElement containerElement = new N2oReportPlace.ContainerElement();
        containerElement.setId(containerId);
        containerElement.setReportsElement(reportsElement);
        if (containerId == null) {
            addToContextMenu(newReports, place, reportsElement);
        } else {
            addToContainer(containerId, newReports, place, containerElement);
        }
        return place;
    }

    private void addToContextMenu(N2oReportPlace.Report[] newReports, N2oReportPlace place, N2oReportPlace.ReportsElement reportsElement) {
        N2oReportPlace.ReportsElement reportsElementOnPlace = place.getReportsElement();
        if (reportsElementOnPlace == null) {
            place.setReportsElement(reportsElement);
        } else {
            fillReportsElement(reportsElementOnPlace, newReports);
        }
    }

    private void addToContainer(String containerId, N2oReportPlace.Report[] newReports, N2oReportPlace place, N2oReportPlace.ContainerElement containerElement) {
        N2oReportPlace.ContainerElement[] placeContainers = place.getContainerElements();
        if (placeContainers != null) {
            Boolean containerExist = false;
            for (N2oReportPlace.ContainerElement container : placeContainers) {
                if (container.getId().equals(containerId)) {
                    containerExist = true;
                    N2oReportPlace.ReportsElement containerReportsElement = container.getReportsElement();
                    fillReportsElement(containerReportsElement, newReports);
                }
            }
            if (!containerExist) {
                List<N2oReportPlace.ContainerElement> containerList = new ArrayList<>(Arrays.asList(placeContainers));
                N2oReportPlace.ContainerElement[] containerElements = new N2oReportPlace.ContainerElement[]{containerElement};
                containerList.addAll(Arrays.asList(containerElements));
                placeContainers = containerList.toArray(new N2oReportPlace.ContainerElement[containerList.size()]);
            }
        } else {
            placeContainers = new N2oReportPlace.ContainerElement[]{containerElement};
        }
        place.setContainerElements(placeContainers);
    }

    private void fillReportsElement(N2oReportPlace.ReportsElement reportsElement, N2oReportPlace.Report[] newReports) {
        if (reportsElement == null)
            return;
        List<N2oReportPlace.Report> newReportList = Arrays.asList(newReports);
        N2oReportPlace.Report[] placeReports = reportsElement.getReports();
        if (placeReports != null) {
            List<N2oReportPlace.Report> placeReportsList = new ArrayList<>(Arrays.asList(placeReports));
            placeReportsList.addAll(newReportList);
            placeReports = placeReportsList.toArray(new N2oReportPlace.Report[placeReportsList.size()]);
        } else {
            placeReports = newReports;
        }
        reportsElement.setReports(placeReports);
    }

    public String addDefaultReportFormat(String fileName, String format) {
        if (format == null)
            format = "pdf";
        return getBirtHost() + fileName + "&__format=" + format;
    }

    public String getUrlToBirtWithParam(String fileName, String format, N2oReportPlace.Param[] params) {
        if (params == null)
            return addDefaultReportFormat(fileName, format);
        if (format == null)
            format = "pdf";
        String url = getBirtHost() + fileName + "&";
        for (N2oReportPlace.Param param : params) {
            url = url + param.getFormFieldId() + "=:" + param.getRef() + ":&";
        }
        return url + "__format=" + format;
    }

    public String getSubDirectory(String fileName, String filePath) {
        String prop = this.properties.getProperty("rmis.report.rptdesign.path");
        if (!prop.endsWith("/"))
            prop = prop + "/";
        filePath = filePath.replace("\\", "/");
        prop = prop.replace("\\", "/");
        String replacedProp = filePath.substring(filePath.lastIndexOf(prop), filePath.length()).replace(prop, "");
        return replacedProp.replace(fileName, "");
    }

    public String extractReportFileNameFromFormHref(String formId) {
        if (!ConfigRegister.getInstance().contains(formId, N2oForm.class))
            return "";
        N2oForm form = retrieveForm(formId);
        if (form == null)
            return "";
        String url = "";
        if (form.getEdit() != null && form.getEdit().getAnchor() != null && form.getEdit().getAnchor().getHref() != null)
            url = form.getEdit().getAnchor().getHref();
        if (!url.contains(".rptdesign"))
            return "";
        String reportParameter = "__report=";
        return url.substring(url.indexOf(reportParameter) + reportParameter.length(), url.indexOf(".rptdesign")) + ".rptdesign";
    }

    public List<N2oForm> getAllReportForms() {
        return getAllReportForms(true);
    }

    public List<N2oForm> getAllReportForms(boolean includePlaces) {
        Set<String> ids = ConfigRegister.getInstance().getIds(N2oForm.class, (i) -> (i.getURI() != null && (i.getURI().contains(getFormLocation()))
                && !Origin.compile.equals(i.getOrigin())));
        if (includePlaces)
            ids.addAll(retrieveAllPlaceFormIds());
        List<N2oForm> forms = new ArrayList<>();
        for (String formId : ids) {
            if (!ConfigRegister.getInstance().contains(formId, N2oForm.class))
                continue;
            N2oForm n2oForm = retrieveForm(formId);
            if (n2oForm == null)
                continue;
            forms.add(n2oForm);
        }
        return forms;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setReportDAO(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }

    public String getBirtHost() {
        return this.properties.getProperty("rmis.report.url") + "?__report=";
    }

    private String getFormLocation() {
        return this.properties.getProperty("rmis.report.form.path");
    }

    private String processedFormLocation() {
        String formPath = this.properties.getProperty("rmis.report.form.path");
        if (formPath.startsWith("/")) {
            formPath = formPath.substring(1, formPath.length());
        }
        if (!formPath.endsWith("/")) {
            formPath += "/";
        }
        return formPath;
    }

    public void setFormService(ReportFormService formService) {
        this.formService = formService;
    }
}

