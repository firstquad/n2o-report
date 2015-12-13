package ru.kirkazan.rmis.app.report.n2o.place.criteria.placeToReport;

/**
 * Created by dfirstov on 28.09.2014.
 */
public class PlaceToReport {
    private String id;
    private String realId;
    private String code;
    private String n2oClass;
    private String placeName;
    private String placeId;
    private String formId;
    private String fileName;
    private String label;
    private String url;
    private Integer reportId;
    private String reportName;
    private String reportContainerId;
    private String reportContainerName;
    private String invalidMessage;
    private Boolean isInvalid;

    public String getRealId() {
        return realId;
    }

    public void setRealId(String realId) {
        this.realId = realId;
    }

    public String getReportContainerName() {
        return reportContainerName;
    }

    public void setReportContainerName(String reportContainerName) {
        this.reportContainerName = reportContainerName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public String getReportContainerId() {
        return reportContainerId;
    }

    public void setReportContainerId(String reportContainerId) {
        this.reportContainerId = reportContainerId;
    }

    public String getInvalidMessage() {
        return invalidMessage;
    }

    public void setInvalidMessage(String invalidMessage) {
        this.invalidMessage = invalidMessage;
    }

    public Boolean getIsInvalid() {
        return isInvalid;
    }

    public void setIsInvalid(Boolean isInvalid) {
        this.isInvalid = isInvalid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getN2oClass() {
        return n2oClass;
    }

    public void setN2oClass(String n2oClass) {
        this.n2oClass = n2oClass;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }
}
