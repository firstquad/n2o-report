function CertificateAdjuster()
{
}

CertificateAdjuster.prototype.extract = function(from, what)
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

CertificateAdjuster.prototype.Print2Digit = function(digit)
{
    return (digit<10) ? "0"+digit : digit;
}

CertificateAdjuster.prototype.GetCertDate = function(paramDate)
{
    var certDate = new Date(paramDate);
    return this.Print2Digit(certDate.getUTCDate())+"."+this.Print2Digit(certDate.getMonth()+1)+"."+certDate.getFullYear() + " " +
        this.Print2Digit(certDate.getUTCHours()) + ":" + this.Print2Digit(certDate.getUTCMinutes()) + ":" + this.Print2Digit(certDate.getUTCSeconds());
}

CertificateAdjuster.prototype.GetCertName = function(certSubjectName)
{
    return this.extract(certSubjectName, 'CN=');
}

CertificateAdjuster.prototype.GetIssuer = function(certIssuerName)
{
    return this.extract(certIssuerName, 'CN=');
}

CertificateAdjuster.prototype.GetCertInfoString = function(certSubjectName, certFromDate)
{
    return this.extract(certSubjectName,'CN=') + "; Выдан: " + this.GetCertDate(certFromDate);
}

function CheckForPlugIn_Async() {
    $("#btn-sign").show();
    $("#cert-list-group").show();
    var CurrentPluginVersion;
    cadesplugin.async_spawn(function *() {
        var oAbout = yield cadesplugin.CreateObjectAsync("CAdESCOM.About");
        FillCertList_Async('CertListBox');
    });//cadesplugin.async_spawn
}

function FillCertList_Async(lstId) {
    $("#loader").fadeIn();
    cadesplugin.async_spawn(function *() {
        var oStore = yield cadesplugin.CreateObjectAsync("CAdESCOM.Store");
        if (!oStore) {
            alert("store failed");
            return;
        }

        try {
            yield oStore.Open();
        }
        catch (ex) {
            alert("Ошибка при открытии хранилища: " + GetErrorMessage(ex));
            return;
        }

        var lst = document.getElementById(lstId);
        if(!lst)
        {
            return;
        }
        var certCnt;
        var certs;

        try {
            certs = yield oStore.Certificates;
            certCnt = yield certs.Count;
        }
        catch (ex) {
            var errormes = document.getElementById("boxdiv").style.display = '';
            return;
        }

        if(certCnt == 0)
        {
            var errormes = document.getElementById("boxdiv").style.display = '';
            return;
        }

        var percVal = 100 / certCnt;

        for (var i = 1; i <= certCnt; i++) {
            var cert;
            try {
                cert = yield certs.Item(i);
            }
            catch (ex) {
                alert("Ошибка при перечислении сертификатов: " + GetErrorMessage(ex));
                return;
            }

            var oOpt = document.createElement("OPTION");
            var dateObj = new Date();
            try {
                var ValidToDate = new Date((yield cert.ValidToDate));
                var ValidFromDate = new Date((yield cert.ValidFromDate));
                var Validator = yield cert.IsValid();
                var IsValid = yield Validator.Result;
                $("#loader .progress-bar").width(i*percVal+'%');
                if(dateObj< ValidToDate && (yield cert.HasPrivateKey()) && IsValid) {
                    oOpt.text = new CertificateAdjuster().GetCertInfoString(yield cert.SubjectName, ValidFromDate);
                }
                else {
                    continue;
                }
            }
            catch (ex) {
                alert("Ошибка при получении свойства SubjectName: " + GetErrorMessage(ex));
            }
            try {
                oOpt.value = yield cert.Thumbprint;
            }
            catch (ex) {
                alert("Ошибка при получении свойства Thumbprint: " + GetErrorMessage(ex));
            }

            lst.options.add(oOpt);
        }
        $("#loader").fadeOut();
        yield oStore.Close();
    });//cadesplugin.async_spawn
}

