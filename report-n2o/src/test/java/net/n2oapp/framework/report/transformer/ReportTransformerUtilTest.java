package net.n2oapp.framework.report.transformer;

import net.n2oapp.framework.api.metadata.control.N2oField;
import net.n2oapp.framework.api.metadata.control.list.N2oClassifier;
import net.n2oapp.framework.api.metadata.control.plain.N2oInputText;
import net.n2oapp.framework.api.metadata.global.view.action.N2oActionMenu;
import net.n2oapp.framework.api.metadata.global.view.action.control.Target;
import org.junit.Test;
import ru.kirkazan.rmis.app.report.n2o.place.model.N2oReportPlace;

import java.util.ArrayList;
import java.util.List;

import static ru.kirkazan.rmis.app.report.n2o.transformer.ReportTransformerUtil.*;

/**
 * @author dfirstov
 * @since 22.06.2015
 */
public class ReportTransformerUtilTest {

    @Test
    public void testisHideForm() {
        List<N2oField> formFields11 = new ArrayList<>();
        formFields11.add(makeN2oInputText("case_id", true, true));
        assert isHideForm(makePlaceParams("case_id"), formFields11);

        List<N2oField> formFields2 = new ArrayList<>();
        formFields2.add(makeN2oInputText("case_id", true, true));
        formFields2.add(makeN2oClassifier("step", true, true));
        assert !isHideForm(makePlaceParams("case_id"), formFields2);

        List<N2oField> formFields3 = new ArrayList<>();
        formFields3.add(makeN2oInputText("case_id", true, true));
        formFields3.add(makeN2oClassifier("step", true, true));
        assert isHideForm(makePlaceParams("case_id", "step.id"), formFields3);

        List<N2oField> formFields4 = new ArrayList<>();
        formFields4.add(makeN2oInputText("case_id", false, true));
        formFields4.add(makeN2oClassifier("step", false, false));
        assert isHideForm(makePlaceParams("case_id"), formFields4);

        List<N2oField> formFields5 = new ArrayList<>();
        formFields5.add(makeN2oInputText("case_id", false, true));
        formFields5.add(makeN2oClassifier("step", false, false));
        assert !isHideForm(new N2oReportPlace.Param[]{}, formFields5);

        List<N2oField> formFields6 = new ArrayList<>();
        formFields6.add(makeN2oInputText("case_id", false, true));
        formFields6.add(makeN2oClassifier("step", false, false));
        assert !isHideForm(makePlaceParams("step.id"), formFields6);

        List<N2oField> formFields7 = new ArrayList<>();
        formFields7.add(makeN2oInputText("case_id", false, true));
        formFields7.add(makeN2oClassifier("step", false, false));
        assert isHideForm(makePlaceParams("case_id"), formFields7);

        List<N2oField> formFields8 = new ArrayList<>();
        formFields8.add(makeN2oInputText("case_id", true, true));
        formFields8.add(makeN2oClassifier("step", false, true));
        assert !isHideForm(makePlaceParams("case_id"), formFields8);

        List<N2oField> formFields9 = new ArrayList<>();
        formFields9.add(makeN2oInputText("case_id", false, true));
        formFields9.add(makeN2oClassifier("step", false, false));
        assert isHideForm(makePlaceParams("case_id", "step.id"), formFields9);
    }
    
    private static N2oReportPlace.Param[] makePlaceParams(String... formFiledIds) {
        List<N2oReportPlace.Param> paramList = new ArrayList<>();
        for (String formFiledId: formFiledIds) {
            paramList.add(new N2oReportPlace.Param(formFiledId, ""));
        }
        return paramList.toArray(new N2oReportPlace.Param[paramList.size()]);
    }

    private static N2oField makeN2oClassifier(String id, Boolean is_Visible, Boolean isRequired) {
        N2oField n2oField31 = new N2oClassifier(id);
        n2oField31.setVisible(is_Visible);
        n2oField31.setRequired(isRequired);
        return n2oField31;
    }

