package ru.kirkazan.rmis.app.report.n2o.impl.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kirkazan.rmis.app.report.n2o.api.model.Report;
import ru.kirkazan.rmis.app.report.n2o.api.service.ReportDAO;
import ru.kirkazan.rmis.app.report.n2o.api.service.ReportService;

/**
 * Created by dfirstov on 14.01.2015.
 */

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportDAO reportDAO;

    @Override
    public Integer deleteReport(Report report) {
        Integer reportId = Integer.valueOf(report.getId());
        reportDAO.prepareReportOrderForDelete(reportId);
        reportDAO.deleteReport(reportId);
        return reportId;
    }

    public void setReportDAO(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }
}
