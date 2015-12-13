package ru.kirkazan.rmis.app.report.n2o.transformer;

import net.n2oapp.framework.api.metadata.global.view.action.N2oActionMenu;
import net.n2oapp.framework.api.metadata.global.view.action.control.Anchor;
import net.n2oapp.framework.api.metadata.global.view.action.control.ShowModalFormWithAction;
import net.n2oapp.framework.api.metadata.global.view.action.control.ShowModalPageWithAction;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oForm;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oWidget;
import net.n2oapp.framework.api.metadata.local.context.WidgetContainerContext;
import net.n2oapp.framework.api.metadata.local.util.CompileUtil;
import net.n2oapp.framework.api.transformer.SourceTransformer;
import net.n2oapp.framework.config.register.ConfigRegister;
import net.n2oapp.framework.config.service.GlobalMetadataStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kirkazan.rmis.app.report.n2o.api.model.Report;
import ru.kirkazan.rmis.app.report.n2o.api.service.ReportDAO;
import ru.kirkazan.rmis.app.report.n2o.form.ReportFormService;
import ru.kirkazan.rmis.app.report.n2o.form.util.ReportFormUtil;
import ru.kirkazan.rmis.app.report.n2o.place.model.N2oReportPlace;
import ru.kirkazan.rmis.app.report.n2o.sec.ReportAccessImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static ru.kirkazan.rmis.app.report.n2o.transformer.ReportTransformerUtil.*;

/**
 * Created by dfirstov on 31.10.2014.
 */
public class ReportWidgetPlaceTransformer implements SourceTransformer<N2oWidget, WidgetContainerContext> {
    protected Properties properties;
    private ReportDAO reportDAO;
    private static final Logger logger = LoggerFactory.getLogger(ReportWidgetPlaceTransformer.class);
    private ReportAccessImpl reportAccess;

    @Override
    public N2oWidget transform(N2oWidget n2oWidget, WidgetContainerContext context) {
        try {
            if (context == null || context.getContainerId() == null)
                return n2oWidget;
            String sourcePageId = retrievePageId(context);
            if (sourcePageId == null)
                return n2oWidget;
            N2oReportPlace place = retrievePlace(sourcePageId);
            if (place == null || place.getContainerElements() == null)
                return n2oWidget;
            List<Report> dbReports = reportDAO.retrieveReports();
            if (dbReports == null)
                return n2oWidget;
            fillWidget(n2oWidget, context.getContainerId(), place.getContainerElements(), dbReports);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            return n2oWidget;
        }
        return n2oWidget;
    }

    protected N2oReportPlace retrievePlace(String sourcePageId) {
        if (!placeExists(sourcePageId))
            return null;
        N2oReportPlace place;
        try {
            place = GlobalMetadataStorage.getInstance().get(sourcePageId, N2oReportPlace.class);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            return null;
        }
        if (place == null)
            return null;
        return place;
    }

    private String retrievePageId(WidgetContainerContext context) {
        if (context == null || context.getParentContext() == null)
            return null;
        return context.getParentContext().getRealId();
    }

    private boolean placeExists(String sourcePageId) {
        return sourcePageId != null && ConfigRegister.getInstance().contains(sourcePageId, N2oReportPlace.class);
    }

    private void fillWidget(N2oWidget n2oWidget, String widgetContainerId, N2oReportPlace.ContainerElement[] placeContainers, List<Report> dbReports) {
        for (N2oReportPlace.ContainerElement placeContainer : placeContainers) {
            if (widgetContainerId.equals(placeContainer.getId()) && placeContainer.getReportsElement() != null) {
                fillWidgetContainer(n2oWidget, dbReports, placeContainer);
            }
        }
    }

    private void fillWidgetContainer(N2oWidget widget, List<Report> dbReports, N2oReportPlace.ContainerElement containerElementOnPlace) {
        N2oReportPlace.Report[] placeAllReports = containerElementOnPlace.getReportsElement().getReports();
        if (placeAllReports == null)
            return;
        List<N2oReportPlace.Report> placeReports = new ArrayList<>();
        for (N2oReportPlace.Report placeReport : placeAllReports) {
            for (Report dbReport : dbReports) {
                if (!hasAccess(dbReport.getFileName()))
                    continue;
                if (hasForm(placeReport, dbReport) || hasAnchor(placeReport, dbReport)) {
                    N2oReportPlace.Report copyPlaceReport = CompileUtil.copy(placeReport);
                    fillLabel(dbReport, copyPlaceReport);
                    placeReports.add(copyPlaceReport);
                }
            }
        }
        String customPlaceMenuItemId = containerElementOnPlace.getReportMenuItemId();
        fillMenuItem(widget, placeReports, customPlaceMenuItemId);
    }

