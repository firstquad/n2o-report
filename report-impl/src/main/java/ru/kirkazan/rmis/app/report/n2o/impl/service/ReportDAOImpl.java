package ru.kirkazan.rmis.app.report.n2o.impl.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.kirkazan.rmis.app.report.n2o.api.model.Report;
import ru.kirkazan.rmis.app.report.n2o.api.model.ReportToGroup;
import ru.kirkazan.rmis.app.report.n2o.api.service.ReportDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by dfirstov on 13.01.2015.
 */
public class ReportDAOImpl implements ReportDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String GET_ALL_REPORTS = "SELECT report.id, report.form_id, report.file_name, " +
            "report.name as report_name, report.is_generated_form, report.is_hide_form, report.is_report_module, report.note, report.name," +
            "(select string_agg(name, ', ') from report.report_group rg " +
            "                join report.report_to_group rtg on rg.id = rtg.group_id " +
            "                AND rtg.report_id = report.id) as report_groups " +
            "FROM report.report report ORDER BY report.id";
    private static final String GET_ALL_REPORTS_TO_GROUP = "select report.id, report.form_id, report.file_name, " +
            " report.name as report_name, report.is_generated_form, report.is_hide_form, report.is_report_module, report.note, report.name, gr.id as group_id, gr.name as group_name" +
            " from report.report " +
            " left join report.report_to_group rtg on rtg.report_id = report.id " +
            " left join report.report_group gr on gr.id = rtg.group_id" +
            " order by rtg.report_order";
    private static final String GET_ALL_REPORTS_OUT_GROUP = "select report.id, report.form_id, report.file_name," +
            " report.name as report_name, report.is_generated_form, report.is_hide_form, report.is_report_module, report.note, report.name, ? as group_id" +
            " from report.report report" +
            " where report.is_report_module" +
            " and report.id not in (select gr.report_id from report.report_to_group gr where gr.group_id = ?)" +
            " order by report.id DESC";
    private static final String GET_ALL_FORM_IDs = "SELECT report.form_id FROM report.report report WHERE report.form_id IS NOT NULL";
    private static final String GET_MAX_REPORT_ID = "SELECT max(id) FROM report.report WHERE file_name = ?";
    private static final String UPDATE_REPORT_FORM = "UPDATE report.report SET form_id = ? WHERE id = ?";
    private static final String DELETE_REPORT = "DELETE FROM report.report WHERE id = ?;";
    private static final String GET_fnc_set_report_order_group_for_delete = "SELECT report.fnc_set_report_order_group_for_delete(?);";
    private static final String INSERT_REPORT = "INSERT INTO report.report(name, file_name, note, is_report_module, is_generated_form, is_hide_form) " +
            "VALUES(?, ?, ?, ?, ?, ?)";
    private static final String INSERT_REPORT_TO_GROUP = "INSERT INTO report.report_to_group (report_id, group_id, report_order)" +
            "SELECT id, ?, (SELECT coalesce(max(report_order), 0) + 1 FROM report.report_to_group WHERE group_id = ?) " +
            "FROM report.report ORDER BY id DESC LIMIT 1;";
    private static final String GET_ALL_CMN_REPORT_DEF_CODEs = "SELECT report.code FROM public.cmn_report_def report";
    private static final String GET_ALL_CMN_REPORT_CODEs = "SELECT report.code FROM public.cmn_report report ";
    private static final String INSERT_CMN_REPORT_DEF = "INSERT INTO public.cmn_report_def(id, code, name)" +
            "VALUES(nextval('public.cmn_report_def_seq'), ?, ?)";
    private static final String INSERT_CMN_REPORT = "INSERT INTO public.cmn_report(id, code, name, url, definition_id) " +
            "VALUES(nextval('cmn_report_seq'), ?, ?, ?, (SELECT max(def.id) FROM public.cmn_report_def def WHERE def.code = ?))";
    private static final String GET_ALL_REPORT_FILE_NAMES = "SELECT file_name FROM report.report";
    private static final String GET_ALL_HIDE_FORM_REPORT_FILE_NAMES = "SELECT file_name FROM report.report WHERE is_hide_form";
    private static final String DELETE_FROM_CMN_REPORT_DEF =
            "DELETE FROM public.cmn_report_def rd WHERE rd.id = " +
                    "(SELECT cr.definition_id FROM public.cmn_report cr " +
                    "JOIN report.report r ON REPLACE(r.file_name, '.rptdesign', '') = cr.code " +
                    "WHERE NOT EXISTS " +
                    "    (SELECT cr1.definition_id FROM public.cmn_report cr1 " +
                    "    JOIN public.cmn_report_place pl1 ON pl1.report_id = cr1.id " +
                    "    AND cr1.definition_id = cr.definition_id) " +
                    "AND cr.code != 'tatar/063u' " +
                    "AND r.id = ? " +
                    "LIMIT 1); " +
                    "DELETE FROM public.cmn_report WHERE id = " +
                    "(SELECT cr.id FROM public.cmn_report cr " +
                    "JOIN report.report r ON REPLACE(r.file_name, '.rptdesign', '') = cr.code " +
                    "WHERE NOT EXISTS " +
                    "    (SELECT cr1.definition_id FROM public.cmn_report cr1 " +
                    "    JOIN public.cmn_report_place pl1 ON pl1.report_id = cr1.id " +
                    "    AND cr1.id = cr.id) " +
                    "AND cr.code != 'tatar/063u' " +
                    "AND r.id = ? " +
                    "LIMIT 1); ";

    @Override
    public List<Report> retrieveReports() {
        return jdbcTemplate.query(GET_ALL_REPORTS, new ReportMapper());
    }

    @Override
    public List<ReportToGroup> retrieveReportToGroup() {
        return jdbcTemplate.query(GET_ALL_REPORTS_TO_GROUP, new ReportToGroupMapper());
    }

    @Override
    public List<ReportToGroup> retrieveReportOutGroup(Integer groupId) {
        return jdbcTemplate.query(GET_ALL_REPORTS_OUT_GROUP, new ReportOutGroupMapper(), groupId, groupId);
    }

    @Override
    public List<String> retrieveAllReportFileNames() {
        return jdbcTemplate.queryForList(GET_ALL_REPORT_FILE_NAMES,
                String.class);
    }

    @Override
    public List<String> retrieveHideFormReportFileNames() {
        return jdbcTemplate.queryForList(GET_ALL_HIDE_FORM_REPORT_FILE_NAMES,
                String.class);
    }

    @Override
    public Set<Report> retrieveReportsSet() {
        List<Report> reportPage = jdbcTemplate.query(GET_ALL_REPORTS,
                new ReportMapper());
        if (reportPage == null) return null;
        return new HashSet<>(reportPage);
    }

    @Override
    public List<String> retrieveReportFormIds() {
        return jdbcTemplate.queryForList(GET_ALL_FORM_IDs, String.class);
    }

    @Override
    public Integer getMaxReportId(String fileName) {
        List<Integer> reportId = jdbcTemplate.queryForList(GET_MAX_REPORT_ID,
                new Object[]{fileName}, Integer.class);
        return reportId.get(0);
    }

    @Override
    public void updateReportForm(String formId, Integer newReportId) {
        jdbcTemplate.update(UPDATE_REPORT_FORM, formId, newReportId);
    }

    @Override
    public void deleteReport(Integer reportId) {
        jdbcTemplate.update(DELETE_REPORT, reportId);
    }

    @Override
    public void deleteCmnReportDef(Integer reportId) {
        jdbcTemplate.update(DELETE_FROM_CMN_REPORT_DEF, reportId, reportId);
    }

    @Override
    public void prepareReportOrderForDelete(Integer reportId) {
        jdbcTemplate.queryForList(GET_fnc_set_report_order_group_for_delete, reportId);
    }

    @Override
    public void insertReport(String name, String fileName, String note, Boolean inModule, Integer groupId, Boolean isGenerated, Boolean isHideForm) {
        jdbcTemplate.update(INSERT_REPORT, name, fileName, note, inModule, isGenerated, isHideForm);
        if (inModule != null && inModule) {
            jdbcTemplate.update(INSERT_REPORT_TO_GROUP, groupId, groupId);
        }
    }

    @Override
    public void addToCmnReportDef(String name, String newCode) {
        List<String> reportDefCodes = getCmnReportDefCodes();
        Boolean exists = false;
        for (String code : reportDefCodes) {
            if (code != null && newCode != null && code.equals(newCode)) {
                exists = true;
                break;
            }
        }
        if (!exists) insertToCmnReportDef(name, newCode);
    }

    @Override
    public void addToCmnReport(String name, String newCode) {
        List<String> reportCodes = getCmnReportCodes();
        Boolean exists = false;
        for (String code : reportCodes) {
            if (code != null && newCode != null && code.equals(newCode)) {
                exists = true;
                break;
            }
        }
        if (!exists) insertToCmnReport(name, newCode);
    }

    @Override
    public List<String> getCmnReportDefCodes() {
        return jdbcTemplate.queryForList(GET_ALL_CMN_REPORT_DEF_CODEs, String.class);
    }

    @Override
    public List<String> getCmnReportCodes() {
        return jdbcTemplate.queryForList(GET_ALL_CMN_REPORT_CODEs, String.class);
    }

    @Override
    public void insertToCmnReportDef(String name, String code) {
        jdbcTemplate.update(INSERT_CMN_REPORT_DEF, code, name);
    }

    @Override
    public void insertToCmnReport(String name, String code) {
        String url = "/birt/run?__report=" + code + ".rptdesign&__format=pdf";
        jdbcTemplate.update(INSERT_CMN_REPORT, code, name, url, code);
    }

    private class ReportMapper implements RowMapper<Report> {
        @Override
        public Report mapRow(ResultSet resultSet, int i) throws SQLException {
            Report report = new Report();
            mapReport(resultSet, report);
            report.setGroupNames(resultSet.getString("report_groups"));
            return report;
        }
    }

    private class ReportToGroupMapper implements RowMapper<ReportToGroup> {
        @Override
        public ReportToGroup mapRow(ResultSet resultSet, int i) throws SQLException {
            ReportToGroup report = new ReportToGroup();
            mapReport(resultSet, report);
            report.setGroupId(resultSet.getInt("group_id"));
            report.setGroupName(resultSet.getString("group_name"));
            return report;
        }
    }

    private class ReportOutGroupMapper implements RowMapper<ReportToGroup> {
        @Override
        public ReportToGroup mapRow(ResultSet resultSet, int i) throws SQLException {
            ReportToGroup report = new ReportToGroup();
            mapReport(resultSet, report);
            report.setGroupId(resultSet.getInt("group_id"));
            return report;
        }
    }

    private void mapReport(ResultSet resultSet, Report report) throws SQLException {
        report.setId((resultSet.getInt("id")));
        report.setName(resultSet.getString("name"));
        report.setFormId(resultSet.getString("form_id"));
        report.setN2oFormId(resultSet.getString("form_id"));
        report.setFileName(resultSet.getString("file_name"));
        report.setLabel(resultSet.getString("report_name"));
        report.setIsGeneratedForm(resultSet.getBoolean("is_generated_form"));
        report.setIsHideForm(resultSet.getBoolean("is_hide_form"));
        report.setIsReportModule(resultSet.getBoolean("is_report_module"));
        report.setNote(resultSet.getString("note"));
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
