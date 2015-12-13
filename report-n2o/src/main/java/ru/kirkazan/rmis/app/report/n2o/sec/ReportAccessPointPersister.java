package ru.kirkazan.rmis.app.report.n2o.sec;

import net.n2oapp.framework.api.metadata.persister.N2oMetadataPersister;
import org.jdom.Element;
import org.jdom.Namespace;

import static net.n2oapp.framework.config.persister.util.PersisterJdomUtil.setAttribute;

/**
 * @author dfirstov
 * @since 13.04.2015
 */
public class ReportAccessPointPersister extends N2oMetadataPersister<ReportAccessPoint> {
    protected String namespaceUri = "http://atria.cz/sec/config/schema/n2o-permission-1.0";
    protected String namespacePrefix = "";

    @Override
    public Element persist(ReportAccessPoint entity) {
        Namespace namespace = Namespace.getNamespace(namespacePrefix, namespaceUri);
        Element rootElement = new Element("report-access", namespace);
        setAttribute(rootElement, "code", entity.getCode());
        return rootElement;
    }

    @Override
    public void setNamespaceUri(String namespaceUri) {
        this.namespaceUri = namespaceUri;
    }

    @Override
    public void setNamespacePrefix(String namespacePrefix) {
        this.namespacePrefix = namespacePrefix;
    }

    @Override
    public Class<? extends ReportAccessPoint> getType() {
        return ReportAccessPoint.class;
    }

}
