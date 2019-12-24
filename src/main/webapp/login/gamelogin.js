$(function () {
    initSpGameServer(2);
});


function login() {
    let appId = $("#save_gameId").val();
    if (appId === -1) {
        $.messager.alert("系统提示", "请选择游戏：");
    }
    let data = {
        "appId": appId,
        "isChannel": "false",
        "name": t_account,
        "pwd": t_pwd,
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
                t_accountid = result.uid;
                t_sign = result.sign;

                let data = "appId=" + t_appid + "token=" + t_token + "uid=" + t_accountid + "sign=" + t_sign;
                console.log(data);

                logincheck();
            }
        },
        error: function (result) {
            $.messager.alert("系统提示", result.err);
        }
    });
}

function logincheck() {
    let appId = $("#save_gameId").val();
    let serverId = $("#save_serverId").val();
    let channelId = $("#save_spId").val();

    let data = "?appId=" + appId + "&token=" + t_token + "&uid=" + t_accountid + "&sign=" + t_sign;
    let url = "/ttt/check" + data;
    console.log("logincheck:" + url);

    $.ajax({
        url: url,
        type: "get",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (result) {
            console.log("result：" + result.status);
            if (result.resultCode === 200) {
                alert("登陆成功:Uid:" + t_accountId);
                entergame();
            }
        },
        error: function () {
            $.messager.alert("系统提示", "操作失败");
        }
    })
}

function  entergame() {
    let url = "/ttt/autoGame?";
    let param =
        "accountId=" + t_accountId +
        "&appId=" + "2" +
        "&serverId=" + "1";
    url += param;
    $.ajax({
        url: url,
        type: "get",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (result) {
            console.info(result);
            if (result.resultCode === 200) {
                let loginurl = result.url;
                $('#res').val(loginurl);
                alert(loginurl);
                window.location.href = loginurl;
            }
        },
        error: function () {
            $.messager.alert("系统提示", "操作失败");
        }
    });
}

let t_accountId;
let t_account;
let t_pwd;
let t_token;
let t_sign;

function auto() {
    let appId = $("#save_gameId").val();
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
                    t_accountId = result.accountId;
                    t_account = result.account;
                    t_pwd = result.pwd;
                    $('#username').val(t_account);
                    $('#password').val(t_pwd);
                    $('#pwd').val(t_pwd);

                    setCookie("username", t_account);
                    setCookie("password", t_pwd);
                    setCookie("accountid", t_accountId);

                    let res = "账号：" + result.account + " 密码：" + result.pwd + " Uid：" + result.accountId;
                    $('#res').val(res);
                    alert("注册成功");
                }
            }
        },
        error: function () {
            $.messager.alert("系统提示", "操作失败");
        }
    });
}


// function copyText() {
//     let input = document.getElementById("input");
//     input.select(); // 选中文本
//     document.execCommand("copy"); // 执行浏览器复制命令
//     alert("复制成功" + input);
// }


function initSpGameServer(type) {
    let gameId = $('#save_gameId').val();
    let serverId = $("#save_serverId").val();
    let spId = $("#save_spId").val();

    let data = {
        "gameId": gameId,
        "serverId": serverId,
        "spId": spId,
        "type": type
    };

    $.ajax({
        //获取下拉
        url: "/server/getDistinctServerInfoAll",
        type: "post",
        data: data,
        async: false,
        dataType: "json",
        success: function (result) {
            if (result.resultCode === 200) {
                // console.log(result);
                if (type === 1) {
                    let select_spId = $("#save_spId");
                    select_spId.find("option").remove();
                    select_spId.append("<option value=-1 selected=selected>请选择</option>");
                    for (let res = 0; res < result.total; res++) {
                        select_spId.append("<option value='" + result.rows[res] + "'>" + result.rows[res] + "</option>");
                    }
                } else if (type === 2) {
                    let select_gameId = $("#save_gameId");
                    select_gameId.find("option").remove();
                    select_gameId.append("<option value=-1 selected=selected>请选择</option>");
                    console.info(result.rows);
                    for (let res = 0; res < result.total; res++) {

                        let gameid = result.rows[res].gameId;
                        let name = result.rows[res].name + "\t" + gameid;

                        if (gameid == 2) {
                            select_gameId.append("<option   selected=selected value='" + gameid + "'>" + name + "</option>");
                        } else {
                            select_gameId.append("<option  value='" + gameid + "'>" + name + "</option>");
                        }
                    }
                } else if (type === 3) {
                    let select_serverId = $("#save_serverId");
                    select_serverId.find("option").remove();
                    select_serverId.append("<option value=-1 selected=selected>请选择</option>");
                    for (let res = 0; res < result.total; res++) {
                        select_serverId.append("<option value='" + result.rows[res] + "'>" + result.rows[res] + "</option>");
                    }
                }

            }
        },
        error: function () {
            $.messager.alert("ERROR！", "获取游戏列表出错");
        }
    });
}