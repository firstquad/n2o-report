package ru.kirkazan.rmis.app.report.n2o.sec;

import net.n2oapp.framework.config.reader.util.ReaderJdomUtil;
import org.jdom.Element;
import org.jdom.Namespace;
import ru.kirkazan.rmis.app.security.n2o.xml.N2oAccessReader;

/**
 * @author dfirstov
 * @since 10.04.2015
 */
public class ReportAccessPointReader implements N2oAccessReader<ReportAccessPoint> {

    @Override
    public ReportAccessPoint read(Element element, Namespace namespace) {
        ReportAccessPoint res = new ReportAccessPoint();
        res.setCode(ReaderJdomUtil.getAttributeString(element, "code"));
        return res;
    }

    @Override
    public String getElementName() {
        return "report-access";
    }

    @Override
    public Class<ReportAccessPoint> getElementClass() {
        return ReportAccessPoint.class;
    }
}
