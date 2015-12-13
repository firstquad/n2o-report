package ru.kirkazan.rmis.app.report.n2o.servlet;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dfirstov
 * @since 15.04.15 11:03
 */

public class ReportRecipient {

    public static void receive(HttpServletRequest request, HttpServletResponse response, String queryString, String postBody, String birtHost) throws IOException {
        String urlString = getBirtUrlString(queryString, birtHost);
        CloseableHttpClient httpClient = HTTP_CLIENT_BUILDER.get().build();
        HttpResponse httpClientResponse = getBirtResponse(request, urlString, httpClient, postBody);
        BufferedOutputStream proxyToClientBuf = prepareResponseToClient(httpClientResponse, response);
        if (proxyToClientBuf != null) {
            proxyToClientBuf.flush();
            proxyToClientBuf.close();
        }
        httpClient.close();
    }

    public static String getRequestBody(BufferedReader bufferedReader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        char[] charBuffer = new char[128];
        int bytesRead;
        while ((bytesRead = bufferedReader.read(charBuffer)) != -1) {
            stringBuilder.append(charBuffer, 0, bytesRead);
        }
        return stringBuilder.toString();
    }

    private static String getBirtUrlString(String queryString, String birtHost) {
        return birtHost + queryString;
    }

    private static HttpResponse getBirtResponse(HttpServletRequest request, String urlString, CloseableHttpClient httpClient, String postBody)
            throws IOException {
        HttpResponse httpClientResponse;
        if (("GET").equals(request.getMethod())) {
            HttpGet httpGet = new HttpGet(urlString);
            Enumeration e = request.getHeaderNames();
            while (e.hasMoreElements()) {
                String s = (String) e.nextElement();
                httpGet.setHeader(s, request.getHeader(s));
            }
            httpClientResponse = httpClient.execute(httpGet);
        } else {
            HttpPost httpPost = new HttpPost(urlString);

            if (!"".equals(postBody) && postBody != null) {
                StringEntity stringEntity = new StringEntity(postBody);
                httpPost.setEntity(stringEntity);
            } else {
                List<NameValuePair> params = new ArrayList<>();
                for (Enumeration e = request.getParameterNames(); e.hasMoreElements(); ) {
                    String headerName = e.nextElement().toString();
                    params.add(new BasicNameValuePair(headerName, request.getParameter(headerName)));
                }
                UrlEncodedFormEntity paramEntity = new UrlEncodedFormEntity(params);
                httpPost.setEntity(paramEntity);
            }
            Enumeration e = request.getHeaderNames();
            while (e.hasMoreElements()) {
                String s = (String) e.nextElement();
                String value = request.getHeader(s);
                if (!"content-length".equals(s)) {
                    httpPost.setHeader(s, value);
                }
            }
            httpClientResponse = httpClient.execute(httpPost);
        }
        return httpClientResponse;
    }

    private static BufferedOutputStream prepareResponseToClient(HttpResponse httpClientResponse,
                                                                HttpServletResponse response)
            throws IOException {
        BufferedOutputStream proxyToClientBuf;
        Header[] headers = httpClientResponse.getAllHeaders();
        HttpEntity entity = httpClientResponse.getEntity();
        if (entity != null) {
            List<String> allowedHeaders = getAllowedHeaders();
            InputStream inputStream = entity.getContent();
            int oneByte;
            for (Header header : headers) {
                if (allowedHeaders.contains(header.getName().toUpperCase())) {
                    String clientHeaderValue = header.getValue();
                    response.setHeader(header.getName(), clientHeaderValue);
                }
            }

            proxyToClientBuf = new BufferedOutputStream(response.getOutputStream());
            while ((oneByte = inputStream.read()) != -1) {
                proxyToClientBuf.write(oneByte);
            }
            inputStream.close();
            return proxyToClientBuf;
        }
        return null;
    }


    private static List<String> getAllowedHeaders() {
        return Arrays.asList("Content-Disposition,Content-Type,Set-Cookie".trim().toUpperCase().split(","));
    }


    public static boolean isSourceReportUrl(String queryString) {
        Pattern anonymous_PATTERN = Pattern.compile("__report=(.*?).rptdesign.*");

        if (queryString == null) return false;
        else {
            Matcher matcher = anonymous_PATTERN.matcher(queryString);
            return matcher.matches();
        }
    }

    private static final ThreadLocal<HttpClientBuilder> HTTP_CLIENT_BUILDER =
            new ThreadLocal<HttpClientBuilder>() {
                @Override
                protected HttpClientBuilder initialValue() {
                    return HttpClientBuilder.create();
                }
            };

}
