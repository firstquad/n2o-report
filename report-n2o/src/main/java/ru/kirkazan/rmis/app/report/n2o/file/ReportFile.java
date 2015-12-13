package ru.kirkazan.rmis.app.report.n2o.file;

/**
 * Created by dfirstov on 22.09.2014.
 */
public class ReportFile {
    private String id;
    private String name;
    private String path;
    private String filterDouble;
    private Boolean hideForm;

    public ReportFile(String id, String name, String path, Boolean hideForm) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.hideForm = hideForm;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public String getFilterDouble() {
        return filterDouble;
    }

    public void setFilterDouble(String filterDouble) {
        this.filterDouble = filterDouble;
    }

    public Boolean getHideForm() {
        return hideForm;
    }

    public void setHideForm(Boolean hideForm) {
        this.hideForm = hideForm;
    }
}
