<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>指悦账号 - 登录</title>
    <meta name="keywords" content="perfect-ssm">
    <meta name="description" content="perfect-ssm">

    <link href="${pageContext.request.contextPath}/images/favicon.ico" rel="shortcut icon" type="image/x-icon"/>
    <link href=" http://cdn.amazeui.org/amazeui/2.5.0/css/amazeui.css" rel='stylesheet' type='text/css'/>
    <link href="http://cdn.amazeui.org/amazeui/2.5.0/css/amazeui.min.css" rel='stylesheet' type='text/css'/>
    <link href="  http://cdn.amazeui.org/amazeui/2.5.0/js/amazeui.js" rel='stylesheet' type='text/css'/>
    <link href="  http://cdn.amazeui.org/amazeui/2.5.0/js/amazeui.min.js" rel='stylesheet' type='text/css'/>
    <link href=" http://cdn.amazeui.org/amazeui/2.5.0/js/amazeui.ie8polyfill.js" rel='stylesheet' type='text/css'/>
    <link href=" http://cdn.amazeui.org/amazeui/2.5.0/js/amazeui.ie8polyfill.min.js" rel='stylesheet' type='text/css'/>
    <link href=" http://cdn.amazeui.org/amazeui/2.5.0/js/amazeui.widgets.helper.js" rel='stylesheet' type='text/css'/>
    <link href=" http://cdn.amazeui.org/amazeui/2.5.0/js/amazeui.widgets.helper.min.js" rel='stylesheet'
          type='text/css'/>
    <!--[if lt IE 9]>
    <meta http-equiv="refresh" content="0;ie.html"/>
    <![endif]-->
    <script src="${pageContext.request.contextPath}/jquery-easyui-1.7.0/jquery.min.js"></script>
    <script src="${pageContext.request.contextPath}/jquery-easyui-1.7.0/jquery.easyui.min.js"></script>
    <script src="${pageContext.request.contextPath}/bootstrap-3.3.7-dist/js/bootstrap.js"></script>
    <%--    <script src="${pageContext.request.contextPath}/js/vconsole.min.js"></script>--%>
    <script src="${pageContext.request.contextPath}/js/clipboard.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/mobile-detect.js"></script>
    <script src="${pageContext.request.contextPath}/login/dev/H5GameDev.js?0606"></script>

</head>

<body class="gray-bg">

<div>

    <div class="login-form" id="login">
        <div class="head-info"><img src="../image/zhiyue.png" alt=""/></div>
        <div style="margin-bottom: 2em;"><img src="../image/jiemian5.png" style="width: 80%" alt=""></div>

        <div class="key">
            <input type="text" value="" placeholder="请输入账号" class="text" id="username">
            <input type="password" value="" placeholder="设置密码" id="password">
        </div>
        <div class="key" id="phoneLogin" hidden="hidden">
            <input type="number" class="text" placeholder="请输入手机验证码" required maxlength="6" id="phone_logincode">
        </div>
        <div class="key" id="phoneLoginClick" hidden="hidden">
            <input type="button" class="getcode" value=" 获取验证码"
                   onclick="getLoginCode(this);">
        </div>
        <div id="nnnn">
            <span class="logintip1">我已详细阅读并同意 指悦网络科技游戏隐私保护协议</span>

            <button type="button" onclick="zy_Login();" class="btn btn-primary block  m-b reg111">
                进入游戏
            </button>
        </div>
        <div hidden="hidden" id="nameLogin">
            <span class="logintip1">我已详细阅读并同意 指悦网络科技游戏隐私保护协议</span>
            <button type="button" onclick="zy_phone_Login();" class="btn btn-primary block  m-b reg111">
                手机进入游戏
            </button>
        </div>

        <div class="outer">
            <button class="btn btn-login" onClick="showRegPage()" style="width: 25%;color: #ebaf61;">
                一键注册
            </button>
            <button class="btn btn-login" onClick="hiddenNormal()" style="margin-left: 20%;width: 25%;color: #ebaf61;">
                <span id="lll">手机登陆</span>
            </button>
        </div>
        <div class="outer">
            <button class="btn btn-login " onClick="changePhonePage()" style="width: 25%">
                手机注册
            </button>
            <button class="btn btn-login " onClick="return false;" style="margin-left: 20%;width: 25%;;color: #ebaf61;">
                遇到问题
            </button>
        </div>
        <div class="outer">
            <button class="copyBtn btn btn-login " data-clipboard-target="#copy" alt="Copy to clipboard">
                点击复制账号密码
            </button>
        </div>
        <span class="logintip3">客服QQ：602824414</span>
    </div>

</div>


<div hidden="hidden">
    <label for="appId"></label>
    <input type="text" name="appId" id="appId" required="" hidden="hidden">

    <label for="channelId"></label>
    <input type="text" name="channelId" id="channelId" required="" hidden="hidden">

    <label for="uid"></label>
    <input type="text" name="uid" id="uid" required="" hidden="hidden">

    <label for="channelUid"></label>
    <input type="text" name="channelUid" id="channelUid" required="" hidden="hidden">

    <label for="copy"></label>
    <input type="text" name="copy" id="copy" required="" hidden="hidden">
</div>

</div>

<script type="text/javascript">

</script>

</body>

</html>
