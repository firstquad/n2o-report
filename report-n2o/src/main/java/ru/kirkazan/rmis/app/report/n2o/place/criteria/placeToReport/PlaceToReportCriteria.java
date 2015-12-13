package ru.kirkazan.rmis.app.report.n2o.place.criteria.placeToReport;

import net.n2oapp.criteria.api.Criteria;

/**
 * Created by dfirstov on 02.11.2014.
 */
public class PlaceToReportCriteria extends Criteria {
    private String id;
    private String code;
    private String reportName;
    private String formId;
    private String fileName;
    private Integer reportId;
    private String reportContainerId;
    private String placeName;
    private String n2oClass;

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

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
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

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
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
}
