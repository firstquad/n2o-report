package net.n2oapp.framework.report.servlet;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import ru.kirkazan.rmis.app.report.n2o.servlet.ReportUrlBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author dfirstov
 * @since 09.04.2015
 */

public class ReportUrlBuilderTest {

    @Test
    public void testBuildUrl() throws IllegalStateException {
        ReportUrlBuilder urlBuilder = new ReportUrlBuilder();
        String result = "__report=5.rptdesign&user_id=1&__format=pdf";
        try {
            urlBuilder = initBuilder(new LinkedHashMap<>());
            assert result.equals(urlBuilder.build());
        } catch (IllegalStateException e) {
            assert e.getMessage().equals(ReportUrlBuilder.FILE_NOT_FOUND);
        }
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        try {
            mockRequest.setParameter("__report", "");
            urlBuilder = initBuilder(mockRequest.getParameterMap());
            assert result.equals(urlBuilder.build());
        } catch (IllegalStateException e) {
            assert e.getMessage().equals(ReportUrlBuilder.FILE_NOT_FOUND);
        }
        try {
            mockRequest = new MockHttpServletRequest();
            mockRequest.setParameter("clinic_id", "411");
            mockRequest.setParameter("__format", "pdf");
            urlBuilder = initBuilder(mockRequest.getParameterMap());
            assert result.equals(urlBuilder.build());
        } catch (IllegalStateException e) {
            assert e.getMessage().equals(ReportUrlBuilder.FILE_NOT_FOUND);
        }
        mockRequest = new MockHttpServletRequest();
        mockRequest.setParameter("__report", "5.rptdesign");
        urlBuilder = initBuilder(mockRequest.getParameterMap());
        assert result.equals(urlBuilder.build());
        mockRequest = new MockHttpServletRequest();
        mockRequest.setParameter("__report", "5.rptdesign");
        mockRequest.setParameter("__format", "pdf");
        urlBuilder = initBuilder(mockRequest.getParameterMap());
        assert result.equals(urlBuilder.build());

        String resultWithParam = "__report=5.rptdesign&user_id=1&clinic_id=411&__format=pdf";
        mockRequest = new MockHttpServletRequest();
        mockRequest.setParameter("__report", "5.rptdesign");
        mockRequest.setParameter("clinic_id", "411");
        mockRequest.setParameter("__format", "pdf");
        urlBuilder = initBuilder(mockRequest.getParameterMap());
        assert resultWithParam.equals(urlBuilder.build());
        mockRequest = new MockHttpServletRequest();
        mockRequest.setParameter("__report", "5.rptdesign");
        mockRequest.setParameter("clinic_id", "411");
        mockRequest.setParameter("user_id", "5");
        mockRequest.setParameter("__format", "pdf");
        urlBuilder = initBuilder(mockRequest.getParameterMap());
        assert resultWithParam.equals(urlBuilder.build());
        mockRequest = new MockHttpServletRequest();
        mockRequest.setParameter("__report", "5.rptdesign");
        mockRequest.setParameter("clinic_id", "411");
        mockRequest.setParameter("__format", "pdf");
        urlBuilder = initBuilder(mockRequest.getParameterMap());
        assert resultWithParam.equals(urlBuilder.build());

        String resultWithDate = "__report=5.rptdesign&user_id=1&clinic_id=411&store_id=1&from_dt=04.04.2015%252000%3A00&to_dt=05.04.2015%252000%3A00&__format=pdf";
        mockRequest = new MockHttpServletRequest();
        mockRequest.setParameter("__report", "5.rptdesign");
        mockRequest.setParameter("user_id", "1");
        mockRequest.setParameter("clinic_id", "411");
        mockRequest.setParameter("store_id", "1");
        mockRequest.setParameter("from_dt", "04.04.2015%2000:00");
        mockRequest.setParameter("to_dt", "05.04.2015%2000:00");
        mockRequest.setParameter("__format", "pdf");
        urlBuilder = initBuilder(mockRequest.getParameterMap());
        assert resultWithDate.equals(urlBuilder.build());
    }

    private ReportUrlBuilder initBuilder(Map<String, String[]> params) {
        ReportUrlBuilder urlBuilder = new ReportUrlBuilder();
        urlBuilder.addContextUserId(1);
        urlBuilder.addParameters(params);
        return urlBuilder;
    }

}
