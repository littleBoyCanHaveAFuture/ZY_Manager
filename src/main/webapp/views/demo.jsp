<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>测试</title>
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
<%--            src="${pageContext.request.contextPath}/js/libZySdk_v1.js?v=202003181727"></script>--%>
            src="http://zy.hysdgame.cn/sdk/common/libZySdk_v1.js?v=202003191115"></script>
</head>

<body style="margin:1px;" id="ff" bgcolor="#7fffd4">
<div id="sp">
    <label for="save_spId"></label>
    <span style="color: blue; ">渠道:</span>
    <select title="选择渠道" id="save_spId" name="spId">
        <%--        <option value="-1" selected="selected">请选择</option>--%>
        <option value="0">官方渠道 0</option>
        <option value="1" selected="selected">渠道 测试1</option>
        <option value="2">YY-2</option>
        <option value="3">乐趣-3</option>
        <option value="4">小度-4</option>
        <option value="5">爱点游-5</option>
        <option value="6">虫虫游戏-6</option>
        <option value="7">闲兔-7</option>
    </select>


    <label for="save_gameId"></label>
    <span style="color: blue;margin-left:50px  ">游戏:</span>
    <select title="选择游戏" id="save_gameId" name="gameId">
        <%--        <option value="-1" selected="selected">请选择</option>--%>
        <%--        <option value="9" selected="selected">三国游侠 9</option>--%>
        <option value="10" selected="selected">刺沙 10</option>
        <option value="11">刺沙-指悦 11</option>
    </select>

    <label for="save_serverId"></label>
    <span style="color: blue; margin-left:50px">区服:</span>
    <select title="选择区服" id="save_serverId" name="serverId">
        <%--        <option value="-1" selected="selected">请选择</option>--%>
        <option value="1" selected="selected">1 区</option>
        <option value="2">2 区</option>
        <option value="3">3 区</option>
        <option value="4">4 区</option>
        <option value="5">5 区</option>
        <option value="6">6 区</option>
    </select>

    <div>
        <label>Step:1--></label>
        <a href="javascript:init()" class="easyui-linkbutton" iconcls="icon-edit" plain="true">Sdk初始化 ZySDK.init()</a>
    </div>
    <div>
        <label>--------------------------------------</label>
    </div>

    <div>
        <label for="channelUid">Step:2--> 渠道用户id：</label>
        <input type="text" id="channelUid" size="20" onkeydown=""/>
    </div>
    <div>
        <label>Step:3--></label>
        <a href="javascript:SpInit()" class="easyui-linkbutton" iconcls="icon-edit" plain="true">Sdk初始化-渠道用户
            ZySDK.initUser()</a>
    </div>

    <div>
        <label>--------------------------------------</label>
    </div>
    <div>
        <label>Step:4--></label>
        <a href="javascript:zy_Login()" class="easyui-linkbutton" plain="true">账号登录 ZySDK.zyLogin</a>
        <label>
            ------>若 zyLogin()失败,则自动注册
        </label>
    </div>
    <div>
        获取--->
    </div>
    <div>
        <label for="username"> username:</label>
        <input type="text" id="username" size="20"/>
    </div>
    <div>
        <label for="password"> password:</label>
        <input type="text" id="password" size="20" onkeydown=""/>
    </div>

    <div>
        <label for="accountId">指悦账号id：</label>
        <input type="text" id="accountId" size="20" onkeydown=""/>
    </div>
    <div>
        <label>--------------------------------------</label>
    </div>
    <div>
        <label>Step:5-->输入角色id</label>
    </div>
    <div>
        <label for="roleId">角色id：</label>
        <input type="text" id="roleId" size="20" onkeydown=""/>
    </div>
    <div>
        <label>-------------功能---------------</label>
    </div>
    <%--    <div>--%>
    <%--        <a href="javascript:zy_Register()" class="easyui-linkbutton" iconCls="icon-add" plain="true">注册</a>--%>
    <%--    </div>--%>

    <div>
        <label>Step:6-->创建角色上报 ZySDK.uploadGameRoleInfo()</label>
        <a href="javascript:zy_upload(1)" class="easyui-linkbutton" iconCls="icon-add" plain="true">创建角色</a>
    </div>
    <div>
        <label>Step:7-->进入游戏上报 ZySDK.uploadGameRoleInfo()</label>
        <a href="javascript:zy_upload(2)" class="easyui-linkbutton" iconCls="icon-edit" plain="true">进入游戏(游戏场景)</a>
    </div>

    <div>
        <label>Step:8-->退出游戏上报 ZySDK.uploadGameRoleInfo()</label>
        <a href="javascript:zy_upload(3)" class="easyui-linkbutton" iconCls="icon-edit" plain="true">退出游戏</a>
    </div>
    <div>
        <label>--------------------------------------</label>
    </div>
    <div>
        <label>Step:9-->订单数据上报 ZySDK.pay()</label>
        <a href="javascript:zy_UploadPayInfo()" class="easyui-linkbutton" iconCls="icon-add" plain="true">充值上报</a>
        <%--        <a href="javascript:test_PayInfo()" class="easyui-linkbutton" iconCls="icon-edit" plain="true">官方充值</a>--%>
    </div>
    <div>
        <label for="oderid">订单号：</label>
        <input type="text" id="oderid" size="20" onkeydown=""/>
        <label for="money">金额：</label>
        <input type="text" id="money" size="20" onkeydown=""/>
        <label for="payRecord_state">&nbsp;支付状态:</label>
        <select title="选择订单状态" id="payRecord_state">
            <option value="">未选择</option>
            <%--                <option value="0">点开充值界面:未点充值按钮(取消支付)</option>--%>
            <option value="1" selected="selected">选择充值方式界面:未支付(取消支付)</option>
            <option value="2">支付宝微信界面:未支付(取消支付)</option>
            <option value="3">支付成功:未发货</option>
            <option value="4">支付成功:已发货(交易完成)</option>
            <option value="5">支付成功:补单(交易完成)</option>
        </select>
    </div>

    <div>
        <label>--------------------------------------</label>
    </div>

    <div>
        <label for="auto" hidden="true">使用渠道uid注册</label>
        <select id="auto" hidden="true">
            <option value="true" selected="selected">渠道号自动</option>
            <option value="false">否</option>
        </select>
    </div>
    <div>
        <label for="isChannel" hidden="true">是否渠道登录</label>
        <select id="isChannel" hidden="true">
            <option value="true" selected="selected">渠道号自动</option>
            <option value="false">否</option>
        </select>
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
        let appId = $("#save_gameId").val();
        let channelId = $("#save_spId").val();
        let GameKey;
        if (appId === "1") {
            GameKey = "x889btf66ktzqp6p34t4exz10b5r1hl9";
        } else if (appId === "10") {
            GameKey = "f1kn2ta5i3qkg7vi015dkq5muy0ii786";
        } else if (appId === "11") {
            GameKey = "l44i45326jixrlaio9c0025g974125y6"
        } else {
            alert("无此游戏");
            return;
        }
        ZySDK.init(appId, GameKey, channelId, function () {
            console.info("init ok");
        });
    }

    function SpInit() {
        let channelUid = $("#channelUid").val();
        ZySDK.initUser(channelUid, "", function (data) {
            if (data.state === false) {
                setLog(data.message, 1);
            } else {
                setLog(data.message, 0);
            }
        });
    }

    function zy_Register() {
        let regInfo = {};
        regInfo.auto = $("#auto").val() === "true";
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
        ZySDK.zyRegister(regInfo, function (callbackData) {
            if (callbackData.state === false) {
                setLog(callbackData.message, 1);
            } else {
                setLog(callbackData.message, 0);
                setLog(JSON.stringify(callbackData), 0);

                let ZyUid = callbackData.uid;
                let username = callbackData.account;
                let password = callbackData.password;
                let channelUid = callbackData.channelUid;

                $("#accountId").val(ZyUid);
                $("#username").val(username);
                $("#password").val(password);
                $("#channelUid").val(channelUid);
            }
        });
    }

    function zy_Login() {
        let loginInfo = {};
        loginInfo.isAuto = $("#isChannel").val() === "true";
        loginInfo.username = $("#username").val();
        loginInfo.password = $("#password").val();

        ZySDK.zyLogin(loginInfo, function (callbackLoginData) {
            if (callbackLoginData.state === false) {
                setLog(callbackLoginData.message, 1);
                if (callbackLoginData.message === "无此渠道用户") {
                    zy_Register();
                }
            } else {
                setLog(JSON.stringify(callbackLoginData), 0);
                $("#accountId").val(callbackLoginData.zyUid);
                $("#username").val(callbackLoginData.username);
                $("#password").val(callbackLoginData.password);
                $("#channelUid").val(callbackLoginData.channelUid);
            }
        });
    }

    function zy_upload(type) {
        let roleInfo = {};
        roleInfo.roleId = $("#roleId").val();
        roleInfo.roleName = roleInfo.roleId + "_Name";
        roleInfo.roleLevel = 1;
        roleInfo.zoneId = $("#save_serverId").val();
        roleInfo.zoneName = roleInfo.zoneId + " 区";
        roleInfo.balance = 0;
        roleInfo.vip = 1;
        roleInfo.partyName = "无帮派";
        let key;
        if (type === 1) {
            key = "createRole";
            roleInfo.roleCTime = new Date().valueOf();
        } else if (type === 2) {
            key = "enterGame";
        } else if (type === 3) {
            key = "exitGame";
        } else {
            key = "levelUp";
        }
        let result = ZySDK.uploadGameRoleInfo(key, roleInfo, function (callbackData) {
            if (callbackData.state === false) {
                setLog(callbackData.message, 1);
                setLog(JSON.stringify(roleInfo), 1);
            } else {
                setLog(JSON.stringify(callbackData), 0);
            }
        });
    }

    function zy_UploadPayInfo() {
        let orderInfo = {};
        orderInfo.accountID = $("#accountId").val();
        orderInfo.channelOrderID = $("#oderid").val();

        orderInfo.productID = "1";
        orderInfo.productName = "大还丹";
        orderInfo.productDesc = "使用立即回复满血";
        orderInfo.money = $("#money").val();

        orderInfo.roleID = $("#roleId").val();
        orderInfo.roleName = "测试账号_" + orderInfo.roleID;
        orderInfo.roleLevel = 1;

        orderInfo.serverID = $("#save_serverId").val();
        orderInfo.serverName = "区服" + orderInfo.serverID;

        orderInfo.realMoney = "0";
        orderInfo.completeTime = new Date().valueOf();
        orderInfo.sdkOrderTime = new Date().valueOf();

        orderInfo.status = $("#payRecord_state").val();
        orderInfo.notifyUrl = "47.101.44.31";
        orderInfo.signType = "MD5";

        if (status >= OrderStatus[4]) {
            orderInfo.completeTime = orderInfo.sdkOrderTime + 1000;
        }
        let result = ZySDK.pay(orderInfo, function (callbackData) {
            if (callbackData.state === false) {
                setLog(callbackData.message, 1);
                setLog(JSON.stringify(orderInfo), 1);
            } else {
                setLog(JSON.stringify(callbackData), 0);

                // window.open("../mall/mall.html?oid=" + callbackData.orderId + "&appId=" + ZySDK.GameId + "&secretKey=" + ZySDK.GameKey);
                loadZyPayHtml(callbackData.orderId, orderInfo.productDesc, orderInfo.productName, orderInfo.money, orderInfo.productID, "test");

                loadZyPayHtml(callbackData.orderId, orderInfo.productDesc, orderInfo.productName, money, orderInfo.productID, "test");
            }
        });
        console.info("订单当前状态 " + OrderStatusDesc[orderInfo.status]);
    }

</script>

</html>
