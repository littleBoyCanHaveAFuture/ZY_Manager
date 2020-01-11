<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Insert title here</title>
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
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/multiple-select-1.5.2/multiple-select.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/common.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/serverInfo.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/zySdkOffcial.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/libZySdk_v1.js"></script>
</head>

<body style="margin:1px;" id="ff" bgcolor="#7fffd4" onload="checkCookies()">
<%--<div class="assistive-wrap" id="assistive-wrap" style="transition: all 0.5s ease 0s; opacity: 0.3; left: 1px;">--%>
<%--    <div class="assistive-touch ">--%>
<%--        <span></span>--%>
<%--        <em class="new-msg"></em>--%>
<%--    </div>--%>
<%--</div>--%>
<%--<div class="menuList assistive-menu" style="left: 1px; top: 120px; display: none;">--%>
<%--    <a class="closeBtn" href="javascript:;"></a>--%>
<%--    <a class="menulist-item logout" href="javascript:;" onclick="$(this).parent().hide();showLogOutViews();"><em></em><span class="wd">退出</span></a>--%>
<%--    <a class="menulist-item account" href="javascript:;" onclick="$(this).parent().hide();showServiceViews();"><em></em><span class="wd">账户</span></a>--%>
<%--</div>--%>
<div id="sp">
    <div>
        <a href="javascript:initSpGameServer(1)" class="easyui-linkbutton"
           iconCls=" icon-search" plain="true">查询渠道</a>
        <a href="javascript:initSpGameServer(2)" class="easyui-linkbutton" style="margin-left:50px"
           iconCls=" icon-search" plain="true">查询游戏</a>
        <a href="javascript:initSpGameServer(3)" class="easyui-linkbutton" style="margin-left:50px"
           iconCls=" icon-search" plain="true">查询区服</a>
    </div>


    <label for="save_spId"></label>
    <span style="color: blue; ">渠道:</span>
    <select title="选择渠道" id="save_spId" name="spId">
        <%--        <option value="-1" selected="selected">请选择</option>--%>
        <option value="0" selected="selected">渠道 0</option>
    </select>

    <label for="save_gameId"></label>
    <span style="color: blue;margin-left:50px  ">游戏:</span>
    <select title="选择游戏" id="save_gameId" name="gameId">
        <%--        <option value="-1" selected="selected">请选择</option>--%>
        <option value="9" selected="selected">三国游侠 9</option>
    </select>

    <label for="save_serverId"></label>
    <span style="color: blue; margin-left:50px">区服:</span>
    <select title="选择区服" id="save_serverId" name="serverId">
        <%--        <option value="-1" selected="selected">请选择</option>--%>
        <option value="1" selected="selected">1 区</option>
    </select>

    <div><a href="javascript:init()" class="easyui-linkbutton" iconCls=" icon-search" plain="true">sdk初始化</a></div>
    <div>
        <label for="username">用户名：</label>
        <input type="text" id="username" size="20"/>

        <label for="password">密码：</label>
        <input type="text" id="password" size="20" onkeydown=""/>
    </div>
    <div>
        渠道
    </div>
    <div>
        <label for="channelUid">用户id：</label>
        <input type="text" id="channelUid" size="20" onkeydown=""/>
    </div>

    <div>
        <label for="auto">渠道自动注册</label>
        <select id="auto">
            <option value="true" selected="selected">渠道号自动</option>
            <option value="false">否</option>
        </select>

    </div>

    <div>
        <label for="isChannel">是否渠道登录</label>
        <select id="isChannel">
            <option value="true" selected="selected">渠道号自动</option>
            <option value="false">否</option>
        </select>
    </div>
    <div>
        <label for="accountId">账号id：</label>
        <input type="text" id="accountId" size="20" onkeydown=""/>
        <label for="roleId">角色id：</label>
        <input type="text" id="roleId" size="20" onkeydown=""/>
    </div>
    <div>
        <label>-------------功能---------------</label>
    </div>
    <div>
        <a href="javascript:zy_Register()" class="easyui-linkbutton" iconCls="icon-add" plain="true">注册</a>
    </div>
    <div>
        <a href="javascript:test_Login()" class="easyui-linkbutton" iconCls="icon-edit" plain="true">账号登录</a>
    </div>
    <div>
        <a href="javascript:test_CreateRole()" class="easyui-linkbutton" iconCls="icon-add" plain="true">创建角色</a>
        <a href="javascript:test_EnterGame()" class="easyui-linkbutton" iconCls="icon-edit" plain="true">进入游戏(游戏场景)</a>
        <a href="javascript:test_ExitGame()" class="easyui-linkbutton" iconCls="icon-edit" plain="true">退出游戏</a>
    </div>
    <div>
        <div>
            <label>-----------------充值------------------</label>
        </div>
        <div>
            <label for="oderid">订单号：</label>
            <input type="text" id="oderid" size="20" onkeydown=""/>
            <label for="money">金额：</label>
            <input type="text" id="money" size="20" onkeydown=""/>

            <label for="payRecord_state">&nbsp;支付状态:</label>
            <select title="选择订单状态" id="payRecord_state">
                <option value="">未选择</option>
                <option value="0">点开充值界面:未点充值按钮(取消支付)</option>
                <option value="1">选择充值方式界面:未选择充值方式(取消支付)</option>
                <option value="2">支付宝微信界面:未支付(取消支付)</option>
                <option value="3">支付成功:未发货</option>
                <option value="4" selected="selected">支付成功:已发货(交易完成)</option>
                <option value="5">支付成功:补单(交易完成)</option>
            </select>
        </div>
        <div>
            <label>--------------------------------------</label>
        </div>
        <a href="javascript:test_UploadPayInfo()" class="easyui-linkbutton" iconCls="icon-add" plain="true">充值上报</a>
        <a href="javascript:test_PayInfo()" class="easyui-linkbutton" iconCls="icon-edit" plain="true">官方充值</a>
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


</body>
<script type="text/javascript">
    $(function () {
        // initSpGameServer(1);
        // initSpGameServer(2);
        // initSpGameServer(3);
    });

    function init() {
        let auto = $("#auto").val();
        let appId = $("#save_gameId").val();
        let channelId = $("#save_spId").val();
        let channelUid = $("#channelUid").val();
        let username = $("#username").val();
        let password = $("#password").val();
        let secretKey = "x889btf66ktzqp6p34t4exz10b5r1hl9";
        ZySDK.init(appId, secretKey, channelId, function () {
            console.info("init ok");
        });
    }

    function zy_Register() {
        let regInfo = {};
        regInfo.auto = $("#auto").val() === "true";
        regInfo.appId = Number($("#save_gameId").val());
        regInfo.channelId = Number($("#save_spId").val());
        regInfo.channelUid = $("#channelUid").val();
        regInfo.username = $("#username").val();
        regInfo.password = $("#password").val();
        regInfo.phone = "18571470846";
        regInfo.deviceCode = "PC";
        regInfo.imei = "PC";
        regInfo.addparm = "";
        console.info(regInfo);
        if (regInfo.channelId === 0) {
            regInfo.channelUid = "0";
        }
        sdk_ZyRegister(regInfo, function (callbackData) {
            if (callbackData.state === false) {
                console.error(callbackData);
            } else {
                console.info(callbackData);
                $("#username").val(callbackData.account);
                $("#password").val(callbackData.password);
                $("#accountId").val(callbackData.uid);
                $("#channelUid").val(callbackData.channelUid);
            }
        });
    }
</script>

</html>