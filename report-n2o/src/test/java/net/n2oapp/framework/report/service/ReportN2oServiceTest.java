package net.n2oapp.framework.report.service;

import net.n2oapp.framework.report.integration.TestReportDAOImpl;
import org.junit.Test;
import ru.kirkazan.rmis.app.report.n2o.api.model.Report;
import ru.kirkazan.rmis.app.report.n2o.form.ReportFormService;
import ru.kirkazan.rmis.app.report.n2o.report.ReportN2oService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import static org.mockito.Mockito.mock;

/**
 * @author dfirstov
 * @since 24.06.2015
 */

public class ReportN2oServiceTest {

    @Test
    public void testCreateReport() throws IOException {
        String reportPathWithoutParams = retrieveTestReport("report_without_params.rptdesign");
        String reportPathWithParams = retrieveTestReport("report_with_params.rptdesign");
        testWithoutForm(reportPathWithoutParams);
        testCustomForm(reportPathWithParams);
        testWithParams(reportPathWithParams);
        testMultiWithoutForm(reportPathWithParams);
        testMultiWithForm(reportPathWithParams);
    }

    private void testMultiWithForm(String reportPathWithParams) {
        TestReportDAOImpl reportDAO = new TestReportDAOImpl();
        ReportN2oService reportN2oService = new ReportN2oService();
        reportN2oService.setReportDAO(reportDAO);
        Properties properties = new Properties();
        properties.setProperty("rmis.report.form.path", "/");
        reportN2oService.setProperties(properties);
        ReportFormService formService = mock(ReportFormService.class);
        reportN2oService.setFormService(formService);
        reportN2oService.multiCreateReport("report_with_params.rptdesign", reportPathWithParams, "customFormId", "Отчет с формой");
        Report report = reportDAO.getReport();
        assert report.getName() == null;
        assert "report_with_params.rptdesign".equals(report.getFileName());
        assert "customFormId".equals(report.getFormId());
        assert !report.getIsReportModule();
        assert report.getIsHideForm() == null;
        assert !report.getIsGeneratedForm();
        TestReportDAOImpl.CmnReport cmnReport = reportDAO.getCmnReport();
        assert "Отчет с формой".equals(cmnReport.getName());
        assert "report_with_params".equals(cmnReport.getCode());
    }

    private void testMultiWithoutForm(String reportPathWithParams) {
        TestReportDAOImpl reportDAO = new TestReportDAOImpl();
        ReportN2oService reportN2oService = new ReportN2oService();
        reportN2oService.setReportDAO(reportDAO);
        Properties properties = new Properties();
        properties.setProperty("rmis.report.form.path", "/");
        reportN2oService.setProperties(properties);
        ReportFormService formService = mock(ReportFormService.class);
        reportN2oService.setFormService(formService);
        reportN2oService.multiCreateReport("report_with_params.rptdesign", reportPathWithParams, null, "Без формы");
        Report report = reportDAO.getReport();
        assert "report_with_params.rptdesign".equals(report.getName());
        assert "report_with_params.rptdesign".equals(report.getFileName());
        assert report.getFormId() == null;
        assert !report.getIsReportModule();
        assert report.getIsHideForm();
        assert !report.getIsGeneratedForm();
        TestReportDAOImpl.CmnReport cmnReport = reportDAO.getCmnReport();
        assert "report_with_params.rptdesign".equals(cmnReport.getName());
        assert "report_with_params".equals(cmnReport.getCode());
    }

    private void testWithoutForm(String reportPathWithParams) {
        TestReportDAOImpl reportDAO = new TestReportDAOImpl();
        ReportN2oService reportN2oService = new ReportN2oService();
        reportN2oService.setReportDAO(reportDAO);
        reportN2oService.createReport("name", "report_without_params.rptdesign", reportPathWithParams, "note", false, 1, "customFormId", "formMode");
        Report report = reportDAO.getReport();
        assert "name".equals(report.getName());
        assert "report_without_params.rptdesign".equals(report.getFileName());
        assert report.getFormId() == null;
        assert !report.getIsReportModule();
        assert report.getIsHideForm();
        assert !report.getIsGeneratedForm();
        TestReportDAOImpl.CmnReport cmnReport = reportDAO.getCmnReport();
        assert "name".equals(cmnReport.getName());
        assert "report_without_params".equals(cmnReport.getCode());
    }

    private void testCustomForm(String reportWithParams) {
        ReportN2oService reportN2oService = new ReportN2oService();
        TestReportDAOImpl reportDAO = new TestReportDAOImpl();
        reportN2oService.setReportDAO(reportDAO);
        reportN2oService.createReport("name", "report_with_params.rptdesign", reportWithParams, "note", false, 1, "customFormId", "formMode");
        Report report = reportDAO.getReport();
        assert report.getName() == null;
        assert "report_with_params.rptdesign".equals(report.getFileName());
        assert "customFormId".equals(report.getFormId());
        assert !report.getIsReportModule();
        assert report.getIsHideForm() == null;
        assert !report.getIsGeneratedForm();
        TestReportDAOImpl.CmnReport cmnReport = reportDAO.getCmnReport();
        assert "name".equals(cmnReport.getName());
        assert "report_with_params".equals(cmnReport.getCode());
    }

    private void testWithParams(String reportPathWithParams) {
        ReportN2oService reportN2oService = new ReportN2oService();
        TestReportDAOImpl reportDAO = new TestReportDAOImpl();
        reportN2oService.setReportDAO(reportDAO);
        Properties properties = new Properties();
        properties.setProperty("rmis.report.form.path", "/");
        reportN2oService.setProperties(properties);
        ReportFormService formService = mock(ReportFormService.class);
        reportN2oService.setFormService(formService);
        reportN2oService.createReport("name", "report_with_params.rptdesign", reportPathWithParams, "note", false, 1, null, null);
        Report report = reportDAO.getReport();
        assert report.getName() == null;
        assert "report_with_params.rptdesign".equals(report.getFileName());
        assert ("report_report_with_params_" + report.getId()).equals(report.getFormId());
        assert !report.getIsReportModule();
        assert report.getIsHideForm() == null;
        assert report.getIsGeneratedForm();
        TestReportDAOImpl.CmnReport cmnReport = reportDAO.getCmnReport();
        assert "name".equals(cmnReport.getName());
        assert "report_with_params".equals(cmnReport.getCode());
    }

    private String retrieveTestReport(String path) {
        URL resource = this.getClass().getClassLoader().getResource(path);
        assert resource != null : "No such file " + path;
        return new File(resource.getFile()).getAbsolutePath();
    }

}
