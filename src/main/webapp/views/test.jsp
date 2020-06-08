<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
    <%--    <script type="text/javascript"--%>
    <%--            src="${pageContext.request.contextPath}/js/libZySdk_v1.js?v=202005121504"></script>--%>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/libZySdk_v2.js?v=202005121504"></script>
</head>

<%--<body style="margin:1px;" id="ff" bgcolor="#7fffd4" onload="checkCookies()">--%>
<body style="margin:1px;" id="ff" bgcolor="#7fffd4">
<div id="sp">
    <%--    <div>--%>
    <%--        <a href="javascript:initSpGameServer(1)" class="easyui-linkbutton"--%>
    <%--           iconCls=" icon-search" plain="true">查询渠道</a>--%>
    <%--        <a href="javascript:initSpGameServer(2)" class="easyui-linkbutton" style="margin-left:50px"--%>
    <%--           iconCls=" icon-search" plain="true">查询游戏</a>--%>
    <!--        <a href="javascript:initSpGameServer(3)" class="easyui-linkbutton" style="margin-left:50px"-->
    <%--           iconCls=" icon-search" plain="true">查询区服</a>--%>
    <%--    </div>--%>
    <label for="save_gameId"></label>
    <span style="color: blue;">游戏:</span>
    <select title="选择游戏" id="save_gameId" name="gameId" onchange="initSpGameServer(1)">
        <%--        <option value="-1" selected="selected">请选择</option>--%>
        <%--        <option value="9" selected="selected">三国游侠 9</option>--%>
        <%--        <option value="10">刺沙 10</option>--%>
        <option value="11" selected="selected">刺沙-指悦 11</option>
        <option value="14" selected="selected">巨龙战歌-指悦 14</option>
    </select>

    <label for="save_spId"></label>
    <span style="color: blue; margin-left:50px  ">渠道:</span>
    <select title="选择渠道" id="save_spId" name="spId">
        <%--        <option value="-1" selected="selected">请选择</option>--%>
        <option value="0" selected="selected">官方渠道 0</option>
        <option value="1">渠道 测试</option>
        <option value="2">YY</option>
        <option value="3">乐趣</option>
        <option value="4">小度</option>
        <option value="5">爱点游</option>
        <option value="6">虫虫游戏</option>
        <option value="7">闲兔</option>
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
        <option value="170">170 区</option>
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
        <label>Step:10----->一键下单/label>
            <a href="javascript:auto()" class="easyui-linkbutton" iconCls="icon-add" plain="true">一键下单</a>
            <%--        <a href="javascript:test_PayInfo()" class="easyui-linkbutton" iconCls="icon-edit" plain="true">官方充值</a>--%>
            <%--            <a href="javascript:ttt()" class="easyui-linkbutton" iconCls="icon-add" plain="true">测试</a>--%>
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
    <div>
        <a href="javascript:showWindow()">show</a>
    </div>
</div>
<!-- 遮罩层 -->
<div id="cover"
     style="background: #000; position: absolute; left: 0px; top: 0px; width: 100%; filter: alpha(opacity=30); opacity: 0.3; display: none; z-index: 2 ">

</div>
<!-- 弹窗 -->
<div id="showdiv"
     style="width: 80%; margin: 0 auto; height: 9.5rem; border: 1px solid #999; display: none; position: absolute; top: 40%; left: 10%; z-index: 3; background: #fff">
    <!-- 标题 -->
    <div style="background: #F8F7F7; width: 100%; height: 2rem; font-size: 0.65rem; line-height: 2rem; border: 1px solid #999; text-align: center;">
        提示
    </div>
    <!-- 内容 -->
    <div style="text-indent: 50px; height: 4rem; font-size: 0.5rem; padding: 0.5rem; line-height: 1rem; ">
        js弹窗 js弹出DIV,并使整个页面背景变暗
    </div>
    <!-- 按钮 -->
    <div style="background: #418BCA; width: 80%; margin: 0 auto; height: 1.5rem; line-height: 1.5rem; text-align: center;color: #fff;margin-top: 1rem; -moz-border-radius: .128rem; -webkit-border-radius: .128rem; border-radius: .128rem;font-size: .59733rem;"
         onclick="closeWindow()">
        确 定
    </div>
