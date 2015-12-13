package ru.kirkazan.rmis.app.report.n2o.transformer;

import net.n2oapp.framework.api.metadata.control.N2oField;
import net.n2oapp.framework.api.metadata.control.N2oListField;
import net.n2oapp.framework.api.metadata.global.dao.N2oPreFilter;
import net.n2oapp.framework.api.metadata.global.view.action.N2oActionMenu;
import net.n2oapp.framework.api.metadata.global.view.action.control.Anchor;
import net.n2oapp.framework.api.metadata.global.view.action.control.ShowModalFormWithAction;
import net.n2oapp.framework.api.metadata.global.view.action.control.Target;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oForm;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oWidget;
import net.n2oapp.framework.config.register.ConfigRegister;
import net.n2oapp.framework.config.service.GlobalMetadataStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kirkazan.rmis.app.report.n2o.api.model.Report;
import ru.kirkazan.rmis.app.report.n2o.place.model.N2oReportPlace;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dfirstov
 * @since 20.05.2015
 */
public class ReportTransformerUtil {
    private static final Logger logger = LoggerFactory.getLogger(ReportTransformerUtil.class);
    private static final String regexUrlEncode = "\\|.*";
    private static final String regexUrlEncodeWithColon = ".*?(?=:):";

    public static void fillLabel(Report report, N2oReportPlace.Report reportOnPlace) {
        if (isExistsForm(report)) {
            String label = report.getLabel();
            if (report.getLabel() == null || "".equals(report.getLabel())) {
                try {
                    label = GlobalMetadataStorage.getInstance().get(report.getFormId(), N2oForm.class).getName();
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                    reportOnPlace.setLabel(report.getFileName());
                    return;
                }
            }
            reportOnPlace.setLabel(label);
        } else {
            reportOnPlace.setLabel((report.getLabel() == null || "".equals(report.getLabel())) ? report.getFileName() : report.getLabel());
        }
    }

    public static boolean isExistsForm(Report report) {
        return report.getFormId() != null && ConfigRegister.getInstance().contains(report.getFormId(), N2oWidget.class);
    }

    public static void setMenuItemWithForm(List<N2oActionMenu.MenuItem> subMenuItemsList, N2oReportPlace.Report reportOnPlace) {
        String formId = reportOnPlace.getFormId();
        if (formId == null)
            return;
        ShowModalFormWithAction formWithAction = new ShowModalFormWithAction();
        formWithAction.setFormId(reportOnPlace.getFormId());
        if (reportOnPlace.getParams() != null) {
            List<N2oPreFilter> n2oPreFiltersList = new ArrayList<>();
            for (N2oReportPlace.Param param : reportOnPlace.getParams()) {
                N2oPreFilter n2oPreFilter = new N2oPreFilter();
                n2oPreFilter.setFieldId(param.getFormFieldId());
                n2oPreFilter.setRef(param.getRef());
                n2oPreFiltersList.add(n2oPreFilter);
            }
            formWithAction.setPreFilters(n2oPreFiltersList);
            if (reportOnPlace.getParams().length > 0) {
                formWithAction.setMasterFieldId(reportOnPlace.getParams()[0].getRef());
                formWithAction.setDetailFieldId(reportOnPlace.getParams()[0].getFormFieldId());
            }
        }
        formWithAction.setPageName(reportOnPlace.getLabel());
        N2oActionMenu.MenuItem subMenuItem = new N2oActionMenu.MenuItem();
        subMenuItem.setId(reportOnPlace.getFormId());
        subMenuItem.setShowModalForm(formWithAction);
        subMenuItem.setLabel(reportOnPlace.getLabel());
        subMenuItem.setContext(true);
        subMenuItem.setKey(reportOnPlace.getKey());
        setConditions(reportOnPlace, subMenuItem);
        subMenuItemsList.add(subMenuItem);
    }

