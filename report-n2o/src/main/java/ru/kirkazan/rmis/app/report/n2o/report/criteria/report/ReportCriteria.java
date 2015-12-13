package ru.kirkazan.rmis.app.report.n2o.report.criteria.report;

import net.n2oapp.criteria.api.Criteria;

/**
 * Created by dfirstov on 07.11.2014.
 */
public class ReportCriteria extends Criteria {
    private Integer id;
    private String name;
    private String formId;
    private String formName;
    private String fileName;
    private String callMode;
    private Boolean defaultMode;
    private Boolean customForm;
    private Boolean hideForm;

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

    public Boolean getDefaultMode() {
        return defaultMode;
    }

    public void setDefaultMode(Boolean defaultMode) {
        this.defaultMode = defaultMode;
    }

    public Boolean getCustomForm() {
        return customForm;
    }

    public void setCustomForm(Boolean customForm) {
        this.customForm = customForm;
    }

    public Boolean getHideForm() {
        return hideForm;
    }

    public void setHideForm(Boolean hideForm) {
        this.hideForm = hideForm;
    }

    public String getCallMode() {
        return callMode;
    }

    public void setCallMode(String callMode) {
        this.callMode = callMode;
    }
}
