/**
 * Created by emamoshin on 02.11.2015.
 */
var $btn;
var $lastResponse;
function print_doc () {
    var _url = $("#printf").attr("src");
    var _win = window.open(_url);
    $(_win).ready(function(){
        _win.print();
    });
}

function run () {
    var data = $('#btn-sign').data();
    var hashAlg = 100; // ГОСТ Р 34.11-94
    $.get('/frontend/backend/reporting/signature/hash', {url: data.url, emp_position_id: data.empPositionId}, function (response) {
        $lastResponse = response;
        Common_SignHash('CertListBox', response.hash, hashAlg); // cert list, hash, alg
    });
}

function showMessage (color, label, text, url) {
    var $el = $("#messages");
    hideMessage();
    $el.removeClass('alert-danger alert-success');
    $el.addClass(color);
    $el.find('strong.msg-label').text(label);
    $el.find('span.msg-text').text(text);
    if (url) {
        $el.find('a').attr('href', url).show();
    }
    $el.fadeIn();
}

function hideMessage () {
    var $el = $("#messages");
    $el.find('a').hide();
    $el.hide();
}

var canPromise = !!window.Promise;
if(canPromise) {
    cadesplugin.then(function () {
            Common_CheckForPlugIn();
        }
    );
} else {
    window.addEventListener("message", function (event){
            if (event.data == "cadesplugin_loaded") {
                CheckForPlugIn_NPAPI();
            }
        },
        false);
    window.postMessage("cadesplugin_echo_request", "*");
}

$(document).on('signature.ready', function(e, sign){
    var $btnSign = $('#btn-sign');
    var params = {
        value: sign,
        id: $lastResponse.id
    };

    $.ajax({
        type: 'POST',
        url: '/frontend/backend/reporting/signature',
        data: JSON.stringify(params),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (data) {
            showMessage('alert-success', 'Выполнено!', 'Отчет успешно подписан.', "/frontend/backend/reporting/signature/zip/" + $lastResponse.id);
            $btnSign.removeAttr("disabled");
            $btnSign.find('.st2').hide();
            $btnSign.find('.st1').show();
        }.bind(this),
        error: function () {
            console.log('Error!')
        }
    });
});

$(document).on('signature.fail', function(e, msg){
    var $btnSign = $('#btn-sign');
    showMessage('alert-danger', 'Ошибка!', msg);
    $btnSign.removeAttr("disabled");
    $btnSign.find('.st2').hide();
    $btnSign.find('.st1').show();
});

$(document).ready(function(){
    var $cerList = $("#CertListBox");
    var wh = $(document).height();
    var th = $("#tooltip").outerHeight();
    var ih = wh-th;
    $("#iframe").height(ih);

    if ($cerList.val() != ""){
        $("#btn-sign").removeAttr("disabled");
    }

    $cerList.on("change", function(){
        if ($("#CertListBox").val() != ""){
            $("#btn-sign").removeAttr("disabled");
        } else {
            $("#btn-sign").attr("disabled", "disabled");
        }
    });

    $('#btn-sign').on('click', function () {
        $(this).attr("disabled", "disabled");
        $(this).find('.st1').hide();
        $(this).find('.st2').show();
        run();
    })
});