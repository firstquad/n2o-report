package ru.kirkazan.rmis.app.report.n2o.place.criteria.placeToReport;

import net.n2oapp.criteria.api.CollectionPage;
import net.n2oapp.criteria.api.CollectionPageService;
import net.n2oapp.criteria.api.FilteredCollectionPage;
import net.n2oapp.framework.api.metadata.global.view.N2oPage;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oForm;
import net.n2oapp.framework.config.register.ConfigRegister;
import net.n2oapp.framework.config.service.GlobalMetadataStorage;
import net.n2oapp.framework.config.service.MetadataStorage;
import net.n2oapp.framework.standard.header.model.local.CompiledStandardHeader;
import net.n2oapp.properties.StaticProperties;
import ru.kirkazan.rmis.app.report.n2o.place.model.N2oReportPlace;
import ru.kirkazan.rmis.app.report.n2o.report.ReportN2oService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.kirkazan.rmis.app.report.n2o.place.criteria.placeToReport.PlaceToReportUtil.resolvePlaceName;
import static ru.kirkazan.rmis.app.report.n2o.report.ReportN2oService.*;

/**
 * Created by dfirstov on 02.11.2014.
 */
public class PlaceToReportCriteriaService implements CollectionPageService<PlaceToReportCriteria, PlaceToReport> {
    private ReportN2oService reportN2oService;

    @Override
    public CollectionPage<PlaceToReport> getCollectionPage(PlaceToReportCriteria criteria) {
        List<PlaceToReport> list = fillPlaceToReport(criteria);
        return new FilteredCollectionPage<>(list, criteria);
    }

    public List<PlaceToReport> fillPlaceToReport(PlaceToReportCriteria criteria) {
        List<PlaceToReport> placeToReports = new ArrayList<>();
        List<N2oReportPlace> places = GlobalMetadataStorage.getInstance().getAll(N2oReportPlace.class, false);
        String headerId = StaticProperties.get("n2o.ui.header.id");
        CompiledStandardHeader header = MetadataStorage.getInstance().get(headerId, CompiledStandardHeader.class);
        for (N2oReportPlace place : places) {
            String placeName = resolvePlaceName(header, place);
            if (place.getReportsElement() != null) {
                setPlaceToReport(placeToReports, place, placeName);
            }
            if (place.getContainerElements() != null) {
                for (N2oReportPlace.ContainerElement containerElement : place.getContainerElements()) {
                    if (containerElement.getReportsElement() != null) {
                        setPlaceToReportContainer(placeToReports, place, containerElement, placeName);
                    }
                }
            }
        }
        return filter(criteria, placeToReports);
    }

    private String fillContainerName(N2oReportPlace place, String containerElementId) {
        String containerName = containerElementId;
        if (containerElementId != null && ConfigRegister.getInstance().contains(place.getId(), N2oPage.class)) {
            N2oPage page = GlobalMetadataStorage.getInstance().get(place.getId(), N2oPage.class);
            N2oPage.Container[] containers = page.getContainers();
            if (containers != null) {
                for (N2oPage.Container container : containers) {
                    if (container.getId().equals(containerElementId) && container.getWidget() != null && container.getWidget().getName() != null && !container.getWidget().getName().equals("")) {
                        containerName = container.getWidget().getName();
                    }
                }
            }
            if (page.getRegions() != null) {
                for (N2oPage.Region region : page.getRegions()) {
                    if (region.getContainers() != null) {
                        for (N2oPage.Container container : region.getContainers()) {
                            if (container.getId().equals(containerElementId) && container.getWidget() != null && container.getWidget().getName() != null && !container.getWidget().getName().equals("")) {
                                containerName = container.getWidget().getName();
                            }
                        }
                    }
                }
            }
        }
        return containerName;
    }

    private void setPlaceToReport(List<PlaceToReport> placeToReports, N2oReportPlace place, String placeName) {
        int i = 1;
        for (N2oReportPlace.Report report : place.getReportsElement().getReports()) {
            PlaceToReport placeToReport = new PlaceToReport();
            fillInvalid(place, placeToReport);
            placeToReport.setRealId(place.getRealId());
            placeToReport.setPlaceId(place.getId());
            placeToReport.setCode(place.getId());
            placeToReport.setN2oClass(N2oReportPlace.class.getName());
            placeToReport.setPlaceName(placeName);
            placeToReport.setReportName(report.getLabel());
            String formId = report.getFormId();
            if (formId != null && ConfigRegister.getInstance().contains(formId, N2oForm.class)) {
                String fileName;
                fileName = reportN2oService.extractReportFileNameFromFormHref(formId);
                placeToReport.setFileName(fileName);
                placeToReport.setId(place.getId() + "_" + prepareId(formId) + "_" + i);
                placeToReport.setFormId(formId);
                placeToReport.setReportContainerName("Меню страницы");
                placeToReports.add(placeToReport);
            } else {
                String code = report.getCode();
                if (code != null) {
                    placeToReport.setFileName(code);
                    placeToReport.setReportContainerName("Меню страницы");
                    placeToReport.setId(place.getId() + "_" + prepareId(code) + "_" + i);
                    placeToReports.add(placeToReport);
                }
            }
            i++;
        }
    }

