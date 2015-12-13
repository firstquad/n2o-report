package ru.kirkazan.rmis.app.report.n2o.api.service;

import ru.kirkazan.rmis.app.report.n2o.api.model.Report;
import ru.kirkazan.rmis.app.report.n2o.api.model.ReportToGroup;

import java.util.List;
import java.util.Set;

/**
 * Created by dfirstov on 13.01.2015.
 */
public interface ReportDAO {

    public List<Report> retrieveReports();

    public List<ReportToGroup> retrieveReportToGroup();

    public List<ReportToGroup> retrieveReportOutGroup(Integer groupId);

    public List<String> retrieveAllReportFileNames();

    public List<String> retrieveHideFormReportFileNames();

    public Set<Report> retrieveReportsSet();

    public List<String> retrieveReportFormIds();

    public Integer getMaxReportId(String fileName);

    public void updateReportForm(String formId, Integer newReportId);

    public void insertReport(String name, String fileName, String note, Boolean inModule, Integer groupId, Boolean isGenerated, Boolean isHideForm);

    public void deleteReport(Integer reportId);

    public void deleteCmnReportDef(Integer reportId);

    public void prepareReportOrderForDelete(Integer reportId);

    void addToCmnReportDef(String name, String newCode);

    void addToCmnReport(String name, String newCode);

    List<String> getCmnReportDefCodes();

    List<String> getCmnReportCodes();

    void insertToCmnReportDef(String name, String code);

    void insertToCmnReport(String name, String code);
}
