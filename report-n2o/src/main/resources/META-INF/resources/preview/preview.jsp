<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head lang="en">
    <meta charset="UTF-8">
    <title>Отчеты</title>
    <script src="preview/jquery-1.11.3.min.js"></script>
    <!--<script src="./bootstrap/js/bootstrap.min.js"></script>-->
    <link rel="stylesheet" href="preview/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="preview/main.css">
    <script language="javascript" src="preview/es6-promise.min.js"></script>
    <style type="text/css"></style>
    <script language="javascript" src="preview/ie_eventlistner_polyfill.js"></script>
    <script language="javascript" src="preview/cadesplugin_api.js"></script>
    <script language="javascript" src="preview/Code.js"></script>
    <script language="javascript" src="preview/main.js"></script>
</head>
<body>
<div id="messages" class="alert rmis-report-alerts">
    <strong class="msg-label"></strong> <span class="msg-text"></span>
    <a href="#" class="btn btn-default" target="_blank" style="display: none;"><span
            class="glyphicon glyphicon-download-alt" aria-hidden="true"></span> Загрузить</a>
</div>
<div id="main">
    <div id="tooltip" style="padding: 9px">
        <form class="form-inline" style="margin-bottom: 0;">
            <button type="button" id="btn-print" class="btn btn-primary" onclick="print_doc()"><span
                    class="glyphicon glyphicon-print" aria-hidden="true"></span> Печать
            </button>
            <div class="pull-right">
                <div id="cert-list-group" class="form-group" style="display: none;">
                    <label for="CertListBox">Выберите сертификат: </label>
                    <select class="form-control" name="CertListBox" id="CertListBox">
                        <option value=""></option>
                    </select>
                </div>
                <button type="button" data-url="<%=URLEncoder.encode(request.getParameter("url"), "UTF-8")%>"
                        data-emp-position-id="<%=URLEncoder.encode(request.getParameter("emp_position_id"), "UTF-8")%>" id="btn-sign"
                        class="btn btn-primary"
                        style="display: none;" disabled="disabled">
                    <div class="st1"><span class="glyphicon glyphicon-pencil" aria-hidden="true"></span> Подписать</div>
                    <div class="st2">
                        <div class="sk-fading-circle">
                            <div class="sk-circle1 sk-circle"></div>
                            <div class="sk-circle2 sk-circle"></div>
                            <div class="sk-circle3 sk-circle"></div>
                            <div class="sk-circle4 sk-circle"></div>
                            <div class="sk-circle5 sk-circle"></div>
                            <div class="sk-circle6 sk-circle"></div>
                            <div class="sk-circle7 sk-circle"></div>
                            <div class="sk-circle8 sk-circle"></div>
                            <div class="sk-circle9 sk-circle"></div>
                            <div class="sk-circle10 sk-circle"></div>
                            <div class="sk-circle11 sk-circle"></div>
                            <div class="sk-circle12 sk-circle"></div>
                        </div>
                        Подписываю...
                    </div>
                </button>
            </div>
            <div id="loader" class="pull-right">
                <div class="progress" title="Поиск сертификатов...">
                    <div class="progress-bar progress-bar-warning" role="progressbar">
                    </div>
                </div>
                <span>Поиск сертификатов...</span>
            </div>
        </form>
    </div>
    <div id="iframe">
        <iframe id="printf" name="printf" width="100%" height="100%"
                src="<%=URLDecoder.decode(request.getParameter("birtHost"), "UTF-8") + URLDecoder.decode(request.getParameter("url_iframe"), "UTF-8") + "&isPreviewShowed=true"%>"></iframe>
    </div>
</div>
</body>
</html>