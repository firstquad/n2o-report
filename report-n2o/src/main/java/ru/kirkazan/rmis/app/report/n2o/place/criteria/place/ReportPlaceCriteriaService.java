package ru.kirkazan.rmis.app.report.n2o.place.criteria.place;


import net.n2oapp.criteria.api.CollectionPage;
import net.n2oapp.criteria.api.CollectionPageService;
import net.n2oapp.criteria.api.FilteredCollectionPage;
import net.n2oapp.framework.config.service.MetadataStorage;
import net.n2oapp.framework.standard.header.model.local.CompiledHeaderModule;
import net.n2oapp.framework.standard.header.model.local.CompiledStandardHeader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * Created by dfirstov on 12.09.2014.
 */
public class ReportPlaceCriteriaService implements CollectionPageService<ReportPlaceCriteria, ReportPlace> {
    private static final String MODULE_GROUP_ICON = "icon-th-large";
    private static final String MODULE_ICON = "icon-home";
    private static final String SPACE_ICON = "icon-th-list";
    private static final String PAGE_ICON = "icon-file";

    private Properties properties;

    @Override
    public CollectionPage<ReportPlace> getCollectionPage(ReportPlaceCriteria criteria) {
        List<ReportPlace> places = new ArrayList<>();
        String headerId = properties.getProperty("n2o.ui.header.id");
        String criteriaId = criteria.getId();
        String criteriaName = criteria.getName();
        CompiledStandardHeader header = MetadataStorage.getInstance().get(headerId, CompiledStandardHeader.class);
        for (CompiledStandardHeader.ModuleGroup moduleGroup : header.getModuleGroups()) {
            createModuleGroup(places, moduleGroup);
        }
        places = doFilter(places, criteriaId, criteriaName);
//        places.sort((place1, place2) -> place1.getName().compareTo(place2.getName()));
        return new FilteredCollectionPage<>(places, criteria);
    }

    private void createModuleGroup(List<ReportPlace> places, CompiledStandardHeader.ModuleGroup moduleGroup) {
        String moduleGroupId = moduleGroup.getId();
        String moduleGroupName = moduleGroup.getName() == null ? "Прочее" : moduleGroup.getName();
        Collection<CompiledHeaderModule> modules = moduleGroup.getModules();
        boolean hasChildren = resolveHasChildren(modules);
        places.add(new ReportPlace(moduleGroupId, moduleGroupId, moduleGroupName, null, hasChildren, false, MODULE_GROUP_ICON));
        if (!hasChildren)
            return;
        for (CompiledHeaderModule module : modules) {
            createModule(places, moduleGroupId, module);
        }
    }

    private void createModule(List<ReportPlace> places, String moduleGroupId, CompiledHeaderModule module) {
        String moduleId = module.getId();
        Collection<CompiledHeaderModule.Item> items = module.getItems();
        boolean hasChildren = resolveHasChildren(items);
        places.add(new ReportPlace(moduleId, moduleId, module.getName(), moduleGroupId, hasChildren, false, MODULE_ICON));
        if (!hasChildren)
            return;
        for (CompiledHeaderModule.Item item : items) {
            if (item instanceof CompiledHeaderModule.Page) {
                createPage(places, moduleId, (CompiledHeaderModule.Page) item);
            } else if (item instanceof CompiledHeaderModule.Space) {
                createSpace(places, moduleId, (CompiledHeaderModule.Space) item);
            }
        }
    }

    private void createSpace(List<ReportPlace> places, String moduleId, CompiledHeaderModule.Space space) {
        String spaceId = moduleId + "." + space.getId();
        Collection<CompiledHeaderModule.Page> pages = space.getPages();
        boolean hasChildren = resolveHasChildren(pages);
        places.add(new ReportPlace(spaceId, spaceId, space.getName(), moduleId, hasChildren, false, SPACE_ICON));
        if (!hasChildren)
            return;
        for (CompiledHeaderModule.Page page : pages) {
            createPage(places, spaceId, page);
        }
    }

    private boolean resolveHasChildren(Collection collection) {
        return collection != null && collection.size() != 0;
    }

    private void createPage(List<ReportPlace> places, String parentId, CompiledHeaderModule.Page page) {
        if (isExclude(page)) {
            return;
        }
        places.add(new ReportPlace(page.getId(), page.getSourcePageId(), page.getName(), parentId, false, true, PAGE_ICON));
    }

    private boolean isExclude(CompiledHeaderModule.Page page) {
        return page.getSourcePageId().equals("reportModule");
    }

    private List<ReportPlace> doFilter(List<ReportPlace> places, String criteriaId, String criteriaName) {
        if (criteriaId != null && !criteriaId.equals("")) {
            places = getFilterOnId(places, criteriaId);
        }
        if (criteriaName != null && !criteriaName.equals("")) {
            places = getFilterOnName(places, criteriaName);
        }
        return places;
    }

    private List<ReportPlace> getFilterOnName(List<ReportPlace> places, String criteriaName) {
        List<ReportPlace> filteredPlace = new ArrayList<>();
        for (ReportPlace place : places) {
            if (place.getName().toLowerCase().contains(criteriaName.toLowerCase())) {
                filteredPlace.add(place);
            }
        }
        return filteredPlace;
    }

    private List<ReportPlace> getFilterOnId(List<ReportPlace> places, String criteriaId) {
        List<ReportPlace> filteredPlace = new ArrayList<>();
        for (ReportPlace place : places) {
            if (place.getPageId().equals(criteriaId)) {
                filteredPlace.add(place);
            }
        }
        return filteredPlace;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
