$(function () {
    let appId = 14;
    let GameKey = "u6d3047qbltix34a9l0g2bvs5e8q82ol";
    let channelId = 8;
    let requestUri = getNowHost();
    requestUri = requestUri.replace(/\&amp;/g, '&');

    let params = requestUri.split('?')[1];

    sdkInit(appId, GameKey, channelId);
});
let channelUid = 0;
let roleID = 0;

function sdkInit(GameId, GameKey, ChannelCode) {
    ZhiYueSDK.init(GameId, GameKey, ChannelCode, function (status) {
        if (!status) {
            console.error("ZhiYueSDK init fail");
            return;
        }
        console.log("ZhiYueSDK init success");
        ZhiYueSDK.login(function (callbackData) {
            console.log("login " + JSON.stringify(callbackData));
            if (callbackData.status) {
                console.log('GameDemo:ZhiYueSDK登录成功: uid=>' + callbackData.data.uid);
                //模拟cp服务器进行登录校验
                checkUserInfo(callbackData);
                channelUid = callbackData.data.uid;
                roleID = getRndInteger(1000000, 9999999);
                console.log("roleID=" + roleID);
                test()
                // uploadRoleInfo(2, channelUid, roleID);
                // uploadRoleInfo(3, channelUid, roleID);
                // sdkPay(roleID);
                // testChannelPayCallback();
                // uploadRoleInfo(4, channelUid, roleID);
                // uploadRoleInfo(5, channelUid, roleID);
            } else {
                console.log('GameDemo:ZhiYueSDK登录失败:' + callbackData.message);
            }
        });
    });
}

// 模拟支付
function sdkPay(roleId) {
    console.log("sdkPay");
    //1.生成指悦订单
    let cpOrderId = getCpOrderId();
    //2.调用渠道支付接口
    let orderInfo = {};
    orderInfo.gameKey = ZhiYueSDK.GameKey;          //
    orderInfo.uid = channelUid;                     //渠道UID
    orderInfo.username = channelUid;                //渠道username
    orderInfo.userRoleId = roleId;                  //游戏内角色ID
    orderInfo.userRoleName = "名_" + channelUid;    //游戏角色
    orderInfo.serverId = "1";                       //角色所在区服ID
    orderInfo.userServer = "测试1区";                //角色所在区服
    orderInfo.userLevel = "1";                      //角色等级
    orderInfo.cpOrderNo = cpOrderId;                //游戏内的订单,SDK服务器通知中会回传
    orderInfo.amount = 1;                            //购买金额（元）
    orderInfo.count = 1;                            //购买商品个数
    orderInfo.quantifier = "个";                    //购买商品单位，如，个
    orderInfo.subject = "测试道具1";                 //道具名称
    orderInfo.desc = "测试道具1";                    //道具描述
    orderInfo.callbackUrl = "";                     //Cp服务器通知地址
    orderInfo.extrasParams = "";                    //透传参数,服务器通知中原样回传
    orderInfo.goodsId = "1";                        //商品ID
    if (ZhiYueSDK.channelId === 9 || ZhiYueSDK.channelId === 10) {
        orderInfo.amount = 0.01;
    }
    ZhiYueSDK.pay(orderInfo, function (payStatusObject) {
        console.log(payStatusObject);
        let status = payStatusObject.hasOwnProperty("status") ? payStatusObject.orderNo : false;
        let orderNo = payStatusObject.hasOwnProperty("orderNo") ? payStatusObject.orderNo : "";
        let channelOrder = payStatusObject.hasOwnProperty("channelOrder") ? payStatusObject.channelOrder : "";
        console.log("status=" + status);
        console.log("orderNo" + orderNo);
        console.log("channelOrder=" + channelOrder);
    });

    //3.SDK服务器收到渠道支付回调 处理订单
}

