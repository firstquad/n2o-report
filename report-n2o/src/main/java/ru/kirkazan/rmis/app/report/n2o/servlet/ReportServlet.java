package ru.kirkazan.rmis.app.report.n2o.servlet;

import net.n2oapp.context.StaticSpringContext;
import net.n2oapp.framework.access.AdminService;
import net.n2oapp.framework.api.UsersUtil;
import net.n2oapp.framework.context.ContextProcessor;
import net.n2oapp.properties.StaticProperties;
import ru.kirkazan.rmis.app.report.n2o.sec.ReportAccessImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Properties;

import static ru.kirkazan.rmis.app.report.n2o.servlet.ReportRecipient.getRequestBody;
import static ru.kirkazan.rmis.app.report.n2o.servlet.ReportRecipient.receive;

/**
 * Created by dfirstov on 22.01.2015.
 */

public class ReportServlet extends HttpServlet {
    private Properties n2oProperties = ((Properties) StaticSpringContext.getBean("n2oProperties"));
    private ReportAccessImpl reportAccess = ((ReportAccessImpl) StaticSpringContext.getBean("reportAccess"));

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ReportUrlBuilder urlBuilder = new ReportUrlBuilder();
        urlBuilder.addParameters(req.getParameterMap());
        if (!checkAccess(resp, urlBuilder.extractFileName())) {
            return;
        }
        fillFromContext(urlBuilder, req);
        String servletMode = n2oProperties.getProperty("rmis.report.mode");
        if (isRedirect(servletMode)) {
            doRedirect(req, resp, urlBuilder);
        } else if (isForward(servletMode)) {
            doForward(req, resp, urlBuilder);
        } else
            throw new ServletException("Не определен режим сервлета(redirect/forward)");
    }

    private void doRedirect(HttpServletRequest req, HttpServletResponse resp, ReportUrlBuilder urlBuilder) throws ServletException, IOException {
        String externalBirtUrl = ReportUrlBuilder.prepareBirtHost(n2oProperties.getProperty("rmis.report.birt.url"));
        String url = urlBuilder.build();
        if (isShowPreview(urlBuilder, req)) {
            showPreview(req, resp, externalBirtUrl, url);
        } else
            resp.sendRedirect(externalBirtUrl + url);
    }

    private void doForward(HttpServletRequest req, HttpServletResponse resp, ReportUrlBuilder urlBuilder) throws ServletException, IOException {
        String requestBody = null;
        if ("POST".equals(req.getMethod())) {
            requestBody = getRequestBody(req.getReader());
        }
        String url = urlBuilder.build();
        String internalBirtUrl = ReportUrlBuilder.prepareBirtHost(n2oProperties.getProperty("rmis.report.birt.internal_url"));
        if (isShowPreview(urlBuilder, req)) {
            showPreview(req, resp, "report?", url);
        } else
            receive(req, resp, url, requestBody, internalBirtUrl);
    }

    private void showPreview(HttpServletRequest req, HttpServletResponse resp, String birtHost, String url) throws ServletException, IOException {
        getServletContext().getRequestDispatcher("/preview/preview.jsp?birtHost=" + URLEncoder.encode(birtHost, "UTF-8")
                + "&url_iframe=" + URLEncoder.encode(url, "UTF-8")
                + "&url=" + URLEncoder.encode("/run?" + url, "UTF-8")
                + "&emp_position_id=" + ContextProcessor.getInstance().get("emplPos.id")).forward(req, resp);
    }

    private Boolean checkAccess(HttpServletResponse resp, String fileName) throws IOException {
        if (AdminService.getInstance().isAdmin(UsersUtil.getUser().getUsername()))
            return true;
        if (reportAccess.hasAccess(fileName)) {
            return true;
        } else {
            resp.sendError(403);
            return false;
        }
    }

    private ReportUrlBuilder fillFromContext(ReportUrlBuilder urlBuilder, HttpServletRequest req) throws ServletException {
        ContextProcessor context = ContextProcessor.getInstance();
        Object contextUserId = context.get("user.id");
        if (contextUserId == null || !(contextUserId instanceof Integer))
            throw new ServletException("Не определен контекст пользователя.");
        urlBuilder.addContextUserId((Integer) contextUserId);
        urlBuilder.addParameter("emp_position_id", context.get("emplPos.id"));
        return urlBuilder;
    }

    private boolean isRedirect(String servletMode) {
        return servletMode == null || "".equals(servletMode) || "redirect".equals(servletMode);
    }

    private boolean isShowPreview(ReportUrlBuilder urlBuilder, HttpServletRequest req) {
        Boolean isPreviewShowed = req.getParameter("isPreviewShowed") != null && Boolean.parseBoolean(req.getParameter("isPreviewShowed"));
        return StaticProperties.getBoolean("rmis.report.preview.enabled")
                && urlBuilder.getReportFormat() != null && urlBuilder.getReportFormat().equals("pdf")
                && !isPreviewShowed;
    }

    private boolean isForward(String servletMode) {
        return "forward".equals(servletMode);
    }

}