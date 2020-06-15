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
    <link href="${pageContext.request.contextPath}/login/dev/style.css?1118" rel='stylesheet' type='text/css'/>
    <link href="${pageContext.request.contextPath}/images/favicon.ico" rel="shortcut icon" type="image/x-icon"/>
    <!--[if lt IE 9]>
    <meta http-equiv="refresh" content="0;ie.html"/>
    <![endif]-->
    <script src="${pageContext.request.contextPath}/jquery-easyui-1.7.0/jquery.min.js"></script>
    <script src="${pageContext.request.contextPath}/jquery-easyui-1.7.0/jquery.easyui.min.js"></script>
    <script src="${pageContext.request.contextPath}/bootstrap-3.3.7-dist/js/bootstrap.js"></script>
    <%--    <script src="${pageContext.request.contextPath}/js/vconsole.min.js"></script>--%>
    <script src="${pageContext.request.contextPath}/js/clipboard.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/common.js"></script>
    <script src="${pageContext.request.contextPath}/js/md5.js"></script>
    <script src="${pageContext.request.contextPath}/js/mobile-detect.js"></script>
    <script src="${pageContext.request.contextPath}/login/dev/H5GameDev.js?0606"></script>

</head>

<body class="gray-bg">

<h1></h1>
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

<div class="login-form" id="reg" hidden="hidden">
    <div class="head-info"><img src="../image/zhiyue.png" alt=""/></div>
    <div style="margin-bottom: 2em;"><img src="../image/jiemian5.png" style="width: 80%" alt=""></div>

    <div class="reg-info">
        <span class="regtip">一键注册</span>
        <span class="regtip1">点击完成注册快速生成账号密码</span>
    </div>

    <button type="button" onclick="zy_Register();" class="btn btn-primary block  m-b reg111" id="autoreg">
        完成注册
    </button>

    <span class="regtip3">注册即同意服务条款</span>

    <div class="outer">
        <button class="btn btn-login " onclick="showLoginPage()">
            点击返回
        </button>
    </div>
</div>

<%--账号密码注册界面--%>
<div class="login-form" id="zyreg" hidden="hidden">
    <div class="head-info"><img src="../image/zhiyue.png" alt=""/></div>
    <div style="margin-bottom: 2em;"><img src="../image/jiemian5.png" style="width: 80%" alt=""></div>

    <div class="key">
        <input type="text" value="" placeholder="请输入账号" class="text" id="reg_username">
        <input type="password" value="" placeholder="设置密码" id="reg_password">
    </div>
    <div class="outer">
        <button type="button" onclick="test_zy_Register(false);" class="btn btn-primary block  m-b reg111" id="zy_reg">
            注册账号
        </button>
        <button class="btn btn-login " onclick="showLoginPage()">
            点击返回
        </button>
    </div>
</div>

