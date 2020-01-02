const t_domain = "/ttt";
const keys = ["createRole", "levelUp", "enterServer"];
const messgae = ["SUCCESS", "FAIL"];
const OrderStatus = [0, 1, 2, 3, 4, 5];
const OrderStatusDesc = [
    "点开充值界面     未点充值按钮（取消支付）",
    "选择充值方式界面  未选择充值方式（取消支付）",
    "支付宝微信界面   未支付（取消支付）",
    "支付成功 未发货",
    "支付成功 已发货(交易完成)",
    "支付成功 补单(交易完成)"
];
let OrderStateMsg = [0, "发送成功", 2, 3, 4, 5,
    "角色不存在", 7, 8, 9, "金额错误",
    "订单错误", 12, 13, 14, 15,
    16, "参数与订单参数不一致", "参数为空或非法", "账号不存在"];

const appId_CS = 2;
const appId_SGYX = 9999;
$(function () {
    let appId = getCookies("appId");
    let name = getCookies("username");
    let pwd = getCookies("password");
    let uid = getCookies("channelUid");
    let aid = getCookies("accountid");

    console.info("游戏id" + appId);
    console.info("账号" + name);
    console.info("密码" + pwd);
    console.info("uid" + uid);
    console.info("aid" + aid);

    if (checkParam(appId)) {
        alert("请选择游戏");
        window.location.href = "game.html";
    }
    if (!checkParam(name)) {
        $('#username').val(name);
    }
    if (!checkParam(pwd)) {
        $('#password').val(pwd);
    }
    if (!checkParam(uid)) {
        $('#uid').val(uid);
    }
    if (!checkParam(aid)) {
        $('#aid').val(aid);
    }
});

/**
 * @param param 判断的参数
 * @return {boolean}
 * */
function checkParam(param) {
    return (param == null || param === "" || param === "undefined");
}

function test_Register() {
    let auto = "true";
    let appId = getCookies("appId");
    let channelId = "0";
    let channelUid = "";

    let username = $("#username").val();
    let password = $("#password").val();
    let phone = "";
    let deviceCode = "PC";
    let imei = "PC";
    let addparm = "";

    let result = register(auto, appId,
        channelId, channelUid, "account_" + channelUid, "name_" + channelUid,
        username, password, phone, deviceCode, imei, addparm);

    console.info("test_Register");
    console.info(result);

    alert(result.reason);

    if (result.message === messgae[0]) {
        $('#username').val(result.account);
        $('#password').val(result.password);
        $('#uid').val(result.channelUid);
        $('#aid').val(result.uid);

        let res = "账号: " + result.account + " 密码: " + result.password + " Uid: " + result.uid;
        $('#res').val(res);
        setCookie("username", result.account);
        setCookie("password", result.password);
        setCookie("accountid", result.uid);
        setCookie("channelUid", result.channelUid);
    }
}

/**
 * 注册-接口
 * @param   {boolean}       auto             是否自动注册,无需账号密码
 * @param   {number}        appId            游戏id
 * @param   {number}        channelId        渠道id
 * @param   {string}        channelUid       渠道账号id
 * @param   {string}        channelUname     渠道账号名称
 * @param   {string}        channelUnick     渠道账号昵称
 * @param   {string}        username         指悦账号
 * @param   {string}        password         指悦账号密码
 * @param   {string}        phone            手机号
 * @param   {string}        deviceCode
 * @param   {string}        imei
 * @param   {string}        addparm          额外参数
 * @return  {json}
 * */
function register(auto, appId,
                  channelId, channelUid, channelUname, channelUnick,
                  username, password,
                  phone, deviceCode, imei,
                  addparm) {
    let way = "注册-接口 ";
    if (auto === "true") {
        if (checkParam(channelId)) {
            console.error(way + "参数错误" + " channelId=" + channelId);
            return null;
        }
        if (channelId === "0") {
            //官方渠道
            channelUid = "0";
        } else {
            //其他渠道
            if (checkParam(channelUid)) {
                console.error(way + "参数错误" + " channelUid=" + channelUid);
                return null;
            }
        }
    } else {
        if (checkParam(username) || checkParam(password)) {
            console.error(way + "参数错误" + " username=" + username + " password=" + password);
            return null;
        }
    }

    let data = {
        "auto": auto,
        "appId": appId,
        "channelId": channelId,
        "channelUid": channelUid,
        "channelUname": channelUname,
        "channelUnick": channelUnick,

        "username": username,
        "pwd": password,
        "phone": phone,
        "deviceCode": deviceCode,
        "imei": imei,
        "addparm": addparm
    };

    let response;
    $.ajax({
        url: t_domain + "/register",
        type: "post",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(data),
        dataType: "json",
        async: false,
        success: function (result) {
            console.info(result);
            if (result.resultCode === 200) {
                if (result.data.message === messgae[0]) {
                    console.info(way + "recv ", result.data.reason);
                    console.info(way + "recv 账号 " + result.data.account);
                    console.info(way + "recv 密码 " + result.data.password);
                    console.info(way + "recv uid " + result.data.accountId);
                    console.info(way + "recv channelUid " + result.data.channelUid);
                    response = {
                        "account": result.data.account,
                        "password": result.data.password,
                        "uid": result.data.accountId,
                        "channelUid": result.data.channelUid,
                        "reason": result.data.reason,
                        "message": result.data.message
                    }
                } else {
                    console.error(way + "recv ", result.data.reason);
                    response = {
                        "reason": result.data.reason,
                        "message": result.data.message
                    }
                }
            }
        },
        error: function () {
            console.error(way + "通信失败");
        }
    });
    return response;
}

