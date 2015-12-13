package ru.kirkazan.rmis.app.report.n2o.form.criteria.form;

import net.n2oapp.criteria.api.Criteria;

/**
 * Created by dfirstov on 02.11.2014.
 */
public class ReportFormCriteria extends Criteria {
    private String id;
    private String name;
    private String formId;
    private String fileName;

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
}
