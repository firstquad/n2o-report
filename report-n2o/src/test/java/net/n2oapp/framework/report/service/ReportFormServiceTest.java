package net.n2oapp.framework.report.service;

import net.n2oapp.framework.api.metadata.control.N2oField;
import net.n2oapp.framework.api.metadata.control.list.N2oClassifier;
import net.n2oapp.framework.api.metadata.control.plain.N2oDatePicker;
import net.n2oapp.framework.api.metadata.control.plain.N2oInputText;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oForm;
import org.junit.Test;
import ru.kirkazan.rmis.app.report.n2o.file.ReportFile;
import ru.kirkazan.rmis.app.report.n2o.impl.parser.ReportParam;

import java.util.ArrayList;
import java.util.List;

import static ru.kirkazan.rmis.app.report.n2o.form.ReportFormService.createN2oForm;

/**
 * Created by dfirstov on 24.09.2014.
 */
public class ReportFormServiceTest {

    @Test
    public void testCreateN2oForm() throws Exception {
        List<ReportParam> reportParams = new ArrayList<>();
        reportParams.add(new ReportParam("from_dt", "from_dt", true, "type1"));
        reportParams.add(new ReportParam("to_dt", "to_dt", false, "type2"));
        reportParams.add(new ReportParam("clinic_id", "clinic_id", true, "type3"));
        reportParams.add(new ReportParam("department_id", "department_id", true, "type4"));
        reportParams.add(new ReportParam("doctor_id", "doctor_id", true, "type5"));
        reportParams.add(new ReportParam("simple", "simple", true, "type0=1"));
        ReportFile reportFile = new ReportFile("testId", "testName", "testURI", true);
        N2oForm n2oForm = createN2oForm(reportParams, "formId", "fileName.foo", reportFile.getName());
        assert "formId".equals(n2oForm.getId());
        assert "${rmis.report.url}?__report=fileName.foo&from_dt=:from_dt|uc:&to_dt=:to_dt|uc:&clinic_id=:clinic.id|uc:&department_id=:department.id|uc:&doctor_id=:doctor.id|uc:&simple=:simple|uc:&__format=pdf"
                        .equals(n2oForm.getEdit().getAnchor().getHref());

        N2oField[] fields = n2oForm.getFieldSetContainers()[0].getFieldSet().getBlocks()[0].getFields();
        assert fields[0].getId().equals("from_dt");
        assert fields[0].getLabel().equals("От");
        assert fields[0].getRequired();
        assert fields[0].getClass().equals(N2oDatePicker.class);

        assert fields[1].getId().equals("to_dt");
        assert fields[1].getLabel().equals("До");
        assert !fields[1].getRequired();
        assert fields[1].getClass().equals(N2oDatePicker.class);

        assert fields[2].getId().equals("clinic");
        assert fields[2].getLabel().equals("ЛПУ");
        assert fields[2].getRequired();
        assert fields[2].getClass().equals(N2oClassifier.class);

        assert fields[3].getId().equals("department");
        assert fields[3].getLabel().equals("Отделение");
        assert fields[3].getRequired();
        assert fields[3].getClass().equals(N2oClassifier.class);

        assert fields[4].getId().equals("doctor");
        assert fields[4].getLabel().equals("Врач");
        assert fields[4].getRequired();
        assert fields[4].getClass().equals(N2oClassifier.class);

        assert fields[5].getId().equals("simple");
        assert fields[5].getLabel().equals("simple");
        assert fields[5].getRequired();
        assert fields[5].getClass().equals(N2oInputText.class);

    }
}
