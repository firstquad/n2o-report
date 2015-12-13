package ru.kirkazan.rmis.app.report.n2o.report.criteria.report;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by dfirstov on 07.11.2014.
 */
public class ReportCriteriaService implements CollectionPageService<ReportCriteria, Report> {
    private ReportDAO reportDAO;
    private static final Logger logger = LoggerFactory.getLogger(ReportCriteriaService.class);

    @Override
    public CollectionPage<Report> getCollectionPage(ReportCriteria criteria) {
        List<Report> reports = reportDAO.retrieveReports();
        fillAdditionalFields(reports);
        return new FilteredCollectionPage<>((doFilter(reports, criteria)), criteria);
    }

    private void fillAdditionalFields(List<Report> reports) {
        for (Report report : reports) {
            ConfigRegister configRegister = ConfigRegister.getInstance();
            if (report.getFormId() != null) {
                if (configRegister.contains(report.getFormId(), N2oForm.class)) {
                    try {
                        fillFromForm(report);
                    } catch (Exception e) {
                        logger.warn(e.getMessage(), e);
                        report.setIsInvalid(true);
                        report.setInvalidMessage(e.getMessage());
                    }
                } else {
                    report.setIsInvalid(true);
                    report.setInvalidMessage("Форма [" + report.getFormId() + "] не найдена в системе");
                    report.setN2oFormId(null);
                    fillNameFromFileName(report);
                }
            } else {
                fillNameFromFileName(report);
            }
        }
    }

    private void fillNameFromFileName(Report report) {
        if (report.getName() == null || "".equals(report.getName())) {
            report.setName(report.getFileName());
        }
    }

    public static void fillFromForm(Report report) {
        N2oForm form = GlobalMetadataStorage.getInstance().get(report.getFormId(), N2oForm.class);
        report.setIsInvalid(form.isInvalid());
        report.setInvalidMessage(form.getInvalidMessage());
        if (report.getName() == null || "".equals(report.getName())) {
            report.setName(form.getName());
        }
    }

    private List<Report> doFilter(List<Report> reports, ReportCriteria criteria) {
        reports = filterOnId(reports, criteria);
        reports = filterOnFileName(reports, criteria);
        reports = filterOnName(reports, criteria);
        return sort(reports);
    }

    private List<Report> filterOnId(List<Report> files, ReportCriteria criteria) {
        if (criteria == null || criteria.getId() == null)
            return files;
        List<Report> filteredFiles = new ArrayList<>();
        for (Report file : files) {
            if (file.getId() != null && file.getId().equals(criteria.getId())) {
                filteredFiles.add(file);
            }
        }
        return filteredFiles;
    }

    private List<Report> filterOnName(List<Report> files, ReportCriteria criteria) {
        if (criteria == null || criteria.getName() == null || criteria.getName().equals(""))
            return files;
        List<Report> filteredFiles = new ArrayList<>();
        for (Report file : files) {
            if (file.getName() != null && file.getName().toLowerCase().contains(criteria.getName().toLowerCase())) {
                filteredFiles.add(file);
            }
        }
        return filteredFiles;
    }

    private List<Report> filterOnFileName(List<Report> files, ReportCriteria criteria) {
        if (criteria == null || criteria.getFileName() == null || criteria.getFileName().equals(""))
            return files;
        List<Report> filteredFiles = new ArrayList<>();
        for (Report file : files) {
            if (file.getFileName().toLowerCase().contains(criteria.getFileName().toLowerCase())) {
                filteredFiles.add(file);
            }
        }
        return filteredFiles;
    }

    private List<Report> sort(List<Report> files) {
        List<Report> reports = new ArrayList<>(files);
        Collections.sort(reports, new Comparator<Report>() {
            @Override
            public int compare(Report file1, Report file2) {
                return file1.getId().compareTo(file2.getId());
            }
        });
        return reports;
    }

    public void setReportDAO(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }
}
