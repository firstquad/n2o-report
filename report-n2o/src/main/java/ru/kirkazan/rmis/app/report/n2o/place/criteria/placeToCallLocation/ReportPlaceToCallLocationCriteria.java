package ru.kirkazan.rmis.app.report.n2o.place.criteria.placeToCallLocation;

import net.n2oapp.criteria.api.Criteria;

/**
 * Created by dfirstov on 02.11.2014.
 */
public class ReportPlaceToCallLocationCriteria extends Criteria {
    private String id;
    private String name;
    private String formId;
    private String fileName;
    private Integer reportId;
    private String reportContainerId;
    private String reportContainerName;
    private String placeId;
    private String placeName;
    private Boolean isInvalid;

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getReportContainerId() {
        return reportContainerId;
    }

    public void setReportContainerId(String reportContainerId) {
        this.reportContainerId = reportContainerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public String getReportContainerName() {
        return reportContainerName;
    }

    public void setReportContainerName(String reportContainerName) {
        this.reportContainerName = reportContainerName;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public Boolean getIsInvalid() {
        return isInvalid;
    }

    public void setIsInvalid(Boolean isInvalid) {
        this.isInvalid = isInvalid;
    }
}
