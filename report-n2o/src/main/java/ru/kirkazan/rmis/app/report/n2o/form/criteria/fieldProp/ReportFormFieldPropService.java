package ru.kirkazan.rmis.app.report.n2o.form.criteria.fieldProp;

import net.n2oapp.framework.api.metadata.control.N2oField;
import net.n2oapp.framework.api.metadata.control.N2oListField;
import net.n2oapp.framework.api.metadata.global.view.N2oPage;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oFieldSet;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oForm;
import net.n2oapp.framework.config.persister.MetadataPersister;
import net.n2oapp.framework.config.register.ConfigId;
import net.n2oapp.framework.config.register.ConfigRegister;
import net.n2oapp.framework.config.service.GlobalMetadataStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kirkazan.rmis.app.report.n2o.form.ReportFormService;
import ru.kirkazan.rmis.app.report.n2o.place.model.N2oReportPlace;

import java.util.List;
import java.util.Properties;

import static ru.kirkazan.rmis.app.report.n2o.form.criteria.fieldType.ReportFieldTypeCriteriaService.resolveTypeClassById;

/**
 * Created by dfirstov on 23.11.2014.
 */
public class ReportFormFieldPropService {
    private Properties properties;
    private static final Logger logger = LoggerFactory.getLogger(ReportFormFieldPropService.class);

    public void changeFormFields(ReportFormFieldProp formField) {
        String formId = formField.getFormId();
        if (formId == null || formField.getFieldId() == null)
            return;
        if (!ConfigRegister.getInstance().contains(formId, N2oForm.class))
            return;
        N2oForm n2oForm = ReportFormService.retrieveForm(formId);
        resolveVisibleAndRequired(formField);
        N2oForm form = fillFormParam(n2oForm, formField);
        MetadataPersister.getInstance().persist(form, resolveFormPath());
        List<N2oReportPlace> places = GlobalMetadataStorage.getInstance().getAll(N2oReportPlace.class);
        refresh(formId, places);
    }

    public void resolveVisibleAndRequired(ReportFormFieldProp formField) {
        Boolean required = formField.getRequired();
        Boolean visible = formField.getVisible();
        if (required != null && required) {
            formField.setVisible(true);
        } else if (visible != null && !visible) {
            formField.setRequired(false);
        }
    }

