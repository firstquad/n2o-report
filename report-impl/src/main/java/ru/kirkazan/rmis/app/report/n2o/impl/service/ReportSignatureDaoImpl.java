package ru.kirkazan.rmis.app.report.n2o.impl.service;

import cz.atria.common.spring.jdbc.CustomJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.kirkazan.rmis.app.report.n2o.api.model.ReportSignature;
import ru.kirkazan.rmis.app.report.n2o.api.service.ReportSignatureDao;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author rsadikov
 * @since 10.11.2015
 */
@Service
public class ReportSignatureDaoImpl implements ReportSignatureDao {
    private static final String INSERT_REPORT_SIGNATURE = "insert into report.report_signature(id, report_code, employee_position_id, report_file_name, sign_file_name) values(nextval('report.report_signature_id_seq'), ?, ?, ?, ?)";

    private static final String UPDATE_REPORT_SIGNATURE = "update report.report_signature set report_code = ?, employee_position_id = ?, report_file_name = ?, sign_file_name = ? where id = ?";

    private static final String GET_REPORT_SIGNATURE = "select id, report_code, employee_position_id, sign_dt, report_file_name, sign_file_name from report.report_signature where id = ?";

    @Autowired
    private CustomJdbcTemplate jdbcTemplate;

    @Override
    public int saveReportSignature(ReportSignature reportSignature) {
        if (reportSignature.getId() == null) {
            return jdbcTemplate.updateReturningKeyInteger(INSERT_REPORT_SIGNATURE, "id",
                    reportSignature.getReportCode(), reportSignature.getPositionId(),
                    reportSignature.getReportFile(), reportSignature.getSignatureFile());
        } else {
            return jdbcTemplate.update(UPDATE_REPORT_SIGNATURE, reportSignature.getReportCode(),
                    reportSignature.getPositionId(), reportSignature.getReportFile(),
                    reportSignature.getSignatureFile(), reportSignature.getId());
        }
    }

    @Override
    public ReportSignature getReportSignature(int id) {
        return jdbcTemplate.queryForObject(GET_REPORT_SIGNATURE, new Object[]{id}, REPORT_SIGNATURE_ROW_MAPPER);
    }

    private static final RowMapper<ReportSignature> REPORT_SIGNATURE_ROW_MAPPER = new RowMapper<ReportSignature>() {
        @Override
        public ReportSignature mapRow(ResultSet rs, int rowNum) throws SQLException {
            ReportSignature reportSignature = new ReportSignature();
            reportSignature.setId(rs.getInt("id"));
            reportSignature.setReportCode(rs.getString("report_code"));
            reportSignature.setPositionId(rs.getInt("employee_position_id"));
            reportSignature.setSignDate(rs.getDate("sign_dt"));
            reportSignature.setReportFile(rs.getString("report_file_name"));
            reportSignature.setSignatureFile(rs.getString("sign_file_name"));
            return reportSignature;
        }
    };
}
