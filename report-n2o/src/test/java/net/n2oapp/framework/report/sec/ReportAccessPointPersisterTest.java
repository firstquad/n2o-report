package net.n2oapp.framework.report.sec;

import org.jdom.Element;
import org.junit.Test;
import ru.kirkazan.rmis.app.report.n2o.sec.ReportAccessPoint;
import ru.kirkazan.rmis.app.report.n2o.sec.ReportAccessPointPersister;

import static org.junit.Assert.*;

public class ReportAccessPointPersisterTest {

    @Test
    public void testPersist() throws Exception {
        ReportAccessPoint accessPoint = new ReportAccessPoint();
        accessPoint.setCode("test_code");
        ReportAccessPointPersister persister = new ReportAccessPointPersister();
        Element element = persister.persist(accessPoint);
        assert "report-access".equals(element.getName());
        assert "test_code".equals(element.getAttributeValue("code"));
    }
}