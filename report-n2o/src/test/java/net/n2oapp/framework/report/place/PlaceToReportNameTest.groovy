package net.n2oapp.framework.report.place

import net.n2oapp.framework.api.metadata.local.view.page.CompiledPage
import net.n2oapp.framework.config.reader.page.PageXmlReaderV1
import net.n2oapp.framework.config.selective.CompileInfo
import net.n2oapp.framework.config.selective.SelectiveCompilerBuilder
import net.n2oapp.framework.standard.header.model.local.CompiledHeaderModule
import net.n2oapp.framework.standard.header.model.local.CompiledStandardHeader
import net.n2oapp.framework.standard.header.reader.StandardHeaderModuleReader
import net.n2oapp.framework.standard.header.reader.StandardHeaderReader
import org.junit.Test
import ru.kirkazan.rmis.app.report.n2o.place.criteria.placeToReport.PlaceToReportUtil
import ru.kirkazan.rmis.app.report.n2o.place.model.N2oReportPlace

/**
 * @author dfirstov
 * @since 30.07.2015
 */
class PlaceToReportNameTest {
    static private String resourcePath = "net/n2oapp/framework/report/placeToReport/"

    @Test
    public void testResolvePlaceName() {
        def compiler = new SelectiveCompilerBuilder()
                .addCompileInfo(new CompileInfo(resourcePath + "testReportToPlaceName.header.xml", new StandardHeaderReader(), CompiledStandardHeader.class))
                .addCompileInfo(new CompileInfo(resourcePath + "testReportToPlaceName1.page.xml", new PageXmlReaderV1(), CompiledPage.class))
                .addCompileInfo(new CompileInfo(resourcePath + "testReportToPlaceName2.page.xml", new PageXmlReaderV1(), CompiledPage.class))
                .addReader(new StandardHeaderModuleReader()).addCompileClass(CompiledHeaderModule)
                .build()
        def header = compiler.get("testReportToPlaceName", CompiledStandardHeader.class)
        def reportPlace = new N2oReportPlace()
        reportPlace.setId("testReportToPlaceName1")
        def placeName = PlaceToReportUtil.resolvePlaceName(header, reportPlace)
        assert "Модуль > Спейс > Место вызова".equals(placeName)
        reportPlace = new N2oReportPlace()
        reportPlace.setId("testReportToPlaceName2")
        placeName = PlaceToReportUtil.resolvePlaceName(header, reportPlace)
        assert "Модуль > Место вызова".equals(placeName)
    }
}
