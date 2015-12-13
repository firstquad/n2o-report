package ru.kirkazan.rmis.app.report.n2o.place.criteria.place;

/**
 * Created by dfirstov on 12.09.2014.
 */
public class ReportPlace {
    private String id;
    private String pageId;
    private String name;
    private String parentId;
    private Boolean hasChildren;
    private Boolean canResolved;
    private String icon;

    public ReportPlace(String id, String pageId, String name, String parentId, Boolean hasChildren, Boolean canResolved, String icon) {
        this.id = id;
        this.pageId = pageId;
        this.name = name;
        this.parentId = parentId;
        this.hasChildren = hasChildren;
        this.canResolved = canResolved;
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Boolean getHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(Boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public Boolean getCanResolved() {
        return canResolved;
    }

    public void setCanResolved(Boolean canResolved) {
        this.canResolved = canResolved;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
