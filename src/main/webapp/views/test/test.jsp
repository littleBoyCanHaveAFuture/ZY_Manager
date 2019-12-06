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

<body style="margin:1px;" id="ff">

<table id="serverTable" title="服务器列表" class="easyui-datagrid" pagination="true"
       rownumbers="true" fit="true" showFooter="true" toolbar="#sp">
    <thead>
    <tr>
        <td>111</td>
        <td><p id="out"></p></td>
    </tr>
    </thead>

</table>

<div id="sp">
    <div>
        <label for="username">用户名：</label>
        <input type="text" id="username" size="20" oninput="myFunction()"/>

        <label for="password">密码：</label>
        <input type="text" id="password" size="20" onkeydown=""/>
    </div>
    <div>
        <label for="channelId">渠道id：</label>
        <input type="text" id="channelId" size="20" onkeydown=""/>

        <label for="channelUserId"> 渠道 用户id：</label>
        <input type="text" id="channelUserId" size="20" onkeydown=""/>

        <label for="appId">游戏id</label>
        <input type="text" id="appId" size="20" onkeydown=""/>

        <label for="auto">渠道自动注册</label>
        <select id="auto">
            <option value="1" selected="selected">是</option>
            <option value="0" s>否</option>
        </select>
    </div>
    <div>
        <a href="javascript:register()" class="easyui-linkbutton" iconCls="icon-add" plain="true">注册</a>
    </div>

    <div>
        <label for="isChannel">是否渠道登录</label>
        <select id="isChannel">
            <option value="1" selected="selected">是</option>
            <option value="0" s>否</option>
        </select>
    </div>
    <div>
        <a href="javascript:login()" class="easyui-linkbutton" iconCls="icon-edit" plain="true">登录</a>
        <a href="javascript:logincheck()" class="easyui-linkbutton" iconCls="icon-edit" plain="true">登录校验</a>
        <a href="javascript:register()" class="easyui-linkbutton" iconCls="icon-remove" plain="true">退出</a>
    </div>
    <div>
        <a href="javascript:register()" class="easyui-linkbutton" iconCls="icon-remove" plain="true">充值</a>
    </div>
    <div>

        <label for="token"></label>
        <input type="text" id="token" size="20" hidden="true"/>

        <label for="uid"></label>
        <input type="text" id="uid" size="20" hidden="true"/>

        <label for="sign">dasdasdas</label>
        <input type="text" id="sign" size="20" hidden="true"/>


    </div>

</div>
<script type="text/javascript">
    var t_appid;
    var t_token;
    var t_uid;
    var t_sign;

    function myFunction() {
        var x = document.getElementById("username").value;
        document.getElementById("out").innerHTML = "你输入的是: " + x * 10;
    }

    function register() {
        let username = $("#username").val();
        let password = $("#password").val();
        let channelId = $("#channelId").val();
        let channelUid = $("#channelUserId").val();
        let auto = $("#auto").val();
        console.log(username);
        console.log(password);
        console.log(channelId);
        console.log(channelUid);
        let data = {
            "username": username,
            "pwd": password,
            "phone": 18571470846,
            "deviceCode": 5,

            "imei": 6,
            "channelId": channelId,
            "channelUid": channelUid,
            "channelUname": 546546,
            "channelUnick": 10,

            "addparm": 10,
            "gameId": 11,
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
                    $.messager.alert("系统提示", result.data);
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
        let username = $("#username").val();
        let password = $("#password").val();
        let channelId = $("#channelId").val();
        let channelUid = $("#channelUserId").val();

        console.log(appId);
        console.log(isChannel);
        console.log(username);
        console.log(password);
        console.log(channelId);
        console.log(channelUid);


        let data = {
            "appId": appId,
            "isChannel": isChannel,
            "name": username,
            "pwd": password,
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
                    $.messager.alert("系统提示", "登录ok");

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
        let channelUid = t_uid;
        let sign = t_sign;


        let data = "?appId=" + appId + "&token=" + token + "&uid=" + channelUid + "&sign=" + sign;
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
                    $.messager.alert("系统提示", result.status);
                }
            },
            error: function () {
                $.messager.alert("系统提示", "操作失败");
            }
        });
    }
</script>

</body>

</html>