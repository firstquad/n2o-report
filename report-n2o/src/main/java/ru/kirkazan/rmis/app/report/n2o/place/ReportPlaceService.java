package ru.kirkazan.rmis.app.report.n2o.place;

import net.n2oapp.framework.api.metadata.global.view.N2oPage;
import net.n2oapp.framework.config.persister.MetadataPersister;
import net.n2oapp.framework.config.register.ConfigId;
import net.n2oapp.framework.config.register.ConfigRegister;
import net.n2oapp.framework.config.service.GlobalMetadataStorage;
import ru.kirkazan.rmis.app.report.n2o.api.model.Report;
import ru.kirkazan.rmis.app.report.n2o.api.service.ReportDAO;
import ru.kirkazan.rmis.app.report.n2o.place.model.N2oReportPlace;

import java.util.*;

/**
 * Created by dfirstov on 14.11.2014.
 */
public class ReportPlaceService {
    private Properties properties;
    private ReportDAO reportDAO;

    public void connectParams(String placeId, String containerId, String formId, String fieldId, String refId) {
        if (isInvalid(placeId, containerId, fieldId) || refId == null)
            return;
        N2oReportPlace place = GlobalMetadataStorage.getInstance().get(placeId, N2oReportPlace.class);
        N2oReportPlace.ContainerElement[] containers = place.getContainerElements();
        if (containers != null) {
            for (N2oReportPlace.ContainerElement containerElement : containers) {
                if (containerElement != null &&
                        containerElement.getId().equals(containerId) &&
                        containerElement.getReportsElement() != null &&
                        containerElement.getReportsElement().getReports() != null) {
                    N2oReportPlace.Report[] reports = containerElement.getReportsElement().getReports();
                    for (N2oReportPlace.Report report : reports) {
                        if (connect(formId, fieldId, refId, place, report))
                            return;
                    }
                }
            }
        }
        ConfigRegister.getInstance().update(new ConfigId(placeId, ConfigRegister.getInstance().get(placeId, N2oPage.class).getBaseMetaModel()));
    }

