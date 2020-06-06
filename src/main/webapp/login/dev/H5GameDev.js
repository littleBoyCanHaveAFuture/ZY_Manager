let url = "/zhiyue";

// url = "/zysdk/zhiyue"
/**
 * 渠道账号自动注册指悦账号
 * @param regInfo
 * @param regInfo.appId
 * @param regInfo.channelId
 * @param regInfo.appKey
 * @param regInfo.addParam
 * @param return
 * */
function zy_Register() {
    let regInfo = {};
    regInfo.appId = appId;
    regInfo.channelId = channelId;
    regInfo.appKey = appKey;
    regInfo.addParam = "巨龙战歌-官方注册";
    ZhiyueAutoReg(regInfo, function (data) {
        console.info("status = ", data.status);
        console.info("message = ", data.message);
        if (data.status) {
            let ZyUid = data.uid;
            let username = data.account;
            let password = data.password;
            let channelUid = data.channelUid;

            $("#uid").val(ZyUid);
            $("#username").val(username);
            $("#password").val(password);
            $("#channelUid").val(channelUid);

            setCookie("zy_account", username);
            setCookie("zy_password", password);
            setCookie("zy_channelUid", channelUid);
            showLoginPage();
            zy_Login();
        }
    });
}

function ZhiyueAutoReg(regInfo, callback) {
    console.info("autoReg = " + regInfo);
    let rspObj = {};
    $.ajax({
        url: url + "/autoReg",
        type: "post",
        contentType: "text/plain; charset=utf-8",
        data: JSON.stringify(regInfo),
        dataType: "json",
        async: false,
        success: function (result) {
            console.info(result);
            if (result.hasOwnProperty('status')) {
                if (result.status === true) {
                    rspObj.status = true;
                    rspObj.message = result.message;

                    rspObj.uid = result.zhiyueUid;
                    rspObj.account = result.account;
                    rspObj.password = result.password;
                    rspObj.channelUid = result.channelUid;
                } else {
                    rspObj.status = false;
                    rspObj.message = result.message;
                }
            } else {
                rspObj.status = false;
                rspObj.message = "通信失败";
            }
            callback(rspObj);
        },
        error: function () {
            rspObj.message = "通信失败";
            rspObj.status = false;
            callback(rspObj);
        }
    });
}

function zy_Login() {
    if (is_select === false) {
        alert("登录失败,请同意用户协议");
        return;
    }
    let loginInfo = {};
    loginInfo.channelId = channelId;
    loginInfo.appId = appId;
    loginInfo.appKey = appKey;
    loginInfo.username = $("#username").val();
    loginInfo.password = $("#password").val();

    console.info(loginInfo);

    ZhiyueLogin(loginInfo, function (callbackLoginData) {
        if (callbackLoginData.status === false) {
            alert("登录失败", callbackLoginData.message);
        } else {
            console.log("ZhiyueLogin " + callbackLoginData);

            let channelUid = callbackLoginData.channelUid;
            let loginUrl = callbackLoginData.loginUrl;

            $("#channelUid").val(callbackLoginData.channelUid);


            setCookie("zy_account", loginInfo.username);
            setCookie("zy_password", loginInfo.password);
            setCookie("zy_channelUid", channelUid);

            //iframe
            window.location.replace(loginUrl);
        }
    });
}

/**获取指悦Uid*/
function ZhiyueLogin(loginInfo, callback) {
    console.info("autoReg = " + loginInfo);
    let rspObj = {};
    if (document.getElementById("phoneLogin").hidden === false) {
        zy_phone_Login();
        return;
    }

    $.ajax({
        url: url + "/autoLogin",
        type: "post",
        contentType: "text/plain; charset=utf-8",
        data: JSON.stringify(loginInfo),
        dataType: "json",
        async: false,
        success: function (result) {
            console.info(result);
            if (result.hasOwnProperty('status')) {
                if (result.status === true) {
                    rspObj.status = true;
                    rspObj.message = result.message;

                    rspObj.uid = result.channelUid;
                    rspObj.loginUrl = result.loginUrl;
                } else {
                    rspObj.status = false;
                    rspObj.message = result.message;
                }
            } else {
                rspObj.status = false;
                rspObj.message = "通信失败";
            }
            callback(rspObj);
        },
        error: function () {
            rspObj.message = "通信失败";
            rspObj.status = false;
            callback(rspObj);
        }
    });
}

function ZhiYuePhoneCode() {
    let phone = $("#phone_username").val();

    let url = "https://zyh5games.com/zysdk/zhiYueSms/PhoneCodeReg" + "?" +
        "phone=" + phone + "&appId=" + appId + "&appKey=" + appKey;
    url = "/zhiYueSms/PhoneCodeReg" + "?" +
        "phone=" + phone + "&appId=" + appId + "&appKey=" + appKey;
    let rspObj = {};
    $.ajax({
        url: url,
        type: "get",
        dataType: "json",
        async: false,
        success: function (result) {
            console.info(result);
            if (result.hasOwnProperty('status')) {
                if (result.status === true) {
                    tip("获取验证码成功，请稍后");
                } else {
                    tip("获取验证码失败");
                }
            }
            console.log(result.message);
        }
    });
}