    private boolean hasAnchor(N2oReportPlace.Report reportOnPlace, Report report) {
        return (reportOnPlace.getCode() != null && report.getFormId() == null && report.getFileName().equals(reportOnPlace.getCode()));
    }

    private boolean hasForm(N2oReportPlace.Report reportOnPlace, Report report) {
        return report.getFormId() != null && reportOnPlace.getFormId() != null && report.getFormId().equals(reportOnPlace.getFormId());
    }

    protected void fillLabel(Report report, N2oReportPlace.Report copyReportOnPlace) {
        ReportTransformerUtil.fillLabel(report, copyReportOnPlace);
    }

    private boolean hasAccess(String fileName) {
        return reportAccess.hasAccess(fileName);
    }

    private N2oWidget fillMenuItem(N2oWidget widget, List<N2oReportPlace.Report> placeReports, String customId) {
        customId = (customId == null) ? "reports" : customId;
        List<N2oActionMenu.MenuItem> reportMenuItems = new ArrayList<>();
        convertPlaceReportsToMenuItems(reportMenuItems, placeReports);
        if (reportMenuItems.size() == 0)
            return widget;
        if (fillExistsReportsMenuItem(widget, customId, reportMenuItems)) {
            return widget;
        }
        processActionMenu(widget);
        N2oActionMenu.MenuItemGroup lastGroup = retrieveLastGroup(widget);
        fillLastGroup(reportMenuItems, customId, lastGroup);
        return widget;
    }

    private N2oActionMenu.MenuItemGroup retrieveLastGroup(N2oWidget widget) {
        N2oActionMenu.MenuItemGroup[] groups = widget.getActionMenu().getMenuItemGroups();
        return groups[groups.length - 1];
    }

    private void fillLastGroup(List<N2oActionMenu.MenuItem> reportMenuItems, String customId, N2oActionMenu.MenuItemGroup lastGroup) {
        N2oActionMenu.MenuItem[] lastGroupMenuItems = lastGroup.getMenuItems();
        List<N2oActionMenu.MenuItem> resultMenuItems = new ArrayList<>();
        if (lastGroupMenuItems != null) {
            resultMenuItems = new ArrayList<>(Arrays.asList(lastGroupMenuItems));
        }
        N2oActionMenu.MenuItem reportSubMenuItem = new N2oActionMenu.MenuItem();
        fillReportsMenuItem(reportSubMenuItem, customId, reportMenuItems);
        resultMenuItems.add(reportSubMenuItem);
        lastGroup.setMenuItems(resultMenuItems.toArray(new N2oActionMenu.MenuItem[resultMenuItems.size()]));
    }

    private void processActionMenu(N2oWidget widget) {
        N2oActionMenu actionMenu = widget.getActionMenu();
        if (actionMenu == null) {
            N2oActionMenu n2oActionMenu = new N2oActionMenu();
            widget.setActionMenu(n2oActionMenu);
        }
        N2oActionMenu.MenuItemGroup[] menuItemGroups = widget.getActionMenu().getMenuItemGroups();
        if (menuItemGroups == null || menuItemGroups.length == 0) {
            widget.getActionMenu().setMenuItemGroups(new N2oActionMenu.MenuItemGroup[]{new N2oActionMenu.MenuItemGroup()});
        }
    }

    private boolean fillExistsReportsMenuItem(N2oWidget widget, String customId, List<N2oActionMenu.MenuItem> reportMenuItems) {
        if (!haveMenuItems(widget)) {
            return false;
        }
        for (N2oActionMenu.MenuItem menuItem : extractLastGroupMenuItems(widget)) {
            if (!menuItem.getId().equals(customId))
                continue;
            if (menuItem.getSubMenu() == null) {
                convertToSubMenu(menuItem);
            }
            ArrayList<N2oActionMenu.MenuItem> resultMenuItems = new ArrayList<>(Arrays.asList(menuItem.getSubMenu()));
            resultMenuItems.addAll(reportMenuItems);
            fillReportsMenuItem(menuItem, customId, resultMenuItems);
            return true;
        }
        return false;
    }

