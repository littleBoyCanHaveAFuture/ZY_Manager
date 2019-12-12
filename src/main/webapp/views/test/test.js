let t_appid;
let t_token;
let t_accountid;
let t_sign;
let t_pwd;
let t_channelId;
let t_channelUid;

function myFunction(id) {
    let x = document.getElementById("username").value;
    document.getElementById("out").innerHTML = "你输入的是: " + x * 10;
}

function register() {
    let username = $("#username").val();
    let password = $("#password").val();
    // let channelId = $("#channelId").val();
    let channelUid = $("#channelUserId").val();
    // let appId = $("#appId").val();
    // let serverId = $("#serverId").val();

    let appId = $("#save_gameId").val();
    let serverId = $("#save_serverId").val();
    let channelId = $("#save_spId").val();

    let auto = $("#auto").val();
    console.log("auto：" + auto);
    if (auto == "true") {
        console.log("channelId：->" + channelId + "<-");
        console.log("channelUid：->" + channelUid + "<-");
        if (channelId == null || channelId.length === 0 || channelUid == null || channelUid.length === 0) {
            $.messager.alert("系统提示", "请输入 渠道id 渠道用户id");
            return;
        }
    } else {
        console.log("username：" + username);
        console.log("password：" + password);
        if (username == null || password == null) {
            $.messager.alert("系统提示", "请输入 账号密码");
            return;
        }
    }

    let data = {
        "username": username,
        "pwd": password,
        "phone": 18571470846,
        "deviceCode": "PC",

        "imei": "PC",
        "channelId": channelId,
        "channelUid": channelUid,
        "channelUname": 546546,
        "channelUnick": 10,

        "addparm": 10,
        "appId": appId,
        "auto": auto

    };
    $.ajax({
        url: "/ttt/register",
        type: "post",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(data),
        dataType: "json",
        async: false,
        success: function (result) {
            if (result.resultCode === 200) {
                if (result.data.status != 1) {
                    $.messager.alert("注册失败：", result.data.err);
                } else {
                    t_accountid = result.data.account;
                    t_pwd = result.data.pwd;
                    let ss = "账号:" + result.data.account + "\n密码：" + result.data.pwd;
                    $.messager.alert("注册成功：", ss);
                }

            }
        },
        error: function () {
            $.messager.alert("系统提示", "操作失败");
        }
    });
}

function login() {
    // let appId = $("#appId").val();
    let isChannel = $("#isChannel").val();
    let password = $("#password").val();
    // let channelId = $("#channelId").val();
    let channelUid = $("#channelUserId").val();

    let appId = $("#save_gameId").val();
    let serverId = $("#save_serverId").val();
    let channelId = $("#save_spId").val();

    console.log(appId);
    console.log(isChannel);
    console.log(channelId);
    console.log(channelUid);


    let data = {
        "appId": appId,
        "isChannel": isChannel,
        "name": t_accountid,
        "pwd": t_pwd,
        "channelId": channelId,
        "channelUid": channelUid
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
                $.messager.alert("系统提示", "登录回复：" + data);
                logincheck();
            }
        },
        error: function (result) {
            $.messager.alert("系统提示", result.err);
        }
    });
}

function logincheck() {
    // let appId = t_appid;
    let token = t_token;
    let uid = t_accountid;
    let sign = t_sign;
    let appId = $("#save_gameId").val();
    let serverId = $("#save_serverId").val();
    let channelId = $("#save_spId").val();

    let data = "?appId=" + appId + "&token=" + token + "&uid=" + uid + "&sign=" + sign;
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
                $.messager.alert("登录校验", result.status);
            }
        },
        error: function () {
            $.messager.alert("系统提示", "操作失败");
        }
    });
}

