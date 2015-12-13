package ru.kirkazan.rmis.app.report.n2o.form;


import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by dfirstov on 28.09.2014.
 */
public class ReportFormMapper implements RowMapper<ReportForm> {

    @Override
    public ReportForm mapRow(ResultSet resultSet, int i) throws SQLException {
        ReportForm reportPage = new ReportForm();
        reportPage.setPageId(resultSet.getString("form_id"));
        reportPage.setFileName(resultSet.getString("file_name"));
        reportPage.setLabel(resultSet.getString("report_name"));
        reportPage.setUrl(resultSet.getString("url"));
        return reportPage;
    }
}
