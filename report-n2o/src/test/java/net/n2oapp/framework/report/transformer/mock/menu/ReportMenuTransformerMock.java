package net.n2oapp.framework.report.transformer.mock.menu;

import net.n2oapp.framework.api.metadata.global.view.widget.N2oForm;
import net.n2oapp.framework.standard.header.context.ModulePageContext;
import ru.kirkazan.rmis.app.report.n2o.api.model.Report;
import ru.kirkazan.rmis.app.report.n2o.place.model.N2oReportPlace;
import ru.kirkazan.rmis.app.report.n2o.transformer.ReportMenuTransformer;

/**
 * @author dfirstov
 * @since 07.07.2015
 */
public class ReportMenuTransformerMock extends ReportMenuTransformer {
    @Override
    protected boolean isMatch(ModulePageContext context) {
        return true;
    }

    @Override
    protected void fillLabel(N2oReportPlace.Report reportOnPlace, Report report) {
        reportOnPlace.setLabel((report.getLabel() == null || "".equals(report.getLabel())) ? report.getFileName() : report.getLabel());
    }

    @Override
    protected N2oForm retrieveForm(N2oReportPlace.Report reportOnPlace) {
        N2oForm form = new N2oForm();
        form.setId(reportOnPlace.getFormId());
        form.setObjectId("reportMock");
        return form;
    }
}
