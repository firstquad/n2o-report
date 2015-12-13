package ru.kirkazan.rmis.app.report.n2o.api.service;

import ru.kirkazan.rmis.app.report.n2o.api.model.ReportHash;

import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * @author rsadikov
 * @since 02.11.2015
 */
public interface ReportSignatureService {

    ReportHash getReportHash(String url, int empPositionId) throws IOException, NoSuchProviderException, NoSuchAlgorithmException;

    void saveReportSignature(int reportSignatureId, byte[] signature);

    void writeZippedSignature(int reportSignatureId, OutputStream out) throws IOException;

    void writeSignedReport(int reportSignatureId, OutputStream outputStream) throws IOException;
}
