package ru.kirkazan.rmis.app.report.n2o.place.util;

import net.n2oapp.framework.config.service.GlobalMetadataStorage;
import ru.kirkazan.rmis.app.report.n2o.place.model.N2oReportPlace;

import java.util.*;

/**
 * Created by dfirstov on 26.12.2014.
 */
public class ReportPlaceUtil {

    public static List<ReportForm> retrieveAllReportForms() {
        List<N2oReportPlace> places = GlobalMetadataStorage.getInstance().getAll(N2oReportPlace.class);
        List<ReportForm> forms = new ArrayList<>();
        for (N2oReportPlace place : places) {
            for (N2oReportPlace.Report report : retrieveAllReports(place)) {
                forms.add(new ReportForm(place.getId(), report.getFormId(), report.getCode()));
            }
        }
        return forms;
    }

    public static List<String> retrieveAllPlaceFormIds() {
        List<N2oReportPlace> places = GlobalMetadataStorage.getInstance().getAll(N2oReportPlace.class);
        Set<String> forms = new HashSet<>();
        for (N2oReportPlace place : places) {
            forms.addAll(retrieveFormIds(place));
        }
        return new ArrayList<>(forms);
    }

    public static List<String> retrieveAllCodes() {
        List<N2oReportPlace> places = GlobalMetadataStorage.getInstance().getAll(N2oReportPlace.class);
        Set<String> codes = new HashSet<>();
        for (N2oReportPlace place : places) {
            codes.addAll(retrieveCodes(place));
        }
        return new ArrayList<>(codes);
    }

    public static List<String> retrieveFormIds(N2oReportPlace place) {
        return fillFormIds(retrieveAllReports(place));
    }

    private static List<String> fillFormIds(List<N2oReportPlace.Report> reports) {
        List<String> formIds = new ArrayList<>();
        if (reports != null) {
            for (N2oReportPlace.Report report : reports) {
                String formId = report.getFormId();
                if (formId != null) {
                    formIds.add(formId);
                }
            }
        }
        return formIds;
    }

    public static List<String> retrieveCodes(N2oReportPlace place) {
        return fillCodes(retrieveAllReports(place));
    }

    private static List<String> fillCodes(List<N2oReportPlace.Report> reports) {
        List<String> codes = new ArrayList<>();
        if (reports != null) {
            for (N2oReportPlace.Report report : reports) {
                String code = report.getCode();
                if (code != null) {
                    codes.add(code);
                }
            }
        }
        return codes;
    }

    public static List<N2oReportPlace.ContainerElement> retrieveContainers(N2oReportPlace place) {
        N2oReportPlace.ContainerElement[] containerElements = place.getContainerElements();
        if (containerElements == null)
            return new ArrayList<N2oReportPlace.ContainerElement>();
        return Arrays.asList(containerElements);
    }

    public static List<String> retrieveContainerIds(N2oReportPlace place) {
        N2oReportPlace.ContainerElement[] containerElements = place.getContainerElements();
        if (containerElements == null)
            return null;
        List<String> containerIds = new ArrayList<>();
        for (N2oReportPlace.ContainerElement containerElement : containerElements) {
            containerIds.add(containerElement.getId());
        }
        return containerIds;
    }

    public static List<N2oReportPlace.Report> retrieveAllReports() {
        List<N2oReportPlace.Report> reports = new ArrayList<>();
        List<N2oReportPlace> places = GlobalMetadataStorage.getInstance().getAll(N2oReportPlace.class);
        for (N2oReportPlace place: places) {
            reports.addAll(retrieveAllReports(place));
        }
        return reports;
    }

    public static List<N2oReportPlace.Report> retrieveAllReports(N2oReportPlace place) {
        return retrieveReportsFromPlace(place);
    }

    public static List<String> retrieveFormFieldIds(N2oReportPlace.Report report) {
        List<String> formFieldIds = new ArrayList<>();
        N2oReportPlace.Param[] reportParams = report.getParams();
        if (reportParams != null) {
            for (N2oReportPlace.Param param : reportParams) {
                formFieldIds.add(param.getFormFieldId());
            }
        }
        return formFieldIds;
    }

    public static List<String> retrieveFormRefIds(N2oReportPlace.Report report) {
        List<String> formRefIds = new ArrayList<>();
        N2oReportPlace.Param[] reportParams = report.getParams();
        if (reportParams != null) {
            for (N2oReportPlace.Param param : reportParams) {
                formRefIds.add(param.getRef());
            }
        }
        return formRefIds;
    }

    public static List<N2oReportPlace.Report> retrieveReportsFromContainer(N2oReportPlace.ContainerElement containerElement) {
        List<N2oReportPlace.Report> reports = new ArrayList<>();
        N2oReportPlace.ReportsElement reportsElement = containerElement.getReportsElement();
        if (reportsElement != null) {
            N2oReportPlace.Report[] reportsOnPlace = reportsElement.getReports();
            if (reportsOnPlace != null) {
                reports.addAll(Arrays.asList(reportsOnPlace));
            }
        }
        return reports;
    }

    private static List<N2oReportPlace.Report> retrieveReportsFromPlace(N2oReportPlace place) {
        List<N2oReportPlace.Report> reports = new ArrayList<>();
        fillFromReportElement(place, reports);
        fillFromContainerElement(place, reports);
        return reports;
    }

    private static void fillFromReportElement(N2oReportPlace place, List<N2oReportPlace.Report> reports) {
        N2oReportPlace.ReportsElement reportsElement = place.getReportsElement();
        if (reportsElement != null) {
            N2oReportPlace.Report[] reportsOnPlace = reportsElement.getReports();
            if (reportsOnPlace != null) {
                reports.addAll(Arrays.asList(reportsOnPlace));
            }
        }
    }

    private static void fillFromContainerElement(N2oReportPlace place, List<N2oReportPlace.Report> reports) {
        N2oReportPlace.ContainerElement[] placeContainerElements = place.getContainerElements();
        if (placeContainerElements != null) {
            for (N2oReportPlace.ContainerElement containerElement : placeContainerElements) {
                N2oReportPlace.ReportsElement reportsElement = containerElement.getReportsElement();
                if (reportsElement != null) {
                    N2oReportPlace.Report[] reportsOnPlace = reportsElement.getReports();
                    if (reportsOnPlace != null) {
                        reports.addAll(Arrays.asList(reportsOnPlace));
                    }
                }
            }
        }
    }

    public static class ReportForm {
        private String pageId;
        private String formId;
        private String code;

        public ReportForm(String pageId, String formId, String code) {
            this.pageId = pageId;
            this.formId = formId;
            this.code = code;
        }

        public String getPageId() {
            return pageId;
        }

        public void setPageId(String pageId) {
            this.pageId = pageId;
        }

        public String getFormId() {
            return formId;
        }

        public void setFormId(String formId) {
            this.formId = formId;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

}
