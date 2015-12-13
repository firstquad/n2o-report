package ru.kirkazan.rmis.app.report.n2o.place.criteria.fieldToContainer;

import net.n2oapp.criteria.api.Criteria;

/**
 * Created by dfirstov on 13.11.2014.
 */
public class FieldToContainerCriteria extends Criteria {
    private String fieldId;
    private String formId;
    private String containerId;
    private String placeId;
    private String label;

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
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

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
