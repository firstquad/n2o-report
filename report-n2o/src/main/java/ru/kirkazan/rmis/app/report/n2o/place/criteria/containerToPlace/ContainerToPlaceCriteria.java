package ru.kirkazan.rmis.app.report.n2o.place.criteria.containerToPlace;

import net.n2oapp.criteria.api.Criteria;

/**
 * Created by dfirstov on 08.11.2014.
 */
public class ContainerToPlaceCriteria extends Criteria {
    private String id;
    private String pageId;

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
}
