var isPluginEnabled = false;
function CertificateObj(certObj)
{
    this.cert = certObj;
    this.certFromDate = new Date(this.cert.ValidFromDate);
    this.certTillDate = new Date(this.cert.ValidToDate);
}

CertificateObj.prototype.check = function(digit)
{
    return (digit<10) ? "0"+digit : digit;
}

CertificateObj.prototype.extract = function(from, what)
{
    certName = "";

    var begin = from.indexOf(what);

    if(begin>=0)
    {
        var end = from.indexOf(', ', begin);
        certName = (end<0) ? from.substr(begin) : from.substr(begin, end - begin);
    }

    return certName;
}

CertificateObj.prototype.DateTimePutTogether = function(certDate)
{
    return this.check(certDate.getUTCDate())+"."+this.check(certDate.getMonth()+1)+"."+certDate.getFullYear() + " " +
        this.check(certDate.getUTCHours()) + ":" + this.check(certDate.getUTCMinutes()) + ":" + this.check(certDate.getUTCSeconds());
}

CertificateObj.prototype.GetCertString = function()
{
    return this.extract(this.cert.SubjectName,'CN=') + "; Выдан: " + this.GetCertFromDate();
}

CertificateObj.prototype.GetCertFromDate = function()
{
    return this.DateTimePutTogether(this.certFromDate);
}

CertificateObj.prototype.GetCertTillDate = function()
{
    return this.DateTimePutTogether(this.certTillDate);
}

CertificateObj.prototype.GetPubKeyAlgorithm = function()
{
    return this.cert.PublicKey().Algorithm.FriendlyName;
}

CertificateObj.prototype.GetCertName = function()
{
    return this.extract(this.cert.SubjectName, 'CN=');
}

CertificateObj.prototype.GetIssuer = function()
{
    return this.extract(this.cert.IssuerName, 'CN=');
}
var async_code_included = 0;
var async_Promise;
var async_resolve;
function include_async_code()
{
    if(async_code_included)
    {
        return async_Promise;
    }
    var fileref = document.createElement('script');
    fileref.setAttribute("type", "text/javascript");
    fileref.setAttribute("src", "preview/async_code.js");
    document.getElementsByTagName("head")[0].appendChild(fileref);
    async_Promise = new Promise(function(resolve, reject){
        async_resolve = resolve;
    });
    async_code_included = 1;
    return async_Promise;
}

function Common_SignHash(id, hash, alg)
{
    var canAsync = !!cadesplugin.CreateObjectAsync;
    if(canAsync)
    {
        include_async_code().then(function(){
            return SignHash_Async(id, hash, alg);
        });
    }else
    {
        return SignHash_NPAPI(id, hash, alg);
    }
}

function Common_CheckForPlugIn() {
    var canAsync = !!cadesplugin.CreateObjectAsync;
    if(canAsync)
    {
        include_async_code().then(function(){
            return CheckForPlugIn_Async();
        });
    }else
    {
        return CheckForPlugIn_NPAPI();
    }
}

function MakeCadesHashSign_NPAPI(hashedData, certObject) {
    var errormes = "";

    try {
        var oSigner = cadesplugin.CreateObject("CAdESCOM.CPSigner");
    } catch (err) {
        errormes = "Failed to create CAdESCOM.CPSigner: " + err.number;
        throw errormes;
    }

    if (oSigner) {
        oSigner.Certificate = certObject;
    }
    else {
        errormes = "Failed to create CAdESCOM.CPSigner";
        throw errormes;
    }

    try {
        var oSignedData = cadesplugin.CreateObject("CAdESCOM.CadesSignedData");
    } catch (err) {
        throw 'Failed to create CAdESCOM.CadesSignedData: ' + err.number;
    }

    var CADES_BES = 1;
    var Signature;

    if (hashedData) {
        try {
            Signature = oSignedData.SignHash(hashedData, oSigner, CADES_BES);
        }
        catch (err) {
            errormes = "Не удалось создать подпись из-за ошибки: " + GetErrorMessage(err);
            throw errormes;
        }
    }
    return Signature;
}

function SignHash_NPAPI(certListBoxId, hashValue, hashAlg) {
    var certificate = GetCertificate_NPAPI(certListBoxId);

    // Создаем объект CAdESCOM.HashedData
    var oHashedData = cadesplugin.CreateObject("CAdESCOM.HashedData");

    // Инициализируем объект заранее вычисленным хэш-значением
    // Алгоритм хэширования нужно указать до того, как будет передано хэш-значение
    oHashedData.Algorithm = hashAlg;
    oHashedData.SetHashValue(hashValue);

    try
    {
        var signature = MakeCadesHashSign_NPAPI(oHashedData, certificate);
        $(document).triggerHandler('signature.ready', signature);
//        document.getElementById("SignatureTxtBox").innerHTML = signature;
    }
    catch(err)
    {
        $(document).triggerHandler('signature.fail', err);
//        document.getElementById("SignatureTxtBox").innerHTML = err;
    }
}

