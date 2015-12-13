package ru.kirkazan.rmis.app.report.n2o.form.criteria.classifier;

import net.n2oapp.criteria.api.CollectionPage;
import net.n2oapp.criteria.api.CollectionPageService;
import net.n2oapp.criteria.api.FilteredCollectionPage;
import net.n2oapp.framework.api.metadata.global.dao.N2oQuery;
import net.n2oapp.framework.config.register.ConfigRegister;
import net.n2oapp.framework.config.register.Origin;
import net.n2oapp.framework.config.service.GlobalMetadataStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author dfirstov
 * @since 05.10.2015
 */
public class ReportClassifierCriteriaService implements CollectionPageService<ReportClassifierCriteria, ReportClassifier> {
    @Override
    public CollectionPage<ReportClassifier> getCollectionPage(ReportClassifierCriteria reportFieldCriteria) {
        List<ReportClassifier> classifiers = new ArrayList<>();
        Set<String> ids = ConfigRegister.getInstance().getIds(N2oQuery.class, (i) -> (i.getURI() != null && (i.getURI().contains("report/classifier/"))
                && !Origin.compile.equals(i.getOrigin())));
        for (String id : ids) {
            N2oQuery query = GlobalMetadataStorage.getInstance().get(id, N2oQuery.class);
            ReportClassifier classifier = new ReportClassifier();
            classifier.setId(query.getId());
            classifier.setName(query.getName());
            classifiers.add(classifier);
        }
        classifiers = doFilter(reportFieldCriteria, classifiers);
        return new FilteredCollectionPage<>(classifiers, reportFieldCriteria);
    }

    private List<ReportClassifier> doFilter(ReportClassifierCriteria reportFieldCriteria, List<ReportClassifier> classifiers) {
        if (reportFieldCriteria.getId() != null)
            return classifiers.stream().filter(t -> t.getId().equals(reportFieldCriteria.getId())).collect(Collectors.toList());
        return classifiers;
    }

}
