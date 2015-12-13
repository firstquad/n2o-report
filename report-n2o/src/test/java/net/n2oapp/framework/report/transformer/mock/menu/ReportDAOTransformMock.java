package net.n2oapp.framework.report.transformer.mock.menu;

import ru.kirkazan.rmis.app.report.n2o.api.model.Report;
import ru.kirkazan.rmis.app.report.n2o.api.model.ReportToGroup;
import ru.kirkazan.rmis.app.report.n2o.api.service.ReportDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author dfirstov
 * @since 06.07.2015
 */
public class ReportDAOTransformMock implements ReportDAO {
    private List<Report> reports;

    @Override
    public List<Report> retrieveReports() {
        return reports;
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
        return null;
    }

    @Override
    public void updateReportForm(String formId, Integer newReportId) {

    }

    @Override
    public void insertReport(String name, String fileName, String note, Boolean inModule, Integer groupId, Boolean isGenerated, Boolean isHideForm) {

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

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }
}
