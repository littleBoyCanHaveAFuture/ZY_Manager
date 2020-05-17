<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>perfect-ssm - 登录</title>
    <meta name="keywords" content="perfect-ssm">
    <meta name="description" content="perfect-ssm">

    <link href="${pageContext.request.contextPath}css/bootstrap.min14ed.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}css/font-awesome.min93e3.css" rel="stylesheet">

    <link href="${pageContext.request.contextPath}css/animate.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}css/style.min862f.css" rel="stylesheet">
    <link rel="shortcut icon"
          href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon"/>
    <!--[if lt IE 9]>
    <meta http-equiv="refresh" content="0;ie.html"/>
    <![endif]-->

    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.7.0/jquery.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/bootstrap-3.3.7-dist/js/bootstrap.js"></script>
    <script src="${pageContext.request.contextPath}/js/common.js"></script>

</head>

<body class="gray-bg">

<div class="middle-box text-center loginscreen  animated fadeInDown">
    <div>
        <div>
            <h1 class="logo-name">指 悦</h1>
        </div>
        <h3>欢迎使用 指悦游戏后台</h3>

        <form class="m-t" role="form" id="adminlogin" method="post" name="adminlogin" onsubmit="return false"
              action="##">

            <div class="form-group">
                <label for="userName"></label>
                <input type="email" class="form-control" placeholder="用户名" name="userName" id="userName" required="">
            </div>

            <div class="form-group">
                <label for="password"></label>
                <input type="password" class="form-control" placeholder="密码" name="password" id="password" required="">
            </div>

            <button type="button" class="btn btn-primary block full-width m-b" onclick="login();">
                登 录
            </button>

            <p class="text-muted text-center">
                <a href="##" onclick="adminlogin.reset();return false;">
                    <small>重置</small>
                </a>
            </p>
        </form>
    </div>
</div>
<script>
    $(document).keydown(function (e) {
        if (!e) {
            e = window.event;
        }
        if ((e.keyCode || e.which) === 13) {
            login();

        }
    });

    function login() {
        var userName = $("#userName").val();
        var password = $("#password").val();
        var roleName = $("#roleName").val();
        if (userName == null || userName === "") {
            alert("用户名不能为空！");
            return;
        }
        if (password == null || password === "") {
            alert("密码不能为空！");
            return;
        }
        $.ajax({
            type: "POST",
            dataType: "json",
            url: "/users/cookie",
            data: $('#adminlogin').serialize(),
            success: function (result) {
                console.log(result);
                if (result.resultCode === 200) {
                    setCookie("userName", result.data.currentUser.userName);
                    setCookie("roleName", result.data.currentUser.roleName);
                    setCookie("useless", "useless");
                    window.location.href = "main.jsp";
                } else {
                    alert(result.message);
                }
            },
            error: function () {
                alert("登录异常！");
            }
        });

    }
</script>
</body>

</html>
