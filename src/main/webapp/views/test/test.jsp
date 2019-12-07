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
        官方
    </div>
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
        <label for="channelId">渠道id：</label>
        <input type="text" id="channelId" size="20" onkeydown=""/>

        <label for="channelUserId">用户id：</label>
        <input type="text" id="channelUserId" size="20" onkeydown=""/>
    </div>

    <div>
        <label for="appId">游戏id*</label>
        <input type="text" id="appId" size="20" onkeydown=""/>
        <label for="serverId">游戏区服</label>
        <input type="text" id="serverId" size="20" onkeydown=""/>
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
    let t_account;
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
        let channelId = $("#channelId").val();
        let channelUid = $("#channelUserId").val();
        let appId = $("#appId").val();
        let serverId = $("#serverId").val();

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
                        t_account = result.data.account;
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
        let appId = $("#appId").val();
        let isChannel = $("#isChannel").val();
        let password = $("#password").val();
        let channelId = $("#channelId").val();
        let channelUid = $("#channelUserId").val();

        console.log(appId);
        console.log(isChannel);
        console.log(channelId);
        console.log(channelUid);


        let data = {
            "appId": appId,
            "isChannel": isChannel,
            "name": t_account,
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
            error: function () {
                $.messager.alert("系统提示", "操作失败");
            }
        });
    }

    function logincheck() {
        let appId = t_appid;
        let token = t_token;
        let uid = t_uid;
        let sign = t_sign;


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
        let appId = t_appid;
        let serverId = $("#serverId").val();
        let channelId = $("#channelId").val();
        let channelUserId = $("#channelUserId").val();

        t_channelId = channelId;
        t_channelUid = channelUserId;

        let data = "?appId=" + appId +
            "&serverId=" + serverId +
            "&channelId=" + channelId +
            "&channelUid=" + channelUserId +
            "&roleId=" + "";
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
        let appId = t_appid;
        let serverId = $("#serverId").val();
        let channelId = $("#channelId").val();
        let channelUserId = $("#channelUserId").val();

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

        let appId = t_appid;
        let serverId = $("#serverId").val();
        let key = "createrole";

        let value = {
            "channelId": t_channelId,
            "channelUid": t_channelUid,
            "appId": appId,
            "roleId": "1",
            "roleName": "roleName",
            "roleLevel": 0,
            "zoneId": serverId,
            "zoneName": "zoneName",
            "balance": 0,
            "vip": 0,
            "partyName": "",
            "roleCTime": 0,
            "roleLevelMTime":0
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
                    $.messager.alert("系统提示", result.status);
                }
            },
            error: function () {
                $.messager.alert("系统提示", "操作失败");
            }
        });
    }

    function pay() {

    }
</script>

</body>

</html>