package net.n2oapp.framework.report.transformer.mock.widget;

import net.n2oapp.framework.api.metadata.global.view.widget.N2oForm;
import net.n2oapp.framework.config.metadata.transformer.TransformersUtil;
import ru.kirkazan.rmis.app.report.n2o.api.model.Report;
import ru.kirkazan.rmis.app.report.n2o.place.model.N2oReportPlace;
import ru.kirkazan.rmis.app.report.n2o.transformer.ReportWidgetPlaceTransformer;

/**
 * @author dfirstov
 * @since 08.07.2015
 */
public class ReportWidgetPlaceTransformerMock extends ReportWidgetPlaceTransformer {

    @Override
    protected N2oReportPlace retrievePlace(String sourcePageId) {
        return TransformersUtil.getCurrentCompiler().getGlobal(sourcePageId, N2oReportPlace.class);
    }

    @Override
    protected N2oForm retrieveForm(String formId) {
        if (formId == null)
            return null;
        return TransformersUtil.getCurrentCompiler().getGlobal(formId, N2oForm.class);
    }

    @Override
    protected void fillLabel(Report report, N2oReportPlace.Report reportOnPlace) {
    }
}
