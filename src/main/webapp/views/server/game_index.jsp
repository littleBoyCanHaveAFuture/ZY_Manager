<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
    String str = request.getParameter("gameId");
    int gameId = Integer.parseInt(str);
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
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/views/server/game_index.css?1505">
</head>
<body>

<label for="appname" hidden="hidden">游戏名称</label>
<input type="text" id="appname" size="20" hidden="hidden"/>
<label for="appid" hidden="hidden">游戏id</label>
<input type="text" id="appid" size="20" hidden="hidden"/>

<div class="container-fluid" id="main-container">

    <div id="main-content" class="clearfix mainifra">
        <div>
            <%--            <iframe name="mainFrame" id="mainFrame" frameborder="0" src="tab.do"--%>
            <%--                    style="margin-left: 225px; height: 1246px; position: absolute; top: 0px; width: 89%;">--%>
            <%--            </iframe>--%>
        </div>
        <div class="product_bg">
            <div class="product_step">
                <div class="step_title">操作步骤：</div>
                <div class="step_close" style="color:#438eb9;">
                    <img src="http://res.soeasysdk.com/soeasy/images/step_home.png" alt="">返回游戏列表
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
                             onclick="changeStep('product/h5find.do?app_id=3616','gameStep1',$(this),'game','h5')">
                            <img src="http://res.soeasysdk.com/soeasy/images/step_game.png" alt="">游戏资料
                        </div>
                        <div id="gamestep2" class="active gameBtn2"
                             onclick="changeStep('product/h5sdk.do?app_id=3616','gameStep2',$(this),'sdk','h5')">
                            <img src="http://res.soeasysdk.com/soeasy/images/step_sdk_gray.png" alt="">渠道配置
                        </div>
                        <div id="gamestep3" class="active gameBtn3"
                             onclick="changeStep('pack/h5_goAppDownList.do?app_id=3616','gameStep3',$(this),'download','h5')">
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
    let t_appid = $("#appid");
    let t_appname = $("#appname")

    t_appid.val("${param.gameId}");
    t_appname.val("${param.name}");

    let id = t_appid.val();
    let appname = t_appname.val();

    console.info(id);
    console.info(appname);

    let param = "?gameId=" + id + "&name=" + appname;
    document.getElementById("confframe").src = "${pageContext.request.contextPath}/views/server/game_info.html" + param;

    function changeStep(url, name, callback, opttype, gametype) {
        if (name === 'gameStep1') {
            document.getElementById("confframe").src = "${pageContext.request.contextPath}/views/server/game_info.html" + param;
            document.getElementById("gamestep1").className = 'active gameBtn1 currentStates';
            document.getElementById("gamestep2").className = 'active gameBtn2 ';
            document.getElementById("gamestep3").className = 'active gameBtn3 ';

            $('#wizard-steps li').siblings('li').removeClass('active');
            // jQuery('#gamestep1-tip').addClass('active');
            // jQuery('#gamestep2-tip').removeClass('active');
            // jQuery('#gamestep3-tip').removeClass('active');
        } else if (name === 'gameStep2') {
            document.getElementById("confframe").src = "${pageContext.request.contextPath}/views/server/gameConfig.jsp" + param;
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
</script>
</html>
