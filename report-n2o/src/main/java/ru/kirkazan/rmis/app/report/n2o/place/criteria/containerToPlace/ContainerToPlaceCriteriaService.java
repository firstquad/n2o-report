package ru.kirkazan.rmis.app.report.n2o.place.criteria.containerToPlace;

import net.n2oapp.criteria.api.CollectionPage;
import net.n2oapp.criteria.api.CollectionPageService;
import net.n2oapp.criteria.api.FilteredCollectionPage;
import net.n2oapp.framework.api.metadata.global.view.N2oPage;
import net.n2oapp.framework.config.register.ConfigRegister;
import net.n2oapp.framework.config.service.GlobalMetadataStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by dfirstov on 08.11.2014.
 */
public class ContainerToPlaceCriteriaService implements CollectionPageService<ContainerToPlaceCriteria, ContainerToPlace> {
    @Override
    public CollectionPage<ContainerToPlace> getCollectionPage(ContainerToPlaceCriteria criteria) {
        if (!ConfigRegister.getInstance().contains(criteria.getPageId(), N2oPage.class))
            return new FilteredCollectionPage<>(new ArrayList<ContainerToPlace>(), criteria);
        List<ContainerToPlace> containerToPlaces = new ArrayList<>();
        N2oPage page = GlobalMetadataStorage.getInstance().get(criteria.getPageId(), N2oPage.class);
        fillContainerToPlaces(containerToPlaces, page);
        containerToPlaces = filter(criteria, containerToPlaces);
        return new FilteredCollectionPage<>(new ArrayList<>(containerToPlaces), criteria);
    }

    private void fillContainerToPlaces(List<ContainerToPlace> containerToPlaces, N2oPage page) {
        if (page.getContainers() != null) {
            N2oPage.Container[] containers = page.getContainers();
            fillContainerToPlaces(containerToPlaces, page, containers);
        }
        if (page.getRegions() != null) {
            for (N2oPage.Region region : page.getRegions()) {
                if (region.getContainers() != null) {
                    N2oPage.Container[] containers = region.getContainers();
                    fillContainerToPlaces(containerToPlaces, page, containers);
                }
            }
        }
    }

    private void fillContainerToPlaces(List<ContainerToPlace> containerToPlaces, N2oPage page, N2oPage.Container[] containers) {
        for (N2oPage.Container container : containers) {
            String name = container.getId();
            if (container.getWidget() != null && container.getWidget().getName() != null && !container.getWidget().getName().equals("")) {
                name = container.getWidget().getName();
            }
            containerToPlaces.add(new ContainerToPlace(page.getId(), container.getId(), name));
        }
    }

    private List<ContainerToPlace> filter(ContainerToPlaceCriteria criteria, List<ContainerToPlace> containerToPlaces) {
        String Id = criteria.getId();
        if (Id != null && !Id.equals("")) {
            containerToPlaces = filterOnId(containerToPlaces, Id);
        }
        String pageId = criteria.getPageId();
        if (pageId != null && !pageId.equals("")) {
            containerToPlaces = filterOnPageId(containerToPlaces, pageId);
        }
        Collections.sort(containerToPlaces, new Comparator<ContainerToPlace>() {
            @Override
            public int compare(ContainerToPlace file1, ContainerToPlace file2) {
                return file1.getContainerId().compareTo(file2.getContainerId());
            }
        });
        return containerToPlaces;
    }

    private List<ContainerToPlace> filterOnId(List<ContainerToPlace> containerToPlaces, String id) {
        List<ContainerToPlace> filteredContainer = new ArrayList<>();
        for (ContainerToPlace container : containerToPlaces) {
            if (container.getContainerId().toLowerCase().contains(id.toLowerCase())) {
                filteredContainer.add(container);
            }
        }
        return filteredContainer;
    }

    private List<ContainerToPlace> filterOnPageId(List<ContainerToPlace> containerToPlaces, String pageId) {
        List<ContainerToPlace> filteredContainer = new ArrayList<>();
        for (ContainerToPlace container : containerToPlaces) {
            if (container.getPageId().equals(pageId)) {
                filteredContainer.add(container);
            }
        }
        return filteredContainer;
    }
}
