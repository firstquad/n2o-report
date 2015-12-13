package ru.kirkazan.rmis.app.report.n2o.api.service;

import ru.kirkazan.rmis.app.report.n2o.api.model.ReportSignature;

/**
 * @author rsadikov
 * @since 10.11.2015
 */
public interface ReportSignatureDao {
    int saveReportSignature(ReportSignature reportSignature);

    ReportSignature getReportSignature(int id);
}