/**指悦账号注册 获取下一位uid*/
function getZyNextUid() {
    let id = "0";
    let url = ZhiYue_domain.replace("webGame2", "");

    $.ajax({
        url: url + "/test/getId",
        type: "get",
        async: false,
        success: function (result) {
            console.info("nextId=" + result);
            id = result;
        }, error: function () {

        }
    });
    return id;
}

function getCpOrderId() {
    let url = ZhiYue_domain.replace("webGame2", "");
    let id = "0";
    $.ajax({
        url: url + "/test/genOrder",
        type: "get",
        async: false,
        success: function (result) {
            console.info("cpOrderId=" + result);
            id = result;
        }, error: function () {

        }
    });
    return id;
}

function uploadRoleInfo(type, uid, roleId) {
    let roleInfo = {};
    roleInfo.datatype = type;
    roleInfo.roleCreateTime = Date.parse(new Date()) / 1000;
    roleInfo.uid = uid;
    roleInfo.username = 'username_' + uid;
    roleInfo.serverId = 1;
    roleInfo.serverName = '内测1区';
    roleInfo.userRoleName = 'username_' + uid;
    roleInfo.userRoleId = roleId;
    roleInfo.userRoleBalance = 1000;
    roleInfo.vipLevel = 1;
    roleInfo.userRoleLevel = 1;
    roleInfo.partyId = 1;
    roleInfo.partyName = '行会名称';
    roleInfo.gameRoleGender = '男';
    roleInfo.gameRolePower = 100;
    roleInfo.partyRoleId = 1;
    roleInfo.partyRoleName = '会长';
    roleInfo.professionId = '1';
    roleInfo.profession = '武士';
    roleInfo.friendlist = '';
    if (type !== 2) {
         roleInfo = {
            "datatype": 3,
            "roleCreateTime": 1590376401,
            "uid": "10000210268567",
            "username": "username_高山仰止",
            "serverId": 65501,
            "serverName": "测试65501区",
            "userRoleName": "username_无情地域",
            "userRoleId": 42860509,
            "userRoleBalance": 0,
            "vipLevel": 1,
            "userRoleLevel": 59,
            "partyId": "",
            "partyName": "",
            "gameRoleGender": "无",
            "gameRolePower": 5854,
            "partyRoleId": "",
            "partyRoleName": "",
            "professionId": "0",
            "profession": "无",
            "friendlist": "",
            "GameId": "14",
            "GameKey": "u6d3047qbltix34a9l0g2bvs5e8q82ol",
            "channelId": 9
        };
    }
    ZhiYueSDK.uploadGameRoleInfo(roleInfo, function (response) {
        if (response.status) {
            console.log('提交信息成功');
        } else {
            console.log(response.message);
        }
    });
}

function test() {
    let roleInfo = {
        "datatype": 3,
        "roleCreateTime": 1590376401,
        "uid": "10000210268567",
        "username": "username_高山仰止",
        "serverId": 65501,
        "serverName": "测试65501区",
        "userRoleName": "username_无情地域",
        "userRoleId": 42860509,
        "userRoleBalance": 0,
        "vipLevel": 1,
        "userRoleLevel": 59,
        "partyId": "",
        "partyName": "",
        "gameRoleGender": "无",
        "gameRolePower": 5854,
        "partyRoleId": "",
        "partyRoleName": "",
        "professionId": "0",
        "profession": "无",
        "friendlist": "",
        "GameId": "14",
        "GameKey": "u6d3047qbltix34a9l0g2bvs5e8q82ol",
        "channelId": 9
    };
    let rspObject = {};
    let data = JSON.stringify(roleInfo);
    $.ajax({
        url: ZhiYue_domain + "/ajaxUploadGameRoleInfo",
        type: "post",
        contentType: "text/plain; charset=utf-8",
        data: data,
        dataType: "json",
        async: false,
        success: function (respData) {
            console.info(respData);
        },
        error: function () {
            rspObject.status = false;
            rspObject.message = "接口请求失败";
            console.info(rspObject);
        }
    });
}

