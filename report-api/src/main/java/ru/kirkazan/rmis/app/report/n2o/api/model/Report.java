package ru.kirkazan.rmis.app.report.n2o.api.model;

/**
 * Created by dfirstov on 28.09.2014.
 */
public class Report {
    private Integer id;
    private String name;
    private String formId;
    private String n2oFormId;
    private Integer groupId;
    private String groupNames;
    private String formName;
    private String placeId;
    private String fileName;
    private String path;
    private String label;
    private String url;
    private Boolean isGeneratedForm;
    private String containerId;
    private String containerName;
    private String placeName;
    private Boolean isHideForm;
    private Boolean isReportModule;
    private String note;
    private String invalidMessage;
    private Boolean isInvalid;

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

    public String getN2oFormId() {
        return n2oFormId;
    }

    public void setN2oFormId(String n2oFormId) {
        this.n2oFormId = n2oFormId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getGroupNames() {
        return groupNames;
    }

    public void setGroupNames(String groupNames) {
        this.groupNames = groupNames;
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

    public Boolean getIsHideForm() {
        return isHideForm;
    }

    public void setIsHideForm(Boolean isHideForm) {
        this.isHideForm = isHideForm;
    }

    public Boolean getIsReportModule() {
        return isReportModule;
    }

    public void setIsReportModule(Boolean isReportModule) {
        this.isReportModule = isReportModule;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
}
