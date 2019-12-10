<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Insert title here</title>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/jquery-easyui-1.3.3/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/jquery-easyui-1.3.3/themes/icon.css">
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.3.3/jquery.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.3.3/jquery.easyui.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.3.3/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/common.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/multiple-select-1.5.2/multiple-select.js"></script>
</head>

<body style="margin:1px;" id="ff" bgcolor="#7fffd4">

<%--<table id="serverTable" title="服务器列表" class="easyui-datagrid" pagination="true"--%>
<%--       rownumbers="true" fit="true" showFooter="true" toolbar="#sp">--%>
<%--    <thead>--%>
<%--    <tr>--%>

<%--    </tr>--%>
<%--    </thead>--%>

<%--</table>--%>

<div id="sp">
    <div>
        <a href="javascript:initServerList(2)" class="easyui-linkbutton"
           iconCls=" icon-search" plain="true">查询渠道</a>
        <a href="javascript:initGameList()" class="easyui-linkbutton" style="margin-left:50px"
           iconCls=" icon-search" plain="true">查询游戏</a>
        <a href="javascript:initServerList(1)" class="easyui-linkbutton" style="margin-left:50px"
           iconCls=" icon-search" plain="true">查询区服</a>

    </div>
    <label for="save_spId"></label>
    <span style="color: blue; ">渠道:</span>
    <select title="选择渠道" id="save_spId" name="spId">
        <option value="-1" selected="selected">请选择</option>
    </select>

    <label for="save_gameId"></label>
    <span style="color: blue;margin-left:50px  ">游戏:</span>
    <select title="选择游戏" id="save_gameId" name="gameId">
        <option value="-1" selected="selected">请选择</option>
    </select>

    <label for="save_serverId"></label>
    <span style="color: blue; margin-left:50px">区服:</span>
    <select title="选择区服" id="save_serverId" name="serverId">
        <option value="-1" selected="selected">请选择</option>
    </select>


    <div>
        <label for="username">用户名：</label>
        <input type="text" id="username" size="20" oninput="myFunction()"/>

        <label for="password">密码：</label>
        <input type="text" id="password" size="20" onkeydown=""/>
    </div>
    <div>
        渠道
    </div>
    <div>
        <label for="channelUserId">用户id：</label>
        <input type="text" id="channelUserId" size="20" onkeydown=""/>
    </div>

    <div>
        <label for="auto">渠道自动注册</label>
        <select id="auto">
            <option value="true" selected="selected">渠道号自动</option>
            <option value="false" s>否</option>
        </select>

    </div>

    <div>
        <label for="isChannel">是否渠道登录</label>
        <select id="isChannel">
            <option value="true" selected="selected">渠道号自动</option>
            <option value="false" s>否</option>
        </select>
    </div>
    <div>
        <label for="channelUserId">角色id：</label>
        <input type="text" id="roleId" size="20" onkeydown=""/>
    </div>
    <div>
        <label>-------------功能---------------</label>
    </div>
    <div>
        <a href="javascript:register()" class="easyui-linkbutton" iconCls="icon-add" plain="true">注册</a>
    </div>
    <div>
        <a href="javascript:login()" class="easyui-linkbutton" iconCls="icon-edit" plain="true">账号登录</a>
    </div>
    <div>
        <a href="javascript:entergame()" class="easyui-linkbutton" iconCls="icon-edit" plain="true">进入游戏(服务器)</a>
        <a href="javascript:cretaterole()" class="easyui-linkbutton" iconCls="icon-add" plain="true">创建角色</a>
        <a href="javascript:entergame()" class="easyui-linkbutton" iconCls="icon-edit" plain="true">进入游戏(游戏场景)</a>
        <a href="javascript:exitgame()" class="easyui-linkbutton" iconCls="icon-edit" plain="true">退出游戏</a>
    </div>
    <div>
        <div>
            <label for="oderid">订单号：</label>
            <input type="text" id="oderid" size="20" onkeydown=""/>
        </div>
        <label for="payRecord_state">&nbsp;支付状态:</label>
        <select title="选择订单状态" id="payRecord_state">
            <option value="-2">支付取消</option>
            <option value="-1">支付失败</option>
            <option value="0">创建未支付</option>
            <option value="1">支付成功</option>
            <option value="2">交易完成</option>
            <option value="3">未支付(有效)</option>
            <option value="4">未支付(已调起)</option>
        </select>
        <a href="javascript:pay()" class="easyui-linkbutton" iconCls="icon-add" plain="true">充值</a>
        <a href="javascript:pay()" class="easyui-linkbutton" iconCls="icon-edit" plain="true">充值校验</a>
    </div>
    <div>
        <label>--------------------------------------</label>
    </div>
    <div>
        <label for="status">当前状态:</label>
        <p id="status"></p>
    </div>
    <div>
        <label>--------------------------------------</label>
    </div>
    <div>
        <p id="out"></p>
    </div>

</div>


<script type="text/javascript">
    var t_appid;
    var t_token;
    var t_uid;
    var t_sign;
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
                        t_uid = result.data.account;
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
            "name": t_uid,
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
                    t_uid = result.uid;
                    t_sign = result.sign;

                    let data = "appId=" + t_appid + "token=" + t_token + "uid=" + t_uid + "sign=" + t_sign;
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
        let uid = t_uid;
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


        let userID = t_uid;// 指悦账号id
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
        let notifyUrl;//    支付回调通知的游戏服地址
        let signType;//   签名算法， RSA|MD5
        let sign;//    RSA签名

        if (status == 1 || status == 2) {
            extension = {
                "realMoney": 600,
                "completeTime": "",
                "sdkOrderTime": ""
            }
        }
        let data =
            "?userID=" + userID +
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
            "&extension=" + extension +
            // "&extension=" + encodeURIComponent(JSON.stringify(extension)) +
            "&status=" + status +
            "&notifyUrl=" + notifyUrl +
            "&signType=" + signType +
            "&sign=" + sign;
        let jsondata = {
            "userID": userID,
            "channelOrderID": channelOrderID,
            "productID": productID,
            "productName": productName,
            "productDesc": productDesc,
            "money": money,
            "roleID": roleID,
            "roleName": roleName,
            "roleLevel": roleLevel,
            "serverID": serverID,
            "serverName": serverName,
            "extension": JSON.stringify(extension),
            "status": status,
            "notifyUrl": notifyUrl,
            "signType": signType,
            "sign": sign
        }


        let url = "/ttt/payInfo" + data;
        console.log("payInfo:" + url);

        $.ajax({
            url: url,
            type: "get",
            // data: JSON.stringify(jsondata),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function (result) {
                console.log("result：" + result.state);
                if (result.resultCode === 200) {
                    $.messager.alert("系统提示", result.state);
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

</script>

</body>

</html>