package ru.kirkazan.rmis.app.report.n2o.form;

import net.n2oapp.framework.api.metadata.control.N2oField;
import net.n2oapp.framework.api.metadata.control.N2oListField;
import net.n2oapp.framework.api.metadata.control.list.N2oClassifier;
import net.n2oapp.framework.api.metadata.control.plain.N2oDatePicker;
import net.n2oapp.framework.api.metadata.control.plain.N2oInputText;
import net.n2oapp.framework.api.metadata.global.dao.N2oPreFilter;
import net.n2oapp.framework.api.metadata.global.view.action.control.Anchor;
import net.n2oapp.framework.api.metadata.global.view.tools.AfterSubmit;
import net.n2oapp.framework.api.metadata.global.view.tools.N2oEdit;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oFieldSet;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oForm;
import net.n2oapp.framework.api.ui.FormModel;
import net.n2oapp.framework.config.persister.MetadataPersister;
import net.n2oapp.framework.config.service.GlobalMetadataStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kirkazan.rmis.app.report.n2o.impl.parser.ReportParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dfirstov on 24.09.2014.
 */
public class ReportFormService {
    private static final Logger logger = LoggerFactory.getLogger(ReportFormService.class);

    public N2oForm createFormByReport(List<ReportParam> reportParams, String formId, String fileName, String reportName, String formPath) {
        removeExclude(reportParams);
        N2oForm n2oForm = createN2oForm(reportParams, formId, fileName, reportName);
        if (formPath.startsWith("/"))
            formPath = formPath.substring(1, formPath.length());
        if (!formPath.endsWith("/"))
            formPath += "/";
        MetadataPersister.getInstance().persist(n2oForm, formPath);
        return n2oForm;
    }

    private void removeExclude(List<ReportParam> reportParams) {
        new ArrayList<>(reportParams).forEach(r -> {
            if (isExclude(r))
                reportParams.remove(r);
        });
    }

    private static boolean isExclude(ReportParam reportParam) {
        return ("user_id".equals(reportParam.getName()));
    }

    public static N2oForm createN2oForm(List<ReportParam> reportParams, String formId, String fileName, String reportName) {
        String url = getUrlToBirt(fileName, reportParams);
        N2oEdit edit = createEdit(url);
        N2oForm n2oForm = createN2oForm(formId, reportName, edit);
        List<N2oField> fields = createFields(reportParams);
        N2oForm.FieldSetContainer fsc = createFieldSetContainer(fields);
        n2oForm.setFieldSetContainers(new N2oForm.FieldSetContainer[]{fsc});
        return n2oForm;
    }

    private static N2oEdit createEdit(String url) {
        N2oEdit edit = new N2oEdit();
        edit.setAfterSubmit(AfterSubmit.closeModal);
        edit.setFormSubmitLabel("Сформировать отчёт");
        edit.setAnchor(new Anchor(url));
        edit.setModel(FormModel.DEFAULT);
        return edit;
    }

    private static N2oForm createN2oForm(String formId, String reportName, N2oEdit edit) {
        N2oForm n2oForm = new N2oForm();
        n2oForm.setId(formId);
        n2oForm.setObjectId("report");
        n2oForm.setEdit(edit);
        n2oForm.setName(reportName);
        return n2oForm;
    }

    private static List<N2oField> createFields(List<ReportParam> reportParams) {
        List<N2oField> fields = new ArrayList<>();
        for (ReportParam reportParam : reportParams) {
            N2oField field;
            field = new N2oInputText();
            field = fillDefaultReportParameter(reportParam.getName(), field);
            field.setRequired(reportParam.getRequired());
            field.setVisible(true);
            fields.add(field);
        }
        return fields;
    }

    private static N2oField fillDefaultReportParameter(String reportParamName, N2oField field) {
        String id = reportParamName;
        String label = reportParamName;
        switch (reportParamName) {
            case "from_dt": {
                label = "От";
                field = new N2oDatePicker();
                break;
            }
            case "to_dt": {
                label = "До";
                field = new N2oDatePicker();
                break;
            }
            case "clinic_id": {
                id = "clinic";
                label = "ЛПУ";
                field = createClassifier("reportClinics", "name");
                break;
            }
            case "department_id": {
                id = "department";
                label = "Отделение";
                field = createClassifier("reportDepartments", "name");
                break;
            }
            case "doctor_id": {
                id = "doctor";
                label = "Врач";
                field = createClassifier("reportDoctors", "name");
                break;
            }
        }
        field.setId(id);
        field.setLabel(label);
        return field;
    }

    private static N2oClassifier createClassifier(String queryId, String labelField) {
        N2oClassifier classifier = new N2oClassifier();
        N2oListField.Query query = new N2oListField.Query();
        query.setQueryId(queryId);
        query.setValueFieldId("id");
        query.setLabelFieldId(labelField);
        fillPreFilters(query);
        classifier.setQuery(query);
        classifier.setSearchAsYouType(true);
        classifier.setMode(N2oClassifier.Mode.advance);
        return classifier;
    }

    private static void fillPreFilters(N2oListField.Query query) {
        if (query.getQueryId().equals("reportClinics"))
            return;
        N2oPreFilter preFilter = new N2oPreFilter();
        preFilter.setFieldId("org.id");
        preFilter.setRef("clinic.id");
        //todo убрать после фикса NPE
        preFilter.setValues(new ArrayList<>());
        List<N2oPreFilter> preFilters = new ArrayList<>();
        preFilters.add(preFilter);
        query.setPreFilters(preFilters);
    }

    private static N2oForm.FieldSetContainer createFieldSetContainer(List<N2oField> fields) {
        N2oFieldSet.Block block = new N2oFieldSet.Block();
        block.setFields(fields.toArray(new N2oField[fields.size()]));
        N2oFieldSet fs = new N2oFieldSet();
        fs.setBlocks(new N2oFieldSet.Block[]{block});
        N2oForm.FieldSetContainer fsc = new N2oForm.FieldSetContainer();
        fsc.setFieldSet(fs);
        return fsc;
    }

    public static String getUrlToBirt(String fileName, List<ReportParam> reportParams) {
        String birtHost = "${rmis.report.url}";
        String url = birtHost + "?__report=" + fileName + "&";
        if (reportParams != null)
            for (ReportParam reportParam : reportParams) {
                String paramName = reportParam.getName();
                paramName = processParamName(paramName);
                url = url + reportParam.getName() + "=:" + paramName + "|uc:&";
            }
        url = url + "__format=pdf";
        return url;
    }

    private static String processParamName(String paramName) {
        switch (paramName) {
            case "clinic_id":
                paramName = "clinic.id";
                break;
            case "department_id":
                paramName = "department.id";
                break;
            case "doctor_id":
                paramName = "doctor.id";
                break;
        }
        return paramName;
    }

    public static N2oForm retrieveForm(String fromId) {
        N2oForm form = null;
        try {
            form = GlobalMetadataStorage.getInstance().get(fromId, N2oForm.class);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            return null;
        }
        return form;
    }
}
