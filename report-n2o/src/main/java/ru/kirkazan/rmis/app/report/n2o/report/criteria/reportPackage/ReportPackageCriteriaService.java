package ru.kirkazan.rmis.app.report.n2o.report.criteria.reportPackage;

import net.n2oapp.criteria.api.CollectionPage;
import net.n2oapp.criteria.api.CollectionPageService;
import net.n2oapp.criteria.api.FilteredCollectionPage;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oForm;
import net.n2oapp.framework.config.register.ConfigRegister;
import ru.kirkazan.rmis.app.report.n2o.api.model.Report;
import ru.kirkazan.rmis.app.report.n2o.api.service.ReportDAO;
import ru.kirkazan.rmis.app.report.n2o.place.model.N2oReportPlace;
import ru.kirkazan.rmis.app.report.n2o.place.util.ReportPlaceUtil;
import ru.kirkazan.rmis.app.report.n2o.report.ReportN2oService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static ru.kirkazan.rmis.app.report.n2o.form.ReportFormService.retrieveForm;

/**
 * Created by dfirstov on 07.11.2014.
 */
public class ReportPackageCriteriaService implements CollectionPageService<ReportPackageCriteria, CustomReport> {
    private Properties properties;
    private ReportN2oService reportN2oService;
    private ReportDAO reportDAO;

    @Override
    public CollectionPage<CustomReport> getCollectionPage(ReportPackageCriteria criteria) {
        String reportPath = properties.getProperty("rmis.report.rptdesign.path");
        List<CustomReport> customReports = new ArrayList<>();
        Boolean customForm = criteria.getCustomForm() != null && criteria.getCustomForm();
        Boolean withoutForm = criteria.getHideForm() != null && criteria.getHideForm();
        if (criteria.getCustomForm() == null && criteria.getHideForm() == null) {
            customForm = true;
            withoutForm = true;
        }
        List<Report> dbReports = reportDAO.retrieveReports();
        if (customForm) {
            fillCustomForm(reportPath, customReports, withoutForm, dbReports, criteria);
        }
        if (withoutForm) {
            fillWithoutForm(reportPath, customReports, customForm, dbReports, criteria);
        }
        customReports = doFilter(customReports, criteria);
        return new FilteredCollectionPage<>(customReports, criteria);
    }

    private void fillCustomForm(String reportPath, List<CustomReport> customReports, Boolean withoutForm, List<Report> dbReports, ReportPackageCriteria criteria) {
        List<N2oForm> customForms = reportN2oService.getAllReportForms(!withoutForm);
        for (N2oForm form : customForms) {
            CustomReport customReport = new CustomReport();
            String formId = form.getId();
            String formName = form.getName();
            String fileName = reportN2oService.extractReportFileNameFromFormHref(formId);
            customReport.setCustomForm(formId != null);
            customReport.setFileName(fileName);
            customReport.setPath(reportPath + "/" + fileName);
            customReport.setFormId(formId);
            String name = (formName != null) ? formName : formId;
            customReport.setCallModeId(ReportN2oService.CallMode.CUSTOM.name());
            customReport.setName(name);
            if ((criteria.getIsShowLoaded() != null && criteria.getIsShowLoaded()) || notExists(dbReports, formId, fileName))
                customReports.add(customReport);
        }
    }

    private void fillWithoutForm(String reportPath, List<CustomReport> customReports, Boolean customForm, List<Report> dbReports, ReportPackageCriteria criteria) {
        List<N2oReportPlace.Report> reportsOnPlace = ReportPlaceUtil.retrieveAllReports();
        for (N2oReportPlace.Report report : reportsOnPlace) {
            CustomReport customReport = new CustomReport();
            String reportName;
            String fileName;
            String formId = report.getFormId();
            if (customReports.stream().anyMatch(r -> r.getFormId() != null && r.getFormId().equals(formId) ||
                    r.getFormId() == null && report.getCode() != null && report.getCode().equals(r.getFileName())))
                continue;
            if (customForm && formId != null && ConfigRegister.getInstance().contains(formId, N2oForm.class)) {
                N2oForm n2oForm = retrieveForm(formId);
                if (n2oForm == null)
                    continue;
                fileName = reportN2oService.extractReportFileNameFromFormHref(formId);
                reportName = n2oForm.getName();
                customReport.setFormId(formId);
                customReport.setCustomForm(true);
            } else if (report.getCode() != null) {
                fileName = report.getCode();
                reportName = report.getLabel() != null ? report.getLabel() : fileName;
            } else
                continue;
            customReport.setFileName(fileName);
            customReport.setPath(reportPath + "/" + fileName);
            customReport.setName(reportName);
            if ((criteria.getIsShowLoaded() != null && criteria.getIsShowLoaded()) || notExists(dbReports, formId, fileName))
                customReports.add(customReport);
        }
    }

    private boolean notExists(List<Report> dbReports, String formId, String fileName) {
        return !dbReports.stream().anyMatch(r -> r.getFormId() != null && r.getFormId().equals(formId)
                || r.getFormId() == null && formId == null && r.getFileName().equals(fileName));
    }


    private List<CustomReport> doFilter(List<CustomReport> reports, ReportPackageCriteria criteria) {
        if (criteria.getName() != null) {
            reports = reports.stream()
                    .filter(r -> r.getName() != null && r.getName().toLowerCase().contains(criteria.getName().toLowerCase())).collect(Collectors.toList());
        }
        if (criteria.getFileName() != null) {
            reports = reports.stream()
                    .filter(r -> r.getFileName() != null && r.getFileName().toLowerCase().contains(criteria.getFileName().toLowerCase())).collect(Collectors.toList());
        }
        Collections.sort(reports, (report1, report2) -> report1.getFileName().compareTo(report2.getFileName()));
        return reports;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setReportN2oService(ReportN2oService reportN2oService) {
        this.reportN2oService = reportN2oService;
    }

    public void setReportDAO(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }
}
