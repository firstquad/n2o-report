package ru.kirkazan.rmis.app.report.n2o.place.criteria.fieldToContainer;

import net.n2oapp.criteria.api.CollectionPage;
import net.n2oapp.criteria.api.CollectionPageService;
import net.n2oapp.criteria.api.FilteredCollectionPage;
import net.n2oapp.framework.api.metadata.global.dao.N2oQuery;
import net.n2oapp.framework.api.metadata.global.view.N2oPage;
import net.n2oapp.framework.config.register.ConfigRegister;
import net.n2oapp.framework.config.service.GlobalMetadataStorage;
import ru.kirkazan.rmis.app.report.n2o.form.util.ReportFormUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dfirstov on 13.11.2014.
 */
public class FieldToContainerCriteriaService implements CollectionPageService<FieldToContainerCriteria, FieldToContainer> {
    @Override
    public CollectionPage<FieldToContainer> getCollectionPage(FieldToContainerCriteria criteria) {
        List<FieldToContainer> fieldToContainers = new ArrayList<>();
        CollectionPage<FieldToContainer> collectionPage = new FilteredCollectionPage<>(fieldToContainers, criteria);
        String placeId = criteria.getPlaceId();
        String containerId = criteria.getContainerId();
        if (placeId == null || !ConfigRegister.getInstance().contains(placeId, N2oPage.class))
            return collectionPage;
        N2oPage page = GlobalMetadataStorage.getInstance().get(placeId, N2oPage.class);
        N2oPage.Container[] containers = page.getContainers();
        if (containers == null) {
            checkRegions(fieldToContainers, placeId, containerId, page);
        } else {
            fillFieldToContainers(fieldToContainers, placeId, containerId, containers);
        }
        fieldToContainers = filterOnLabel(fieldToContainers, criteria.getLabel());
        return new FilteredCollectionPage<>(fieldToContainers, criteria);
    }

    private void checkRegions(List<FieldToContainer> fieldToContainers, String placeId, String containerId, N2oPage page) {
        N2oPage.Region[] regions = page.getRegions();
        if (regions != null) {
            for (N2oPage.Region region : regions) {
                N2oPage.Container[] regionContainers = region.getContainers();
                if (regionContainers != null) {
                    fillFieldToContainers(fieldToContainers, placeId, containerId, regionContainers);
                }
            }
        }
    }

    private void fillFieldToContainers(List<FieldToContainer> fieldToContainers, String placeId, String containerId, N2oPage.Container[] containers) {
        for (N2oPage.Container container : containers) {
            if (container.getId().equals(containerId)) {
                String queryId = ReportFormUtil.retrieveQueryId(container.getWidget());
                if (ConfigRegister.getInstance().contains(queryId, N2oQuery.class)) {
                    fillFieldToContainers(fieldToContainers, placeId, containerId, queryId);
                }
            }
        }
    }

    private void fillFieldToContainers(List<FieldToContainer> fieldToContainers, String placeId, String containerId, String queryId) {
        N2oQuery query = GlobalMetadataStorage.getInstance().get(queryId, N2oQuery.class);
        N2oQuery.Field[] queryFields = query.getFields();
        if (queryFields != null) {
            for (N2oQuery.Field field : queryFields) {
                FieldToContainer fieldToContainer = new FieldToContainer();
                fieldToContainer.setFieldId(field.getId());
                String label;
                if (field.getName() != null) {
                    label = field.getName();
                } else {
                    label = field.getId();
                }
                fieldToContainer.setLabel(label);
                fieldToContainer.setContainerId(containerId);
                fieldToContainer.setPlaceId(placeId);
                fieldToContainers.add(fieldToContainer);
            }
        }
    }

    private List<FieldToContainer> filterOnLabel(List<FieldToContainer> fieldToContainers, String label) {
        if (label == null || label.equals(""))
            return fieldToContainers;
        List<FieldToContainer> filteredResult = new ArrayList<>();
        for (FieldToContainer fieldToContainer : fieldToContainers) {
            if (fieldToContainer.getLabel() != null && fieldToContainer.getLabel().toLowerCase().contains(label.toLowerCase())) {
                filteredResult.add(fieldToContainer);
            }
        }
        return filteredResult;
    }
}
