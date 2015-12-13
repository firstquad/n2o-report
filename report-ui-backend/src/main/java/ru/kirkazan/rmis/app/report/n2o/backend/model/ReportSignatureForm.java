package ru.kirkazan.rmis.app.report.n2o.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author rsadikov
 * @since 11.11.2015
 */
public class ReportSignatureForm {
    @JsonProperty("id")
    private Integer reportSignatureId;

    @JsonProperty("value")
    private String signature;

    public Integer getReportSignatureId() {
        return reportSignatureId;
    }

    public void setReportSignatureId(Integer reportSignatureId) {
        this.reportSignatureId = reportSignatureId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
