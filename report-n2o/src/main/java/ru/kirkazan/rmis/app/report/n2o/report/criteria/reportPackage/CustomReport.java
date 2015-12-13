package ru.kirkazan.rmis.app.report.n2o.report.criteria.reportPackage;

import ru.kirkazan.rmis.app.report.n2o.api.model.Report;

/**
 * Created by dfirstov on 28.09.2014.
 */
public class CustomReport extends Report {
    private String callModeId;
    private String name;
    private Boolean defaultMode;
    private Boolean customForm;
    private Boolean hideForm;
    private String placeName;
    private Boolean isShowLoaded;

    public Boolean getCustomForm() {
        return customForm;
    }

    public void setCustomForm(Boolean customForm) {
        this.customForm = customForm;
    }

    public Boolean getHideForm() {
        return hideForm;
    }

    public void setHideForm(Boolean hideForm) {
        this.hideForm = hideForm;
    }

    public Boolean getDefaultMode() {
        return defaultMode;
    }

    public void setDefaultMode(Boolean defaultMode) {
        this.defaultMode = defaultMode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCallModeId() {
        return callModeId;
    }

    public void setCallModeId(String callModeId) {
        this.callModeId = callModeId;
    }

    @Override
    public String getPlaceName() {
        return placeName;
    }

    @Override
    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public Boolean getIsShowLoaded() {
        return isShowLoaded;
    }

    public void setIsShowLoaded(Boolean isShowLoaded) {
        this.isShowLoaded = isShowLoaded;
    }
}