function entergame() {
    // let appId = t_appid;
    // let serverId = $("#serverId").val();
    // let channelId = $("#channelId").val();
    let channelUserId = $("#channelUserId").val();

    let appId = $("#save_gameId").val();
    let serverId = $("#save_serverId").val();
    let channelId = $("#save_spId").val();
    let roleId = $("#roleId").val();
    t_channelId = channelId;
    t_channelUid = channelUserId;

    let data = "?appId=" + appId +
        "&serverId=" + serverId +
        "&channelId=" + channelId +
        "&channelUid=" + channelUserId +
        "&roleId=" + roleId;
    let url = "/ttt/enter" + data;
    console.log("entergame:" + url);

    $.ajax({
        url: url,
        type: "get",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (result) {
            if (result.resultCode === 200) {
                $.messager.alert("系统提示", "进入成功");
            }
        },
        error: function () {
            $.messager.alert("系统提示", "操作失败");
        }
    });
}

function exitgame() {
    // let appId = t_appid;
    // let serverId = $("#serverId").val();
    // let channelId = $("#channelId").val();
    let channelUserId = $("#channelUserId").val();

    let appId = $("#save_gameId").val();
    let serverId = $("#save_serverId").val();
    let channelId = $("#save_spId").val();

    let data = "?appId=" + appId +
        "&serverId=" + serverId +
        "&channelId=" + channelId +
        "&channelUid=" + channelUserId +
        "&roleId=" + "";
    let url = "/ttt/exit" + data;
    console.log("logincheck:" + url);

    $.ajax({
        url: url,
        type: "get",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (result) {
            console.log("result：" + result.status);
            if (result.resultCode === 200) {
                $.messager.alert("系统提示", result.status);
            }
        },
        error: function () {
            $.messager.alert("系统提示", "操作失败");
        }
    });
}

function cretaterole() {
    let appId = $("#save_gameId").val();
    let serverId = $("#save_serverId").val();
    let channelId = $("#save_spId").val();
    let key = "createrole";
    let roleId = $("#roleId").val();
    let value = {
        "channelId": channelId,
        "channelUid": t_channelUid,
        "appId": appId,
        "roleId": roleId,
        "roleName": "roleName",
        "roleLevel": 0,
        "zoneId": serverId,
        "zoneName": "zoneName",
        "balance": 0,
        "vip": 0,
        "partyName": "",
        "roleCTime": 0,
        "roleLevelMTime": 0
    };
    let ss = JSON.stringify(value);
    let data = {
        "key": key,
        "value": ss
    };

    $.ajax({
        url: "/ttt/setdata",
        type: "post",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(data),
        dataType: "json",
        async: false,
        success: function (result) {
            console.log("result：" + result.status);
            if (result.resultCode === 200) {
                $.messager.alert("角色id", result.roleId);
            }
        },
        error: function () {
            $.messager.alert("系统提示", "操作失败");
        }
    });
}

