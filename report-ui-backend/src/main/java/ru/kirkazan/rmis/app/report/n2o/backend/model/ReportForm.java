package ru.kirkazan.rmis.app.report.n2o.backend.model;

import ru.kirkazan.rmis.app.report.n2o.api.model.Report;

/**
 * Created by dfirstov on 28.09.2014.
 */
public class ReportForm {
    private Integer id;
    private String formId;
    private String formName;
    private String placeId;
    private String fileName;
    private String path;
    private String label;
    private String url;
    private Boolean isGeneratedForm;
    private String containerId;
    private String placeName;
    private String containerName;

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Boolean getIsGeneratedForm() {
        return isGeneratedForm;
    }

    public void setIsGeneratedForm(Boolean isGeneratedForm) {
        this.isGeneratedForm = isGeneratedForm;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public Report extractReport() {
        Report report = new Report();
        report.setId(id);
        report.setFormId(formId);
        report.setLabel(label);
        report.setContainerId(containerId);
        report.setContainerName(containerName);
        report.setFileName(fileName);
        report.setFormName(formName);
        report.setIsGeneratedForm(isGeneratedForm);
        report.setPath(path);
        report.setPlaceId(placeId);
        report.setPlaceName(placeName);
        report.setUrl(url);
     return report;
    }
}
