package net.n2oapp.framework.report.sec;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import net.n2oapp.framework.config.reader.util.ReaderJdomUtil;
import ru.kirkazan.rmis.app.report.n2o.sec.ReportAccessPointReader;

/**
 * @author dfirstov
 * @since 10.04.2015
 */
public class ReportAccessPointReaderTest {

    @Test
    public void testRead() throws Exception {
        ReaderJdomUtil.clearTextProcessing();
        Element rootElement = ((new SAXBuilder(false)).build(new ClassPathResource("test_report_access.accessElement.xml").getInputStream()).getRootElement());
        Namespace ns = rootElement.getNamespace();
        Element reportAccessElement = (Element) ((Element) rootElement.getChildren().get(0)).getChildren().get(0);
        assert "report-access".equals(new ReportAccessPointReader().getElementName());
        assert "report_file_name".equals(new ReportAccessPointReader().read(reportAccessElement, ns).getCode());
    }

}
