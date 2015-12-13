package ru.kirkazan.rmis.app.report.n2o.place.model;

import net.n2oapp.framework.api.metadata.global.N2oMetadata;
import net.n2oapp.framework.api.metadata.global.aware.IdAware;
import net.n2oapp.framework.api.metadata.global.aware.NameAware;
import net.n2oapp.framework.api.metadata.global.view.action.N2oActionMenu;

import java.io.Serializable;

/**
 * Created by dfirstov on 27.10.2014.
 */
public class N2oReportPlace extends N2oMetadata implements NameAware {
    private String realId;
    private String name;
    private ReportsElement reportsElement;
    private ContainerElement[] containerElements;

    public String getRealId() {
        return realId;
    }

    public void setRealId(String realId) {
        this.realId = realId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ReportsElement getReportsElement() {
        return reportsElement;
    }

    public void setReportsElement(ReportsElement reportsElement) {
        this.reportsElement = reportsElement;
    }

    public ContainerElement[] getContainerElements() {
        return containerElements;
    }

    public void setContainerElements(ContainerElement[] containerElements) {
        this.containerElements = containerElements;
    }

    @Override
    public String getPostfix() {
        return "place";
    }

    @Override
    public Class<? extends N2oMetadata> getBaseClass() {
        return N2oReportPlace.class;
    }

    @Override
    public String getName() {
        return name;
    }

    public static class ReportsElement implements Serializable {
        private Report[] reports;

        public Report[] getReports() {
            return reports;
        }

        public void setReports(Report[] reports) {
            this.reports = reports;
        }
    }

    public static class Report implements Serializable {
        private String label;
        private String formId;
        private Param[] params;
        private String code;
        private String format;
        private Boolean showForm;
        private N2oActionMenu.MenuItem.Condition[] enablingConditions;
        private N2oActionMenu.MenuItem.Condition[] visibilityConditions;
        private String key;

        public String getFormId() {
            return formId;
        }

        public void setFormId(String formId) {
            this.formId = formId;
        }

        public Param[] getParams() {
            return params;
        }

        public void setParams(Param[] params) {
            this.params = params;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public Boolean getShowForm() {
            return showForm;
        }

        public void setShowForm(Boolean showForm) {
            this.showForm = showForm;
        }

        public N2oActionMenu.MenuItem.Condition[] getEnablingConditions() {
            return enablingConditions;
        }

        public void setEnablingConditions(N2oActionMenu.MenuItem.Condition[] enablingConditions) {
            this.enablingConditions = enablingConditions;
        }

        public N2oActionMenu.MenuItem.Condition[] getVisibilityConditions() {
            return visibilityConditions;
        }

        public void setVisibilityConditions(N2oActionMenu.MenuItem.Condition[] visibilityConditions) {
            this.visibilityConditions = visibilityConditions;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    public static class Param implements Serializable {
        private String formFieldId;
        private String name;
        private String ref;

        public Param() {
        }

        public Param(String formFieldId, String ref) {
            this.formFieldId = formFieldId;
            this.ref = ref;
        }

        public String getFormFieldId() {
            return formFieldId;
        }

        public void setFormFieldId(String formFieldId) {
            this.formFieldId = formFieldId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRef() {
            return ref;
        }

        public void setRef(String ref) {
            this.ref = ref;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Param param = (Param) o;
            if (!formFieldId.equals(param.formFieldId))
                return false;
            if (name != null ? !name.equals(param.name) : param.name != null)
                return false;
            if (!ref.equals(param.ref))
                return false;
            return true;
        }

        @Override
        public int hashCode() {
            int result = formFieldId.hashCode();
            result = 31 * result + (name != null ? name.hashCode() : 0);
            result = 31 * result + ref.hashCode();
            return result;
        }
    }

    public static class ContainerElement implements Serializable, IdAware {
        private String id;
        private String reportMenuItemId;
        private ReportsElement reportsElement;

        @Override
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getReportMenuItemId() {
            return reportMenuItemId;
        }

        public void setReportMenuItemId(String reportMenuItemId) {
            this.reportMenuItemId = reportMenuItemId;
        }

        public ReportsElement getReportsElement() {
            return reportsElement;
        }

        public void setReportsElement(ReportsElement reportsElement) {
            this.reportsElement = reportsElement;
        }
    }
}
