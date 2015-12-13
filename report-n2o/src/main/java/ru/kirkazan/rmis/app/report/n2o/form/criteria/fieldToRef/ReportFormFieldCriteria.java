package ru.kirkazan.rmis.app.report.n2o.form.criteria.fieldToRef;

import net.n2oapp.criteria.api.Criteria;

/**
 * Created by dfirstov on 13.11.2014.
 */
public class ReportFormFieldCriteria extends Criteria {
    private String fieldId;
    private String refId;
    private String formId;
    private String containerId;
    private String placeId;

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
