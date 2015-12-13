package ru.kirkazan.rmis.app.report.n2o.report.criteria.reportOutGroup;

import net.n2oapp.criteria.api.CollectionPage;
import net.n2oapp.criteria.api.CollectionPageService;
import net.n2oapp.criteria.api.FilteredCollectionPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kirkazan.rmis.app.report.n2o.api.model.ReportToGroup;
import ru.kirkazan.rmis.app.report.n2o.api.service.ReportDAO;

import java.util.List;

import static ru.kirkazan.rmis.app.report.n2o.report.criteria.reportToGroup.ReportToGroupCriteriaService.fillAdditionalFields;
import static ru.kirkazan.rmis.app.report.n2o.report.criteria.reportToGroup.ReportToGroupCriteriaService.removeInvalid;

/**
 * @author dfirstov
 * @since 07.10.2015
 */
public class ReportOutGroupCriteriaService implements CollectionPageService<ReportOutGroupCriteria, ReportToGroup> {
    private ReportDAO reportDAO;
    private static final Logger logger = LoggerFactory.getLogger(ReportOutGroupCriteriaService.class);

    @Override
    public CollectionPage<ReportToGroup> getCollectionPage(ReportOutGroupCriteria criteria) {
        List<ReportToGroup> reports = reportDAO.retrieveReportOutGroup(criteria.getGroupId());
        fillAdditionalFields(reports);
        return new FilteredCollectionPage<>((doFilter(reports, criteria)), criteria);
    }

    private List<ReportToGroup> doFilter(List<ReportToGroup> reports, ReportOutGroupCriteria criteria) {
        reports = removeInvalid(reports);
        return reports;
    }

    public void setReportDAO(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }
}
