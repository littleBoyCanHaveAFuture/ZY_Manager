<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>指悦账号 - 登录</title>
    <meta name="keywords" content="perfect-ssm">
    <meta name="description" content="perfect-ssm">

    <link href="${pageContext.request.contextPath}/css/bootstrap.min14ed.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/font-awesome.min93e3.css" rel="stylesheet">

    <link href="${pageContext.request.contextPath}/css/animate.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.min862f.css" rel="stylesheet">
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon"/>
    <!--[if lt IE 9]>
    <meta http-equiv="refresh" content="0;ie.html"/>
    <![endif]-->
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.7.0/jquery.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.7.0/jquery.easyui.min.js"></script>
    <script src="${pageContext.request.contextPath}/bootstrap-3.3.7-dist/js/bootstrap.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/clipboard@2/dist/clipboard.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/common.js"></script>
    <script src="${pageContext.request.contextPath}/js/md5.js"></script>
    <script src="${pageContext.request.contextPath}/login/gamelogin.js?1731"></script>
</head>

<body class="gray-bg">

<div class="middle-box text-center loginscreen  animated fadeInDown">
    <div class="pan">
        <form class="m-t" role="form" id="adminlogin" method="post" name="adminlogin" onsubmit="return false"
              action="##">
            <div>
                <p>游戏登录</p>
            </div>
            <div class="form-group">
                <label for="username"></label>
                <input type="text" class="form-control" placeholder="用户名" name="username" id="username" required="">
            </div>

            <div class="form-group">
                <label for="password"></label>
                <input type="password" class="form-control" placeholder="密码" name="password" id="password" required="">
            </div>

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

            <button type="button" class="btn btn-primary block full-width m-b" onclick="zy_Register();"> 一 键 注 册
            </button>
            <button type="button" class="btn btn-primary block full-width m-b" onclick="zy_Login();"> 登 录</button>
            <button class="copyBtn btn btn-primary block full-width m-b" data-clipboard-target="#copy"
                    alt="Copy to clipboard">
                点击复制账号密码
            </button>
            <p class="text-muted text-center">
                <a href="##" onclick="adminlogin.reset();return false;" style="float: right">
                    <small>重置账号密码</small>
                </a>
            </p>
            <a href="##" onclick="clearGameCookie();return false;" style="float: left">
                <small>清理缓存</small>
            </a>
        </form>
    </div>
</div>

<script type="text/javascript">
    setCookie("zy_appId", 11);
    setCookie("zy_channelId", 0);
    if (getCookies("zy_channelUid") === "") {
        setCookie("zy_channelUid", 0);
    }

    let clipboard = new ClipboardJS('.copyBtn', {
        text: function () {
            return $("input:hidden[name='copy']").val();
        }
    });
    const t_key = "l44i45326jixrlaio9c0025g974125y6";

    let t_name = getCookies("zy_user");
    let t_pwd = getCookies("zy_pwd");
    let res = "账号: " + t_name + " 密码: " + t_pwd;
    $('#copy').val(res);

    clipboard.on('success', function (e) {
        // console.info('Action:', e.action);
        console.info('Text:', e.text);
        // console.info('Trigger:', e.trigger);
        alert("复制成功");
        e.clearSelection();
    });

    clipboard.on('error', function (e) {
        console.error('Action:', e.action);
        console.error('Trigger:', e.trigger);
    });
</script>

</body>

</html>
