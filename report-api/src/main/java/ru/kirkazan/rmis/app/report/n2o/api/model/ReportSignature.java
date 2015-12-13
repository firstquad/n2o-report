package ru.kirkazan.rmis.app.report.n2o.api.model;

import java.util.Date;

/**
 * @author rsadikov
 * @since 10.11.2015
 */
public class ReportSignature {
    private Integer id;
    private String reportCode;
    private Integer positionId;
    private Date signDate;
    private String reportFile;
    private String signatureFile;

    public ReportSignature() {
    }

    public ReportSignature(String reportCode, Integer positionId, String reportFile) {
        this.reportCode = reportCode;
        this.positionId = positionId;
        this.reportFile = reportFile;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReportCode() {
        return reportCode;
    }

    public void setReportCode(String reportCode) {
        this.reportCode = reportCode;
    }

    public Integer getPositionId() {
        return positionId;
    }

    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
    }

    public Date getSignDate() {
        return signDate;
    }

    public void setSignDate(Date signDate) {
        this.signDate = signDate;
    }

    public String getReportFile() {
        return reportFile;
    }

    public void setReportFile(String reportFile) {
        this.reportFile = reportFile;
    }

    public String getSignatureFile() {
        return signatureFile;
    }

    public void setSignatureFile(String signatureFile) {
        this.signatureFile = signatureFile;
    }
}
