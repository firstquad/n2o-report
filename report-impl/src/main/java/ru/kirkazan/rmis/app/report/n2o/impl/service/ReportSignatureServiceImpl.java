package ru.kirkazan.rmis.app.report.n2o.impl.service;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kirkazan.rmis.app.report.n2o.api.model.ReportHash;
import ru.kirkazan.rmis.app.report.n2o.api.model.ReportSignature;
import ru.kirkazan.rmis.app.report.n2o.api.service.ReportSignatureDao;
import ru.kirkazan.rmis.app.report.n2o.api.service.ReportSignatureService;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author rsadikov
 * @since 02.11.2015
 */
@Service
public class ReportSignatureServiceImpl implements ReportSignatureService {
    private static final Logger log = LoggerFactory.getLogger(ReportSignatureServiceImpl.class);

    private static final String HASH_ALGORITHM = "GOST3411";

    private static final String PARAM_REPORT = "__report";

    private static final int BUFFER_SIZE = 1024;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Autowired
    private SignedReportFileStorageImpl signedReportFileStorage;

    @Autowired
    private ReportSignatureDao reportSignatureDao;

    @Value("${rmis.report.birt.internal_url}")
    private String internalBirtUrl;

    @Override
    @Transactional
    public ReportHash getReportHash(String urlStr, int empPositionId) throws IOException, NoSuchProviderException, NoSuchAlgorithmException {
        BufferedInputStream bufferedInputStream = null;
        InputStream is = null;
        try {
            String internalReportUrl = internalBirtUrl + URLDecoder.decode(urlStr, "UTF-8");
            log.debug("Internal report url: " + internalReportUrl);

            URL url = new URL(internalReportUrl);
            bufferedInputStream = new BufferedInputStream(url.openStream());
            // todo check content type?
            String fullPath = signedReportFileStorage.saveContent(bufferedInputStream, generateReportName(empPositionId) + ".pdf");

            ReportSignature reportSignature = new ReportSignature(getParamValue(url.getQuery(), PARAM_REPORT), empPositionId, fullPath);
            int id = reportSignatureDao.saveReportSignature(reportSignature);

            is = signedReportFileStorage.getContent(fullPath);
            return new ReportHash(id, getDocumentHash(is));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw ex;
        } finally {
            if (bufferedInputStream != null) {
                bufferedInputStream.close();
            }
            if (is != null) {
                is.close();
            }
        }
    }

    private static String getParamValue(String query, String key) throws UnsupportedEncodingException {
        String[] params = query.split("&");
        for (String param : params)
        {
            String[] pair = param.split("=");
            if (pair[0].equals(key)) {
                if (pair[1] != null) {
                    return URLDecoder.decode(pair[1], "UTF-8");
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    public byte[] getDocumentHash(InputStream inputStream) throws NoSuchProviderException, NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM, BouncyCastleProvider.PROVIDER_NAME);

        byte[] dataBytes = new byte[BUFFER_SIZE];
        int numRead;
        while ((numRead = inputStream.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, numRead);
        }

        return md.digest();
    }

    private String generateReportName(int empPositionId) {
        return System.currentTimeMillis() + "_" + empPositionId;
    }

    @Override
    public void saveReportSignature(int reportSignatureId, byte[] signature) {
        ReportSignature reportSignature = reportSignatureDao.getReportSignature(reportSignatureId);
        String signPath = signedReportFileStorage.saveContentWithFullPath(new ByteArrayInputStream(signature),
                generateSignatureName(reportSignature.getReportFile()) + ".sig");

        reportSignature.setSignatureFile(signPath);
        reportSignatureDao.saveReportSignature(reportSignature);
    }

    private String generateSignatureName(String reportFileName) {
        return reportFileName.substring(0, reportFileName.length() - 4);
    }

    @Override
    public void writeZippedSignature(int reportSignatureId, OutputStream out) throws IOException {
        ReportSignature reportSignature = reportSignatureDao.getReportSignature(reportSignatureId);

        ZipOutputStream zos = new ZipOutputStream(out);
        addFileToZip(zos, reportSignature.getReportFile(), "report.pdf");
        addFileToZip(zos, reportSignature.getSignatureFile(), "report.sig");
        zos.close();
    }

    private void addFileToZip(ZipOutputStream zos, String fileName, String zipEntryName) throws IOException {
        InputStream inputStream = signedReportFileStorage.getContent(fileName);
        zos.putNextEntry(new ZipEntry(zipEntryName));

        byte bytes[] = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = inputStream.read(bytes)) != -1) {
            zos.write(bytes, 0, bytesRead);
        }
        inputStream.close();
        zos.closeEntry();
    }

    @Override
    public void writeSignedReport(int reportSignatureId, OutputStream outputStream) throws IOException {
        ReportSignature reportSignature = reportSignatureDao.getReportSignature(reportSignatureId);
        InputStream inputStream = signedReportFileStorage.getContent(reportSignature.getReportFile());

        IOUtils.copy(inputStream, outputStream);
    }
}