function SignHash_Async(certListBoxId, hashValue, hashAlg) {
    cadesplugin.async_spawn(function*(arg) {
        var e = document.getElementById(arg[0]);
        var selectedCertID = e.selectedIndex;
        if (selectedCertID == -1) {
            alert("Select certificate");
            return;
        }

        var thumbprint = e.options[selectedCertID].value.split(" ").reverse().join("").replace(/\s/g, "").toUpperCase();
        try {
            var oStore = yield cadesplugin.CreateObjectAsync("CAdESCOM.Store");
            yield oStore.Open();
        } catch (err) {
            $(document).triggerHandler('signature.fail', 'Failed to create CAdESCOM.Store: ' + err.number);
            return;
        }

        var CAPICOM_CERTIFICATE_FIND_SHA1_HASH = 0;
        var all_certs = yield oStore.Certificates;
        var oCerts = yield all_certs.Find(CAPICOM_CERTIFICATE_FIND_SHA1_HASH, thumbprint);

        if ((yield oCerts.Count) == 0) {
            $(document).triggerHandler('signature.fail', "Certificate not found");
            return;
        }
        var certificate = yield oCerts.Item(1);

        var Signature;
        try
        {
            var errormes = "";
            try {
                // Создаем объект CAdESCOM.HashedData
                var oHashedData = yield cadesplugin.CreateObjectAsync("CAdESCOM.HashedData");

                // Инициализируем объект заранее вычисленным хэш-значением
                // Алгоритм хэширования нужно указать до того, как будет передано хэш-значение
                yield oHashedData.Algorithm = hashAlg;
                yield oHashedData.SetHashValue(hashValue);
            } catch (err) {
                errormes = "Failed to create CAdESCOM.HashedData: " + err.number;
                $(document).triggerHandler('signature.fail', errormes);
                throw errormes;
            }

            try {
                var oSigner = yield cadesplugin.CreateObjectAsync("CAdESCOM.CPSigner");
            } catch (err) {
                errormes = "Failed to create CAdESCOM.CPSigner: " + err.number;
                $(document).triggerHandler('signature.fail', errormes);
                throw errormes;
            }
            var oSigningTimeAttr = yield cadesplugin.CreateObjectAsync("CADESCOM.CPAttribute");

            var CAPICOM_AUTHENTICATED_ATTRIBUTE_SIGNING_TIME = 0;
            yield oSigningTimeAttr.propset_Name(CAPICOM_AUTHENTICATED_ATTRIBUTE_SIGNING_TIME);
            var oTimeNow = new Date();
            yield oSigningTimeAttr.propset_Value(oTimeNow);
            var attr = yield oSigner.AuthenticatedAttributes2;
            yield attr.Add(oSigningTimeAttr);


            var oDocumentNameAttr = yield cadesplugin.CreateObjectAsync("CADESCOM.CPAttribute");
            var CADESCOM_AUTHENTICATED_ATTRIBUTE_DOCUMENT_NAME = 1;
            yield oDocumentNameAttr.propset_Name(CADESCOM_AUTHENTICATED_ATTRIBUTE_DOCUMENT_NAME);
            yield oDocumentNameAttr.propset_Value("Document Name");
            yield attr.Add(oDocumentNameAttr);

            if (oSigner) {
                yield oSigner.propset_Certificate(certificate);
            }
            else {
                errormes = "Failed to create CAdESCOM.CPSigner";
                $(document).triggerHandler('signature.fail', errormes);
                throw errormes;
            }


            var oSignedData = yield cadesplugin.CreateObjectAsync("CAdESCOM.CadesSignedData");
            var CADES_BES = 1;

            if (oHashedData) {
                var CADESCOM_CADES_BES = 1;

                // Вычисляем значение подписи
                try {
                    Signature = yield oSignedData.SignHash(oHashedData, oSigner, CADES_BES);
                } catch (err) {
                    $(document).triggerHandler('signature.fail', "Failed to create signature. Error: " + GetErrorMessage(err));
                    return;
                }
            }
//            alert(Signature);
            $(document).triggerHandler('signature.ready', Signature);
//            document.getElementById("SignatureTxtBox").innerHTML = Signature;
//            SignatureFieldTitle[0].innerHTML = "Подпись сформирована успешно:";
        }
        catch(err)
        {
            $(document).triggerHandler('signature.fail', "Возникла ошибка: "+err);
//            SignatureFieldTitle[0].innerHTML = "Возникла ошибка:";
//            document.getElementById("SignatureTxtBox").innerHTML = err;
        }
    }, certListBoxId, hashValue, hashAlg); //cadesplugin.async_spawn
}

async_resolve();
