let t_url = "/pay/webGame";
t_url = "/webGame";
$(function () {
    let appId = getCookies("zy_appId");
    let channelId = getCookies("zy_channelId");
    let username = getCookies("zy_user");
    let password = getCookies("zy_pwd");
    let channelUid = getCookies("zy_channelUid");
    let uid = getCookies("zy_uid");

    console.info("游戏id=" + appId);
    console.info("渠道id=" + channelId);
    console.info("账号=" + username);
    console.info("密码=" + password);
    console.info("uid=" + uid);
    console.info("渠道uid=" + channelUid);

    if (checkParam(appId)) {
        alert("请选择游戏");
        window.location.href = "game.html";
    } else {
        $('#appId').val(appId);
    }
    if (!checkParam(channelId)) {
        $('#channelId').val(channelId);
    }
    if (!checkParam(username)) {
        $('#username').val(username);
    }
    if (!checkParam(password)) {
        $('#password').val(password);
    }
    if (!checkParam(channelUid)) {
        $('#channelUid').val(channelUid);
    }
    if (!checkParam(uid)) {
        $('#uid').val(uid);
    }
});

/**
 * @param param 判断的参数
 * @return {boolean}
 * */
function checkParam(param) {
    return (param == null || param === "" || param === "undefined");
}

function gameList() {
    window.location.href = "game.html";
}

function zy_Register() {
    let regInfo = {};
    regInfo.auto = Boolean(true);
    regInfo.appId = Number($("#appId").val());
    regInfo.channelId = Number($("#channelId").val());
    regInfo.channelUid = $("#channelUid").val();
    regInfo.username = $("#username").val();
    regInfo.password = $("#password").val();
    regInfo.phone = "";
    regInfo.deviceCode = "PC";
    regInfo.imei = "PC";
    regInfo.addparm = "官方注册";

    console.info(regInfo);

    sdk_ZyRegister(regInfo, function (callbackData) {
        if (callbackData.state === false) {
            console.info(callbackData.message);
        } else {
            console.info(callbackData.message);
            console.info(JSON.stringify(callbackData));

            let ZyUid = callbackData.uid;
            let username = callbackData.account;
            let password = callbackData.password;
            let channelUid = callbackData.channelUid;

            $("#uid").val(ZyUid);
            $("#username").val(username);
            $("#password").val(password);
            $("#channelUid").val(channelUid);

            let res = "账号: " + username + " 密码: " + password + " Uid: " + channelUid;
            $('#copy').val(res);
            setCookie("zy_uid", ZyUid);
            setCookie("zy_user", username);
            setCookie("zy_pwd", password);
            setCookie("zy_channelUid", channelUid);
        }
    });
}

function zy_Login() {
    let loginInfo = {};
    loginInfo.isAuto = Boolean(true);
    loginInfo.username = $("#username").val();
    loginInfo.password = $("#password").val();
    loginInfo.GameId = $("#appId").val();
    loginInfo.channelId = $("#channelId").val();
    loginInfo.channelUid = $("#channelUid").val();
    loginInfo.timestamp = new Date().valueOf();

    sdk_ZyLogin(loginInfo, function (callbackLoginData) {
        if (callbackLoginData.state === false) {
            alert("登录失败", callbackLoginData.message);
        } else {
            let ZyUid = callbackLoginData.zyUid;
            let username = callbackLoginData.username;
            let password = callbackLoginData.password;
            let channelUid = callbackLoginData.channelUid;
            let loginUrl = callbackLoginData.loginUrl;

            $("#uid").val(callbackLoginData.zyUid);
            $("#username").val(callbackLoginData.username);
            $("#password").val(callbackLoginData.password);
            $("#channelUid").val(callbackLoginData.channelUid);

            setCookie("zy_uid", ZyUid);
            setCookie("zy_user", username);
            setCookie("zy_pwd", password);
            setCookie("zy_channelUid", channelUid);
            alert("登录成功", callbackLoginData.message);
            window.open(loginUrl);
        }
    });
}


/**
 * 注册-接口
 * @param   {object}        regInfo                  注册信息
 * @param   {boolean}       regInfo.auto             是否无需账号密码注册
 * @param   {number}        regInfo.appId            游戏id
 * @param   {number}        regInfo.channelId        渠道id
 * @param   {string}        regInfo.channelUid       渠道账号id
 * @param   {string}        regInfo.channelUname     渠道账号名称
 * @param   {string}        regInfo.channelUnick     渠道账号昵称
 * @param   {string}        regInfo.username         指悦账号
 * @param   {string}        regInfo.password         指悦账号密码
 * @param   {string}        regInfo.phone            手机号
 * @param   {string}        regInfo.deviceCode
 * @param   {string}        regInfo.imei
 * @param   {string}        regInfo.addparm          额外参数
 * @param   {function}      callback
 * */
