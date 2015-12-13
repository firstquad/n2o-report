package ru.kirkazan.rmis.app.report.n2o.util;

import net.n2oapp.framework.api.metadata.global.N2oMetadata;
import net.n2oapp.framework.api.metadata.global.view.N2oPage;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oWidget;
import net.n2oapp.framework.config.register.ConfigId;
import net.n2oapp.framework.config.register.ConfigRegister;
import ru.kirkazan.rmis.app.report.n2o.place.util.ReportPlaceUtil;
import ru.kirkazan.rmis.app.security.engine.util.MetadataStorageSchemaProvider;
import ru.kirkazan.rmis.app.security.schema.model.source.AccessScheme;
import ru.kirkazan.rmis.app.security.schema.model.source.AccessSection;

/**
 * Created by dfirstov on 15.10.2014.
 */

public class ConfigRegisterUtil {
    private static ConfigRegister configRegister = ConfigRegister.getInstance();

    public static void updatePage(String metadataId) {
        if (metadataId == null || !isRegistered(metadataId, N2oPage.class))
            return;
        configRegister.update(extractBaseMetaModel(metadataId, N2oPage.class));
    }

    public static void updateWidget(String metadataId) {
        if (metadataId == null || !isRegistered(metadataId, N2oWidget.class))
            return;
        configRegister.update(extractBaseMetaModel(metadataId, N2oWidget.class));
    }

    public static void updateReport(String metadataId, String code) {
        if (metadataId != null && isRegistered(metadataId, N2oWidget.class))
            configRegister.update(extractBaseMetaModel(metadataId, N2oWidget.class));
        updatePlaces(metadataId, code);
    }

    private static void updatePlaces(String metadataId, String code) {
        for (ReportPlaceUtil.ReportForm reportForm : ReportPlaceUtil.retrieveAllReportForms()) {
            if (metadataId != null) {
                if (reportForm.getFormId() != null && reportForm.getFormId().equals(metadataId)) {
                    configRegister.update(extractBaseMetaModel(reportForm.getPageId(), N2oPage.class));
                }
            } else if (code != null) {
                if (reportForm.getCode() != null && reportForm.getCode().equals(code)) {
                    configRegister.update(extractBaseMetaModel(reportForm.getPageId(), N2oPage.class));
                }
            }
        }
    }

    public static void updateAccessSchema(String accessSection) {
        configRegister.update(extractBaseMetaModel(MetadataStorageSchemaProvider.findSchemaId(), AccessScheme.class));//todo убрать после фикса n2o, т.к. сброс секции должен потянуть сброс схемы
        configRegister.update(extractBaseMetaModel(accessSection, AccessSection.class));
    }

    public static void remove(String metadataId, Class<? extends N2oMetadata> metadata) {
        if (!isRegistered(metadataId, metadata))
            return;
        ConfigRegister.getInstance().remove(extractBaseMetaModel(metadataId, metadata));
    }

    private static boolean isRegistered(String metadataId, Class<? extends N2oMetadata> metadata) {
        return metadataId != null && configRegister.contains(metadataId, metadata);
    }

    private static ConfigId extractBaseMetaModel(String metadataId, Class<? extends N2oMetadata> metadata) {
        return new ConfigId(metadataId, configRegister.get(metadataId, metadata).getBaseMetaModel());
    }
}
