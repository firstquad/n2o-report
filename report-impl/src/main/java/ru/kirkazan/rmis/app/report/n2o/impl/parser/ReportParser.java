package ru.kirkazan.rmis.app.report.n2o.impl.parser;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dfirstov on 18.09.2014.
 */
public class ReportParser {

    private static ReportParser ourInstance = new ReportParser();

    public static ReportParser getInstance() {
        return ourInstance;
    }

    private ReportParser() {
    }

    public List<ReportParam> parseParams(File file) {
        List<ReportParam> reportParamses = new ArrayList<>();
        try {
            Element rootElement = (new SAXBuilder(false)).build(file).getRootElement();
            Namespace ns = rootElement.getNamespace();
            Element parameters = rootElement.getChild("parameters", ns);
            if (parameters == null) return null;
            List<Element> parametersList = parameters.getChildren("scalar-parameter", ns);
            for (Element elementParametersList : parametersList) {
                Element parametersElement = elementParametersList;
                String id = parametersElement.getAttributeValue("id");
                String name = parametersElement.getAttributeValue("name");
                Boolean required = null;
                String type = null;
                List<Element> propertyList = parametersElement.getChildren("property", ns);
                for (Element elementPropertyList : propertyList) {
                    Element propertyElement = elementPropertyList;
                    if (propertyElement.getAttributeValue("name").equals("isRequired")) {
                        if (propertyElement.getValue().equals("true")) required = true;
                        if (propertyElement.getValue().equals("false")) required = false;
                    }
                    if (propertyElement.getAttributeValue("name").equals("dataType")) {
                        type = propertyElement.getValue();
                    }
                }
                reportParamses.add(new ReportParam(id, name, required, type));
            }
        } catch (JDOMException | IOException e) {
            throw new RuntimeException(e);
        }
        return reportParamses;
    }
}
