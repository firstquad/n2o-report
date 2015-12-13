package ru.kirkazan.rmis.app.report.n2o.place.criteria.place;

import net.n2oapp.criteria.api.Criteria;

/**
 * Created by dfirstov on 12.09.2014.
 */
public class ReportPlaceCriteria extends Criteria {
    private String id;
    private String name;

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
}
