package net.n2oapp.framework.report.integration;

import ru.kirkazan.rmis.app.report.n2o.api.model.Report;
import ru.kirkazan.rmis.app.report.n2o.api.model.ReportToGroup;
import ru.kirkazan.rmis.app.report.n2o.api.service.ReportDAO;

import java.util.List;
import java.util.Set;

/**
 * @author dfirstov
 * @since 24.06.2015
 */
public class TestReportDAOImpl implements ReportDAO {
    private Report report = new Report();
    private CmnReport cmnReport = new CmnReport();

    @Override
    public List<Report> retrieveReports() {
        return null;
    }

    @Override
    public List<ReportToGroup> retrieveReportToGroup() {
        return null;
    }

    @Override
    public List<ReportToGroup> retrieveReportOutGroup(Integer groupId) {
        return null;
    }

    @Override
    public List<String> retrieveAllReportFileNames() {
        return null;
    }

    @Override
    public List<String> retrieveHideFormReportFileNames() {
        return null;
    }

    @Override
    public Set<Report> retrieveReportsSet() {
        return null;
    }

    @Override
    public List<String> retrieveReportFormIds() {
        return null;
    }

    @Override
    public Integer getMaxReportId(String fileName) {
        return 1;
    }

    @Override
    public void updateReportForm(String formId, Integer newReportId) {
        report.setFormId(formId);
    }

    @Override
    public void insertReport(String name, String fileName, String note, Boolean inModule, Integer groupId, Boolean isGenerated, Boolean isHideForm) {
        report.setId(1);
        report.setName(name);
        report.setFileName(fileName);
        report.setNote(note);
        report.setIsReportModule(inModule);
        report.setGroupId(groupId);
        report.setIsGeneratedForm(isGenerated);
        report.setIsHideForm(isHideForm);
    }

    @Override
    public void deleteReport(Integer reportId) {

    }

    @Override
    public void deleteCmnReportDef(Integer reportId) {

    }

    @Override
    public void prepareReportOrderForDelete(Integer reportId) {

    }

    @Override
    public void addToCmnReportDef(String name, String newCode) {

    }

    @Override
    public void addToCmnReport(String name, String newCode) {
        cmnReport.setName(name);
        cmnReport.setCode(newCode);
    }

    @Override
    public List<String> getCmnReportDefCodes() {
        return null;
    }

    @Override
    public List<String> getCmnReportCodes() {
        return null;
    }

    @Override
    public void insertToCmnReportDef(String name, String code) {

    }

    @Override
    public void insertToCmnReport(String name, String code) {

    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public class CmnReport {
        private Integer id;
        private String code;
        private String name;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public CmnReport getCmnReport() {
        return cmnReport;
    }

    public void setCmnReport(CmnReport cmnReport) {
        this.cmnReport = cmnReport;
    }
}
