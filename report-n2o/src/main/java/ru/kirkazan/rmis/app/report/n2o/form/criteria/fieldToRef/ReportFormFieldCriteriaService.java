package ru.kirkazan.rmis.app.report.n2o.form.criteria.fieldToRef;

import net.n2oapp.criteria.api.CollectionPage;
import net.n2oapp.criteria.api.CollectionPageService;
import net.n2oapp.criteria.api.FilteredCollectionPage;
import net.n2oapp.framework.api.metadata.control.N2oField;
import net.n2oapp.framework.api.metadata.control.N2oListField;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oFieldSet;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oForm;
import net.n2oapp.framework.config.register.ConfigRegister;
import net.n2oapp.framework.config.service.GlobalMetadataStorage;
import ru.kirkazan.rmis.app.report.n2o.place.model.N2oReportPlace;
import ru.kirkazan.rmis.app.report.n2o.report.ReportN2oService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dfirstov on 13.11.2014.
 */
public class ReportFormFieldCriteriaService implements CollectionPageService<ReportFormFieldCriteria, ReportFormField> {
    private ReportN2oService reportN2oService;

    @Override
    public CollectionPage<ReportFormField> getCollectionPage(ReportFormFieldCriteria criteria) {
        List<ReportFormField> formFields = new ArrayList<>();
        List<N2oForm> forms = reportN2oService.getAllReportForms();
        String containerId = criteria.getContainerId();
        String placeId = criteria.getPlaceId();
        String formId = criteria.getFormId();
        N2oReportPlace place = new N2oReportPlace();
        if (placeId != null && ConfigRegister.getInstance().contains(placeId, N2oReportPlace.class)) {
            place = GlobalMetadataStorage.getInstance().get(placeId, N2oReportPlace.class);
        }
        for (N2oForm form : forms) {
            fillFormParam(formFields, containerId, placeId, formId, place, form);
        }
        List<ReportFormField> fields = filter(criteria, formFields, containerId, formId);
        for (Integer i = 0; i < fields.size(); i++) {
            fields.get(i).setId(i.toString());
        }
        return new FilteredCollectionPage<>(fields, criteria);
    }

    private void fillFormParam(List<ReportFormField> formFields, String containerId, String placeId, String formId, N2oReportPlace place, N2oForm form) {
        N2oForm.FieldSetContainer[] fieldSetContainers = form.getFieldSetContainers();
        if (fieldSetContainers != null) {
            for (N2oForm.FieldSetContainer fieldSetContainer : fieldSetContainers) {
                if (fieldSetContainer != null) {
                    N2oFieldSet n2oFieldSet = fieldSetContainer.getFieldSet();
                    if (n2oFieldSet != null) {
                        N2oFieldSet.Block[] blocks = n2oFieldSet.getBlocks();
                        if (blocks != null) {
                            for (N2oFieldSet.Block block : blocks) {
                                if (block != null) {
                                    N2oField[] N2oformFields = block.getFields();
                                    if (N2oformFields != null) {
                                        for (N2oField n2oFormField : N2oformFields) {
                                            if (n2oFormField != null) {
                                                ReportFormField formField = new ReportFormField();
                                                String fieldId = n2oFormField.getId();
                                                if (n2oFormField instanceof N2oListField)
                                                    fieldId = fieldId + ".id";
                                                fillFieldRefId(formField, fieldId, place.getContainerElements(), formId, containerId);
                                                formField.setFieldId(fieldId);
                                                String label;
                                                if (n2oFormField.getLabel() != null) {
                                                    label = n2oFormField.getLabel();
                                                } else {
                                                    label = fieldId;
                                                }
                                                formField.setLabel(label);
                                                formField.setFormId(form.getId());
                                                formField.setContainerId(containerId);
                                                formField.setPlaceId(placeId);
                                                formFields.add(formField);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void fillFieldRefId(ReportFormField formField, String fieldId, N2oReportPlace.ContainerElement[] placeContainerElements, String formId, String containerId) {
        if (placeContainerElements != null) {
            for (N2oReportPlace.ContainerElement containerElement : placeContainerElements) {
                if (containerElement.getId().equals(containerId)) {
                    N2oReportPlace.ReportsElement reports = containerElement.getReportsElement();
                    if (reports != null && reports.getReports() != null) {
                        for (N2oReportPlace.Report report : reports.getReports()) {
                            if (report.getParams() != null && report.getFormId() != null && report.getFormId().equals(formId)) {
                                for (N2oReportPlace.Param param : report.getParams()) {
                                    if (param.getFormFieldId().equals(fieldId)) {
                                        formField.setRefId(param.getRef());
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private List<ReportFormField> filter(ReportFormFieldCriteria criteria, List<ReportFormField> formFields, String containerId, String formId) {
        if (formId != null && !formId.equals("")) {
            formFields = filterOnFormId(formFields, formId);
        }
        String fieldId = criteria.getFieldId();
        if (fieldId != null && !fieldId.equals("")) {
            formFields = filterOnFieldId(formFields, fieldId);
        }
        if (containerId != null && !containerId.equals("")) {
            formFields = filterOnContainerId(formFields, containerId);
        }
        return formFields;
    }

    private List<ReportFormField> filterOnFormId(List<ReportFormField> reportForms, String formId) {
        List<ReportFormField> filteredPlace = new ArrayList<>();
        for (ReportFormField formField : reportForms) {
            if (formField.getFormId() != null && formField.getFormId().equals(formId)) {
                filteredPlace.add(formField);
            }
        }
        return filteredPlace;
    }

    private List<ReportFormField> filterOnFieldId(List<ReportFormField> reportForms, String fieldId) {
        List<ReportFormField> filteredPlace = new ArrayList<>();
        for (ReportFormField formField : reportForms) {
            if (formField.getFieldId() != null && formField.getFieldId().equals(fieldId)) {
                filteredPlace.add(formField);
            }
        }
        return filteredPlace;
    }

    private List<ReportFormField> filterOnContainerId(List<ReportFormField> reportForms, String containerId) {
        List<ReportFormField> filteredPlace = new ArrayList<>();
        for (ReportFormField formField : reportForms) {
            if (formField.getContainerId() != null && formField.getContainerId().equals(containerId)) {
                filteredPlace.add(formField);
            }
        }
        return filteredPlace;
    }

    public void setReportN2oService(ReportN2oService reportN2oService) {
        this.reportN2oService = reportN2oService;
    }
}
