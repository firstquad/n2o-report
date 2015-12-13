package ru.kirkazan.rmis.app.report.n2o.form.util;

import net.n2oapp.framework.api.metadata.control.N2oField;
import net.n2oapp.framework.api.metadata.global.dao.N2oQuery;
import net.n2oapp.framework.api.metadata.global.view.N2oPage;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oFieldSet;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oForm;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oWidget;
import net.n2oapp.framework.config.register.ConfigRegister;
import net.n2oapp.framework.config.service.GlobalMetadataStorage;
import ru.kirkazan.rmis.app.report.n2o.form.ReportFormService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dfirstov on 29.12.2014.
 */
public class ReportFormUtil {

    public static List<N2oField> retrieveFormFields(N2oForm form) {
        List<N2oField> n2oFields = new ArrayList<>();
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
                                                n2oFields.add(n2oFormField);
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
        return n2oFields;
    }

    public static List<N2oField> retrieveFormFields(String formId) {
        if (!ConfigRegister.getInstance().contains(formId, N2oForm.class))
            return new ArrayList<N2oField>();
        N2oForm form = ReportFormService.retrieveForm(formId);
        return retrieveFormFields(form);
    }

    public static List<String> retrieveFormFieldIds(String formId) {
        if (!ConfigRegister.getInstance().contains(formId, N2oForm.class))
            return new ArrayList<String>();
        N2oForm form = ReportFormService.retrieveForm(formId);
        List<N2oField> n2oFields = retrieveFormFields(form);
        List<String> n2oFieldIds = new ArrayList<>();
        for (N2oField n2oField : n2oFields) {
            n2oFieldIds.add(n2oField.getId());
        }
        return n2oFieldIds;
    }

    public static List<N2oQuery.Field> retrieveFieldsFromContainer(N2oPage.Container container) {
        if (container.getWidget() != null && container.getWidget().getQueryId() != null) {
            String queryId = container.getWidget().getQueryId();
            if (ConfigRegister.getInstance().contains(queryId, N2oQuery.class)) {
                return retrieveFieldsFromQuery(queryId);
            }
        }
        return new ArrayList<>();
    }

    public static List<String> retrieveFieldIdsFromContainer(N2oPage.Container container) {
        List<String> fieldIds = new ArrayList<>();
        N2oWidget widget = container.getWidget();
        if (widget != null) {
            String queryId = retrieveQueryId(widget);
            List<N2oQuery.Field> fields = new ArrayList<>();
            if (ConfigRegister.getInstance().contains(queryId, N2oQuery.class)) {
                fields = retrieveFieldsFromQuery(queryId);
            }
            for (N2oQuery.Field field : fields) {
                fieldIds.add(field.getId());
            }
        }
        return fieldIds;
    }

    public static String retrieveQueryId(N2oWidget widget) {
        if (widget == null)
            return "";
        if (widget.getQueryId() != null) {
            return widget.getQueryId();
        } else if (ConfigRegister.getInstance().contains(widget.getId(), N2oWidget.class)) {
            N2oWidget n2oWidget = GlobalMetadataStorage.getInstance().get(widget.getId(), N2oWidget.class);
            return n2oWidget.getQueryId() == null ? "" : n2oWidget.getQueryId();
        }
        return "";
    }

    private static List<N2oQuery.Field> retrieveFieldsFromQuery(String queryId) {
        if (!ConfigRegister.getInstance().contains(queryId, N2oQuery.class))
            return new ArrayList<>();
        N2oQuery query = GlobalMetadataStorage.getInstance().get(queryId, N2oQuery.class);
        N2oQuery.Field[] queryFields = query.getFields();
        if (queryFields != null) {
            return Arrays.asList(queryFields);
        }
        return new ArrayList<>();
    }

}
