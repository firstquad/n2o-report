package ru.kirkazan.rmis.app.report.n2o.form.criteria.form;

import net.n2oapp.criteria.api.CollectionPage;
import net.n2oapp.criteria.api.CollectionPageService;
import net.n2oapp.criteria.api.FilteredCollectionPage;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oForm;
import net.n2oapp.framework.config.register.ConfigRegister;
import ru.kirkazan.rmis.app.report.n2o.api.model.Report;
import ru.kirkazan.rmis.app.report.n2o.api.service.ReportDAO;
import ru.kirkazan.rmis.app.report.n2o.form.ReportForm;
import ru.kirkazan.rmis.app.report.n2o.report.ReportN2oService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by dfirstov on 02.11.2014.
 */
public class ReportFormCriteriaService implements CollectionPageService<ReportFormCriteria, ReportForm> {
    private ReportDAO reportDAO;
    private ReportN2oService reportN2oService;

    @Override
    public CollectionPage<ReportForm> getCollectionPage(ReportFormCriteria criteria) {
        Set<ReportForm> reportForms = new HashSet<>();
        List<N2oForm> forms = reportN2oService.getAllReportForms();
        fillReportForms(reportForms, forms);
        String fileName = criteria.getFileName();
        if (fileName != null && !fileName.equals("")) {
            reportForms = filterOnFileName(reportForms, fileName);
        }
        return new FilteredCollectionPage<>(new ArrayList<>(reportForms), criteria);
    }

    private void fillReportForms(Set<ReportForm> reportFormsOnPlace, List<N2oForm> forms) {
        List<Report> reports = reportDAO.retrieveReports();
        List<String> reportForms = new ArrayList<>();
        for (Report report : reports) {
            if (report.getFormId() != null) reportForms.add(report.getFormId());
        }
        for (N2oForm form : forms) {
            if (!reportForms.contains(form.getId())) {
                String formId = form.getId();
                if (!ConfigRegister.getInstance().contains(formId, N2oForm.class))
                    continue;
                ReportForm reportForm = new ReportForm();
                reportForm.setFormId(formId);
                String formName = formId;
                if (form.getName() != null) formName = form.getName();
                reportForm.setId(formId);
                reportForm.setName(formName);
                reportForm.setFileName(reportN2oService.extractReportFileNameFromFormHref(formId));
                reportFormsOnPlace.add(reportForm);
            }

        }
    }

    private Set<ReportForm> filterOnFileName(Set<ReportForm> reportForms, String fileName) {
        Set<ReportForm> filteredForms = new HashSet<>();
        for (ReportForm form : reportForms) {
            if (form.getFileName() != null && form.getFileName().equals(fileName)) {
                filteredForms.add(form);
            }
        }
        return filteredForms;
    }

    public void setReportDAO(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }

    public void setReportN2oService(ReportN2oService reportN2oService) {
        this.reportN2oService = reportN2oService;
    }
}