    public N2oForm fillFormParam(N2oForm form, ReportFormFieldProp reportFormField) {
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
                                    N2oField[] formFields = block.getFields();
                                    if (formFields != null) {
                                        N2oField[] newFormFields = new N2oField[formFields.length];
                                        for (int i = 0; i < formFields.length; i++) {
                                            N2oField n2oFormField = formFields[i];
                                            if (n2oFormField == null)
                                                continue;
                                            newFormFields[i] = n2oFormField;
                                            if (n2oFormField.getId().equals(reportFormField.getFieldId())) {
                                                updateField(reportFormField, newFormFields, n2oFormField, i);
                                            }
                                            String href = form.getEdit().getAnchor().getHref();
                                            String newHref = resolveHref(n2oFormField, newFormFields[i], href);
                                            form.getEdit().getAnchor().setHref(newHref);
                                        }
                                        block.setFields(newFormFields);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return form;
    }

    private void updateField(ReportFormFieldProp reportFormField, N2oField[] newFormFields, N2oField n2oFormField, int i) {
        String typeId = reportFormField.getTypeId();
        String queryId = reportFormField.getQueryId();
        if (typeId != null) {
            changeType(typeId, newFormFields, n2oFormField, queryId, i);
        }
        newFormFields[i].setLabel(n2oFormField.getLabel());
        newFormFields[i].setRequired(n2oFormField.getRequired());
        newFormFields[i].setVisible(n2oFormField.getVisible());
        String label = reportFormField.getLabel();
        Boolean required = reportFormField.getRequired();
        Boolean visible = reportFormField.getVisible();
        if (label != null)
            newFormFields[i].setLabel(label);
        if (required != null)
            newFormFields[i].setRequired(required);
        if (visible != null)
            newFormFields[i].setVisible(visible);
    }

    private void changeType(String typeId, N2oField[] newFormFields, N2oField n2oFormField, String queryId, int i) {
        try {
            Class<? extends N2oField> n2oClass = resolveTypeClassById(typeId);
            N2oField field = n2oClass.newInstance();
            newFormFields[i] = field;
            String formFieldId = n2oFormField.getId();
            newFormFields[i].setId(formFieldId);
            if (field instanceof N2oListField) {
                N2oListField classifier = (N2oListField) field;
                N2oListField.Query query = new N2oListField.Query();
                query.setQueryId(queryId);
                query.setValueFieldId("id");
                query.setLabelFieldId("name");
                classifier.setQuery(query);
                String id = resolveClassifierId(n2oFormField);
                classifier.setId(id);
                newFormFields[i] = classifier;
            }
        } catch (InstantiationException | IllegalAccessException e) {
            logger.warn(e.getMessage(), e);
        }
    }

    private String resolveHref(N2oField oldField, N2oField newField, String href) {
        String id = oldField.getId();
        String prefix = "=:";
        String suffix = ".id";
        String newId = newField.getId();
        if (oldField instanceof N2oListField) {
            return convertListField(newField, href, id, prefix, suffix, newId);
        } else {
            return convertPlaneField(newField, href, id, prefix, suffix, newId);
        }
    }

    private String convertPlaneField(N2oField newFieldId, String href, String id, String prefix, String suffix, String newId) {
        if (newFieldId instanceof N2oListField) {
            String planeFieldId = prefix + id + "_id";
            return (href.contains(planeFieldId))
                    ? href.replaceFirst(planeFieldId, prefix + newId + suffix)
                    : href.replaceFirst(prefix + id, prefix + newId + suffix);
        } else
            return href;
    }

    private String convertListField(N2oField newFieldId, String href, String id, String prefix, String suffix, String newId) {
        if (newFieldId instanceof N2oListField) {
            return href;
        } else {
            return href.replaceFirst(prefix + id + suffix, prefix + newId);
        }
    }

    private String resolveClassifierId(N2oField field) {
        String id = field.getId().replace(".", "_");
        String suffix = "_id";
        return (id.endsWith(suffix)) ? id.substring(0, id.indexOf(suffix)) : id;
    }

    private String resolveFormPath() {
        String formPath = this.properties.getProperty("rmis.report.form.path");
        if (formPath.startsWith("/"))
            formPath = formPath.substring(1, formPath.length());
        if (!formPath.endsWith("/"))
            formPath += "/";
        return formPath;
    }

    private void refresh(String formId, List<N2oReportPlace> places) {
        for (N2oReportPlace place : places) {
            if (place.getReportsElement() != null && place.getReportsElement().getReports() != null) {
                N2oReportPlace.Report[] reports = place.getReportsElement().getReports();
                verifyReports(formId, place, reports);
            }
            if (place.getContainerElements() != null) {
                for (N2oReportPlace.ContainerElement containerElement : place.getContainerElements()) {
                    if (containerElement.getReportsElement() != null && containerElement.getReportsElement().getReports() != null) {
                        N2oReportPlace.Report[] reports = containerElement.getReportsElement().getReports();
                        verifyReports(formId, place, reports);
                    }
                }
            }
        }
    }

    private void verifyReports(String formId, N2oReportPlace place, N2oReportPlace.Report[] reports) {
        for (N2oReportPlace.Report report : reports) {
            if (report.getFormId() != null && report.getFormId().equals(formId)) {
                ConfigRegister.getInstance().update(new ConfigId(place.getId(), ConfigRegister.getInstance().get(place.getId(), N2oPage.class).getBaseMetaModel()));
            }
        }
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
