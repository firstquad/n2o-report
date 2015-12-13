package ru.kirkazan.rmis.app.report.n2o.impl.parser;

/**
 * Created by dfirstov on 18.09.2014.
 */
public class ReportParam {
    private String id;
    private String name;
    private Boolean required;
    private String type;
    private Boolean visible;

    public ReportParam() {
    }

    public ReportParam(String id, String name, Boolean required, String type) {
        this.id = id;
        this.name = name;
        this.required = required;
        this.type = type;
    }

    public ReportParam(String id, Boolean required, Boolean visible) {
        this.id = id;
        this.required = required;
        this.visible = visible;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }
}
