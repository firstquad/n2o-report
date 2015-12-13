package ru.kirkazan.rmis.app.report.n2o.place.criteria.reportOnPlace;

import net.n2oapp.criteria.api.Criteria;

/**
 * Created by dfirstov on 07.11.2014.
 */
public class ReportOnPlaceCriteria extends Criteria {
    private String id;
    private String name;
    private String formId;
    private String formName;
    private String fileName;
    private String placeId;
    private String placeName;
    private String reportContainerId;
    private String reportContainerName;
    private String reportPlaceToCallLocationId;

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
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
}