function test_Login() {
    let isChannel = "false";
    let appId = getCookies("appId");
    let channelId = "0";
    let accountId = $('#aid').val();
    let channelUid = $('#uid').val();
    let username = $('#username').val();
    let password = $('#password').val();

    if (checkParam(channelUid)) {
        channelUid = "0";
    }


    let result = login(isChannel, appId, channelId, channelUid, username, password);
    console.info("test_Login");
    console.info(result);

    if (result.message === messgae[1]) {
        alert(result.reason);
    } else if (result.message === messgae[0]) {
        let loginResult = loginCheck(result.appid, result.uid, result.token, result.sign);
        console.info("test_Login");
        console.info(loginResult);

        if (loginResult.message === messgae[0]) {
            setCookie("username", username);
            setCookie("password", password);
            setCookie("accountid", loginResult.accountId);
            setCookie("channelUid", channelUid);
            entergame(loginResult.accountId, appId);
        } else {
            alert(loginResult.reason);
        }
    }
}

/**
 * 请求登录-接口
 * @param    {boolean}      isChannel        是否渠道自动注册
 * @param   {number}        appId            游戏id
 * @param   {number}        channelId        渠道id
 * @param   {string}        channelUid       渠道账号id
 * @param   {string}        username         指悦账号
 * @param   {string}        password         指悦账号密码
 * @return  {json}
 * */
function login(isChannel, appId,
               channelId, channelUid,
               username, password) {
    let way = "请求登录-接口 ";
    if (checkParam(appId)) {
        console.error(way + "参数错误" + " appId=" + appId);
        return null;
    }

    if (isChannel === "true") {
        if (checkParam(channelId)) {
            console.error(way + "参数错误" + " channelId=" + channelId);
            return null;
        }
        if (checkParam(channelUid)) {
            console.error(way + "参数错误" + " channelUid=" + channelUid);
            return null;
        }
    } else if (isChannel === "false") {
        if (checkParam(username) || checkParam(password)) {
            console.error(way + "参数错误" + " username=" + username + " password=" + password);
            return null;
        }
    } else {
        console.error(way + "参数错误" + " isChannel=" + isChannel);
        return null;
    }

    let data = {
        "isChannel": isChannel,
        "appId": appId,
        "channelId": channelId,
        "channelUid": channelUid,
        "name": username,
        "pwd": password
    };
    let response;
    $.ajax({
        url: t_domain + "/login",
        type: "post",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(data),
        dataType: "json",
        async: false,
        success: function (result) {
            console.info(result);
            if (result.message === messgae[0]) {
                console.info(way + "recv reason " + result.reason);
                console.info(way + "recv appid " + result.appid);
                console.info(way + "recv token " + result.token);
                console.info(way + "recv uid " + result.uid);
                console.info(way + "recv sign " + result.sign);
                response = {
                    "appid": result.appid,
                    "uid": result.uid,
                    "token": result.token,
                    "sign": result.sign,
                    "reason": result.reason,
                    "message": result.message
                };
            } else {
                console.error("recv ", result.reason);
                response = {
                    "reason": result.reason,
                    "message": result.message
                };
            }
        },
        error: function () {
            console.error(way + "请求登录接口失败");
        }
    });
    return response;
}

/**
 * 请求登录校验-接口
 * @param   {number}        appId           游戏id
 * @param   {number}        accountId       指悦uid
 * @param   {string}        token           登录token
 * @param   {string}        sign            签名
 * @return  {json}
 * */
