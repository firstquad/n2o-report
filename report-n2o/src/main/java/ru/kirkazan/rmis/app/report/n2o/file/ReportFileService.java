package ru.kirkazan.rmis.app.report.n2o.file;

import net.n2oapp.criteria.api.CollectionPage;
import net.n2oapp.criteria.api.CollectionPageService;
import net.n2oapp.criteria.api.FilteredCollectionPage;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import ru.kirkazan.rmis.app.report.n2o.api.service.ReportDAO;
import ru.kirkazan.rmis.app.report.n2o.report.ReportN2oService;

import java.io.IOException;
import java.util.*;

/**
 * Created by dfirstov on 12.09.2014.
 */
public class ReportFileService implements CollectionPageService<ReportFileCriteria, ReportFile> {
    private Properties properties;
    private PathMatchingResourcePatternResolver pathPatternResolver = new PathMatchingResourcePatternResolver();
    private ReportN2oService reportN2oService;
    private ReportDAO reportDAO;

    @Override
    public CollectionPage<ReportFile> getCollectionPage(ReportFileCriteria criteria) {
        List<ReportFile> files = retrieveFiles(criteria);
        return new FilteredCollectionPage<>(files, criteria);
    }

    public List<ReportFile> retrieveFiles(ReportFileCriteria criteria) {
        String path = properties.getProperty("rmis.report.rptdesign.path");
        if (!path.startsWith("/"))
            path = "/" + path;
        List<String> reports = new ArrayList<>();
        if (criteria.getFilterDouble() == null || !criteria.getFilterDouble())
            reports = reportDAO.retrieveAllReportFileNames();
        List<String> hideFormReports = reportDAO.retrieveHideFormReportFileNames();
        List<ReportFile> files = extractFiles(path, reports, hideFormReports);
        String criteriaId = criteria.getId();
        String criteriaName = criteria.getName();
        files = getFilter(files, criteriaId, criteriaName);
        return files;
    }

    private List<ReportFile> extractFiles(String path, List<String> reports, List<String> hideFormReports) {
        List<ReportFile> files = new ArrayList<>();
        try {
            Resource[] resources = pathPatternResolver.getResources("file:" + path + "/**/*.rptdesign");
            Set<Resource> resourceSet = new HashSet<>();
            Collections.addAll(resourceSet, resources);
            for (Resource resource : resourceSet) {
                String fileName = reportN2oService.getSubDirectory(resource.getFilename(), resource.getFile().getPath()) + resource.getFilename();
                if (!reports.contains(fileName)) {
                    files.add(new ReportFile(fileName, fileName, resource.getFile().getAbsolutePath(), hideFormReports.contains(fileName)));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return files;
    }

    private List<ReportFile> getFilter(List<ReportFile> files, String criteriaId, String criteriaName) {
        if (criteriaId != null && !criteriaId.equals("")) {
            files = getFilterOnId(files, criteriaId);
        }
        if (criteriaName != null && !criteriaName.equals("")) {
            files = getFilterOnName(files, criteriaName);
        }
        Collections.sort(files, new Comparator<ReportFile>() {
            @Override
            public int compare(ReportFile file1, ReportFile file2) {
                return file1.getId().compareTo(file2.getId());
            }
        });
        return files;
    }

    private List<ReportFile> getFilterOnId(List<ReportFile> files, String criteriaId) {
        List<ReportFile> filteredFiles = new ArrayList<>();
        for (ReportFile file : files) {
            if (file.getId().equals(criteriaId)) {
                filteredFiles.add(file);
            }
        }
        return filteredFiles;
    }

    private List<ReportFile> getFilterOnName(List<ReportFile> files, String criteriaName) {
        List<ReportFile> filteredFiles = new ArrayList<>();
        for (ReportFile file : files) {
            if (file.getId().toLowerCase().contains(criteriaName.toLowerCase())) {
                filteredFiles.add(file);
            }
        }
        return filteredFiles;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setReportN2oService(ReportN2oService reportN2oService) {
        this.reportN2oService = reportN2oService;
    }

    public void setReportDAO(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }
}