<%--手机注册界面--%>
<div class="login-form" id="phonereg" hidden="hidden">
    <div class="head-info"><img src="../image/zhiyue.png" alt=""/></div>
    <div style="margin-bottom: 2em;"><img src="../image/jiemian5.png" style="width: 80%" alt=""></div>

    <div class="key">
        <input type="text" value="" placeholder="请输入手机号" class="text" id="phone_username">
        <input type="password" value="" placeholder="设置密码" id="phone_password">
    </div>
    <div class="key">
        <input type="number" class="code" placeholder="请输入手机验证码" required maxlength="6" id="phone_code">
        <input type="button" class="getcode" value=" 获取验证码" onclick="getCode(this);">
    </div>
    <div class="outer">
        <button type="button" onclick="phone();" class="btn btn-primary block  m-b reg111"
                id="phone_reg">
            注册账号
        </button>
        <button class="btn btn-login " onclick="showLoginPage()">
            点击返回
        </button>
        <button type="button" class="btn btn-primary block  m-b reg111" id="mybtn">
            测试
        </button>
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
    let appId = 14;
    let appKey = "u6d3047qbltix34a9l0g2bvs5e8q82ol";
    let channelId = 0;
    let copy = $("#copy");

    $(function () {
        // 初始化
        // window.vConsole = new window.VConsole();
        // console.log('Hello world');
        // console.log(window.location.href);

        if (channelId !== 0) {
            return;
        }

        let zy_account = getCookies("zy_account");
        let zy_password = getCookies("zy_password");
        let zy_channelUid = getCookies("zy_channelUid");

        // 指悦官方渠道-无账号自动注册
        if (zy_account === "" || zy_password === "" || zy_channelUid === "") {
            // zy_Register();
        } else {
            $("#username").val(zy_account);
            $("#password").val(zy_password);
        }
        setCopy(zy_account, zy_password);


    });

    function setCopy(account, password) {
        let res = "账号: " + account + " 密码: " + password;
        copy.val(res);
    }


    let clipboard = new ClipboardJS('.copyBtn', {
        text: function () {
            return $("input:hidden[name='copy']").val();
        }
    });
    clipboard.on('success', function (e) {
        alert("已复制到剪贴板" + "\n账号: " + t_name + "\n密码: " + t_pwd);
        // e.clearSelection();
    });
    clipboard.on('error', function (e) {
        console.error('Action:', e.action);
        console.error('Trigger:', e.trigger);
    });

    let is_select = true;
    //给返回首页修改密码按钮添加单击事件
    $("#select-protocol").on("click", function () {
        select_protocol();
    });

    function select_protocol() {
        if (is_select) {
            document.getElementById("select-protocol").className = 'unselected';
            is_select = false;
        } else {
            document.getElementById("select-protocol").className = 'icon-select';
            is_select = true;
        }
    }

    //一键注册界面
    function showRegPage() {
        //隐藏
        document.getElementById("login").hidden = true;
        //显示
        document.getElementById("reg").hidden = false;
    }

    //登录界面
    function showLoginPage() {
        //显示
        document.getElementById("login").hidden = false;
        //隐藏
        document.getElementById("reg").hidden = true;
        document.getElementById("zyreg").hidden = true;
        document.getElementById("phonereg").hidden = true;
    }

    function showPhoneRegPage() {
        //隐藏
        document.getElementById("login").hidden = true;
        document.getElementById("reg").hidden = true;
        document.getElementById("zyreg").hidden = true;
        //显示
        document.getElementById("phonereg").hidden = false;
    }

    //手机注册界面
    function changePhonePage() {
        showPhoneRegPage();
    }

    //账号密码注册 暂时无
    function showZyRegPage() {
        //显示
        document.getElementById("zyreg").hidden = false;
        //隐藏
        document.getElementById("reg").hidden = true;
        document.getElementById("login").hidden = true;
    }

    let res = false;

    function hiddenNormal() {
        if (res) {
            //显示
            document.getElementById("password").hidden = false;
            document.getElementById("nnnn").hidden = false;


            //隐藏
            document.getElementById("phoneLogin").hidden = true;
            document.getElementById("nameLogin").hidden = true;
            document.getElementById("phoneLoginClick").hidden = true;

            res = false;
            $("#lll").html("手机登录");
        } else {
            res = true;
            //显示
            document.getElementById("phoneLogin").hidden = false;
            document.getElementById("nameLogin").hidden = false;
            document.getElementById("phoneLoginClick").hidden = false;
            //隐藏

            document.getElementById("password").hidden = true;
            document.getElementById("nnnn").hidden = true;

            $("#lll").html("返回");
        }


    }

    function resetLogin() {
        delCookie("zy_account");
        delCookie("zy_password");
        delCookie("zy_channelUid");
    }

    function getRndInteger(min, max) {
        return Math.floor(Math.random() * (max - min)) + min;
    }


    function updateQueryStringParameter(uri, key, value) {
        if (value === "" || value === undefined) {
            return uri;
        }
        let re = new RegExp("([?&])" + key + "=.*?(&|$)", "i");
        let separator = uri.indexOf('?') !== -1 ? "&" : "?";
        if (uri.match(re)) {
            return uri.replace(re, '$1' + key + "=" + value + '$2');
        } else {
            return uri + separator + key + "=" + value;
        }
    }

    function checkPhoneReg() {
        let phone = document.getElementById('phone_username').value;
        return /^1[3456789]\d{9}$/.test(phone);
    }

    function checkPhoneLogin() {
        let phone = document.getElementById('username').value;
        return /^1[3456789]\d{9}$/.test(phone);
    }

    //倒计时
    let codeTime = 60;
    let countdown = 60;

    function getCode(val) {
        countdown = codeTime;
        if (checkPhoneReg()) {
            ZhiYuePhoneCode(val, function (res) {
                setTime(val, res)
            });
        } else {
            alert("手机号码有误，请重填");
            countdown = 0;
        }
    }

    function getLoginCode(val) {
        countdown = codeTime;
        if (checkPhoneLogin()) {
            ZhiYuePhoneLoginCode(val, function (res) {
                setTime(val, res)
            });
        } else {
            alert("手机号码有误，请重填");
            countdown = 0;
        }
    }

    function setTime(val, res) {
        if (res !== undefined && !res) {
            return;
        }
        if (countdown === 0) {
            val.removeAttribute("disabled");
            val.value = "获取验证码";
            countdown = codeTime;
            return false;
        } else {
            val.setAttribute("disabled", true);
            val.value = "重新发送(" + countdown + ")";
            countdown--;
        }
        setTimeout(function () {
            setTime(val);
        }, 1000);
    }

</script>

</body>

</html>