function loginCheck(appId, accountId, token, sign) {
    let way = "请求登录校验-接口 ";
    if (checkParam(appId) || checkParam(accountId) || checkParam(token) || checkParam(sign)) {
        console.error(way + "参数错误" + " appId=" + appId + " accountId=" + accountId + " token=" + token + " sign=" + sign);
        return null;
    }
    let param = "appId=" + appId + "&uid=" + accountId + "&token=" + token + "&sign=" + sign;
    console.info(way + "send " + param);
    let response;
    $.ajax({
        url: t_domain + "/check?" + param,
        type: "get",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        async: false,
        success: function (result) {
            console.info(result);
            if (result.resultCode === 200) {
                let rdata = result.data;
                if (result.data.message === messgae[0]) {
                    console.info(way + "recv reason " + rdata.reason);
                    console.info(way + "recv uid " + rdata.accountId);

                    response = {
                        "accountId": rdata.accountId,
                        "reason": rdata.reason,
                        "message": rdata.message
                    }
                } else {
                    console.error(way + "recv ", rdata.reason);
                    response = {
                        "reason": rdata.reason,
                        "message": rdata.message
                    }
                }
            }
        },
        error: function () {
            console.error(way + "登录校验失败");
        }
    });
    return response;
}


function entergame(accountId, appid) {
    let url = "/ttt/autoGame" + "?accountId=" + accountId + "&appId=" + appid + "&serverId=" + "1";
    $.ajax({
        url: url,
        type: "get",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (result) {
            console.info(result);
            if (result.resultCode === 200) {
                let loginUrl = result.url;
                $('#res').val(loginUrl);
                // window.location.href = loginUrl;
                window.open(loginUrl);
            }
        },
        error: function () {
            alert("系统提示", "操作失败");
        }
    });
}

function auto() {
    let appId = getCookies("appId");
    let url = "/ttt/autoReg?auto=true&appid=" + appId;

    $.ajax({
        url: url,
        type: "get",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (result) {
            console.info(result);
            if (result.resultCode === 200) {
                if (result.status === 1) {
                    $('#username').val(result.account);
                    $('#password').val(result.pwd);

                    setCookie("username", result.account);
                    setCookie("password", result.pwd);
                    setCookie("accountid", result.accountId);

                    let res = "账号: " + result.account + " 密码: " + result.pwd + " Uid: " + result.accountId;
                    $('#res').val(res);
                    alert("注册成功");
                }
            }
        },
        error: function () {
            alert("系统提示", "操作失败");
        }
    });
}

let t_appid;
let t_token;
let t_accountId;
let t_sign;

/*
function login() {
    let appId = getCookies("appId");
    let account = $('#username').val();
    let password = $('#password').val()

    setCookie("username", account);
    setCookie("password", password);

    if (appId == null || appId === "") {
        alert("请选择游戏");
        window.location.href = "game.html";
    }

    let data = {
        "appId": appId,
        "isChannel": "false",
        "name": account,
        "pwd": password,
        "channelId": "0",
        "channelUid": ""
    };
    $.ajax({
        url: "/ttt/login",
        type: "post",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(data),
        dataType: "json",
        async: false,
        success: function (result) {
            if (result.resultCode === 200) {

                t_appid = result.appid;
                t_token = result.token;
                t_accountId = result.uid;
                t_sign = result.sign;

                let data = "appId=" + t_appid + "token=" + t_token + "uid=" + t_accountId + "sign=" + t_sign;
                console.info(data);

                logincheck();
            }
        },
        error: function (result) {
            console.info(result);
            alert(result.err);
        }
    });
}

function logincheck() {
    let url = "/ttt/check" + "?appId=" + t_appid + "&token=" + t_token + "&uid=" + t_accountId + "&sign=" + t_sign;
    console.info("logincheck:" + url);

    $.ajax({
        url: url,
        type: "get",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (result) {
            console.log("result：" + result.status);
            if (result.resultCode === 200) {
                entergame();
            }
        },
        error: function () {
            alert("系统提示", "操作失败");
        }
    })
}

function entergame() {
    let url = "/ttt/autoGame" + "?accountId=" + t_accountId + "&appId=" + t_appid + "&serverId=" + "1";
    $.ajax({
        url: url,
        type: "get",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (result) {
            console.info(result);
            if (result.resultCode === 200) {
                let loginUrl = result.url;
                $('#res').val(loginUrl);
                // window.location.href = loginUrl;
                window.open(loginUrl);
            }
        },
        error: function () {
            alert("系统提示", "操作失败");
        }
    });
}*/

function gameList() {
    window.location.href = "game.html";
}