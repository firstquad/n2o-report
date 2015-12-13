package ru.kirkazan.rmis.app.report.n2o.api.model;

/**
 * Created by dfirstov on 26.01.2015.
 */
public class ReportToGroup extends Report {
    private Integer groupId;
    private String groupName;

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
