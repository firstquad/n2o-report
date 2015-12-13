package net.n2oapp.framework.report.place;

import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.junit.Test;
import net.n2oapp.framework.config.reader.util.ReaderJdomUtil;
import org.springframework.core.io.ClassPathResource;
import ru.kirkazan.rmis.app.report.n2o.place.model.N2oReportPlace;
import ru.kirkazan.rmis.app.report.n2o.place.config.N2oReportPlacePersister;
import ru.kirkazan.rmis.app.report.n2o.place.config.N2oReportPlaceReaderV1;

import java.io.IOException;
import java.io.InputStream;


/**
 * Created by dfirstov on 29.10.2014.
 */
public class ReportPlaceIOTestTest {

    @Test
    public void testPersist () throws Exception {
        ReaderJdomUtil.clearTextProcessing();
        N2oReportPlacePersister n2oPlaceXmlPersister = new N2oReportPlacePersister();
        String inXml;
        try (InputStream stream = new ClassPathResource("net/n2oapp/framework/report/persister/test_reader.place.xml").getInputStream()) {
            inXml = IOUtils.toString(stream, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Element rootElement = ((new SAXBuilder(false)).build(new ClassPathResource("net/n2oapp/framework/report/persister/test_reader.place.xml").getInputStream()).getRootElement());
        Namespace ns = rootElement.getNamespace();
        N2oReportPlaceReaderV1 n2oPlaceXmlReaderV1 = new N2oReportPlaceReaderV1();
        N2oReportPlace n2oPlace = n2oPlaceXmlReaderV1.read(rootElement, ns);
        Element outElement = n2oPlaceXmlPersister.persist(n2oPlace);
        String outXml = new Printer().getString(outElement);
        Diff diff = XMLUnit.compareXML(inXml, outXml);
        assert diff.similar();
    }
}