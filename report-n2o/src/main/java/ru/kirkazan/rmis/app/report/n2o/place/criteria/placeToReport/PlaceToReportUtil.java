package ru.kirkazan.rmis.app.report.n2o.place.criteria.placeToReport;

import net.n2oapp.framework.api.metadata.global.view.N2oPage;
import net.n2oapp.framework.config.register.ConfigRegister;
import net.n2oapp.framework.config.service.GlobalMetadataStorage;
import net.n2oapp.framework.standard.header.model.local.CompiledHeaderModule;
import net.n2oapp.framework.standard.header.model.local.CompiledStandardHeader;
import ru.kirkazan.rmis.app.report.n2o.place.model.N2oReportPlace;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author dfirstov
 * @since 30.07.2015
 */
public class PlaceToReportUtil {
    private static final String SEPARATOR = " > ";

    public static String resolvePlaceName(CompiledStandardHeader header, N2oReportPlace place) {
        String placeName = "";
        Map<String, CompiledHeaderModule.Page> pageMap = header.getPageMap();
        List<CompiledHeaderModule.Page> modulePages = pageMap.values().stream()
                .filter(page -> page.getSourcePageId().equals(place.getId())).collect(Collectors.toList());
        if (modulePages != null && modulePages.size() > 0) {
            CompiledHeaderModule.Page page = modulePages.get(0);
            place.setRealId(page.getId());
            CompiledHeaderModule module = header.getModule(page.getModuleId());
            placeName += module.getName() + SEPARATOR;
            placeName = fillFromModuleSpace(place.getId(), placeName, module);
            if (placeName.endsWith(SEPARATOR)) {
                placeName = fillFromModuleOrPage(place.getId(), placeName, module);
            }
        }
        return placeName;
    }

    private static String fillFromModuleSpace(String placeId, String placeName, CompiledHeaderModule module) {
        List<CompiledHeaderModule.Item> spaces = module.getItems().stream()
                .filter(item -> item instanceof CompiledHeaderModule.Space).collect(Collectors.toList());
        final String[] spacePageName = {""};
        List<CompiledHeaderModule.Item> targetSpaces = spaces.stream()
                .filter(findTargetPage(placeId, spacePageName)).collect(Collectors.toList());
        if (targetSpaces.size() > 0) {
            placeName += targetSpaces.get(0).getName() + SEPARATOR;
            if (!spacePageName[0].equals("")) {
                placeName += spacePageName[0];
            }
        }
        return placeName;
    }

    private static Predicate<CompiledHeaderModule.Item> findTargetPage(String placeId, String[] spacePageName) {
        return space -> ((CompiledHeaderModule.Space) space).getPages().stream().anyMatch(page -> {
            boolean equals = page.getSourcePageId().equals(placeId);
            if (equals)
                spacePageName[0] = page.getName();
            return equals;
        });
    }

    private static String fillFromModuleOrPage(String placeId, String placeName, CompiledHeaderModule module) {
        List<CompiledHeaderModule.Page> pages = module.getAllPages().stream()
                .filter(page -> page.getSourcePageId().equals(placeId)).collect(Collectors.toList());
        if (pages.size() > 0) {
            return placeName + pages.get(0).getName();
        }
        if (ConfigRegister.getInstance().contains(placeId, N2oPage.class)) {
            N2oPage page = GlobalMetadataStorage.getInstance().get(placeId, N2oPage.class);
            if (page.getName() != null)
                placeName += page.getName();
            else
                placeName += page.getId();
        }
        return placeName;
    }
}
