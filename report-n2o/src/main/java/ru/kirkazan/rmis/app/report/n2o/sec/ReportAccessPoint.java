package ru.kirkazan.rmis.app.report.n2o.sec;

import ru.kirkazan.rmis.app.security.schema.model.AccessPoint;

import java.util.List;

/**
 * @author dfirstov
 * @since 10.04.2015
 */
public class ReportAccessPoint extends AccessPoint {

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean dependsOn(AccessPoint accessPoint) {
        if (accessPoint instanceof ReportAccessPoint) {
            ReportAccessPoint reportAccessPoint = (ReportAccessPoint) accessPoint;
            if (reportAccessPoint.code.equals(code)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<AccessPoint> disassemble() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportAccessPoint that = (ReportAccessPoint) o;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return (code != null ? code.hashCode() : 0);
    }
}