    private static N2oField makeN2oInputText(String id, Boolean is_Visible, Boolean isRequired) {
        N2oField n2oField31 = new N2oInputText(id);
        n2oField31.setVisible(is_Visible);
        n2oField31.setRequired(isRequired);
        return n2oField31;
    }

    @Test
    public void testRetrieveParams() {
        String href1 = "report?:shift:&store_id=:store_id:&from_dt=:from_dt:&to_dt=:to_dt:&__format=pdf";
        List<String> expectedResult1 = new ArrayList<>();
        expectedResult1.add("shift");
        expectedResult1.add("store_id");
        expectedResult1.add("from_dt");
        expectedResult1.add("to_dt");
        List<String> params1 = retrieveParams(href1);
        assert expectedResult1.equals(params1);

        String href2 = "report?shift=:shift:&store_id=:store_id:&from_dt=:from_dt:&to_dt=:to_dt:&__format=pdf";
        List<String> expectedResult2 = new ArrayList<>();
        expectedResult2.add("shift");
        expectedResult2.add("store_id");
        expectedResult2.add("from_dt");
        expectedResult2.add("to_dt");
        List<String> params2 = retrieveParams(href2);
        assert expectedResult2.equals(params2);

        String href3 = "${rmis.report.url}?__report=001-1y-84.rptdesign&clinic_id=:clinic.id|uc:&department_id=:department.id|uc:&__format=pdf";
        List<String> expectedResult3 = new ArrayList<>();
        expectedResult3.add("clinic.id");
        expectedResult3.add("department.id");
        List<String> params3 = retrieveParams(href3);
        assert expectedResult3.equals(params3);

        String href4 = "${rmis.report.url}?__report=001-1y-84.rptdesign&__format=pdf";
        List<String> params4 = retrieveParams(href4);
        assert params4.isEmpty();

        String href5 = "${rmis.report.url}?__report=001-1y-84.rptdesign&clinic_id=:clinic.id:&department_id=:department.id:&__format=:format:";
        List<String> expectedResult5 = new ArrayList<>();
        expectedResult5.add("clinic.id");
        expectedResult5.add("department.id");
        expectedResult5.add("format");
        List<String> params5 = retrieveParams(href5);
        assert expectedResult5.equals(params5);
    }

    @Test
    public void testHrefParamsReplace() {
        String href1 = "report?:shift:&store_id=:store_id:&from_dt=:from_dt:&to_dt=:to_dt:&__format=pdf";
        N2oReportPlace.Param param1 = new N2oReportPlace.Param();
        param1.setFormFieldId("store_id");
        param1.setRef("id");
        String result1 = hrefParamsReplace(href1, param1);
        assert "report?:shift:&store_id=:id|uc:&from_dt=:from_dt:&to_dt=:to_dt:&__format=pdf".equals(result1);

        String href2 = "${rmis.report.url}?store_id=:store.id:&from_dt=:from_dt:&to_dt=:to_dt:&__format=pdf";
        N2oReportPlace.Param param2 = new N2oReportPlace.Param();
        param2.setFormFieldId("store.id");
        param2.setRef("patient.id");
        String result2 = hrefParamsReplace(href2, param2);
        assert "${rmis.report.url}?store_id=:patient.id|uc:&from_dt=:from_dt:&to_dt=:to_dt:&__format=pdf".equals(result2);

        String href3 = "${rmis.report.url}?store_id=:store.id:&from_dt=:from_dt|uc:&to_dt=:to_dt:&__format=pdf";
        N2oReportPlace.Param param3 = new N2oReportPlace.Param();
        param3.setFormFieldId("from_dt");
        param3.setRef("date_begin");
        String result3 = hrefParamsReplace(href3, param3);
        assert "${rmis.report.url}?store_id=:store.id:&from_dt=:date_begin|uc:&to_dt=:to_dt:&__format=pdf".equals(result3);

        String href4 = "report?store_id=:store.id:&from_dt=:from_dt|uc:&to_dt=:to_dt|s:&__format=pdf";
        N2oReportPlace.Param param4 = new N2oReportPlace.Param();
        param4.setFormFieldId("to_dt");
        param4.setRef("date_end");
        String result4 = hrefParamsReplace(href4, param4);
        assert "report?store_id=:store.id:&from_dt=:from_dt|uc:&to_dt=:date_end|uc:&__format=pdf".equals(result4);

        String href5 = "report?store_id=:store.id:&from_dt=:from_dt|uc:&to_dt=:to_dt|s:&__format=:format|uc:";
        N2oReportPlace.Param param5 = new N2oReportPlace.Param();
        param5.setFormFieldId("format");
        param5.setRef("format_source");
        String res5lt4 = hrefParamsReplace(href5, param5);
        assert "report?store_id=:store.id:&from_dt=:from_dt|uc:&to_dt=:to_dt|s:&__format=:format_source|uc:".equals(res5lt4);
    }

