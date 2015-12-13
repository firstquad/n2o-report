package ru.kirkazan.rmis.app.report.n2o.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kirkazan.rmis.app.report.n2o.api.service.ReportService;
import ru.kirkazan.rmis.app.report.n2o.backend.model.ReportForm;

/**
 * Created by dfirstov on 14.01.2015.
 */

@Service
@Transactional
public class ReportFormController {
    @Autowired
    private ReportService reportServiceImpl;

    public Integer deleteReport(ReportForm reportForm) {
        return reportServiceImpl.deleteReport(reportForm.extractReport());
    }

    public void setReportService(ReportService reportService) {
        this.reportServiceImpl = reportService;
    }
}
