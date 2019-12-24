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
            src="${pageContext.request.contextPath}/jquery-easyui-1.3.3/jquery.min.js"></script>
    <script src="http://apps.bdimg.com/libs/bootstrap/3.3.4/js/bootstrap.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/multiple-select-1.5.2/multiple-select.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/jquery.md5.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/clipboard@2/dist/clipboard.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/common.js"></script>
    <script src="${pageContext.request.contextPath}/login/gamelogin.js"></script>
</head>

<body class="gray-bg">

<div class="middle-box text-center loginscreen  animated fadeInDown">
    <div class="pan">
        <form class="m-t" role="form" id="adminlogin" method="post" name="adminlogin" onsubmit="return false"
              action="##">

            <label for="save_spId"></label>
            <%--            <span style="color: blue; ">渠道:</span>--%>
            <select title="选择渠道" id="save_spId" name="spId" hidden="hidden">
                <option value="-1" selected="selected">请选择</option>
            </select>

            <label for="save_gameId"></label>
            <span style="color: blue;margin-left:50px  ">游戏:</span>
            <select title="选择游戏" id="save_gameId" name="gameId">
                <option value="-1" selected="selected">请选择</option>
            </select>
            <label for="save_serverId"></label>
            <%--            <span style="color: blue; margin-left:50px">区服:</span>--%>
            <select title="选择区服" id="save_serverId" name="serverId" hidden="hidden">
                <option value="-1" selected="selected">请选择</option>
            </select>

            <div class="form-group">
                <label for="username"></label>
                <input type="text" class="form-control" placeholder="用户名" name="username" id="username" required="">
            </div>

            <div class="form-group">
                <label for="password"></label>
                <input type="password" class="form-control" placeholder="密码" name="password" id="password" required="">
            </div>


            <input type="text" name="res" id="res" required="" hidden="hidden">

            <button type="button" class="btn btn-primary block full-width m-b" onclick="auto();"> 一 键 注 册</button>
            <button type="button" class="btn btn-primary block full-width m-b" onclick="login();"> 登 录</button>
            <%--            <button type="button" class="btn btn-primary block full-width m-b" onclick="register();">注 册</button>--%>
            <button class="copyBtn btn btn-primary block full-width m-b" data-clipboard-target="#res"
                    alt="Copy to clipboard">
                点击复制账号密码
            </button>
            <p class="text-muted text-center">
                <a href="##" onclick="adminlogin.reset();return false;">
                    <small>重置</small>
                </a>
            </p>
        </form>
    </div>
</div>

<style type="text/css" >

/*    .m-t {*/
/*        position: fixed;*/
/*        top: 0px;*/
/*        left: 0px;*/
/*        right: 0px;*/
/*        bottom: 0px;*/
/*        margin: auto;*/
/*        background:url("http://111.231.244.198:8080/try/login/ui_1.png") center no-repeat;*/
/*        background-size: 100% 100%;*/
/*    }*/
</style>

<script type="text/javascript">

    var clipboard = new ClipboardJS('.copyBtn', {
        text: function () {
            return $("input:hidden[name='res']").val();
        }
    });
    clipboard.on('success', function (e) {
        console.info('Action:', e.action);
        console.info('Text:', e.text);
        console.info('Trigger:', e.trigger);
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
