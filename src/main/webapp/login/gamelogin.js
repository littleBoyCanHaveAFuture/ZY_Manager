$(function () {
    let appId = getCookies("appId");
    let name = getCookies("username");
    let pwd = getCookies("password");

    console.info(appId);
    console.info(name);
    console.info(pwd);

    if (appId == null || appId === "") {
        alert("请选择游戏");
        window.location.href = "game.html";
    }

    $('#username').val(name);
    $('#password').val(pwd);
});

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
}
function gameList(){
    window.location.href = "game.html";
}