    private void fillReportsMenuItem(N2oActionMenu.MenuItem reportsMenuItem, String customId, List<N2oActionMenu.MenuItem> reportMenuItems) {
        reportsMenuItem.setId(customId);
        reportsMenuItem.setContext(true);
        reportsMenuItem.setLabel("Отчеты");
        reportsMenuItem.setIcon("glyphicon glyphicon-print");
        reportsMenuItem.setSubMenu(reportMenuItems.toArray(new N2oActionMenu.MenuItem[reportMenuItems.size()]));
    }

    private List<N2oActionMenu.MenuItem> extractLastGroupMenuItems(N2oWidget widget) {
        N2oActionMenu.MenuItemGroup[] groups = widget.getActionMenu().getMenuItemGroups();
        List<N2oActionMenu.MenuItem> widgetMenuItems = new ArrayList<>();
        if (groups.length == 0)
            return widgetMenuItems;
        widgetMenuItems = new ArrayList<>(Arrays.asList(groups[groups.length - 1].getMenuItems()));
        return widgetMenuItems;
    }

    private boolean haveMenuItems(N2oWidget n2oWidget) {
        return n2oWidget.getActionMenu() != null && n2oWidget.getActionMenu().getMenuItemGroups() != null;
    }

    private void convertToSubMenu(N2oActionMenu.MenuItem menuItem) {
        N2oActionMenu.MenuItem copyMenuItem = CompileUtil.copy(menuItem);
        ShowModalPageWithAction showModal = copyMenuItem.getShowModal();
        ShowModalFormWithAction showModalForm = copyMenuItem.getShowModalForm();
        Anchor anchor = copyMenuItem.getAnchor();
        N2oActionMenu.MenuItem newMenuItem = new N2oActionMenu.MenuItem();
        newMenuItem.setId("rigidReport");
        newMenuItem.setLabel(copyMenuItem.getLabel());
        N2oActionMenu.MenuItem[] subMenu = new N2oActionMenu.MenuItem[1];
        if (showModal != null) {
            newMenuItem.setShowModal(showModal);
            subMenu[0] = newMenuItem;
            menuItem.setShowModal(null);
        } else if (showModalForm != null) {
            newMenuItem.setShowModalForm(showModalForm);
            subMenu[0] = newMenuItem;
            menuItem.setShowModalForm(null);
        } else if (anchor != null) {
            newMenuItem.setAnchor(anchor);
            subMenu[0] = newMenuItem;
            menuItem.setAnchor(null);
        }
        menuItem.setSubMenu(subMenu);
    }

    private List<N2oActionMenu.MenuItem> convertPlaceReportsToMenuItems(List<N2oActionMenu.MenuItem> subMenuItems, List<N2oReportPlace.Report> reportsOnPlace) {
        for (N2oReportPlace.Report reportOnPlace : reportsOnPlace) {
            N2oForm form = retrieveForm(reportOnPlace.getFormId());
            if (reportOnPlace.getFormId() == null && form == null) {
                addAnchor(subMenuItems, reportOnPlace, properties.getProperty("rmis.report.url") + "?__report=");
            } else {
                addForm(subMenuItems, reportOnPlace, form);
            }
        }
        return subMenuItems;
    }

    protected N2oForm retrieveForm(String formId) {
        if (formId != null && ConfigRegister.getInstance().contains(formId, N2oForm.class)) {
            return ReportFormService.retrieveForm(formId);
        }
        return null;
    }

    private void addForm(List<N2oActionMenu.MenuItem> subMenuItemsNew, N2oReportPlace.Report reportOnPlace, N2oForm form) {
        Boolean hideForm = isHideForm(reportOnPlace.getParams(), ReportFormUtil.retrieveFormFields(form));
        if (hideForm) {
            setMenuItemWithoutForm(subMenuItemsNew, reportOnPlace, retrieveHref(form));
        } else {
            setMenuItemWithForm(subMenuItemsNew, reportOnPlace);
        }
    }

    private String retrieveHref(N2oForm form) {
        String href = null;
        if (form.getEdit() != null && form.getEdit().getAnchor() != null && form.getEdit().getAnchor().getHref() != null) {
            href = form.getEdit().getAnchor().getHref();
        }
        return href;
    }

    @Override
    public Class<N2oWidget> getMetadataClass() {
        return N2oWidget.class;
    }

    @Override
    public Class<WidgetContainerContext> getContextClass() {
        return WidgetContainerContext.class;
    }

    public void setReportDAO(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setReportAccess(ReportAccessImpl reportAccess) {
        this.reportAccess = reportAccess;
    }
}