    public void disconnectParams(String placeId, String containerId, String formId, String fieldId) {
        if (isInvalid(placeId, containerId, fieldId))
            return;
        N2oReportPlace place = GlobalMetadataStorage.getInstance().get(placeId, N2oReportPlace.class);
        N2oReportPlace.ContainerElement[] containers = place.getContainerElements();
        if (containers != null) {
            for (N2oReportPlace.ContainerElement containerElement : containers) {
                if (containerElement != null &&
                        containerElement.getId().equals(containerId) &&
                        containerElement.getReportsElement() != null &&
                        containerElement.getReportsElement().getReports() != null) {
                    N2oReportPlace.Report[] reports = containerElement.getReportsElement().getReports();
                    for (N2oReportPlace.Report report : reports) {
                        if (report.getFormId() != null && report.getFormId().equals(formId)) {
                            N2oReportPlace.Param[] reportParams = report.getParams();
                            if (reportParams != null) {
                                List<N2oReportPlace.Param> newParams = new ArrayList<>();
                                Arrays.asList(reportParams).stream().forEach(p -> {
                                    if (!p.getFormFieldId().equals(fieldId))
                                        newParams.add(p);
                                });
                                report.setParams(newParams.toArray(new N2oReportPlace.Param[newParams.size()]));
                                persistPlace(place);
                                ConfigRegister.getInstance().update(new ConfigId(placeId, ConfigRegister.getInstance().get(placeId, N2oPage.class).getBaseMetaModel()));
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isInvalid(String placeId, String containerId, String fieldId) {
        return (placeId == null || containerId == null || fieldId == null
                || !ConfigRegister.getInstance().contains(placeId, N2oReportPlace.class));
    }

    private boolean connect(String formId, String fieldId, String refId, N2oReportPlace place, N2oReportPlace.Report report) {
        if (report.getFormId() != null && report.getFormId().equals(formId)) {
            N2oReportPlace.Param param = new N2oReportPlace.Param();
            param.setFormFieldId(fieldId);
            param.setRef(refId);
            if (report.getParams() != null) {
                for (N2oReportPlace.Param existsParam : report.getParams()) {
                    if (existsParam.getFormFieldId() != null && existsParam.getFormFieldId().equals(fieldId)) {
                        existsParam.setRef(refId);
                        persistPlace(place);
                        return true;
                    }
                }
                List<N2oReportPlace.Param> existsParams = new ArrayList<>(Arrays.asList(report.getParams()));
                existsParams.add(param);
                report.setParams(existsParams.toArray(new N2oReportPlace.Param[existsParams.size()]));
                persistPlace(place);
            } else {
                N2oReportPlace.Param[] params = new N2oReportPlace.Param[]{param};
                report.setParams(params);
                persistPlace(place);
                return true;
            }
        }
        return false;
    }

    public void deleteFromPlace(String placeId, String containerId, String formId, String fileName) {
        if (placeId == null)
            return;
        if (!ConfigRegister.getInstance().contains(placeId, N2oReportPlace.class))
            return;
        N2oReportPlace place = GlobalMetadataStorage.getInstance().get(placeId, N2oReportPlace.class);
        if (containerId == null && place.getReportsElement() != null && place.getReportsElement().getReports() != null) {
            N2oReportPlace.Report[] reports = place.getReportsElement().getReports();
            reports = deleteFromReportsElement(reports, formId, fileName);
            place.getReportsElement().setReports(reports);
            persistPlace(place);
            return;
        }
        N2oReportPlace.ContainerElement[] containers = place.getContainerElements();
        if (containerId != null && containers != null) {
            for (N2oReportPlace.ContainerElement containerElement : containers) {
                if (containerElement != null &&
                        containerElement.getId().equals(containerId) &&
                        containerElement.getReportsElement() != null &&
                        containerElement.getReportsElement().getReports() != null) {
                    N2oReportPlace.Report[] reports = containerElement.getReportsElement().getReports();
                    reports = deleteFromReportsElement(reports, formId, fileName);
                    containerElement.getReportsElement().setReports(reports);
                    persistPlace(place);
                    return;
                }
            }
        }
    }

    private N2oReportPlace.Report[] deleteFromReportsElement(N2oReportPlace.Report[] reports, String formId, String fileName) {
        List<N2oReportPlace.Report> reportList = new ArrayList<>(Arrays.asList(reports));
        for (N2oReportPlace.Report report : reports) {
            String placeFormId = report.getFormId();
            if (formId != null && placeFormId != null && placeFormId.equals(formId)) {
                reportList.remove(report);
                break;
            } else {
                String code = report.getCode();
                if (fileName != null && code != null && code.equals(fileName)) {
                    reportList.remove(report);
                    break;
                }
            }
        }
        if (reports.length != reportList.size()) {
            reports = reportList.toArray(new N2oReportPlace.Report[reportList.size()]);
        }
        return reports;
    }

    public void upReport(String placeId, String containerId, String formId, String fileName) {
        sort(placeId, containerId, formId, fileName, true);
    }

    public void downReport(String placeId, String containerId, String formId, String fileName) {
        sort(placeId, containerId, formId, fileName, false);
    }

    public void sort(String placeId, String containerId, String formId, String fileName, Boolean up) {
        if (placeId == null)
            return;
        if (!ConfigRegister.getInstance().contains(placeId, N2oReportPlace.class))
            return;
        N2oReportPlace place = GlobalMetadataStorage.getInstance().get(placeId, N2oReportPlace.class);
        if (containerId == null && place.getReportsElement() != null && place.getReportsElement().getReports() != null) {
            N2oReportPlace.Report[] reports = place.getReportsElement().getReports();
            List<N2oReportPlace.Report> reportsOnPlace = new ArrayList<>(Arrays.asList(reports));
            List<N2oReportPlace.Report> reportsForSorting = splitReportsOnDataBaseExisting(reportsOnPlace);
            reports = swapReport(reportsForSorting, reportsOnPlace, formId, fileName, up);
            place.getReportsElement().setReports(reports);
            persistPlace(place);
            return;
        }
        N2oReportPlace.ContainerElement[] containers = place.getContainerElements();
        if (containerId != null && containers != null) {
            for (N2oReportPlace.ContainerElement containerElement : containers) {
                if (containerElement != null &&
                        containerElement.getId().equals(containerId) &&
                        containerElement.getReportsElement() != null &&
                        containerElement.getReportsElement().getReports() != null) {
                    N2oReportPlace.Report[] reports = containerElement.getReportsElement().getReports();
                    List<N2oReportPlace.Report> reportsOnPlace = new ArrayList<>(Arrays.asList(reports));
                    List<N2oReportPlace.Report> reportsForSorting = splitReportsOnDataBaseExisting(reportsOnPlace);
                    reports = swapReport(reportsForSorting, reportsOnPlace, formId, fileName, up);
                    containerElement.getReportsElement().setReports(reports);
                    persistPlace(place);
                    return;
                }
            }
        }
    }

    private N2oReportPlace.Report[] swapReport(List<N2oReportPlace.Report> reportsForSorting, List<N2oReportPlace.Report> reportsOnPlace, String formId, String fileName, Boolean up) {
        List<N2oReportPlace.Report> reportList = new ArrayList<>(reportsForSorting);
        int i = 0;
        for (N2oReportPlace.Report report : reportsForSorting) {
            if (report.getFormId() != null && report.getFormId().equals(formId)) {
                if (up) {
                    if (i == 0)
                        break;
                    Collections.swap(reportList, i, i - 1);
                    break;
                } else {
                    if (i == reportsForSorting.size() - 1)
                        break;
                    Collections.swap(reportList, i, i + 1);
                    break;
                }
            } else if (fileName != null && report.getCode() != null && report.getCode().equals(fileName)) {
                if (up) {
                    if (i == 0)
                        break;
                    Collections.swap(reportList, i, i - 1);
                    break;
                } else {
                    if (i == reportsForSorting.size() - 1)
                        break;
                    Collections.swap(reportList, i, i + 1);
                    break;
                }
            }
            i++;
        }
        reportList.addAll(reportsOnPlace);
        return reportList.toArray(new N2oReportPlace.Report[reportList.size()]);
    }

    private List<N2oReportPlace.Report> splitReportsOnDataBaseExisting(List<N2oReportPlace.Report> reports) {
        List<Report> reportsInDB = reportDAO.retrieveReports();
        List<N2oReportPlace.Report> reportsInDataBase = new ArrayList<>();
        List<N2oReportPlace.Report> reportsOnPlace = new ArrayList<>(reports);
        for (N2oReportPlace.Report reportOnPlace : reportsOnPlace) {
            for (Report report : reportsInDB) {
                String fileName = report.getFileName();
                String reportOnPlaceFileName = reportOnPlace.getCode();
                if (fileName != null && report.getFormId() == null && reportOnPlaceFileName != null && fileName.equals(reportOnPlaceFileName)) {
                    reportOnPlace.setLabel(report.getLabel());
                    reports.remove(reportOnPlace);
                    reportsInDataBase.add(reportOnPlace);
                }
                String formId = report.getFormId();
                String reportOnPlaceFormId = reportOnPlace.getFormId();
                if (formId != null && reportOnPlaceFormId != null && formId.equals(reportOnPlaceFormId)) {
                    reportOnPlace.setLabel(report.getLabel());
                    reports.remove(reportOnPlace);
                    reportsInDataBase.add(reportOnPlace);
                }
            }
        }
        return reportsInDataBase;
    }

    private void persistPlace(N2oReportPlace place) {
        String formPath = this.properties.getProperty("rmis.report.form.path");
        if (formPath.startsWith("/"))
            formPath = formPath.substring(1, formPath.length());
        if (!formPath.endsWith("/"))
            formPath += "/";
        MetadataPersister.getInstance().persist(place, formPath);
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setReportDAO(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }
}
