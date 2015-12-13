package ru.kirkazan.rmis.app.report.n2o.form.criteria.fieldProp;

import net.n2oapp.criteria.api.CollectionPage;
import net.n2oapp.criteria.api.CollectionPageService;
import net.n2oapp.criteria.api.FilteredCollectionPage;
import net.n2oapp.framework.api.metadata.control.N2oField;
import net.n2oapp.framework.api.metadata.control.N2oListField;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oFieldSet;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oForm;
import net.n2oapp.framework.config.register.ConfigRegister;
import net.n2oapp.framework.config.service.GlobalMetadataStorage;
import ru.kirkazan.rmis.app.report.n2o.report.ReportN2oService;

import java.util.ArrayList;
import java.util.List;

import static ru.kirkazan.rmis.app.report.n2o.form.criteria.fieldType.ReportFieldTypeCriteriaService.Type.CLASSIFIER;
import static ru.kirkazan.rmis.app.report.n2o.form.criteria.fieldType.ReportFieldTypeCriteriaService.resolveTypeByClass;
import static ru.kirkazan.rmis.app.report.n2o.form.criteria.fieldType.ReportFieldTypeCriteriaService.resolveTypeNameById;

/**
 * Created by dfirstov on 13.11.2014.
 */
public class ReportFormFieldPropCriteriaService implements CollectionPageService<ReportFormFieldPropCriteria, ReportFormFieldProp> {

    @Override
    public CollectionPage<ReportFormFieldProp> getCollectionPage(ReportFormFieldPropCriteria criteria) {
        String formId = criteria.getFormId();
        if (formId == null || !ConfigRegister.getInstance().contains(formId, N2oForm.class)) {
            return new FilteredCollectionPage<>(new ArrayList<>(), criteria);
        }
        N2oForm form = GlobalMetadataStorage.getInstance().get(formId, N2oForm.class);
        List<ReportFormFieldProp> formFields = new ArrayList<>();
        fillFormParam(formFields, form);
        List<ReportFormFieldProp> fields = filter(criteria, formFields);
        return new FilteredCollectionPage<>(fields, criteria);
    }

    private void fillFormParam(List<ReportFormFieldProp> formFields, N2oForm form) {
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
                                    N2oField[] n2oFormFields = block.getFields();
                                    if (n2oFormFields != null) {
                                        for (N2oField n2oFormField : n2oFormFields) {
                                            if (n2oFormField != null) {
                                                ReportFormFieldProp formField = new ReportFormFieldProp();
                                                String fieldId = n2oFormField.getId();
                                                formField.setFieldId(fieldId);
                                                formField.setFormId(form.getId());
                                                formField.setLabel(n2oFormField.getLabel());
                                                formField.setRequired(n2oFormField.getRequired());
                                                formField.setVisible(n2oFormField.getVisible());
                                                String typeId = resolveTypeByClass(n2oFormField);
                                                formField.setTypeId(typeId);
                                                formField.setTypeName(resolveTypeNameById(typeId));
                                                if (typeId.equals(CLASSIFIER.id)) {
                                                    N2oListField field = (N2oListField) n2oFormField;
                                                    N2oListField.Query query = field.getQuery();
                                                    if (query != null)
                                                        formField.setQueryId(query.getQueryId());
                                                }
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

    private List<ReportFormFieldProp> filter(ReportFormFieldPropCriteria
                                                     criteria, List<ReportFormFieldProp> formFields) {
        String fieldId = criteria.getFieldId();
        if (fieldId != null && !fieldId.equals("")) {
            formFields = filterOnFieldId(formFields, fieldId);
        }
        return formFields;
    }

    private List<ReportFormFieldProp> filterOnFieldId(List<ReportFormFieldProp> reportForms, String fieldId) {
        List<ReportFormFieldProp> filteredPlace = new ArrayList<>();
        for (ReportFormFieldProp formField : reportForms) {
            if (formField.getFieldId() != null && formField.getFieldId().equals(fieldId)) {
                filteredPlace.add(formField);
            }
        }
        return filteredPlace;
    }

}