</div>

</body>
<script type="text/javascript">
    $(function () {
        initSpGameServer(2);
        let auto = $("#auto").val();
        let appId = $("#save_gameId").val();
        let channelId = $("#save_spId").val();
        let channelUid = $("#channelUid").val();
        let username = $("#username").val();
        let password = $("#password").val();
    });

    function auto() {
        let t = 500;
        setTimeout("init()", 500);
        // $("#channelUid").val(100);
        setTimeout("SpInit()", 600);
        setTimeout("zy_Login()", 700);
        // $("#roleId").val(200);
        setTimeout("zy_upload(2)", 800);
        $("#oderid").val(new Date().valueOf());
        // $("#money").val(200);
        setTimeout("zy_UploadPayInfo()", 900);
    }

    function init() {
        let appId = $("#save_gameId").val();
        let channelId = $("#save_spId").val();
        let GameKey;
        if (appId === "1") {
            GameKey = "x889btf66ktzqp6p34t4exz10b5r1hl9";
        } else if (appId === "10") {
            GameKey = "f1kn2ta5i3qkg7vi015dkq5muy0ii786";
        } else if (appId === "11") {
            GameKey = "l44i45326jixrlaio9c0025g974125y6";
        } else if (appId === "14") {
            GameKey = "u6d3047qbltix34a9l0g2bvs5e8q82ol";
        } else {
            alert("无此游戏");
            return;
        }

        let newurl = updateQueryStringParameter(window.location.href, 'GameId', appId);
        newurl = updateQueryStringParameter(newurl, 'GameKey', GameKey);
        newurl = updateQueryStringParameter(newurl, 'channelId', channelId);
        //向当前url添加参数，没有历史记录
        window.history.replaceState({
            path: newurl
        }, '', newurl);

        ZySDK.init(appId, GameKey, function (state) {
            ZySDK.login(function (callbackData) {
                if (callbackData.status) {
                    console.log('GameDemo:zySDK登录成功: uid=>' + callbackData.data.uid);
                } else {
                    console.log('GameDemo:zySDK登录失败:' + callbackData.message);
                }
            });
            console.log("zySDK init succ");
        });
    }

    function SpInit() {
        //渠道用户id
        let channelUid = $("#channelUid").val();
        //渠道用户登录名
        let channelUName = "";
        ZySDK.initUser(channelUid, channelUName, function (data) {
            if (data.state === false) {
                console.error(data.message);
            } else {
                console.info(data.message);
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
                console.error(callbackData.message);
            } else {
                console.info(callbackData.message);
                console.info(JSON.stringify(callbackData));

                let ZyUid = callbackData.uid;
                let username = callbackData.account;
                let password = callbackData.password;
                let channelUid = callbackData.channelUid;

                $("#accountId").val(ZyUid);
                $("#username").val(username);
                $("#password").val(password);
                $("#channelUid").val(channelUid);
                SpInit();
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
                console.error(callbackLoginData.message);
                if (callbackLoginData.message === "无此渠道用户") {
                    zy_Register();
                }
            } else {
                console.info(JSON.stringify(callbackLoginData));
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
                console.error(callbackData.message);
                console.error(JSON.stringify(roleInfo));
            } else {
                console.info(JSON.stringify(callbackData));
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
        test();
        let result = ZySDK.pay(orderInfo, function (callbackData) {
            console.info(orderInfo);
            if (callbackData.state === false) {
                console.error(callbackData.message);
                console.error(JSON.stringify(orderInfo));
            } else {
                console.info(JSON.stringify(callbackData));
                let yuan;
                let fen = orderInfo.money;
                // if (fen.length > 2) {
                //     let fen1 = fen.substr(0, fen.length - 2);
                //     let fen2 = fen.substr(fen.length - 2, 2);
                //     console.info(fen1);
                //     console.info(fen2);
                //     yuan = fen1 + "." + fen2;
                // } else {
                //     yuan = "0." + fen;
                // }
                loadZyPayHtml(callbackData.orderId, orderInfo.productDesc, orderInfo.productName, fen, orderInfo.productID, "sss");
                // window.open("../mall/mall.html?oid=" + callbackData.orderId + "&appId=" + ZySDK.GameId + "&secretKey=" + ZySDK.GameKey);
                // let param = "?orderId=" +
                //     + "&body=" + orderInfo.productDesc
                //     + "&subject=" + orderInfo.productName
                //     + "&totalAmount=" + yuan
                //     + "&productId=" + orderInfo.productID
                //     + "&passBackParams=" + "透传参数"
                //     + "&appId=" + ZySDK.GameId + "&spId=" + ZySDK.channelId;
                // console.info(ZySDK.channelId);
                // // window.open("http://localhost:8081/static/pay.html" + encodeURI(param));
                // // window.open("http://localhost:8080/mall/pay.html" + encodeURI(param));
                // window.open("http://zy.hysdgame.cn/pay/static/pay.html" + encodeURI(param));
            }
        });
        console.info("订单当前状态 " + OrderStatusDesc[orderInfo.status]);

    }

    function test() {
        let order = {
            accountID: "1000064",
            channelId: "0",
            channelUid: "1000064",
            appId: "11",
            channelOrderID: "6e90bb15-3dbb-4a7a-87de-97f48713b5fe",
            productID: "59",
            productName: "10元档首充前置",
            productDesc: "10元档首充前置",
            money: 0.01,
            roleID: "5987857551844908",
            roleName: "捂裆派掌门5987857551844908",
            roleLevel: 1,
            serverID: "170",
            serverName: "170服务器170",
            realMoney: 0.01,
            completeTime: 1582600283766,
            sdkOrderTime: 1582600283766,
            status: 1,
            notifyUrl: "47.101.44.31",
            signType: "MD5",
            sign: "591725af8527d1ce5df76ba4697ec871",
        };
        let sign = orderSign(order);
        console.info(sign);
    }

    // 弹窗
    function showWindow() {
        $('#showdiv').show();  //显示弹窗
        $('#cover').css('display', 'block'); //显示遮罩层
        $('#cover').css('height', document.body.clientHeight + 'px'); //设置遮罩层的高度为当前页面高度
    }

    // 关闭弹窗
    function closeWindow() {
        $('#showdiv').hide();  //隐藏弹窗
        $('#cover').css('display', 'none');   //显示遮罩层
    }

    function ttt() {
        let data = {
            id: 1,
            openId: 2,
            itemId: 3,
            exchangeTime: 4,
            status: 1,
            message: 2,
            finishedTime: 5
        };
        // $.ajax({
        //     url: "/h5/addExRecord",
        //     type: "post",
        //     data: JSON.stringify(data),
        //     dataType: "json",//预期服务器返回的数据类型
        //     contentType: "application/json; charset=utf-8",
        //     async: false,
        //     success: function (result) {
        //         console.info(result);
        //
        //     },
        //     error: function () {
                tip("ERROR！", "查询失败");
        //     }
        // });
        test();
        return;
        send("/h5/addExRecord", "json", data);

    }

    function send(url, type, data) {
        let xhr = new XMLHttpRequest();
        xhr.open("post", url, true);
        if (type === "formdata") {
            data = new FormData();
            data.append("key", "value");
        } else if (type === "json") {
            xhr.setRequestHeader("Content-Type", "application/json");
            data = JSON.stringify(data);
        } else if (type === "text") {
            data = "key=value";
        } else if (type === "www") {
            // 这个header 其实是 传统post 表单的格式
            xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            data = "key=value";
        }
        xhr.send(data);
    }

    function test() {
        var httpdata = {
            id: 1,
            openId: 123456,
            itemId: 3,
            exchangeTime: 4,
            status: 1,
            message: 2,
            finishedTime: 5,
            address: "11",
            phone: "22",
            name: "33"
        };
        var url = "http://111.231.244.198:8080/huoguo/h5/addExRecord";
        let xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && (xhr.status >= 200 && xhr.status < 400)) {
                var response = xhr.responseText;
                console.log(response);
            }
        };

        xhr.open("POST", url, true);
        xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        xhr.send(JSON.stringify(httpdata));
    }
</script>

</html>
