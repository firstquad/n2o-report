package ru.kirkazan.rmis.app.report.n2o.form.criteria.classifier;

import net.n2oapp.criteria.api.Criteria;

/**
 * @author dfirstov
 * @since 05.10.2015
 */
public class ReportClassifierCriteria extends Criteria {
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
