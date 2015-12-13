package ru.kirkazan.rmis.app.report.n2o.sec;

import ru.kirkazan.rmis.app.security.engine.AccessEngine;

/**
 * @author dfirstov
 * @since 14.04.2015
 */
public class ReportAccessImpl {
    private AccessEngine accessEngine;

    public boolean hasAccess(String code) {
        ReportAccessPoint accessPoint = new ReportAccessPoint();
        accessPoint.setCode(code);
        return accessEngine.resolveAccess(accessPoint).getValue();
    }

    public void setAccessEngine(AccessEngine accessEngine) {
        this.accessEngine = accessEngine;
    }
}
