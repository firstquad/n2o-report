package ru.kirkazan.rmis.app.report.n2o.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author dfirstov
 * @since 08.04.2015
 */
public class ReportUrlBuilder {
    private static final Logger logger = LoggerFactory.getLogger(ReportUrlBuilder.class);
    private static final String REPORT_PARAMETER = "__report";
    private static final String USER_ID_PARAMETER = "user_id";
    private static final String FORMAT_PARAMETER = "__format";
    private static final String DEFAULT_FORMAT = "pdf";
    private static final String CONTEXT_USER_ID_NOT_FOUND = "Не определен контекст пользователя.";
    private static final String PARAMETERS_NOT_FOUND = "Не заданы параметры.";
    public static final String FILE_NOT_FOUND = "Название файла отчета не определено.";
    private static final String ENC = "UTF-8";

    private Integer contextUserId;
    private Map<String, String[]> params = new LinkedHashMap<>();
    private StringBuilder url = new StringBuilder();

    public ReportUrlBuilder addContextUserId(Integer contextUserId) {
        this.contextUserId = contextUserId;
        this.params.put(USER_ID_PARAMETER, new String[]{String.valueOf(contextUserId)});
        return this;
    }

    public ReportUrlBuilder addParameter(String name, Object value) {
        this.params.put(name, new String[]{String.valueOf(value)});
        return this;
    }

    public ReportUrlBuilder addParameters(Map<String, String[]> params) {
        LinkedHashMap<String, String[]> tempMap = new LinkedHashMap<>(params);
        tempMap.remove(USER_ID_PARAMETER);
        this.params.putAll(tempMap);
        return this;
    }

    public String build() {
        validate();
        appendParameter(REPORT_PARAMETER).append("&");
        appendParameter(USER_ID_PARAMETER);
        appendParameters();
        return url.toString();
    }

    private StringBuilder appendParameter(String key) {
        url.append(key).append("=").append(params.get(key)[0]);
        params.remove(key);
        return url;
    }

    private void appendParameters() {
        if (valueNotFound(params, FORMAT_PARAMETER)) {
            params.put(FORMAT_PARAMETER, new String[]{DEFAULT_FORMAT});
        }
        for (Map.Entry<String, String[]> param : params.entrySet()) {
            String key = param.getKey();
            String[] values = param.getValue();
            if (values != null && !"".equals(values[0])) {
                url.append("&").append(key).append("=").append(encodeValue(values[0]));
            }
        }
    }

    private String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, ENC);
        } catch (UnsupportedEncodingException e) {
            logger.warn(e.getMessage(), "Не удалось закодировать значение параметра в формате " + ENC);
            return value;
        }
    }

    public static String prepareBirtHost(String birtHost) {
        return birtHost.endsWith("/") ? birtHost + "run?" : birtHost + "/run?";
    }

    private static boolean valueNotFound(Map<String, String[]> params, String key) {
        return !params.containsKey(key) || params.get(key) == null || params.get(key)[0].equals("");
    }

    private void validate() {
        if (params == null) {
            throw new IllegalStateException(PARAMETERS_NOT_FOUND);
        }
        if (valueNotFound(params, REPORT_PARAMETER)) {
            throw new IllegalStateException(FILE_NOT_FOUND);
        }
        if (contextUserId == null) {
            throw new IllegalStateException(CONTEXT_USER_ID_NOT_FOUND);
        }
    }

    public String extractFileName() {
        if (valueNotFound(params, REPORT_PARAMETER)) {
            throw new IllegalStateException(FILE_NOT_FOUND);
        }
        return params.get(REPORT_PARAMETER)[0];
    }

    public String getReportFormat() {
        return getParameter(FORMAT_PARAMETER);
    }

    public String getParameter(String name) {
        String[] param = params.get(name);
        if (param == null || param.length == 0)
            return null;
        return param[0];
    }
}
