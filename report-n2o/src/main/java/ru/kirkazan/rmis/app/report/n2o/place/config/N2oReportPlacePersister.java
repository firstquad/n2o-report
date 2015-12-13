package ru.kirkazan.rmis.app.report.n2o.place.config;

import net.n2oapp.framework.api.metadata.global.view.action.N2oActionMenu;
import net.n2oapp.framework.api.metadata.persister.N2oMetadataPersister;
import net.n2oapp.framework.config.persister.util.PersisterJdomUtil;
import org.jdom.Element;
import org.jdom.Namespace;
import ru.kirkazan.rmis.app.report.n2o.place.model.N2oReportPlace;

import static net.n2oapp.framework.config.persister.util.PersisterJdomUtil.setAttribute;

/**
 * Created by dfirstov on 27.10.2014.
 */
public class N2oReportPlacePersister extends N2oMetadataPersister<N2oReportPlace> {
    protected String namespaceUri = "http://n2oapp.net/framework/config/schema/report-n2o-place-1.0";
    protected String namespacePrefix = "";

    @Override
    public void setNamespaceUri(String namespaceUri) {
        this.namespaceUri = namespaceUri;
    }

    @Override
    public void setNamespacePrefix(String namespacePrefix) {
        this.namespacePrefix = namespacePrefix;
    }

    @Override
    public Element persist(N2oReportPlace entity) {
        Namespace namespace = Namespace.getNamespace(namespacePrefix, namespaceUri);
        Element rootElement = new Element("place", namespace);
        if (entity.getReportsElement() != null) {
            Element reportsElement = persistReportsElement(entity.getReportsElement(), namespace);
            rootElement.addContent(reportsElement);
        }
        if (entity.getContainerElements() != null) {
            Element containersElement = persistContainersElement(entity.getContainerElements(), namespace);
            rootElement.addContent(containersElement);
        }
        return rootElement;
    }

    private Element persistReportsElement(N2oReportPlace.ReportsElement reports, Namespace namespace) {
        Element reportsElement = new Element("reports", namespace);
        if (reports.getReports() != null) {
            N2oReportPlace.Report[] reportContextMenuItems = reports.getReports();
            for (N2oReportPlace.Report report : reportContextMenuItems) {
                if (report.getFormId() != null) {
                    Element reportFormElement = new Element("report-form", namespace);
                    setAttribute(reportFormElement, "form-id", report.getFormId());
                    setAttribute(reportFormElement, "label", report.getLabel());
                    setAttribute(reportFormElement, "show-form", report.getShowForm());
                    setAttribute(reportFormElement, "format", report.getFormat());
                    setAttribute(reportFormElement, "key", report.getKey());
                    if (report.getParams() != null) {
                        Element reportParamsElement = persistParams(report.getParams(), namespace);
                        reportFormElement.addContent(reportParamsElement);
                    }
                    setConditions(report, reportFormElement, namespace);
                    reportsElement.addContent(reportFormElement);
                }
                if (report.getCode() != null) {
                    Element reportUrlElement = new Element("report-url", namespace);
                    setAttribute(reportUrlElement, "code", report.getCode());
                    setAttribute(reportUrlElement, "label", report.getLabel());
                    setAttribute(reportUrlElement, "format", report.getFormat());
                    setAttribute(reportUrlElement, "key", report.getKey());
                    if (report.getParams() != null) {
                        Element reportParamsElement = persistParams(report.getParams(), namespace);
                        reportUrlElement.addContent(reportParamsElement);
                    }
                    setConditions(report, reportUrlElement, namespace);
                    reportsElement.addContent(reportUrlElement);
                }
            }
        }
        return reportsElement;
    }

    private Element persistParams(N2oReportPlace.Param[] params, Namespace namespace) {
        Element reportParamsElement = new Element("params", namespace);
        for (N2oReportPlace.Param param : params) {
            Element paramElement = new Element("param", namespace);
            setAttribute(paramElement, "form-field-id", param.getFormFieldId());
            setAttribute(paramElement, "name", param.getName());
            setAttribute(paramElement, "ref", param.getRef());
            reportParamsElement.addContent(paramElement);
        }
        return reportParamsElement;
    }

    private Element persistContainersElement(N2oReportPlace.ContainerElement[] containerElements, Namespace namespace) {
        Element containersElement = new Element("containers", namespace);
        for (N2oReportPlace.ContainerElement container : containerElements) {
            if (container != null) {
                Element containerElement = new Element("container", namespace);
                setAttribute(containerElement, "id", container.getId());
                setAttribute(containerElement, "report-menu-item-id", container.getReportMenuItemId());
                if (container.getReportsElement() != null) {
                    Element reportsElement = persistReportsElement(container.getReportsElement(), namespace);
                    containerElement.addContent(reportsElement);
                }
                containersElement.addContent(containerElement);
            }
        }
        return containersElement;
    }

    public void setConditions(N2oReportPlace.Report report, Element root, Namespace namespace) {
        Element child = new Element("conditions", namespace);
        if (report.getEnablingConditions() != null && report.getEnablingConditions().length > 0) {
            for (N2oActionMenu.MenuItem.Condition condition : report.getEnablingConditions()) {
                Element enableCondition = PersisterJdomUtil.setEmptyElement(child, "enabling-condition");
                setCondition(condition, enableCondition);
                if (condition.getTooltip() != null)
                    PersisterJdomUtil.setElementString(enableCondition, "tooltip", condition.getTooltip());
            }
        }
        if (report.getVisibilityConditions() != null && report.getVisibilityConditions().length > 0) {
            for (N2oActionMenu.MenuItem.Condition condition : report.getVisibilityConditions()) {
                Element visibilityCondition = PersisterJdomUtil.setEmptyElement(child, "visibility-condition");
                setCondition(condition, visibilityCondition);
            }
        }
        if (child.getChildren().size() > 0)
            root.addContent(child);
    }

    private void setCondition(N2oActionMenu.MenuItem.Condition condition, Element root) {
        if (condition.getExpression() != null) {
            Element expression = PersisterJdomUtil.setElementString(root, "expression", condition.getExpression());
            PersisterJdomUtil.setAttribute(expression, "on", condition.getOn());
        }
    }

    @Override
    public Class<? extends N2oReportPlace> getType() {
        return N2oReportPlace.class;
    }
}
