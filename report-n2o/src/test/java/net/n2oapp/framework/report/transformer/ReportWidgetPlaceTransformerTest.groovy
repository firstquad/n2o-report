package net.n2oapp.framework.report.transformer

import net.n2oapp.framework.api.metadata.global.view.action.N2oActionMenu
import net.n2oapp.framework.api.metadata.global.view.action.control.Target
import net.n2oapp.framework.api.metadata.global.view.widget.N2oWidget
import net.n2oapp.framework.api.metadata.local.view.page.CompiledPage
import net.n2oapp.framework.api.metadata.local.view.widget.CompiledForm
import net.n2oapp.framework.config.reader.control.N2oClassifierXmlReaderV1
import net.n2oapp.framework.config.reader.control.N2oDateTimeXmlReaderV1
import net.n2oapp.framework.config.reader.control.N2oInputTextXmlReaderV1
import net.n2oapp.framework.config.reader.util.N2oComplexMetadataReader
import net.n2oapp.framework.config.reader.widget.FormXmlReaderV2
import net.n2oapp.framework.config.selective.CompileInfo
import net.n2oapp.framework.config.selective.SelectiveCompiler
import net.n2oapp.framework.config.selective.SimpleSelectiveCompileBuilder
import net.n2oapp.framework.report.transformer.mock.menu.ReportAccessImplTest
import net.n2oapp.framework.report.transformer.mock.menu.ReportDAOTransformMock
import net.n2oapp.framework.report.transformer.mock.widget.ReportWidgetPlaceTransformerMock
import net.n2oapp.framework.rmis.report.CompiledReportPlace
import net.n2oapp.framework.standard.header.model.local.CompiledHeaderModule
import net.n2oapp.framework.standard.header.model.local.CompiledStandardHeader
import net.n2oapp.framework.standard.header.reader.StandardHeaderModuleReader
import net.n2oapp.framework.standard.header.reader.StandardHeaderReader
import org.junit.Test
import ru.kirkazan.rmis.app.report.n2o.api.model.Report
import ru.kirkazan.rmis.app.report.n2o.place.config.N2oReportPlaceReaderV1
import ru.kirkazan.rmis.app.report.n2o.transformer.ReportWidgetPlaceTransformer

/**
 * @author dfirstov
 * @since 07.07.2015
 */
class ReportWidgetPlaceTransformerTest {
    private static String resourcePath = "net/n2oapp/framework/report/transformer"
    private static SelectiveCompiler compiler

    @Test
    public void testTransform() {
        compilePage()
        testMenuItemPosition()

        def reportsMenuItem = getMenuItem("c4", 1)
        assert "reports".equals(reportsMenuItem.id)
        def item = reportsMenuItem.subMenu[0]
        assert "reportFormMock".equals(item.id)
        assert item.context
        assert "reportFormMock".equals(item.showModalForm.formId)
        assert "filial".equals(item.showModalForm.preFilters.get(0).fieldId)
        assert "1".equals(item.showModalForm.preFilters.get(0).getRef())
        assert "department.id".equals(item.showModalForm.preFilters.get(1).fieldId)
        assert "1".equals(item.showModalForm.preFilters.get(1).getRef())
        assert "filial".equals(item.showModalForm.detailFieldId)
        assert "1".equals(item.showModalForm.masterFieldId)

        reportsMenuItem = getMenuItem("c5", 1)
        assert "reports".equals(reportsMenuItem.id)
        item = reportsMenuItem.subMenu[0]
        assert "reportFormMock".equals(item.id)
        assert item.context
        assert "\${rmis.report.url}?__report=30_2014.rptdesign&from_dt=:1|uc:&to_dt=:1|uc:&clinic_id=:1|uc:&part=:1|uc:&department_id=:1|uc:&filial=:1|uc:&__format=pdf".equals(item.anchor.href)
        assert Target.newWindow.equals(item.anchor.target)

        reportsMenuItem = getMenuItem("c6", 1)
        assert "reports".equals(reportsMenuItem.id)
        item = reportsMenuItem.subMenu[0]
        assert "reportFormMock".equals(item.id)
        assert item.context
        assert "\${rmis.report.url}?__report=30_2014.rptdesign&from_dt=:from_dt|uc:&to_dt=:to_dt|uc:&clinic_id=:1|uc:&part=:1|uc:&department_id=:1|uc:&filial=:1|uc:&__format=pdf".equals(item.anchor.href)
        assert Target.newWindow.equals(item.anchor.target)

        reportsMenuItem = getMenuItem("c7", 0)
        assert "reports".equals(reportsMenuItem.id)
        item = reportsMenuItem.subMenu[0]
        assert "reportFormMock".equals(item.id)
        assert item.context
        assert "\${rmis.report.url}?__report=30_2014.rptdesign&from_dt=:from_dt|uc:&to_dt=:to_dt|uc:&clinic_id=:1|uc:&part=:1|uc:&department_id=:1|uc:&filial=:1|uc:&__format=pdf".equals(item.anchor.href)
        assert Target.newWindow.equals(item.anchor.target)

        reportsMenuItem = getMenuItem("c8", 0)
        assert "reports".equals(reportsMenuItem.id)
        item = reportsMenuItem.subMenu[0]
        assert "reportFormMock".equals(item.id)
        assert item.context
        assert "\${rmis.report.url}?__report=30_2014.rptdesign&from_dt=:from_dt|uc:&to_dt=:to_dt|uc:&clinic_id=:1|uc:&part=:1|uc:&department_id=:1|uc:&filial=:1|uc:&__format=pdf".equals(item.anchor.href)
        assert Target.newWindow.equals(item.anchor.target)

        checkConditions()
    }

