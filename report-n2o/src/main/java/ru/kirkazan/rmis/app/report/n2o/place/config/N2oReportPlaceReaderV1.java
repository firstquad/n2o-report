package ru.kirkazan.rmis.app.report.n2o.place.config;

import net.n2oapp.framework.api.metadata.reader.ElementReader;
import net.n2oapp.framework.api.metadata.reader.N2oMetadataReader;
import net.n2oapp.framework.config.reader.MetadataReaderException;
import net.n2oapp.framework.config.reader.tools.MenuItemConditionReader;
import net.n2oapp.framework.config.reader.util.ReaderJdomUtil;
import org.jdom.Element;
import org.jdom.Namespace;
import ru.kirkazan.rmis.app.report.n2o.place.model.N2oReportPlace;

import java.util.ArrayList;
import java.util.List;

import static net.n2oapp.framework.config.reader.util.ReaderJdomUtil.*;

/**
 * Created by dfirstov on 27.10.2014.
 */
public class N2oReportPlaceReaderV1 extends N2oMetadataReader<N2oReportPlace> {
    @Override
    public N2oReportPlace read(Element root, Namespace namespace) {
        String elementName = root.getName();
        Namespace ns = root.getNamespace();
        if (!elementName.equals("place")) throw new MetadataReaderException("element <place> not found");
        N2oReportPlace n2oPlace = new N2oReportPlace();
        fillReports(root, ns, n2oPlace);
        fillContainers(root, ns, n2oPlace);
        return n2oPlace;
    }

    private void fillReports(Element root, Namespace ns, N2oReportPlace n2oPlace) {
        if (root.getChild("reports", ns) != null) {
            N2oReportPlace.ReportsElement reportsElement = new N2oReportPlace.ReportsElement();
            List<Element> list = root.getChild("reports", ns).getChildren();
            List<N2oReportPlace.Report> reports = new ArrayList<>();
            ReportsReader reportsReader = new ReportsReader();
            for (Element element : list) {
                reports.add(reportsReader.read(element, ns));
            }
            reportsElement.setReports(reports.toArray(new N2oReportPlace.Report[reports.size()]));
            n2oPlace.setReportsElement(reportsElement);
        }
    }

    private void fillContainers(Element root, Namespace ns, N2oReportPlace n2oPlace) {
        if (root.getChild("containers", ns) != null) {
            List<Element> containers = root.getChild("containers", ns).getChildren();
            List<N2oReportPlace.ContainerElement> containersElement = new ArrayList<>();
            for (Element container : containers) {
                if (container.getChild("reports", ns) != null) {
                    N2oReportPlace.ReportsElement reportsElement = new N2oReportPlace.ReportsElement();
                    List<Element> list = container.getChild("reports", ns).getChildren();
                    List<N2oReportPlace.Report> reportContextMenuItems = new ArrayList<>();
                    ReportsReader reportsReader = new ReportsReader();
                    for (Element element : list) {
                        reportContextMenuItems.add(reportsReader.read(element, ns));
                    }
                    reportsElement.setReports(reportContextMenuItems.toArray(new N2oReportPlace.Report[reportContextMenuItems.size()]));
                    N2oReportPlace.ContainerElement containerElement = new N2oReportPlace.ContainerElement();
                    containerElement.setId(getAttributeString(container, "id"));
                    containerElement.setReportMenuItemId(getAttributeString(container, "report-menu-item-id"));
                    containerElement.setReportsElement(reportsElement);
                    containersElement.add(containerElement);
                }
            }
            n2oPlace.setContainerElements(containersElement.toArray(new N2oReportPlace.ContainerElement[containersElement.size()]));
        }
    }

    private static class ReportsReader implements ElementReader<N2oReportPlace.Report> {
        @Override
        public N2oReportPlace.Report read(Element element, Namespace namespace) {
            N2oReportPlace.Report report = new N2oReportPlace.Report();
            report.setFormId(getAttributeString(element, "form-id"));
            report.setCode(getAttributeString(element, "code"));
            report.setLabel(getAttributeString(element, "label"));
            report.setShowForm(getAttributeBoolean(element, "show-form"));
            report.setFormat(getAttributeString(element, "format"));
            report.setKey(getAttributeString(element, "key"));
            report.setParams(getChilds(element, element.getNamespace(), "params", "param", new ParamsReader()));
            report.setEnablingConditions(ReaderJdomUtil.getChilds(element, namespace, "conditions", "enabling-condition",
                    MenuItemConditionReader.getInstance()));
            report.setVisibilityConditions(ReaderJdomUtil.getChilds(element, namespace, "conditions", "visibility-condition",
                    MenuItemConditionReader.getInstance()));
            return report;
        }

        @Override
        public Class<N2oReportPlace.Report> getElementClass() {
            return N2oReportPlace.Report.class;
        }
    }

    public static class ParamsReader implements ElementReader<N2oReportPlace.Param> {
        @Override
        public N2oReportPlace.Param read(Element element, Namespace namespace) {
            N2oReportPlace.Param param = new N2oReportPlace.Param();
            param.setFormFieldId(getAttributeString(element, "form-field-id"));
            param.setName(getAttributeString(element, "name"));
            param.setRef(getAttributeString(element, "ref"));
            return param;
        }

        @Override
        public Class<N2oReportPlace.Param> getElementClass() {
            return N2oReportPlace.Param.class;
        }
    }

    @Override
    public Class<N2oReportPlace> getElementClass() {
        return N2oReportPlace.class;
    }

    @Override
    public String getType() {
        return "http://n2oapp.net/framework/config/schema/report-n2o-place-1.0";
    }
}
