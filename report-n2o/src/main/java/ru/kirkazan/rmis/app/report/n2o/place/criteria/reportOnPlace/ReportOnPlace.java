package ru.kirkazan.rmis.app.report.n2o.place.criteria.reportOnPlace;

/**
 * Created by dfirstov on 28.09.2014.
 */
public class ReportOnPlace {
    private String id;
    private String placeName;
    private String placeId;
    private String formId;
    private String fileName;
    private String label;
    private String url;
    private Integer reportId;
    private String reportContainerId;
    private String reportContainerName;
    private String reportPlaceToCallLocationId;
    private Boolean isGeneratedForm;
    private Boolean isInvalid;

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    private String containerName;

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

    public String getReportContainerName() {
        return reportContainerName;
    }

    public void setReportContainerName(String reportContainerName) {
        this.reportContainerName = reportContainerName;
    }

    public String getReportPlaceToCallLocationId() {
        return reportPlaceToCallLocationId;
    }

    public void setReportPlaceToCallLocationId(String reportPlaceToCallLocationId) {
        this.reportPlaceToCallLocationId = reportPlaceToCallLocationId;
    }

    public Boolean getIsGeneratedForm() {
        return isGeneratedForm;
    }

    public void setIsGeneratedForm(Boolean isGeneratedForm) {
        this.isGeneratedForm = isGeneratedForm;
    }

    public Boolean getIsInvalid() {
        return isInvalid;
    }

    public void setIsInvalid(Boolean isInvalid) {
        this.isInvalid = isInvalid;
    }
}
