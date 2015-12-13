package ru.kirkazan.rmis.app.report.n2o.validation;

import net.n2oapp.framework.api.metadata.global.view.N2oPage;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oForm;
import net.n2oapp.framework.api.metadata.validation.Level;
import net.n2oapp.framework.api.metadata.validation.TypedMetadataValidator;
import net.n2oapp.framework.api.metadata.validation.exception.N2oMetadataValidationException;
import net.n2oapp.framework.config.register.ConfigRegister;
import net.n2oapp.framework.config.service.GlobalMetadataStorage;
import ru.kirkazan.rmis.app.report.n2o.place.model.N2oReportPlace;
import ru.kirkazan.rmis.app.report.n2o.api.service.ReportDAO;
import java.util.ArrayList;
import java.util.List;

import static ru.kirkazan.rmis.app.report.n2o.form.util.ReportFormUtil.*;
import static ru.kirkazan.rmis.app.report.n2o.place.util.ReportPlaceUtil.*;
import static net.n2oapp.framework.config.validation.ValidationUtil.*;

/**
 * Created by dfirstov on 26.12.2014.
 */
public class ReportPlaceValidation extends TypedMetadataValidator<N2oReportPlace> {
    private ReportDAO reportDAO;
    private static final String CALL_PLACE = "В месте вызова отчета [";
    private static final String DONT_FOUND_PAGE = "] не найдена страница.";
    private static final String PLACE_ID_IS_NULL = "Не задан id страницы";
    private static final String DONT_FOUND_FORM = "] не найдена форма [";
    private static final String DONT_FOUND_CONTAINER = "] не найден контейнер [";
    private static final String DONT_FOUND_FORM_REF = "] для формы [%s] не найдена ссылка из контейнера [%s]";

    @Override
    public void check(N2oReportPlace place) {
        checkPageId(place);
        checkFormIds(place);
        checkContainers(place);
        checkReportParamRefs(place);
    }

    private void checkPageId(N2oReportPlace place) {
        if (place == null)
            return;
        String placeId = place.getId();
        if (placeId == null) throw new N2oMetadataValidationException(PLACE_ID_IS_NULL);
        if (hasLinks(placeId))
            return;
        if (!ConfigRegister.getInstance().contains(placeId, N2oPage.class)) {
            throw new N2oMetadataValidationException(CALL_PLACE + placeId + DONT_FOUND_PAGE);
        }
    }

    private void checkFormIds(N2oReportPlace place) {
        if (place == null)
            return;
        List<String> placeFormIds = retrieveFormIds(place);
        List<String> reportFormIds = reportDAO.retrieveReportFormIds();
        if (placeFormIds == null)
            return;
        for (String formId : placeFormIds) {
            if (reportFormIds != null && reportFormIds.contains(formId) && !hasLinks(formId)
                    && !ConfigRegister.getInstance().contains(formId, N2oForm.class)) {
                throw new N2oMetadataValidationException(CALL_PLACE + place.getId() + DONT_FOUND_FORM + formId + "]");
            }
        }
    }

    private void checkContainers(N2oReportPlace place) {
        if (place == null)
            return;
        N2oPage page = GlobalMetadataStorage.getInstance().get(place.getId(), N2oPage.class);
        List<String> placeContainerIds = retrieveContainerIds(place);
        if (placeContainerIds == null)
            return;
        List<String> pageContainerIds = retrieveContainerIdsFromPage(page);
        for (String placeContainerId : placeContainerIds) {
            if (!hasLinks(placeContainerId) && !pageContainerIds.contains(placeContainerId)) {
                throw new N2oMetadataValidationException(CALL_PLACE + place.getId() + DONT_FOUND_CONTAINER + placeContainerId + "]");
            }
        }
    }

    private List<String> retrieveContainerIdsFromPage(N2oPage page) {
        if (page == null)
            return new ArrayList<>();
        List<N2oPage.Container> pageContainers = retrieveContainers(page);
        List<String> pageContainerIds = new ArrayList<>();
        if (pageContainers == null)
            return new ArrayList<>();
        for (N2oPage.Container container : pageContainers) {
            pageContainerIds.add(container.getId());
        }
        return pageContainerIds;
    }

    private void checkReportParamRefs(N2oReportPlace place) {
        if (place == null)
            return;
        List<N2oReportPlace.ContainerElement> placeContainerIds = retrieveContainers(place);
        String placeId = place.getId();
        N2oPage page = GlobalMetadataStorage.getInstance().get(placeId, N2oPage.class);
        List<N2oPage.Container> pageContainers = retrieveContainers(page);
        if (placeContainerIds == null)
            return;
        for (N2oReportPlace.ContainerElement placeContainer : placeContainerIds) {
            for (N2oPage.Container pageContainer : pageContainers) {
                compareContainers(placeId, placeContainer, pageContainer);
            }
        }
    }

    private void compareContainers(String placeId, N2oReportPlace.ContainerElement placeContainer, N2oPage.Container pageContainer) {
        if (pageContainer.getId().equals(placeContainer.getId())) {
            List<String> pageFieldIds = retrieveFieldIdsFromContainer(pageContainer);
            List<N2oReportPlace.Report> reports = retrieveReportsFromContainer(placeContainer);
            compareReportRefWithPageFields(placeId, pageFieldIds, reports);
        }
    }

    private void compareReportRefWithPageFields(String placeId, List<String> pageFieldIds, List<N2oReportPlace.Report> reports) {
        List<String> reportFormIds = reportDAO.retrieveReportFormIds();
        if (reportFormIds.size() == 0)
            return;
        if (reports == null)
            return;
        for (N2oReportPlace.Report report : reports) {
            List<String> reportRefIds = retrieveFormRefIds(report);
            if (reportRefIds == null)
                return;
            for (String reportRefId : reportRefIds) {
                String formId = report.getFormId();
                if (reportRefId != null && !hasLinks(reportRefId) && formId != null && reportFormIds.contains(formId)
                        && !isExists(pageFieldIds, reportRefId)) {
                    throw new N2oMetadataValidationException(CALL_PLACE + placeId
                            + String.format(DONT_FOUND_FORM_REF, formId, reportRefId));
                }
            }
        }
    }

    private boolean isExists(List<String> pageFieldIds, String reportRefId) {
        for (String pageFieldId : pageFieldIds) {
            if (reportRefId.contains(pageFieldId))
                return true;
        }
        return false;
    }

    @Override
    public Class<N2oReportPlace> getMetadataClass() {
        return N2oReportPlace.class;
    }

    @Override
    public Level getLevel() {
        return Level.first;
    }

    public void setReportDAO(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }
}
