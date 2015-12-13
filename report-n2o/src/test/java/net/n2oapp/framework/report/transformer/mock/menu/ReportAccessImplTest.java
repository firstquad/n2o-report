package net.n2oapp.framework.report.transformer.mock.menu;

import ru.kirkazan.rmis.app.report.n2o.sec.ReportAccessImpl;

/**
 * @author dfirstov
 * @since 06.07.2015
 */
public class ReportAccessImplTest extends ReportAccessImpl {

    @Override
    public boolean hasAccess(String code) {
        return true;
    }
}