    private void fillInvalid(N2oReportPlace place, PlaceToReport placeToReport) {
        placeToReport.setIsInvalid(place.isInvalid());
        if (place.isInvalid()) {
            placeToReport.setInvalidMessage(place.getInvalidMessage());
        }
    }

    private void setPlaceToReportContainer(List<PlaceToReport> placeToReports, N2oReportPlace place, N2oReportPlace.ContainerElement containerElement, String placeName) {
        String containerId = containerElement.getId();
        String containerName = fillContainerName(place, containerId);
        int i = 1;
        for (N2oReportPlace.Report report : containerElement.getReportsElement().getReports()) {
            PlaceToReport placeToReport = new PlaceToReport();
            fillInvalid(place, placeToReport);
            placeToReport.setRealId(place.getRealId());
            placeToReport.setPlaceId(place.getId());
            placeToReport.setCode(place.getId());
            placeToReport.setN2oClass(N2oReportPlace.class.getName());
            placeToReport.setPlaceName(placeName);
            placeToReport.setReportContainerId(containerId);
            placeToReport.setReportContainerName(containerName);
            placeToReport.setReportName(report.getLabel());
            String formId = report.getFormId();
            if (formId != null && ConfigRegister.getInstance().contains(formId, N2oForm.class)) {
                String fileName;
                fileName = reportN2oService.extractReportFileNameFromFormHref(formId);
                placeToReport.setFileName(fileName);
                placeToReport.setId(place.getId() + "_" + prepareId(formId) + "_" + containerId + "_" + i);
                placeToReport.setFormId(formId);
                placeToReports.add(placeToReport);
            } else {
                String code = report.getCode();
                if (code != null) {
                    placeToReport.setFileName(code);
                    placeToReport.setId(place.getId() + "_" + prepareId(code) + "_" + containerId + "_" + i);
                    placeToReports.add(placeToReport);
                }
            }
            i++;
        }
    }

    private List<PlaceToReport> filter(PlaceToReportCriteria criteria, List<PlaceToReport> placeToReports) {
        if (criteria.getId() != null)
            placeToReports = placeToReports.stream().filter(p -> p.getId().equals(criteria.getId())).collect(Collectors.toList());
        String formId = criteria.getFormId();
        String fileName = criteria.getFileName();
        if (formId != null && !formId.equals("")) {
            placeToReports = filterOnFormId(placeToReports, formId);
        } else {
            if (fileName != null && !fileName.equals("")) {
                placeToReports = filterOnFileName(placeToReports, fileName);
            }
        }
        String placeName = criteria.getPlaceName();
        if (placeName != null && !placeName.equals("")) {
            placeToReports = filterOnPlaceName(placeToReports, placeName);
        }
        List<PlaceToReport> sortedPlaces = new ArrayList<>();
        List<PlaceToReport> nullNamePlaces = new ArrayList<>();
        for (PlaceToReport place : placeToReports) {
            if (place.getPlaceName() != null) {
                sortedPlaces.add(place);
            } else {
                nullNamePlaces.add(place);
            }
        }
        sortedPlaces.sort((place1, place2) -> place1.getPlaceName().compareTo(place2.getPlaceName()));
        sortedPlaces.addAll(nullNamePlaces);
        return sortedPlaces;
    }

    private List<PlaceToReport> filterOnFormId(List<PlaceToReport> placeToReports, String formId) {
        List<PlaceToReport> filteredPlace = new ArrayList<>();
        for (PlaceToReport place : placeToReports) {
            if (place.getFormId() != null && place.getFormId().equals(formId)) {
                filteredPlace.add(place);
            }
        }
        return filteredPlace;
    }

    private List<PlaceToReport> filterOnPlaceName(List<PlaceToReport> placeToReports, String placeName) {
        List<PlaceToReport> filteredPlace = new ArrayList<>();
        for (PlaceToReport place : placeToReports) {
            if (place.getPlaceName() != null && place.getPlaceName().toLowerCase().contains(placeName.toLowerCase())) {
                filteredPlace.add(place);
            }
        }
        return filteredPlace;
    }

    private List<PlaceToReport> filterOnFileName(List<PlaceToReport> placeToReports, String fileName) {
        List<PlaceToReport> filteredPlace = new ArrayList<>();
        for (PlaceToReport place : placeToReports) {
            if (place.getFileName() != null && place.getFileName().equals(fileName) && place.getFormId() == null) {
                filteredPlace.add(place);
            }
        }
        return filteredPlace;
    }

    public void setReportN2oService(ReportN2oService reportN2oService) {
        this.reportN2oService = reportN2oService;
    }
}