function pay() {
    let channelUserId = $("#channelUserId").val();
    let appId = $("#save_gameId").val();
    let serverId = $("#save_serverId").val();
    let channelId = $("#save_spId").val();
    let roleId = $("#roleId").val();
    let order_status = $("#payRecord_state").val();


    let accountId = t_accountid;// 指悦账号id
    let channelOrderID = $("#oderid").val();// 渠道订单号
    let productID = "1";//   当前商品ID
    let productName = "测试1";// 商品名称
    let productDesc = "0.0.1";//  游戏版本
    let money = "600";//  单位 分
    let roleID = roleId;//   玩家在游戏服中的角色ID
    let roleName = "测试账号1";//  玩家在游戏服中的角色名称
    let roleLevel = "1";//   玩家等级
    let serverID = serverId;//   玩家所在的服务器ID
    let serverName = "111";//  玩家所在的服务器名称
    let extension;//   额外参数
    let status = order_status;//    订单状态
    let notifyUrl = null;//    支付回调通知的游戏服地址
    let signType = null;//   签名算法， RSA|MD5
    let sign = null;//    RSA签名

    // if (status === "1" || status === "2") {
    extension = {
        "realMoney": 600,
        "completeTime": new Date().getTime(),
        "sdkOrderTime": new Date().getTime()
    };
    console.info("extension:" + extension);
    // }
    let data =
        "?accountID=" + accountId +
        "&channelOrderID=" + channelOrderID +
        "&productID=" + productID +
        "&productName=" + productName +
        "&productDesc=" + productDesc +

        "&money=" + money +
        "&roleID=" + roleID +
        "&roleName=" + roleName +
        "&roleLevel=" + roleLevel +
        "&serverID=" + serverID +

        "&serverName=" + serverName +
        // "&extension=" + extension +
        "&extension=" + encodeURIComponent(JSON.stringify(extension)) +
        "&status=" + status +
        "&notifyUrl=" + notifyUrl +
        "&signType=" + signType +

        "&sign=" + sign;


    let url = "/ttt/payInfo" + data;
    console.log("payInfo:" + url);
    let stateMsg = [0, "发送成功", 2, 3, 4, 5, "角色不存在", 7, 8, 9,
        "金额错误", "订单错误", 12, 13, 14, 15, 16, "参数与订单参数不一致", "参数为空或非法", 19];
    $.ajax({
        url: url,
        type: "get",
        // data: JSON.stringify(jsondata),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (result) {
            console.log("result：" + result.state);
            if (result.resultCode === 200) {
                $.messager.alert("系统提示", stateMsg[result.state]);
            }
        },
        error: function () {
            $.messager.alert("系统提示", "操作失败");
        }
    });
}

function initGameList() {
    $.ajax({
        //获取下拉
        url: "/server/getGameList",
        type: "get",
        async: false,
        dataType: "json",
        success: function (result) {
            if (result.resultCode === 501) {
                relogin();
            } else if (result.resultCode === 200) {
                let select_gameId = $("#save_gameId");
                select_gameId.find("option").remove();
                select_gameId.append("<option value=-1 selected=selected>请选择</option>");
                for (let res = 0; res < result.total; res++) {
                    select_gameId.append("<option  value='" + result.rows[res].gameId + "'>" + result.rows[res].name + "</option>");
                }
            }
        },
        error: function () {
            $.messager.alert("ERROR！", "获取游戏列表出错");
        }
    });
}

function initServerList(type) {
    let gameId = $('#save_gameId').val();
    let serverId = $("#save_serverId").val();
    let spId = $("#save_spId").val();

    let data = {
        "gameId": gameId,
        "serverId": serverId,
        "spId": spId,
        "type": type
    };
    // console.log("data " + gameId);
    // console.log("data " + serverId);
    // console.log("data " + spId);
    $.ajax({
        //获取下拉
        url: "/server/getDistinctServerInfo",
        type: "post",
        data: data,
        async: false,
        dataType: "json",
        success: function (result) {
            if (result.resultCode === 501) {
                relogin();
            } else if (result.resultCode === 200) {
                // console.log(result);
                if (type === 1) {
                    let select_serverId = $("#save_serverId");
                    select_serverId.find("option").remove();
                    select_serverId.append("<option value=-1 selected=selected>请选择</option>");
                    for (let res = 0; res < result.total; res++) {
                        select_serverId.append("<option value='" + result.rows[res] + "'>" + result.rows[res] + "</option>");
                    }
                } else {
                    let select_spId = $("#save_spId");
                    select_spId.find("option").remove();
                    select_spId.append("<option value=-1 selected=selected>请选择</option>");
                    for (let res = 0; res < result.total; res++) {
                        select_spId.append("<option value='" + result.rows[res] + "'>" + result.rows[res] + "</option>");
                    }
                }
            }
        },
        error: function () {
            $.messager.alert("ERROR！", "获取游戏列表出错");
        }
    });
}

//登录超时 重新返回到登录界面
function relogin() {
    // 登录失效
    console.log("登录失效");
    $.messager.confirm(
        "系统提示",
        "登录超时！",
        function (r) {
            if (r) {
                delCookie("userName");
                delCookie("roleName");
                parent.location.href = "../../login.jsp";
            }
        });
}