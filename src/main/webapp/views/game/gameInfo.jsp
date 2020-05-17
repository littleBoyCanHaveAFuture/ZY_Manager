<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<html>
<head>
    <title>Title</title>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/jquery-easyui-1.7.0/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/jquery-easyui-1.7.0/themes/icon.css">
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.7.0/jquery.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.7.0/jquery.easyui.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.7.0/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/common.js"></script>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/css/game_index.css?202005071137">
</head>
<body>

<label for="appname" hidden="hidden">游戏名称</label>
<input type="text" id="appname" size="20" hidden="hidden"/>
<label for="appid" hidden="hidden">游戏id</label>
<input type="text" id="appid" size="20" hidden="hidden"/>
<input type="text" id="type" size="20" hidden="hidden"/>
<input type="text" id="iscreate" size="20" hidden="hidden"/>

<div class="container-fluid" id="main-container">

    <div id="main-content" class="clearfix mainifra">
        <div class="product_bg">
            <div class="product_step">
                <div class="step_title">操作步骤：</div>
                <div class="step_close" style="color:#438eb9;">
                    <img src="http://res.soeasysdk.com/soeasy/images/step_home.png" alt="">
                    <span onclick="gameback()">返回游戏列表</span>
                </div>
                <div id="fuelux-wizard" class="row-fluid step_top">
                    <ul class="wizard-steps">
                        <li class="gameStep1 active" id="gameStep1-tip"><span class="title">游戏资料完善</span></li>
                        <li class="gameStep2 active" id="gameStep2-tip"><span class="title">渠道配置</span></li>
                        <li class="gameStep3 active" id="gameStep3-tip"><span class="title">打包游戏下载</span></li>
                    </ul>
                </div>
                <div class="step_container">
                    <div class="step_container_left">
                        <div id="gamestep1" class="active gameBtn1 currentStates"
                             onclick="changeStep('','gameStep1',$(this),'game','h5')">
                            <img src="http://res.soeasysdk.com/soeasy/images/step_game.png" alt="">游戏资料
                        </div>
                        <div id="gamestep2" class="active gameBtn2"
                             onclick="changeStep('','gameStep2',$(this),'sdk','h5')">
                            <img src="http://res.soeasysdk.com/soeasy/images/step_sdk_gray.png" alt="">渠道配置
                        </div>
                        <div id="gamestep3" class="active gameBtn3"
                             onclick="changeStep('','gameStep3',$(this),'download','h5')">
                            <img src="http://res.soeasysdk.com/soeasy/images/step_download_gray.png" alt="">打包游戏下载
                        </div>
                    </div>
                    <iframe id="confframe" src=""></iframe>
                </div>

            </div>
        </div>
    </div>

</div>
</body>
<script>
    let t_type = $("#type");
    let t_appid = $("#appid");
    let t_appname = $("#appname");
    let t_isCreate = $("#iscreate");

    t_appid.val("${param.gameId}");
    t_appname.val("${param.name}");
    t_type.val("${param.type}");
    t_isCreate.val("${param.create}");

    console.info("id=" + t_appid.val());
    console.info("appname=" + t_appname.val());
    console.info("type=" + t_type.val());
    console.info("iscreate=" + t_isCreate.val());

    let url_gameData = "${pageContext.request.contextPath}/views/game/gameData.html";
    let url_gameChannel = "${pageContext.request.contextPath}/views/game/gameChannel.jsp";

    let param = "?appId=" + t_appid.val() + "&appName=" + t_appname.val() + "&type=" + t_type.val() + "&create=" + t_isCreate.val();
    //创建游戏 只能查看基本资料
    if (t_isCreate.val() === 1) {
        document.getElementById("gamestep2").className = '';
        document.getElementById("gamestep3").className = '';
    }
    document.getElementById("confframe").src = url_gameData + param;

    function changeStep(url, name, callback, opttype, gametype) {
        console.info(t_isCreate.val());
        if (t_isCreate.val() === 1 || t_isCreate.val() === "1") {
            return;
        }
        if (name === 'gameStep1') {
            document.getElementById("confframe").src = url_gameData + param;
            document.getElementById("gamestep1").className = 'active gameBtn1 currentStates';
            document.getElementById("gamestep2").className = 'active gameBtn2 ';
            document.getElementById("gamestep3").className = 'active gameBtn3 ';

            $('#wizard-steps li').siblings('li').removeClass('active');
            // jQuery('#gamestep1-tip').addClass('active');
            // jQuery('#gamestep2-tip').removeClass('active');
            // jQuery('#gamestep3-tip').removeClass('active');
        } else if (name === 'gameStep2') {
            document.getElementById("confframe").src = url_gameChannel + param;
            document.getElementById("gamestep1").className = 'active gameBtn1 ';
            document.getElementById("gamestep2").className = 'active gameBtn2 currentStates';
            document.getElementById("gamestep3").className = 'active gameBtn3 ';

            jQuery('#gamestep1-tip').addClass('active');
            jQuery('#gamestep2-tip').addClass('active');
            jQuery('#gamestep3-tip').removeClass('active');
        } else if (name === 'gameStep3') {
            document.getElementById("gamestep1").className = 'active gameBtn1 ';
            document.getElementById("gamestep2").className = 'active gameBtn2 ';
            document.getElementById("gamestep3").className = 'active gameBtn3 currentStates';

            jQuery('#gamestep1-tip').addClass('active');
            jQuery('#gamestep2-tip').addClass('active');
            jQuery('#gamestep3-tip').addClass('active');
        }
    }


    function gameback() {
        tip("跳转回去");
        window.location.href = "/views/game/h5game.jsp";
    }

    function parentFunction(appid) {
        t_isCreate.val(0);
        t_appid.val(appid);
        console.log("parentFunction appid:" + appid);
        changeStep('', 'gameStep2', $(this), 'sdk', 'h5');
    }

</script>
</html>