function GetCertificate_NPAPI(certListBoxId) {
    var e = document.getElementById(certListBoxId);
    var selectedCertID = e.selectedIndex;
    if (selectedCertID == -1) {
        $(document).triggerHandler('signature.fail', "Select certificate");
        return;
    }

    var thumbprint = e.options[selectedCertID].value.split(" ").reverse().join("").replace(/\s/g, "").toUpperCase();
    try {
        var oStore = cadesplugin.CreateObject("CAdESCOM.Store");
        oStore.Open();
    } catch (err) {
        $(document).triggerHandler('signature.fail', 'Failed to create CAdESCOM.Store: ' + GetErrorMessage(err));
        return;
    }

    var CAPICOM_CERTIFICATE_FIND_SHA1_HASH = 0;
    var oCerts = oStore.Certificates.Find(CAPICOM_CERTIFICATE_FIND_SHA1_HASH, thumbprint);

    if (oCerts.Count == 0) {
        $(document).triggerHandler('signature.fail', "Certificate not found");
        return;
    }
    var oCert = oCerts.Item(1);
    return oCert;
}

function CheckForPlugIn_NPAPI() {
    var isPluginLoaded = false;
    var isPluginWorked = false;
    var isActualVersion = false;
    try {
        var oAbout = cadesplugin.CreateObject("CAdESCOM.About");
        isPluginLoaded = true;
        isPluginEnabled = true;
        isPluginWorked = true;
        // Это значение будет проверяться сервером при загрузке демо-страницы
        var CurrentPluginVersion = oAbout.PluginVersion;
        if( typeof(CurrentPluginVersion) == "undefined")
            CurrentPluginVersion = oAbout.Version;

        $("#btn-sign").show();
        $("#cert-list-group").show();
    }
    catch (err) {
        // Объект создать не удалось, проверим, установлен ли
        // вообще плагин. Такая возможность есть не во всех браузерах
        var mimetype = navigator.mimeTypes["application/x-cades"];
        if (mimetype) {
            isPluginLoaded = true;
            var plugin = mimetype.enabledPlugin;
            if (plugin) {
                isPluginEnabled = true;
            }
        }
    }
    // заполняем список сертификатов
    FillCertList_NPAPI('CertListBox');
}

function FillCertList_NPAPI(lstId) {
    try {
        var oStore = cadesplugin.CreateObject("CAdESCOM.Store");
        oStore.Open();
    }
    catch (ex) {
        alert("Ошибка при открытии хранилища: " + GetErrorMessage(ex));
        return;
    }

    try {
        var lst = document.getElementById(lstId);
        if(!lst)
            return;
    }
    catch (ex) {
        return;
    }

    var certCnt;

    try {
        certCnt = oStore.Certificates.Count;
        if(certCnt==0)
            throw "Cannot find object or property. (0x80092004)";
    }
    catch (ex) {
        var message = GetErrorMessage(ex);
        if("Cannot find object or property. (0x80092004)" == message ||
           "oStore.Certificates is undefined" == message ||
           "Объект или свойство не найдено. (0x80092004)" == message)
        {
            oStore.Close();
            var errormes = document.getElementById("boxdiv").style.display = '';
            return;
        }
    }

    for (var i = 1; i <= certCnt; i++) {
        var cert;
        try {
            cert = oStore.Certificates.Item(i);
        }
        catch (ex) {
            alert("Ошибка при перечислении сертификатов: " + GetErrorMessage(ex));
            return;
        }

        var oOpt = document.createElement("OPTION");
        var dateObj = new Date();
        try {
            if(dateObj<cert.ValidToDate && cert.HasPrivateKey() && cert.IsValid().Result) {
                var certObj = new CertificateObj(cert);
                oOpt.text = certObj.GetCertString();
            }
            else {
                continue;
            }
        }
        catch (ex) {
            alert("Ошибка при получении свойства SubjectName: " + GetErrorMessage(ex));
        }
        try {
            oOpt.value = cert.Thumbprint;
        }
        catch (ex) {
            alert("Ошибка при получении свойства Thumbprint: " + GetErrorMessage(ex));
        }

        lst.options.add(oOpt);
    }

    oStore.Close();
}

function decimalToHexString(number) {
    if (number < 0) {
        number = 0xFFFFFFFF + number + 1;
    }

    return number.toString(16).toUpperCase();
}

function GetErrorMessage(e) {
    var err = e.message;
    if (!err) {
        err = e;
    } else if (e.number) {
        err += " (0x" + decimalToHexString(e.number) + ")";
    }
    return err;
}