package net.n2oapp.framework.report.place;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * Created by dfirstov on 01.10.2014.
 */
public  class Printer {
    public  String getString(Element element) {
        Document doc = new Document();
        doc.addContent(element);
        XMLOutputter xmlOutput = new XMLOutputter();
        Format format = Format.getRawFormat();
        format.setLineSeparator("\r\n");
        format.setIndent("    ");
        xmlOutput.setFormat(format);
        StringWriter sw = new StringWriter();
        try {
            xmlOutput.output(doc, sw);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sw.toString();
    }
}
