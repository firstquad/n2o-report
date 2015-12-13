package ru.kirkazan.rmis.app.report.n2o.report.criteria.reportToGroup;

import net.n2oapp.criteria.api.CollectionPage;
import net.n2oapp.criteria.api.CollectionPageService;
import net.n2oapp.criteria.api.FilteredCollectionPage;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oForm;
import net.n2oapp.framework.config.register.ConfigRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kirkazan.rmis.app.report.n2o.api.model.Report;
import ru.kirkazan.rmis.app.report.n2o.api.model.ReportToGroup;
import ru.kirkazan.rmis.app.report.n2o.api.service.ReportDAO;
import ru.kirkazan.rmis.app.report.n2o.report.criteria.report.ReportCriteriaService;

import java.util.*;

/**
 * Created by dfirstov on 07.11.2014.
 */
public class ReportToGroupCriteriaService implements CollectionPageService<ReportToGroupCriteria, ReportToGroup> {
    private ReportDAO reportDAO;
    private static final Logger logger = LoggerFactory.getLogger(ReportToGroupCriteriaService.class);

    @Override
    public CollectionPage<ReportToGroup> getCollectionPage(ReportToGroupCriteria criteria) {
        List<ReportToGroup> reports = reportDAO.retrieveReportToGroup();
        fillAdditionalFields(reports);
        return new FilteredCollectionPage<>((doFilter(reports, criteria)), criteria);
    }

    public static void fillAdditionalFields(List<ReportToGroup> reports) {
        for (Report report : new ArrayList<>(reports)) {
            ConfigRegister configRegister = ConfigRegister.getInstance();
            if (report.getFormId() != null) {
                if (configRegister.contains(report.getFormId(), N2oForm.class)) {
                    try {
                        ReportCriteriaService.fillFromForm(report);
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

    private static void fillNameFromFileName(Report report) {
        if (report.getName() == null || "".equals(report.getName())) {
            report.setName(report.getFileName());
        }
    }

    private List<ReportToGroup> doFilter(List<ReportToGroup> reports, ReportToGroupCriteria criteria) {
        reports = removeInvalid(reports);
        reports = filterOnGroup(reports, criteria);
        return reports;
    }

    private List<ReportToGroup> filterOnGroup(List<ReportToGroup> reportToGroups, ReportToGroupCriteria criteria) {
        if (criteria == null || criteria.getGroupId() == null)
            return reportToGroups;
        List<ReportToGroup> filteredFiles = new ArrayList<>();
        for (ReportToGroup reportToGroup : reportToGroups) {
            if (Objects.equals(reportToGroup.getGroupId(), criteria.getGroupId())) {
                filteredFiles.add(reportToGroup);
            }
        }
        return filteredFiles;
    }

    public static List<ReportToGroup> removeInvalid(List<ReportToGroup> reportToGroups) {
        List<ReportToGroup> filteredFiles = new ArrayList<>();
        for (ReportToGroup reportToGroup : reportToGroups) {
            if (reportToGroup.getIsInvalid() == null || !reportToGroup.getIsInvalid()) {
                filteredFiles.add(reportToGroup);
            }
        }
        return filteredFiles;
    }

    public void setReportDAO(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }
}
