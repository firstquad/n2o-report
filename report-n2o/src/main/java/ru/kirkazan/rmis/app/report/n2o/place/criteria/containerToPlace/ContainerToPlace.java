package ru.kirkazan.rmis.app.report.n2o.place.criteria.containerToPlace;

/**
 * Created by dfirstov on 08.11.2014.
 */
public class ContainerToPlace {
    private String id;
    private String pageId;
    private String containerId;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ContainerToPlace(String pageId, String containerId, String name) {
        this.pageId = pageId;
        this.containerId = containerId;
        this.name = name;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
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
}
