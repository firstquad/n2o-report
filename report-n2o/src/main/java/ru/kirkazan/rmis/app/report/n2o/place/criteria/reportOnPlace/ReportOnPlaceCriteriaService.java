package ru.kirkazan.rmis.app.report.n2o.place.criteria.reportOnPlace;

import net.n2oapp.criteria.api.CollectionPage;
import net.n2oapp.criteria.api.CollectionPageService;
import net.n2oapp.criteria.api.FilteredCollectionPage;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oForm;
import net.n2oapp.framework.config.register.ConfigRegister;
import net.n2oapp.framework.config.service.GlobalMetadataStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kirkazan.rmis.app.report.n2o.api.model.Report;
import ru.kirkazan.rmis.app.report.n2o.api.service.ReportDAO;
import ru.kirkazan.rmis.app.report.n2o.place.model.N2oReportPlace;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dfirstov on 07.11.2014.
 */
public class ReportOnPlaceCriteriaService implements CollectionPageService<ReportOnPlaceCriteria, ReportOnPlace> {
    private static final Logger logger = LoggerFactory.getLogger(ReportOnPlaceCriteriaService.class);
    private ReportDAO reportDAO;

    @Override
    public CollectionPage<ReportOnPlace> getCollectionPage(ReportOnPlaceCriteria criteria) {
        String placeId = criteria.getPlaceId();
        String containerId = criteria.getReportContainerId();
        List<ReportOnPlace> reports = new ArrayList<>();
        N2oReportPlace place = new N2oReportPlace();
        if (placeId != null && ConfigRegister.getInstance().contains(placeId, N2oReportPlace.class))
            try {
                place = GlobalMetadataStorage.getInstance().get(placeId, N2oReportPlace.class);
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        if (place != null) {
            if (containerId == null && place.getReportsElement() != null && place.getReportsElement().getReports() != null) {
                N2oReportPlace.ContainerElement containerElement = new N2oReportPlace.ContainerElement();
                containerElement.setId(null);
                N2oReportPlace.Report[] reportsPlace = place.getReportsElement().getReports();
                fillReports(reports, place, containerElement, reportsPlace);
            }
            if (containerId != null && place.getContainerElements() != null) {
                N2oReportPlace.ContainerElement[] containerElements = place.getContainerElements();
                for (N2oReportPlace.ContainerElement containerElement : containerElements) {
                    if (containerElement.getId().equals(containerId) && containerElement.getReportsElement() != null && containerElement.getReportsElement().getReports() != null) {
                        N2oReportPlace.Report[] reportsPlace = containerElement.getReportsElement().getReports();
                        fillReports(reports, place, containerElement, reportsPlace);
                    }
                }
            }
            reports = filterOnDB(reports);
        }
        return new FilteredCollectionPage<>(reports, criteria);
    }

    private List<ReportOnPlace> filterOnDB(List<ReportOnPlace> reports) {
        List<Report> reportsInDB = reportDAO.retrieveReports();
        List<ReportOnPlace> reportsFiltered = new ArrayList<>();
        for (ReportOnPlace reportOnPlace : reports) {
            for (Report report : reportsInDB) {
                String fileName = report.getFileName();
                String formId = report.getFormId();
                String reportOnPlaceFileName = reportOnPlace.getFileName();
                if (fileName != null && formId == null && reportOnPlaceFileName != null && fileName.equals(reportOnPlaceFileName)) {
                    reportOnPlace.setLabel(retrieveName(report));
                    reportOnPlace.setIsGeneratedForm(report.getIsGeneratedForm());
                    reportOnPlace.setIsInvalid(report.getIsInvalid());
                    reportsFiltered.add(reportOnPlace);
                }
                String reportOnPlaceFormId = reportOnPlace.getFormId();
                if (formId != null && reportOnPlaceFormId != null && formId.equals(reportOnPlaceFormId)) {
                    String label = retrieveName(report);
                    if (ConfigRegister.getInstance().contains(formId, N2oForm.class)) {
                        N2oForm form = GlobalMetadataStorage.getInstance().get(formId, N2oForm.class);
                        label = form.getName() == null ? label : form.getName();
                    }
                    reportOnPlace.setLabel(label);
                    reportOnPlace.setIsGeneratedForm(report.getIsGeneratedForm());
                    reportOnPlace.setIsInvalid(report.getIsInvalid());
                    reportsFiltered.add(reportOnPlace);
                }
            }
        }
        return reportsFiltered;
    }

    private String retrieveName(Report report) {
        return (report.getLabel() == null || "".equals(report.getLabel())) ? report.getFileName() : report.getLabel();
    }

    private void fillReports(List<ReportOnPlace> reports, N2oReportPlace place, N2oReportPlace.ContainerElement containerElement, N2oReportPlace.Report[] reportsPlace) {
        for (N2oReportPlace.Report reportOnPlace : reportsPlace) {
            ReportOnPlace report = new ReportOnPlace();
            String formId = reportOnPlace.getFormId();
            String fileName = reportOnPlace.getCode();
            if (formId != null) {
                report.setId(formId);
            } else if (fileName != null) {
                report.setId(fileName);
            }
            report.setPlaceId(place.getId());
            report.setReportContainerId(containerElement.getId());
            report.setFormId(formId);
            report.setFileName(fileName);
            reports.add(report);
        }
    }

    public void setReportDAO(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }
}
