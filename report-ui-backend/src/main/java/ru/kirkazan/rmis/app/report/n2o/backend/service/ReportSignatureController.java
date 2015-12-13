package ru.kirkazan.rmis.app.report.n2o.backend.service;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kirkazan.rmis.app.report.n2o.api.model.ReportHash;
import ru.kirkazan.rmis.app.report.n2o.api.service.ReportSignatureService;
import ru.kirkazan.rmis.app.report.n2o.backend.model.ReportHashForm;
import ru.kirkazan.rmis.app.report.n2o.backend.model.ReportSignatureForm;

import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * @author rsadikov
 * @since 03.11.2015
 */
@Service
public class ReportSignatureController {
    @Autowired
    private ReportSignatureService reportSignatureService;

    public ReportHashForm getReportHash(String url, int empPositionId) throws IOException, NoSuchProviderException, NoSuchAlgorithmException {
        ReportHash reportHash = reportSignatureService.getReportHash(url, empPositionId);
        return new ReportHashForm(reportHash.getId(), Hex.encodeHexString(reportHash.getHash()));
    }

    public void saveSignature(ReportSignatureForm form) {
        reportSignatureService.saveReportSignature(form.getReportSignatureId(), Base64.decodeBase64(form.getSignature()));
    }

    public void writeZippedSignature(Integer reportSignatureId, OutputStream outputStream) throws IOException {
        reportSignatureService.writeZippedSignature(reportSignatureId, outputStream);
    }

    public void writeSignedReport(Integer reportSignatureId, OutputStream outputStream) throws IOException {
        reportSignatureService.writeSignedReport(reportSignatureId, outputStream);
    }
}