    @Test
    public void testSetMenuItemWithoutForm() {
        List<N2oActionMenu.MenuItem> subMenuItemsList = new ArrayList<>();
        N2oReportPlace.Report reportOnPlace = new N2oReportPlace.Report();
        reportOnPlace.setLabel("label");
        reportOnPlace.setFormId("report_fileName_1");
        N2oReportPlace.Param param1 = new N2oReportPlace.Param("clinic.id", "moId");
        N2oReportPlace.Param param2 = new N2oReportPlace.Param("from_dt", "begin_dt");
        N2oReportPlace.Param param3 = new N2oReportPlace.Param("to_dt", "end_dt");
        reportOnPlace.setParams(new N2oReportPlace.Param[]{param1, param2, param3});
        String href = "${rmis.report.url}?__report=fileName.rptdesign&clinic_id=:clinic.id:&from_dt=:from_dt:&to_dt=:to_dt:&__format=pdf";
        setMenuItemWithoutForm(subMenuItemsList, reportOnPlace, href);
        N2oActionMenu.MenuItem menuItem = subMenuItemsList.get(0);
        assert menuItem.getId().equals("report_fileName_1");
        assert menuItem.getLabel().equals("label");
        assert menuItem.getContext();
        assert menuItem.getAnchor().getHref().equals("${rmis.report.url}?__report=fileName.rptdesign&clinic_id=:moId|uc:&from_dt=:begin_dt|uc:&to_dt=:end_dt|uc:&__format=pdf");
        assert menuItem.getAnchor().getTarget().equals(Target.newWindow);

        subMenuItemsList = new ArrayList<>();
        reportOnPlace = new N2oReportPlace.Report();
        reportOnPlace.setLabel("labe2");
        reportOnPlace.setFormId("report_fileName_2");
        param1 = new N2oReportPlace.Param("clinic.id", "moId");
        param2 = new N2oReportPlace.Param("from_dt", "begin_dt");
        param3 = new N2oReportPlace.Param("to_dt", "end_dt");
        reportOnPlace.setParams(new N2oReportPlace.Param[]{param1, param2, param3});
        href = "${rmis.report.url}?__report=fileName.rptdesign&clinic_id=:clinic.id|uc:&from_dt=:from_dt|uc:&to_dt=:to_dt|uc:&__format=pdf";
        setMenuItemWithoutForm(subMenuItemsList, reportOnPlace, href);
        menuItem = subMenuItemsList.get(0);
        assert menuItem.getId().equals("report_fileName_2");
        assert menuItem.getLabel().equals("labe2");
        assert menuItem.getContext();
        assert menuItem.getAnchor().getHref().equals("${rmis.report.url}?__report=fileName.rptdesign&clinic_id=:moId|uc:&from_dt=:begin_dt|uc:&to_dt=:end_dt|uc:&__format=pdf");
        assert menuItem.getAnchor().getTarget().equals(Target.newWindow);
    }
}