function ZhiYuePhoneLoginCode() {
    let phone = $("#username").val();

    let url = "https://zyh5games.com/zysdk/zhiYueSms/PhoneCodeLogin" + "?" +
        "phone=" + phone + "&appId=" + appId + "&appKey=" + appKey;
    url = "/zhiYueSms/PhoneCodeLogin" + "?" +
        "phone=" + phone + "&appId=" + appId + "&appKey=" + appKey;
    let rspObj = {};
    $.ajax({
        url: url,
        type: "get",
        dataType: "json",
        async: false,
        success: function (result) {
            console.info(result);
            if (result.hasOwnProperty('status')) {
                if (result.status === true) {
                    tip("获取验证码成功，请稍后");
                } else {
                    tip("获取验证码失败");
                }
            }
            console.log(result.message);
        }
    });
}

function phone() {
    phone_Register(function (data) {
        console.info("status = ", data.status);
        console.info("message = ", data.message);
        if (data.status) {
            let ZyUid = data.uid;
            let username = data.account;
            let password = data.password;
            let channelUid = data.channelUid;

            $("#uid").val(ZyUid);
            $("#username").val(username);
            $("#password").val(password);
            $("#channelUid").val(channelUid);

            setCookie("zy_account", username);
            setCookie("zy_password", password);
            setCookie("zy_channelUid", channelUid);
            showLoginPage();
            // zy_Login();
        } else {
            alert(data.message);
        }
    })
}

function phone_Register(callback) {
    let regInfo = {};
    regInfo.appId = appId;
    regInfo.channelId = channelId;
    regInfo.appKey = appKey;
    regInfo.code = $("#phone_logincode").val();
    regInfo.phone = $("#phone_username").val();
    regInfo.password = $("#phone_password").val();

    console.info("autoReg = " + regInfo);

    let url = "https://zyh5games.com/zysdk/zhiYueSms/PhoneReg";
    url = "/zhiYueSms/PhoneReg";

    let rspObj = {};
    $.ajax({
        url: url,
        type: "post",
        contentType: "text/plain; charset=utf-8",
        data: JSON.stringify(regInfo),
        dataType: "json",
        async: false,
        success: function (result) {
            console.info(result);
            if (result.hasOwnProperty('status')) {
                if (result.status === true) {
                    rspObj.status = true;
                    rspObj.message = result.message;

                    rspObj.uid = result.zhiyueUid;
                    rspObj.account = result.account;
                    rspObj.password = result.password;
                    rspObj.channelUid = result.channelUid;
                } else {
                    rspObj.status = false;
                    rspObj.message = result.message;
                }
                callback(rspObj);
            } else {
                rspObj.status = false;
                rspObj.message = "通信失败";
                callback(rspObj);
            }
        },
        error: function () {
            rspObj.message = "通信失败";
            rspObj.status = false;
            callback(rspObj);
        }
    });
}

function zy_phone_Login() {
    if (is_select === false) {
        alert("登录失败,请同意用户协议");
        return;
    }
    let loginInfo = {};
    loginInfo.appId = appId;
    loginInfo.channelId = channelId;
    loginInfo.appKey = appKey;
    loginInfo.phone = $("#username").val();
    loginInfo.password = $("#phone_password").val();
    loginInfo.code = $("#phone_logincode").val();
    console.info(loginInfo);
    phone_Login(loginInfo, function (callbackLoginData) {
        if (callbackLoginData.status === false) {
            alert("登录失败", callbackLoginData.message);
        } else {
            console.log("ZhiyueLogin " + callbackLoginData);

            let channelUid = callbackLoginData.channelUid;
            let loginUrl = callbackLoginData.loginUrl;

            $("#channelUid").val(callbackLoginData.channelUid);


            setCookie("zy_account", loginInfo.username);
            setCookie("zy_password", loginInfo.password);
            setCookie("zy_channelUid", channelUid);

            //iframe
            window.location.replace(loginUrl);
        }
    });
}

function phone_Login(loginInfo, callback) {
    console.info("autoReg = " + loginInfo);
    let rspObj = {};
    let url = "https://zyh5games.com/zysdk/zhiYueSms/PhoneLogin";
    url = "/zhiYueSms/PhoneLogin";

    $.ajax({
        url: url,
        type: "post",
        contentType: "text/plain; charset=utf-8",
        data: JSON.stringify(loginInfo),
        dataType: "json",
        async: false,
        success: function (result) {
            console.info(result);
            if (result.hasOwnProperty('status')) {
                if (result.status === true) {
                    rspObj.status = true;
                    rspObj.message = result.message;

                    rspObj.uid = result.channelUid;
                    rspObj.loginUrl = result.loginUrl;
                } else {
                    rspObj.status = false;
                    rspObj.message = result.message;
                }
            } else {
                rspObj.status = false;
                rspObj.message = "通信失败";
            }
            callback(rspObj);
        },
        error: function () {
            rspObj.message = "通信失败";
            rspObj.status = false;
            callback(rspObj);
        }
    });
}
