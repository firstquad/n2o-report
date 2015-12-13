package ru.kirkazan.rmis.app.report.n2o.transformer;

import net.n2oapp.framework.api.metadata.global.view.N2oPage;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oForm;
import net.n2oapp.framework.api.metadata.local.util.CompileUtil;
import net.n2oapp.framework.api.metadata.local.view.menu.ContextMenuItem;
import net.n2oapp.framework.api.metadata.local.view.page.CompiledPage;
import net.n2oapp.framework.api.transformer.CompileTransformer;
import net.n2oapp.framework.config.metadata.transformer.TransformersUtil;
import net.n2oapp.framework.config.register.ConfigRegister;
import net.n2oapp.framework.rmis.report.CompiledReportPlace;
import net.n2oapp.framework.standard.header.context.ModulePageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kirkazan.rmis.app.report.n2o.api.model.Report;
import ru.kirkazan.rmis.app.report.n2o.api.service.ReportDAO;
import ru.kirkazan.rmis.app.report.n2o.form.ReportFormService;
import ru.kirkazan.rmis.app.report.n2o.place.model.N2oReportPlace;
import ru.kirkazan.rmis.app.report.n2o.sec.ReportAccessImpl;

import java.util.List;
import java.util.Properties;

/**
 * Created by dfirstov on 26.09.2014.
 */
public class ReportMenuTransformer implements CompileTransformer<CompiledPage, ModulePageContext> {
    private Properties properties;
    private ReportDAO reportDAO;
    private ReportAccessImpl reportAccess;
    private static final Logger logger = LoggerFactory.getLogger(ReportMenuTransformer.class);

    @Override
    public CompiledPage transform(CompiledPage compiledPage, ModulePageContext context) {
        try {
            if (!isMatch(context))
                return compiledPage;
            N2oReportPlace place;
            place = TransformersUtil.getCurrentCompiler().get(context.getRealId(), CompiledReportPlace.class, context).getSource();
            if (place == null || place.getReportsElement() == null || place.getReportsElement().getReports() == null)
                return compiledPage;
            fillContextMenu(compiledPage, place);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            return compiledPage;
        }
        return compiledPage;
    }

    private void fillContextMenu(CompiledPage compiledPage, N2oReportPlace place) {
        List<Report> reports = reportDAO.retrieveReports();
        N2oReportPlace.Report[] reportsOnPlace = place.getReportsElement().getReports();
        for (N2oReportPlace.Report reportOnPlace : reportsOnPlace) {
            for (Report report : reports) {
                if (!hasAccess(report.getFileName()))
                    continue;
                setContextMenuItemWithForm(compiledPage, reportOnPlace, report);
                setContextMenuItemWithUrl(compiledPage, reportOnPlace, report);
            }
        }
    }

    protected boolean isMatch(ModulePageContext context) {
        return ConfigRegister.getInstance().contains(context.getRealId(), N2oReportPlace.class);
    }

    @Override
    public Class<CompiledPage> getCompiledMetadataClass() {
        return CompiledPage.class;
    }

    private void setContextMenuItemWithUrl(CompiledPage compiledPage, N2oReportPlace.Report reportOnPlace, Report report) {
        if (reportOnPlace.getCode() != null && reportOnPlace.getCode().equals(report.getFileName())) {
            String fileName = reportOnPlace.getCode();
            String contextMenuItemId = fileName.substring(0, fileName.lastIndexOf('.')).replace("-", "");
            String format = reportOnPlace.getFormat();
            processLabel(reportOnPlace, report);
            ContextMenuItem contextMenuItem = new ContextMenuItem(contextMenuItemId, "reports", addDefaultReportFormat(fileName, format), reportOnPlace.getLabel(), "report");
            contextMenuItem.setType(ContextMenuItem.Type.anchor);
            compiledPage.addAdminItem(contextMenuItem);
        }
    }

    public String addDefaultReportFormat(String fileName, String format) {
        if (format == null)
            format = "pdf";
        return properties.getProperty("rmis.report.url") + "?__report=" + fileName + "&__format=" + format;
    }

    private void processLabel(N2oReportPlace.Report reportOnPlace, Report report) {
        if (reportOnPlace.getLabel() != null && !reportOnPlace.getLabel().equals(""))
            return;
        reportOnPlace.setLabel(report.getLabel() != null && !report.getLabel().equals("") ? report.getLabel() : report.getFileName());
    }

    private void setContextMenuItemWithForm(CompiledPage compiledPage, N2oReportPlace.Report reportOnPlace, Report report) {
        if (reportOnPlace.getFormId() != null && reportOnPlace.getFormId().equals(report.getFormId())) {
            fillLabel(reportOnPlace, report);
            N2oForm form = retrieveForm(reportOnPlace);
            if (form == null)
                return;
            N2oForm formCopied = CompileUtil.copy(form);
            N2oPage page = CompileUtil.createSimplePage(formCopied, form.getObjectId());
            page.setName(reportOnPlace.getLabel());
            ContextMenuItem contextMenuItem = new ContextMenuItem(page.getId(), "reports", page, reportOnPlace.getLabel(), "report");
            contextMenuItem.setType(ContextMenuItem.Type.showModal);
            compiledPage.addAdminItem(contextMenuItem);
        }
    }

    protected N2oForm retrieveForm(N2oReportPlace.Report reportOnPlace) {
        return ReportFormService.retrieveForm(reportOnPlace.getFormId());
    }

    protected void fillLabel(N2oReportPlace.Report reportOnPlace, Report report) {
        ReportTransformerUtil.fillLabel(report, reportOnPlace);
    }

    private boolean hasAccess(String fileName) {
        return reportAccess.hasAccess(fileName);
    }

    @Override
    public Class<ModulePageContext> getContextClass() {
        return ModulePageContext.class;
    }

    public void setReportDAO(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }

    public void setReportAccess(ReportAccessImpl reportAccess) {
        this.reportAccess = reportAccess;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
