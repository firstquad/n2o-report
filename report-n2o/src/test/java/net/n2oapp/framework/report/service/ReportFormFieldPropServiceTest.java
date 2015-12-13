package net.n2oapp.framework.report.service;

import net.n2oapp.framework.api.metadata.control.N2oField;
import net.n2oapp.framework.api.metadata.control.N2oListField;
import net.n2oapp.framework.api.metadata.control.list.N2oClassifier;
import net.n2oapp.framework.api.metadata.control.plain.N2oPlainField;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oForm;
import net.n2oapp.framework.config.reader.util.N2oComplexMetadataReader;
import net.n2oapp.framework.config.reader.widget.FormXmlReaderV2;
import net.n2oapp.framework.config.selective.reader.SelectiveReader;
import net.n2oapp.framework.config.selective.reader.SelectiveStandardReader;
import org.junit.Test;
import ru.kirkazan.rmis.app.report.n2o.form.criteria.fieldProp.ReportFormFieldProp;
import ru.kirkazan.rmis.app.report.n2o.form.criteria.fieldProp.ReportFormFieldPropService;

import java.util.Arrays;
import java.util.stream.Collectors;

import static ru.kirkazan.rmis.app.report.n2o.form.criteria.fieldType.ReportFieldTypeCriteriaService.Type.*;

/**
 * @author dfirstov
 * @since 06.10.2015
 */
public class ReportFormFieldPropServiceTest {
    private N2oForm sourceForm;
    private SelectiveReader widgetReader = new SelectiveStandardReader().addFieldSet2Reader()
            .addReader(new N2oComplexMetadataReader("http://n2oapp.net/framework/config/schema/n2o-widget-2.0", new FormXmlReaderV2()));


    @Test
    public void testChangeFormFields() {
        sourceForm = widgetReader.readByPath("net/n2oapp/framework/report/transformer/mock/reportFormMock.widget.xml");
        String href = "${rmis.report.url}?__report=30_2014.rptdesign&from_dt=:from_dt|uc:&to_dt=:to_dt|uc:&clinic_id=:clinic.id|uc:&part=:part|uc:&department_id=:department.id|uc:&filial=:filial.id|uc:&__format=pdf";
        testChangeParam("filial", "filial", "Филиал", "reportQueryTest", CLASSIFIER.id, href);
        href = "${rmis.report.url}?__report=30_2014.rptdesign&from_dt=:from_dt|uc:&to_dt=:to_dt|uc:&clinic_id=:clinic.id|uc:&part=:part|uc:&department_id=:department.id|uc:&filial=:filial|uc:&__format=pdf";
        testChangeParam("filial", "filial", "Филиал", "reportQueryTest", DATE_TIME.id, href);
        href = "${rmis.report.url}?__report=30_2014.rptdesign&from_dt=:from_dt|uc:&to_dt=:to_dt|uc:&clinic_id=:clinic.id|uc:&part=:part|uc:&department_id=:department.id|uc:&filial=:filial|uc:&__format=pdf";
        testChangeParam("clinic", "clinic","Филиал", "reportQueryTest", CLASSIFIER.id, href);
        href = "${rmis.report.url}?__report=30_2014.rptdesign&from_dt=:from_dt|uc:&to_dt=:to_dt|uc:&clinic_id=:clinic|uc:&part=:part|uc:&department_id=:department.id|uc:&filial=:filial|uc:&__format=pdf";
        testChangeParam("clinic", "clinic","Филиал", "reportQueryTest", CHECKBOX.id, href);
        sourceForm = widgetReader.readByPath("net/n2oapp/framework/report/transformer/mock/reportFormMock2.widget.xml");
        href = "${rmis.report.url}?__report=30_2014.rptdesign&filial_id=:filial.id|uc:&__format=pdf";
        testChangeParam("filial_id", "filial","Филиал", "reportQueryTest", CLASSIFIER.id, href);
        sourceForm = widgetReader.readByPath("net/n2oapp/framework/report/transformer/mock/reportFormMock2.widget.xml");
        href = "${rmis.report.url}?__report=30_2014.rptdesign&filial_id=:filial_id|uc:&__format=pdf";
        testChangeParam("filial_id", "filial_id","Филиал", "reportQueryTest", CHECKBOX.id, href);
    }

    private void testChangeParam(String id, String newId, String label, String queryId, String typeId, String href) {
        ReportFormFieldProp field = new ReportFormFieldProp();
        field.setFieldId(id);
        field.setLabel(label);
        field.setFormId("reportFormMock");
        field.setVisible(true);
        field.setRequired(true);
        field.setTypeId(typeId);
        field.setQueryId(queryId);
        N2oForm form = new ReportFormFieldPropService().fillFormParam(sourceForm, field);
        assert form.getEdit().getAnchor().getHref().equals(href);
        N2oField newField = Arrays.asList(form.getFieldSetContainers()[0].getFieldSet().getBlocks()[0].getFields()).stream()
                .filter(f -> f.getId().equals(newId)).collect(Collectors.toList()).get(0);
        assert newField.getId().equals(newId);
        assert newField.getLabel().equals(label);
        assert newField.getRequired();
        assert newField.getVisible();
        if (CLASSIFIER.id.equals(typeId)) {
            assert (newField instanceof N2oListField);
            N2oClassifier classifier = (N2oClassifier) newField;
            assert classifier.getQuery().getQueryId().equals(queryId);
        } else {
            assert (newField instanceof N2oPlainField);
        }
    }

    @Test
    public void testResolveVisibleAndRequired() {
        ReportFormFieldPropService service = new ReportFormFieldPropService();
        ReportFormFieldProp field = new ReportFormFieldProp();
        field.setVisible(true);
        field.setRequired(true);
        service.resolveVisibleAndRequired(field);
        assert field.getVisible();
        assert field.getRequired();
        field = new ReportFormFieldProp();
        field.setRequired(false);
        field.setVisible(false);
        service.resolveVisibleAndRequired(field);
        assert !field.getVisible();
        assert !field.getRequired();
        field = new ReportFormFieldProp();
        field.setVisible(true);
        field.setRequired(null);
        service.resolveVisibleAndRequired(field);
        assert field.getVisible();
        assert field.getRequired() == null;
        field = new ReportFormFieldProp();
        field.setVisible(false);
        field.setRequired(null);
        service.resolveVisibleAndRequired(field);
        assert !field.getVisible();
        assert !field.getRequired();
        field = new ReportFormFieldProp();
        field.setVisible(null);
        field.setRequired(true);
        service.resolveVisibleAndRequired(field);
        assert field.getVisible();
        assert field.getRequired();
        field = new ReportFormFieldProp();
        field.setVisible(null);
        field.setRequired(false);
        service.resolveVisibleAndRequired(field);
        assert field.getVisible() == null;
        assert !field.getRequired();
    }

}
