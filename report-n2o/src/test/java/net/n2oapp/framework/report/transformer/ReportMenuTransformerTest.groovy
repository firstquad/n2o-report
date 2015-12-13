package net.n2oapp.framework.report.transformer

import net.n2oapp.framework.api.metadata.local.view.menu.ContextMenuItem
import net.n2oapp.framework.api.metadata.local.view.page.CompiledPage
import net.n2oapp.framework.config.selective.CompileInfo
import net.n2oapp.framework.config.selective.SelectiveCompiler
import net.n2oapp.framework.config.selective.SimpleSelectiveCompileBuilder
import net.n2oapp.framework.report.transformer.mock.menu.ReportAccessImplTest
import net.n2oapp.framework.report.transformer.mock.menu.ReportDAOTransformMock
import net.n2oapp.framework.report.transformer.mock.menu.ReportMenuTransformerMock
import net.n2oapp.framework.rmis.report.CompiledReportPlace
import net.n2oapp.framework.standard.header.model.local.CompiledHeaderModule
import net.n2oapp.framework.standard.header.model.local.CompiledStandardHeader
import net.n2oapp.framework.standard.header.reader.StandardHeaderModuleReader
import net.n2oapp.framework.standard.header.reader.StandardHeaderReader
import org.junit.Test
import ru.kirkazan.rmis.app.report.n2o.api.model.Report
import ru.kirkazan.rmis.app.report.n2o.place.config.N2oReportPlaceReaderV1
import ru.kirkazan.rmis.app.report.n2o.transformer.ReportMenuTransformer

/**
 * @author dfirstov
 * @since 06.07.2015
 */
class ReportMenuTransformerTest {
    static private String resourcePath = "net/n2oapp/framework/report/transformer"

    @Test
    public void testTransform() {
        Report report = new Report();
        report.setFileName("Clinics_integrated_with_FER.rptdesign")
        report.setLabel("Отчет")
        List<Report> reports = new ArrayList<>()
        reports.add(report)
        ReportMenuTransformerMock transformer = makeTransformer(reports)
        SelectiveCompiler compiler = makeCompiler(transformer)
        List<ContextMenuItem> contextMenuItems = retrieveContextMenuItems(compiler, "reportTransformTest1")
        def contextMenuItem = contextMenuItems.get(0)
        assert "Clinics_integrated_with_FER".equals(contextMenuItem.id)
        assert "report?__report=Clinics_integrated_with_FER.rptdesign&__format=pdf".equals(contextMenuItem.pageId)
        assert "report".equals(contextMenuItem.objectId)
        assert "reports".equals(contextMenuItem.contextMenuId)
        assert ContextMenuItem.Type.anchor.equals(contextMenuItem.type)

        report = new Report();
        report.setFormId("reportFormMock")
        report.setFileName("005y.rptdesign")
        report.setLabel("Отчет")
        reports.add(report)
        transformer = makeTransformer(reports)
        compiler = makeCompiler(transformer)
        contextMenuItems = retrieveContextMenuItems(compiler, "reportTransformTest2")
        contextMenuItem = contextMenuItems.get(0)
        assert "Clinics_integrated_with_FER".equals(contextMenuItem.id)
        assert "report?__report=Clinics_integrated_with_FER.rptdesign&__format=pdf".equals(contextMenuItem.pageId)
        assert "report".equals(contextMenuItem.objectId)
        assert "reports".equals(contextMenuItem.contextMenuId)
        assert ContextMenuItem.Type.anchor.equals(contextMenuItem.type)

        contextMenuItem = contextMenuItems.get(1)
        assert "reportFormMock".equals(contextMenuItem.id)
        assert contextMenuItem.pageId.contains("reportFormMock")
        assert "report".equals(contextMenuItem.objectId)
        assert "reports".equals(contextMenuItem.contextMenuId)
        assert ContextMenuItem.Type.showModal.equals(contextMenuItem.type)
    }

    private static List<ContextMenuItem> retrieveContextMenuItems(SelectiveCompiler compiler, String pageId) {
        compiler.get("reportHeaderMock", CompiledStandardHeader)
        def page = compiler.get("reportModuleMock." + pageId, CompiledPage)
        def contextMenuItems = page.contextMenus.get("reports")
        contextMenuItems
    }

    private static ReportMenuTransformerMock makeTransformer(List<Report> reports) {
        def dAOMock = new ReportDAOTransformMock()
        dAOMock.setReports(reports)
        Properties properties = new Properties()
        properties.put("rmis.report.url", "report")
        ReportMenuTransformer transformer = new ReportMenuTransformerMock();
        transformer.setReportDAO(dAOMock)
        transformer.setProperties(properties)
        transformer.setReportAccess(new ReportAccessImplTest())
        transformer
    }

    private static SelectiveCompiler makeCompiler(ReportMenuTransformerMock transformer) {
        def compiler = new SimpleSelectiveCompileBuilder()
                .addPage(resourcePath + "/reportTransformTest1.page.xml")
                .addPage(resourcePath + "/reportTransformTest2.page.xml")
                .addObject(resourcePath + "/mock/reportObjectMock.object.xml")
                .addQuery3(resourcePath + "/mock/reportQueryMock.query.xml")
                .addCompileInfo(new CompileInfo(resourcePath + "/mock/place/reportTransformTest1.place.xml", new N2oReportPlaceReaderV1(), CompiledReportPlace.class))
                .addCompileInfo(new CompileInfo(resourcePath + "/mock/place/reportTransformTest2.place.xml", new N2oReportPlaceReaderV1(), CompiledReportPlace.class))
                .addCompileInfo(new CompileInfo(resourcePath + "/mock/context/reportHeaderMock.header.xml", new StandardHeaderReader(), CompiledStandardHeader.class))
                .addCompileInfo(new CompileInfo(resourcePath + "/mock/context/reportModuleMock.module.xml", new StandardHeaderModuleReader(), CompiledHeaderModule.class))
                .addCompileTransformer(transformer)
                .build()
        compiler
    }
}