function sdk_ZyRegister(regInfo, callback) {
    let rspObject = {};
    if (!regInfo.hasOwnProperty('auto')) {
        rspObject.message = "auto 参数为空";
        rspObject.state = false;
        callback(rspObject);
        return;
    }
    if (!regInfo.hasOwnProperty('appId')) {
        rspObject.message = "appId 参数为空";
        rspObject.state = false;
        callback(rspObject);
        return;
    }
    if (regInfo.auto === true) {
        let mustKey = ['channelId', 'channelUid'];
        if (regInfo.hasOwnProperty('channelId') && regInfo.channelId === 0) {

        } else {
            for (let keyIndex of mustKey) {
                if (!regInfo.hasOwnProperty(keyIndex)) {
                    rspObject.message = "渠道id 或 渠道uid 为空";
                    rspObject.state = false;
                    callback(rspObject);
                    return;
                }
            }
        }
    } else {
        let mustKey = ['channelId', 'username', 'password'];
        for (let keyIndex of mustKey) {
            if (!regInfo.hasOwnProperty(keyIndex)) {
                rspObject.message = "用户名 或 密码 为空";
                rspObject.state = false;
                callback(rspObject);
                return;
            }
        }
    }

    $.ajax({
        url: t_url + "/register",
        type: "post",
        contentType: "text/plain; charset=utf-8",
        data: JSON.stringify(regInfo),
        dataType: "json",
        async: false,
        success: function (result) {
            console.info(result);
            if (result.hasOwnProperty('state')) {
                if (result.state === true) {
                    rspObject.state = true;
                    rspObject.message = result.message;

                    rspObject.uid = result.accountId;
                    rspObject.account = result.account;
                    rspObject.password = result.password;
                    rspObject.channelUid = result.channelUid;
                } else {
                    rspObject.state = false;
                    rspObject.message = result.message;
                }
            } else {
                rspObject.state = false;
                rspObject.message = "通信失败";
            }
            callback(rspObject);
        },
        error: function () {
            rspObject.message = "通信失败";
            rspObject.state = false;
            callback(rspObject);
        }
    });
}

/**
 * 请求登录-接口
 * @param   {Object}        loginInfo
 * @param   {boolean}       loginInfo.isAuto           是否渠道自动注册
 * @param   {number}        loginInfo.GameId           游戏id
 * @param   {number}        loginInfo.channelId        渠道id
 * @param   {string}        loginInfo.channelUid       渠道账号id
 * @param   {string}        loginInfo.username         指悦账号
 * @param   {string}        loginInfo.password         指悦账号密码
 * @param   {number}        loginInfo.timestamp        时间戳
 * @param   {string}        loginInfo.sign             签名
 * @param   {function}      callback                   回调函数
 * */
function sdk_ZyLogin(loginInfo, callback) {
    let rspObject = {};

    let mustKey = ['isAuto', 'GameId', 'channelId'];
    for (let keyIndex of mustKey) {
        if (!loginInfo.hasOwnProperty(keyIndex)) {
            rspObject.state = false;
            rspObject.message = "参数为空：isAuto GameId channelId";
            callback(rspObject);
            return;
        }
    }
    if (loginInfo.isAuto === true) {
        if (checkParam(loginInfo.channelId) || checkParam(loginInfo.channelUid)) {
            rspObject.state = false;
            rspObject.message = "参数为空：channelId  channelUid";
            callback(rspObject);
            return;
        }
    } else if (loginInfo.isAuto === false) {
        if (checkParam(loginInfo.username) || checkParam(loginInfo.password)) {
            rspObject.state = false;
            rspObject.message = "参数为空：username  password";
            callback(rspObject);
            return;
        }
    } else {
        rspObject.state = false;
        rspObject.message = "参数错误：isAuto";
        callback(rspObject);
        return;
    }
    let param = "isAuto" + "=" + loginInfo.isAuto + "&" +
        "GameId" + "=" + loginInfo.GameId + "&" +
        "channelId" + "=" + loginInfo.channelId + "&" +
        "channelUid" + "=" + loginInfo.channelUid + "&" +
        "username" + "=" + loginInfo.username + "&" +
        "password" + "=" + loginInfo.password + "&" +
        "timestamp" + "=" + loginInfo.timestamp;

    //md5 加密
    loginInfo.sign = md5(param, t_key);

    let params = param + "&sign=" + loginInfo.sign;

    console.info(params);

    $.ajax({
        url: t_url + "/login?" + params,
        type: "get",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (result) {
            if (result.state === true) {
                rspObject.message = result.message;
                rspObject.state = result.state;

                rspObject.GameId = result.GameId;
                rspObject.channelId = result.channelId;
                rspObject.channelUid = result.channelUid;
                rspObject.channelToken = result.channelToken;
                rspObject.zyUid = result.zyUid;
                rspObject.username = result.username;
                rspObject.password = result.password;
                rspObject.loginUrl = result.loginUrl;
                rspObject.paybackUrl = result.paybackUrl;
                callback(rspObject);
            } else {
                rspObject.message = result.message;
                rspObject.state = result.state;
                callback(rspObject);
            }
        },
        error: function () {
            rspObject.message = "通信失败";
            rspObject.state = false;
            callback(rspObject);
        }
    });
}

function md5(info, secretKey) {
    let strInfo = info;
    strInfo += "&" + secretKey;

    let sign_uri = encodeURIComponent(strInfo);
    let hex_sign_uri = hex_md5(sign_uri);

    return hex_sign_uri;
}