    private static void checkConditions() {
        def item, reportsMenuItem
        reportsMenuItem = getMenuItem("c9", 0)
        assert "reports".equals(reportsMenuItem.id)
        item = reportsMenuItem.subMenu[0]
        assert "reportFormMock".equals(item.id)
        assert item.context
        def enablingConditions = item.getEnablingConditions()
        assert enablingConditions != null
        assert enablingConditions[0].expression.equals("false")
        assert enablingConditions[0].tooltip.equals("disabled")

        item = reportsMenuItem.subMenu[1]
        assert "Clinics_integrated_with_FER.rptdesign".equals(item.id)
        enablingConditions = item.getEnablingConditions()
        assert enablingConditions != null
        assert enablingConditions[0].expression.equals("false")
        assert enablingConditions[0].tooltip.equals("disabled")

        reportsMenuItem = getMenuItem("c10", 0)
        assert "reports".equals(reportsMenuItem.id)
        item = reportsMenuItem.subMenu[0]
        assert "reportFormMock".equals(item.id)
        assert item.context
        N2oActionMenu.MenuItem.Condition[] visibilityConditions = item.getVisibilityConditions()
        assert visibilityConditions != null
        assert visibilityConditions[0].expression.equals("false")

        item = reportsMenuItem.subMenu[1]
        assert "Clinics_integrated_with_FER.rptdesign".equals(item.id)
        visibilityConditions = item.getVisibilityConditions()
        assert visibilityConditions != null
        assert visibilityConditions[0].expression.equals("false")
    }

    private static void testMenuItemPosition() {
        N2oActionMenu.MenuItem reportsMenuItem = getMenuItem("c1", 1)
        assert "reports".equals(reportsMenuItem.id)
        def item = reportsMenuItem.subMenu[0]
        assert "Clinics_integrated_with_FER.rptdesign".equals(item.id)
        assert !item.context
        assert "report?__report=Clinics_integrated_with_FER.rptdesign&__format=pdf".equals(item.anchor.href)
        assert Target.newWindow.equals(item.anchor.target)
        assert "f2".equals(item.getKey())
        item = reportsMenuItem.subMenu[1]
        assert "reportFormMock".equals(item.id)
        assert item.context
        assert "reportFormMock".equals(item.showModalForm.formId)
        assert "f2".equals(item.getKey())

        reportsMenuItem = getMenuItem("c2", 0)
        assert "reports".equals(reportsMenuItem.id)
        item = reportsMenuItem.subMenu[1]
        assert "Clinics_integrated_with_FER.rptdesign".equals(item.id)
        assert item.context
        assert "report?__report=Clinics_integrated_with_FER.rptdesign&from_date=:from_dt|uc:&__format=pdf".equals(item.anchor.href)
        assert Target.newWindow.equals(item.anchor.target)
        item = reportsMenuItem.subMenu[2]
        assert "reportFormMock".equals(item.id)
        assert item.context
        assert "reportFormMock".equals(item.showModalForm.formId)

        reportsMenuItem = getMenuItem("c3", 0)
        assert "reports".equals(reportsMenuItem.id)
        item = reportsMenuItem.subMenu[1]
        assert "Clinics_integrated_with_FER.rptdesign".equals(item.id)
        assert !item.context
        assert "report?__report=Clinics_integrated_with_FER.rptdesign&__format=pdf".equals(item.anchor.href)
        assert Target.newWindow.equals(item.anchor.target)
        item = reportsMenuItem.subMenu[2]
        assert "reportFormMock".equals(item.id)
        assert item.context
        assert "reportFormMock".equals(item.showModalForm.formId)
    }

