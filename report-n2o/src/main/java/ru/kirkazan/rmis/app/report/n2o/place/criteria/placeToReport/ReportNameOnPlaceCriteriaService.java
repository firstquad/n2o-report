package ru.kirkazan.rmis.app.report.n2o.place.criteria.placeToReport;

import net.n2oapp.criteria.api.CollectionPage;
import net.n2oapp.criteria.api.CollectionPageService;
import net.n2oapp.criteria.api.FilteredCollectionPage;

import java.util.List;

/**
 * @author dfirstov
 * @since 08.09.2015
 */
public class ReportNameOnPlaceCriteriaService implements CollectionPageService<PlaceToReportCriteria, PlaceToReport> {
    private PlaceToReportCriteriaService placeToReportCriteriaService;

    @Override
    public CollectionPage<PlaceToReport> getCollectionPage(PlaceToReportCriteria placeToReportCriteria) {
        List<PlaceToReport> placeToReports = placeToReportCriteriaService.fillPlaceToReport(placeToReportCriteria);
        placeToReportCriteria.setCount(1);
        return new FilteredCollectionPage<>(placeToReports, placeToReportCriteria);
    }

    public void setPlaceToReportCriteriaService(PlaceToReportCriteriaService placeToReportCriteriaService) {
        this.placeToReportCriteriaService = placeToReportCriteriaService;
    }
}
