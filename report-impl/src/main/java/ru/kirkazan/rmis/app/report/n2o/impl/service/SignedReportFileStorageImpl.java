package ru.kirkazan.rmis.app.report.n2o.impl.service;

import org.springframework.stereotype.Component;
import ru.i_novus.common.file.storage.BaseFileStorage;
import ru.kirkazan.rmis.app.report.n2o.api.service.SignedReportFileStorage;

/**
 * @author rsadikov
 * @since 02.11.2015
 */
@Component
public class SignedReportFileStorageImpl extends BaseFileStorage implements SignedReportFileStorage {
    @Override
    protected String getWorkspaceName() {
        return "report";
    }

    @Override
    protected String getSpaceName() {
        return "signature";
    }
}