    public static void addAnchor(List<N2oActionMenu.MenuItem> subMenuItemsList, N2oReportPlace.Report reportOnPlace, String birtHost) {
        String code = reportOnPlace.getCode();
        if (code == null)
            return;
        N2oActionMenu.MenuItem subMenuItem = new N2oActionMenu.MenuItem();
        Anchor anchor = new Anchor(getUrlToBirtWithParam(code, reportOnPlace.getFormat(), reportOnPlace.getParams(), birtHost));
        subMenuItem.setId(code);
        subMenuItem.setAnchor(anchor);
        subMenuItem.setLabel(reportOnPlace.getLabel());
        subMenuItem.setKey(reportOnPlace.getKey());
        setConditions(reportOnPlace, subMenuItem);
        anchor.setTarget(Target.newWindow);
        subMenuItem.setContext(reportOnPlace.getParams() != null);
        subMenuItemsList.add(subMenuItem);
    }

    private static void setConditions(N2oReportPlace.Report reportOnPlace, N2oActionMenu.MenuItem subMenuItem) {
        subMenuItem.setEnablingConditions(reportOnPlace.getEnablingConditions());
        subMenuItem.setVisibilityConditions(reportOnPlace.getVisibilityConditions());
    }

    public static String getUrlToBirtWithParam(String fileName, String format, N2oReportPlace.Param[] reportParams, String birtHost) {
        if (format == null)
            format = "pdf";
        if (reportParams == null) {
            return birtHost + fileName + "&__format=" + format;
        }
        String url = birtHost + fileName + "&";
        for (N2oReportPlace.Param reportParam : reportParams) {
            url = url + reportParam.getName() + "=:" + reportParam.getRef() + "|uc:&";
        }
        return url + "__format=" + format;
    }

    public static void setMenuItemWithoutForm(List<N2oActionMenu.MenuItem> subMenuItemsList, N2oReportPlace.Report reportOnPlace, String href) {
        String formId = reportOnPlace.getFormId();
        if (formId == null)
            return;
        N2oActionMenu.MenuItem subMenuItem = new N2oActionMenu.MenuItem();
        for (N2oReportPlace.Param param : reportOnPlace.getParams()) {
            href = hrefParamsReplace(href, param);
        }
        Anchor anchor = new Anchor(href);
        subMenuItem.setId(formId);
        subMenuItem.setAnchor(anchor);
        subMenuItem.setLabel(reportOnPlace.getLabel());
        anchor.setTarget(Target.newWindow);
        subMenuItem.setContext(true);
        subMenuItem.setKey(reportOnPlace.getKey());
        subMenuItemsList.add(subMenuItem);
    }

    public static String hrefParamsReplace(String href, N2oReportPlace.Param param) {
        String hrefParam = new StringBuilder().append(":").append(param.getFormFieldId()).append(regexUrlEncodeWithColon).toString();
        String placeRef = new StringBuilder().append(":").append(param.getRef()).append("|uc:").toString();
        return href.replaceFirst(hrefParam, placeRef);
    }

    public static Boolean isHideForm(N2oReportPlace.Param[] placeParams, List<N2oField> formFields) {
        if (placeParams == null || placeParams.length == 0)
            return false;
        List<String> placeParamIds = retrievePlaceParamIds(placeParams);
        for (N2oField n2oField : formFields) {
            String fieldId = processControlId(n2oField);
            if (!placeParamIds.contains(fieldId) && isVisibleOrRequired(n2oField)) {
                return false;
            }
        }
        return true;
    }

    private static List<String> retrievePlaceParamIds(N2oReportPlace.Param[] placeParams) {
        List<String> placeParamIds = new ArrayList<>();
        for (N2oReportPlace.Param param : placeParams) {
            placeParamIds.add(param.getFormFieldId());
        }
        return placeParamIds;
    }

    private static String processControlId(N2oField n2oField) {
        String fieldId = n2oField.getId();
        if (n2oField instanceof N2oListField)
            fieldId = fieldId.concat(".id");
        return fieldId;
    }

    private static boolean isVisibleOrRequired(N2oField n2oField) {
        return ((n2oField.getRequired() != null && n2oField.getRequired()) || (n2oField.getVisible() != null && n2oField.getVisible()));
    }


    public static List<String> retrieveParams(String href) {
        List<String> params = new ArrayList<>();
        if (!href.contains(":"))
            return params;
        String[] split = href.split("\\:");
        int minSizeForParam = 3;
        if (split.length < minSizeForParam)
            return params;
        for (String s : split) {
            if (!s.contains("?") && !s.contains("&")) {
                params.add(s.replaceAll(regexUrlEncode, ""));
            }
        }
        return params;
    }

}
