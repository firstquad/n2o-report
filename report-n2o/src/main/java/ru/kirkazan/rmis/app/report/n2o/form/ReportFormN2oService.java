package ru.kirkazan.rmis.app.report.n2o.form;

import ru.kirkazan.rmis.app.report.n2o.api.service.ReportDAO;
import ru.kirkazan.rmis.app.report.n2o.file.ReportFile;
import ru.kirkazan.rmis.app.report.n2o.file.ReportFileCriteria;
import ru.kirkazan.rmis.app.report.n2o.file.ReportFileService;
import ru.kirkazan.rmis.app.report.n2o.impl.parser.ReportParam;
import ru.kirkazan.rmis.app.report.n2o.impl.parser.ReportParser;

import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * @author dfirstov
 * @since 25.06.2015
 */
public class ReportFormN2oService {
    private ReportFileService fileCriteriaService;
    private ReportDAO reportDAO;
    private Properties properties;

    public void regenerateForm(int reportId, String reportName, String fileName, String formId) {
        ReportFileCriteria criteria = new ReportFileCriteria();
        criteria.setId(fileName);
        criteria.setFilterDouble(true);
        List<ReportFile> files = fileCriteriaService.retrieveFiles(criteria);
        if (files.size() == 0)
            throw new RuntimeException(String.format("Не найден файл %s", fileName));
        if (files.size() > 1)
            throw new RuntimeException(String.format("Найдены два или более файлов %s", fileName));
        String path = files.get(0).getPath();
        List<ReportParam> reportParams = ReportParser.getInstance().parseParams(new File(path));
        if (reportParams == null)
            throw new RuntimeException(String.format("Не найдены параметры в файле отчета %s", fileName));
        new ReportFormService().createFormByReport(reportParams, formId, fileName, reportName, properties.getProperty("rmis.report.form.path"));
        reportDAO.updateReportForm(formId, reportId);
    }

    public void setFileCriteriaService(ReportFileService fileCriteriaService) {
        this.fileCriteriaService = fileCriteriaService;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setReportDAO(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }
}
