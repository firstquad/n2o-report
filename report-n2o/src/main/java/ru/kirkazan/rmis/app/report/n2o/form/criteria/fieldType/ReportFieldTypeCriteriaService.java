package ru.kirkazan.rmis.app.report.n2o.form.criteria.fieldType;

import net.n2oapp.criteria.api.CollectionPage;
import net.n2oapp.criteria.api.CollectionPageService;
import net.n2oapp.criteria.api.FilteredCollectionPage;
import net.n2oapp.framework.api.metadata.control.N2oField;
import net.n2oapp.framework.api.metadata.control.N2oHidden;
import net.n2oapp.framework.api.metadata.control.interval.N2oDateInterval;
import net.n2oapp.framework.api.metadata.control.list.N2oClassifier;
import net.n2oapp.framework.api.metadata.control.multi.N2oMultiClassifier;
import net.n2oapp.framework.api.metadata.control.plain.N2oCheckbox;
import net.n2oapp.framework.api.metadata.control.plain.N2oDatePicker;
import net.n2oapp.framework.api.metadata.control.plain.N2oInputText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.kirkazan.rmis.app.report.n2o.form.criteria.fieldType.ReportFieldTypeCriteriaService.Type.*;

/**
 * @author dfirstov
 * @since 05.10.2015
 */
public class ReportFieldTypeCriteriaService implements CollectionPageService<ReportFieldTypeCriteria, ReportFieldType> {
    private static final Logger logger = LoggerFactory.getLogger(ReportFieldTypeCriteriaService.class);

    @Override
    public CollectionPage<ReportFieldType> getCollectionPage(ReportFieldTypeCriteria reportFieldTypeCriteria) {
        List<ReportFieldType> types = new ArrayList<>();
        addType(types, INPUT_TEXT);
        addType(types, CHECKBOX);
        addType(types, DATE_TIME);
        addType(types, CLASSIFIER);
        addType(types, HIDDEN);
        types = doFilter(reportFieldTypeCriteria, types);
        return new FilteredCollectionPage<>(types, reportFieldTypeCriteria);
    }

    private List<ReportFieldType> doFilter(ReportFieldTypeCriteria reportFieldTypeCriteria, List<ReportFieldType> types) {
        if (reportFieldTypeCriteria.getId() != null)
            return types.stream().filter(t -> t.getId().equals(reportFieldTypeCriteria.getId())).collect(Collectors.toList());
        return types;
    }

    private void addType(List<ReportFieldType> types, Type type) {
        ReportFieldType fieldType = new ReportFieldType();
        fieldType.setId(type.id);
        fieldType.setName(type.name);
        types.add(fieldType);
    }

    public static enum Type {
        INPUT_TEXT("input-text", "Текстовое поле", N2oInputText.class.getName()),
        CHECKBOX("checkbox", "Чекбокс", N2oCheckbox.class.getName()),
        DATE_TIME("date-time", "Дата", N2oDatePicker.class.getName()),
        DATE_INTERVAL("date-interval", "Интервал", N2oDateInterval.class.getName()),
        CLASSIFIER("classifier", "Выпадающий список", N2oClassifier.class.getName()),
        MULTI_CLASSIFIER("multi-classifier", "Список со множественным выбором", N2oMultiClassifier.class.getName()),
        HIDDEN("hidden", "Скрытое поле", N2oHidden.class.getName());

        public String id;
        public String name;
        public String className;

        Type(String id, String name, String className) {
            this.id = id;
            this.name = name;
            this.className = className;
        }
    }


    public static String resolveTypeByClass(N2oField n2oFormField) {
        String className = n2oFormField.getClass().getName();
        for (Type type : Type.values()) {
            if (type.className.equals(className))
                return type.id;
        }
        return "";
    }

    public static String resolveTypeNameById(String typeId) {
        for (Type type : Type.values()) {
            if (type.id.equals(typeId))
                return type.name;
        }
        return "";
    }

    public static Class<? extends N2oField> resolveTypeClassById(String typeId) {
        for (Type type : Type.values()) {
            if (type.id.equals(typeId))
                return resolveClass(type.className);
        }
        return N2oInputText.class;
    }


    @SuppressWarnings("unchecked")
    private static Class<? extends N2oField> resolveClass(String className) {
        try {
            return (Class<? extends N2oField>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            logger.warn(e.getMessage(), e);
            return null;
        }
    }
}
