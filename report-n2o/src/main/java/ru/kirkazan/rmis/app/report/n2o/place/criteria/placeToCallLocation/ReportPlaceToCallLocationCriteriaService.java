package ru.kirkazan.rmis.app.report.n2o.place.criteria.placeToCallLocation;

import net.n2oapp.criteria.api.CollectionPage;
import net.n2oapp.criteria.api.CollectionPageService;
import net.n2oapp.criteria.api.FilteredCollectionPage;
import net.n2oapp.framework.api.metadata.global.view.N2oPage;
import net.n2oapp.framework.config.register.ConfigRegister;
import net.n2oapp.framework.config.service.GlobalMetadataStorage;
import net.n2oapp.framework.config.service.MetadataStorage;
import net.n2oapp.framework.standard.header.model.local.CompiledStandardHeader;
import net.n2oapp.properties.StaticProperties;
import ru.kirkazan.rmis.app.report.n2o.place.model.N2oReportPlace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.kirkazan.rmis.app.report.n2o.place.criteria.placeToReport.PlaceToReportUtil.resolvePlaceName;

/**
 * Created by dfirstov on 02.11.2014.
 */
public class ReportPlaceToCallLocationCriteriaService implements CollectionPageService<ReportPlaceToCallLocationCriteria, ReportPlaceToCallLocation> {

    @Override
    public CollectionPage<ReportPlaceToCallLocation> getCollectionPage(ReportPlaceToCallLocationCriteria criteria) {
        List<ReportPlaceToCallLocation> placeToReports = new ArrayList<>();
        List<N2oReportPlace> places = GlobalMetadataStorage.getInstance().getAll(N2oReportPlace.class, false);
        String headerId = StaticProperties.get("n2o.ui.header.id");
        CompiledStandardHeader header = MetadataStorage.getInstance().get(headerId, CompiledStandardHeader.class);
        for (N2oReportPlace place : places) {
            place.setName(resolvePlaceName(header, place));
            fillPlaceToReports(placeToReports, place);
        }
        List<ReportPlaceToCallLocation> list = filter(criteria, placeToReports);
        return new FilteredCollectionPage<>(list, criteria);
    }

    private void fillPlaceToReports(List<ReportPlaceToCallLocation> placeToReports, N2oReportPlace place) {
        if (place.getReportsElement() != null && place.getReportsElement().getReports() != null) {
            setPlace(placeToReports, place);
        }
        if (place.getContainerElements() != null) {
            for (N2oReportPlace.ContainerElement containerElement : place.getContainerElements()) {
                if (containerElement.getReportsElement() != null && containerElement.getReportsElement().getReports() != null) {
                    String containerElementId = containerElement.getId();
                    String containerName = fillContainerName(place, containerElementId);
                    setPlaceToContainer(placeToReports, place, containerElementId, containerName);
                }
            }
        }
    }

    private List<ReportPlaceToCallLocation> filter(ReportPlaceToCallLocationCriteria criteria, List<ReportPlaceToCallLocation> reportPlaces) {
        if (criteria.getPlaceName() != null)
            reportPlaces = reportPlaces.stream().filter(p -> p.getPlaceName().toLowerCase().contains(criteria.getPlaceName().toLowerCase())).collect(Collectors.toList());
        if (criteria.getIsInvalid() != null)
            reportPlaces = reportPlaces.stream().filter(p -> p.getIsInvalid().equals(criteria.getIsInvalid())).collect(Collectors.toList());
        List<ReportPlaceToCallLocation> sortedPlaces = new ArrayList<>();
        List<ReportPlaceToCallLocation> nullNamePlaces = new ArrayList<>();
        for (ReportPlaceToCallLocation place : reportPlaces) {
            if (place.getPlaceName() != null) {
                sortedPlaces.add(place);
            } else {
                nullNamePlaces.add(place);
            }
        }
        Collections.sort(sortedPlaces, (place1, place2) -> place1.getPlaceName().compareTo(place2.getPlaceName()));
        sortedPlaces.addAll(nullNamePlaces);
        return sortedPlaces;
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

    private void setPlace(List<ReportPlaceToCallLocation> reportPlaces, N2oReportPlace place) {
        String placeId = place.getId();
        if (!ConfigRegister.getInstance().contains(placeId, N2oPage.class))
            return;
        ReportPlaceToCallLocation reportPlace = new ReportPlaceToCallLocation();
        reportPlace.setId(placeId);
        reportPlace.setPlaceId(placeId);
        reportPlace.setRealId(place.getRealId());
        reportPlace.setPlaceName(place.getName());
        reportPlace.setReportContainerName("Меню страницы");
        reportPlace.setIsInvalid(place.isInvalid());
        reportPlace.setInvalidMessage(place.getInvalidMessage());
        reportPlaces.add(reportPlace);
    }

    private void setPlaceToContainer(List<ReportPlaceToCallLocation> reportPlaces, N2oReportPlace place, String containerId, String containerName) {
        String placeId = place.getId();
        if (!ConfigRegister.getInstance().contains(placeId, N2oPage.class))
            return;
        ReportPlaceToCallLocation reportPlace = new ReportPlaceToCallLocation();
        reportPlace.setId(placeId + containerId);
        reportPlace.setPlaceId(placeId);
        reportPlace.setRealId(place.getRealId());
        reportPlace.setPlaceName(place.getName());
        reportPlace.setReportContainerId(containerId);
        reportPlace.setReportContainerName(containerName);
        reportPlace.setIsInvalid(place.isInvalid());
        reportPlace.setInvalidMessage(place.getInvalidMessage());
        reportPlaces.add(reportPlace);
    }

}