    private static N2oActionMenu.MenuItem getMenuItem(String containerId, int menuItemIndex) {
        def reportsMenuItem = compiler.getGlobal("reportTransformTest3." + containerId, N2oWidget).actionMenu.menuItemGroups[0].menuItems[menuItemIndex]
        reportsMenuItem
    }

    private static CompiledPage compilePage() {
        ReportWidgetPlaceTransformer transformer = makeTransformer()
        compiler = makeCompiler(transformer)
        compiler.get("reportTransformTest3", CompiledPage)
    }

    private static ArrayList<Report> makeReports() {
        Report report = new Report();
        report.setFileName("Clinics_integrated_with_FER.rptdesign")
        report.setLabel("Отчет")
        List<Report> reports = new ArrayList<>()
        reports.add(report)
        report = new Report();
        report.setFormId("reportFormMock")
        report.setLabel("Отчет")
        reports.add(report)
        reports
    }

    private static ReportWidgetPlaceTransformerMock makeTransformer() {
        def dAOMock = new ReportDAOTransformMock()
        dAOMock.setReports(makeReports())
        Properties properties = new Properties()
        properties.put("rmis.report.url", "report")
        ReportWidgetPlaceTransformerMock transformer = new ReportWidgetPlaceTransformerMock();
        transformer.setReportDAO(dAOMock)
        transformer.setProperties(properties)
        transformer.setReportAccess(new ReportAccessImplTest())
        transformer
    }

    private static SelectiveCompiler makeCompiler(ReportWidgetPlaceTransformer transformer) {
        def widgetReader = new N2oComplexMetadataReader("http://n2oapp.net/framework/config/schema/n2o-widget-2.0", new FormXmlReaderV2())
        def selectiveCompileBuilder = new SimpleSelectiveCompileBuilder()
        def compiler = selectiveCompileBuilder
                .addAllWithoutBlank()
                .addPage(resourcePath + "/reportTransformTest1.page.xml")
                .addPage(resourcePath + "/reportTransformTest2.page.xml")
                .addPage(resourcePath + "/reportTransformTest3.page.xml")
                .addObject(resourcePath + "/mock/reportObjectMock.object.xml")
                .addQuery3(resourcePath + "/mock/reportQueryMock.query.xml")
                .addWidgetsCompiler()
                .addWidgetsReader()
                .addControlsReader()
                .addReader(new N2oComplexMetadataReader("http://n2oapp.net/framework/config/schema/n2o-control-1.0", new N2oClassifierXmlReaderV1(), new N2oDateTimeXmlReaderV1(), new N2oInputTextXmlReaderV1()))
                .addCompileInfo(new CompileInfo(resourcePath + "/mock/place/reportTransformTest3.place.xml", new N2oReportPlaceReaderV1(), CompiledReportPlace.class))
                .addCompileInfo(new CompileInfo(resourcePath + "/mock/context/reportHeaderMock.header.xml", new StandardHeaderReader(), CompiledStandardHeader.class))
                .addCompileInfo(new CompileInfo(resourcePath + "/mock/context/reportModuleMock.module.xml", new StandardHeaderModuleReader(), CompiledHeaderModule.class))
                .addCompileInfo(new CompileInfo(resourcePath + "/mock/reportFormMock.widget.xml", widgetReader, CompiledForm.class))
                .addSourceTransformer(transformer)
                .build()
        compiler
    }

}
