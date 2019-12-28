/**
 * @author song minghua
 * @date 2019/12/27
 * @version 0.1
 */
const t_domain = "http://localhost:8080/ttt";
const keys = ["createRole", "levelUp", "enterServer"];
const messgae = ["SUCCESS", "FAIL"];
const OrderStatus = [0, 1, 2, 3, 4, 5];
const OrderStatusDesc = [
    "点开充值界面     未点充值按钮（取消支付）",
    "选择充值方式界面  未选择充值方式（取消支付）",
    "支付宝微信界面   未支付（取消支付）",
    "支付成功 未发货",
    "支付成功 已发货(交易完成)",
    "支付成功 补单(交易完成)"
];
let OrderStateMsg = [0, "发送成功", 2, 3, 4, 5,
    "角色不存在", 7, 8, 9, "金额错误",
    "订单错误", 12, 13, 14, 15,
    16, "参数与订单参数不一致", "参数为空或非法", "账号不存在"];

//测试用例
function test_CreateRole() {
    let appId = $("#save_gameId").val();
    let serverId = $("#save_serverId").val();
    let channelId = $("#save_spId").val();
    let channelUserId = $("#channelUserId").val();
    let roleId = $("#roleId").val();

    let timestamp = (new Date()).valueOf();
    createRole(keys[0], appId, channelId, channelUserId,
        roleId, "测试角色" + roleId, 1,
        serverId, "区服" + serverId, 100, 1,
        "无帮派", timestamp, 0);
}

/**
 * 角色-上报数据接口
 * @param  {string} key
 *              <p> createrole    创建新角色时调用
 *              <p> levelup       玩家升级角色时调用
 *              <p> enterServer   选择服务器进入时调用
 * @param   {number}     appId           游戏id
 * @param   {number}     channelId       玩家渠道id
 * @param   {number}     channelUid      玩家渠道账号id
 * @param   {number}     roleId          当前登录的玩家角色ID，必须为数字
 * @param   {string}     roleName        当前登录的玩家角色名，不能为空，不能为null
 * @param   {number}     roleLevel       当前登录的玩家角色等级，必须为数字，且不能为0，若无，传入1
 * @param   {number}     zoneId          当前登录的游戏区服ID，必须为数字，且不能为0，若无，传入1
 * @param   {string}     zoneName        当前登录的游戏区服名称，不能为空，不能为null
 * @param   {number}     balance         用户游戏币余额，必须为数字，若无，传入0
 * @param   {number}     vip             当前用户VIP等级，必须为数字，若无，传入1
 * @param   {string}     partyName       当前角色所属帮派，不能为空，不能为null，若无，传入“无帮派”
 * @param   {number}     roleCTime       单位为毫秒，创建角色的时间
 * @param   {number}     roleLevelMTime  单位为毫秒，角色等级变化时间
 * @return
 * */
function createRole(key, appId, channelId, channelUid,
                    roleId, roleName, roleLevel,
                    zoneId, zoneName, balance, vip,
                    partyName, roleCTime, roleLevelMTime) {
    let hasKey = false;
    for (let aKey of keys) {
        console.info(aKey);
        if (aKey === key) {
            hasKey = true;
            break;
        }
    }

    if (!hasKey) {
        console.error("不存在key " + key);
        return;
    }

    let value = {
        "channelId": channelId,
        "channelUid": channelUid,
        "appId": appId,
        "roleId": roleId,
        "roleName": roleName,
        "roleLevel": roleLevel,
        "zoneId": zoneId,
        "zoneName": zoneName,
        "balance": balance,
        "vip": vip,
        "partyName": partyName,
        "roleCTime": roleCTime,
        "roleLevelMTime": roleLevelMTime
    };

    for (let key in value) {
        if (value[key] === "undefined") {
            console.error(key + ':' + value[key]);
            return;
        }
    }

    let ss = JSON.stringify(value);
    let data = {
        "key": key,
        "value": ss
    };
    console.info("设置角色基本数据 send " + ss);
    $.ajax({
        url: t_domain + "/setdata",
        type: "post",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(data),
        dataType: "json",
        async: false,
        success: function (result) {
            console.info("设置角色基本数据 recv code=" + result.resultCode);
            if (result.resultCode === 200) {
                console.info("设置角色基本数据 recv result:" + result.messgae);
                if (result.messgae === messgae[0]) {
                    console.info("设置角色基本数据 recv " + result.data);
                }
            }
            return result;
        },
        error: function (result) {
            console.info(result.data);
            console.info("系统提示", "上传角色数据失败");
        }
    });
    return "";
}

//测试用例
function test_EnterGame() {
    let channelUserId = $("#channelUserId").val();
    let appId = $("#save_gameId").val();
    let serverId = $("#save_serverId").val();
    let channelId = $("#save_spId").val();
    let roleId = $("#roleId").val();
    let result = enterGame(appId, serverId, channelId, channelUserId, roleId);

}

/**
 * 进入游戏-上报数据接口
 * @param   {number}     appId           游戏id
 * @param   {number}     serverId        当前登录的游戏区服ID，必须为数字，且不能为0，若无，传入1
 * @param   {number}     channelId       玩家渠道id
 * @param   {number}     channelUid      玩家渠道账号id
 * @param   {number}     roleId          当前登录的玩家角色ID，必须为数字
 * */
function enterGame(appId, serverId, channelId, channelUid, roleId) {
    if (appId == null || serverId == null || channelId == null || channelUid == null || roleId == null) {
        console.error("enterGame 参数为空 请检查 ");
        console.error(" appId " + appId);
        console.error(" serverId " + serverId);
        console.error(" channelId " + channelId);
        console.error(" channelUid " + channelUid);
        console.error(" roleId " + roleId);
        return;
    }
    let param = "appId=" + appId +
        "&serverId=" + serverId +
        "&channelId=" + channelId +
        "&channelUid=" + channelUid +
        "&roleId=" + roleId;

    console.info("进入游戏上报 send " + param);

    $.ajax({
        url: t_domain + "/enter?" + param,
        type: "get",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (result) {
            console.info("进入游戏上报 recv " + result.resultCode);
            if (result.resultCode === 200) {
                console.info("进入游戏上报 recv result:" + result.message);
                if (result.message === messgae[0]) {
                    console.info("进入游戏上报 recv " + result.data);
                } else {
                    console.error("进入游戏上报 recv " + result.reason);
                }
            }
            return result;
        },
        error: function () {
            console.error("系统提示", "进入游戏上报失败");
        }
    });
    return "";
}

//测试用例
function test_ExitGame() {
    let channelUserId = $("#channelUserId").val();
    let appId = $("#save_gameId").val();
    let serverId = $("#save_serverId").val();
    let channelId = $("#save_spId").val();
    let roleId = $("#roleId").val();
    exitGame(appId, serverId, channelId, channelUserId, roleId);
}

/**
 * 退出游戏-上报数据接口
 * @param   {number}     appId           游戏id
 * @param   {number}     serverId        当前登录的游戏区服ID，必须为数字，且不能为0，若无，传入1
 * @param   {number}     channelId       玩家渠道id
 * @param   {number}     channelUid      玩家渠道账号id
 * @param   {number}     roleId          当前登录的玩家角色ID，必须为数字
 * */
function exitGame(appId, serverId, channelId, channelUid, roleId) {
    if (appId == null || serverId == null || channelId == null || channelUid == null || roleId == null) {
        console.error("enterGame 参数为空 请检查 ");
        console.error(" appId " + appId);
        console.error(" serverId " + serverId);
        console.error(" channelId " + channelId);
        console.error(" channelUid " + channelUid);
        console.error(" roleId " + roleId);
        return;
    }
    let param = "appId=" + appId +
        "&serverId=" + serverId +
        "&channelId=" + channelId +
        "&channelUid=" + channelUid +
        "&roleId=" + roleId;

    console.info("退出游戏上报 send " + param);

    $.ajax({
        url: t_domain + "/exit?" + param,
        type: "get",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (result) {
            if (result.resultCode === 200) {
                console.info("退出游戏上报 recv " + result.resultCode);
                if (result.resultCode === 200) {
                    console.info("退出游戏上报 recv result:" + result.message);
                    if (result.message === messgae[0]) {
                        console.info("退出游戏上报 recv " + result.data);
                    } else {
                        console.error("退出游戏上报 recv " + result.reason);
                    }
                }
            }
            return result;
        },
        error: function () {
            console.error("系统提示", "退出游戏上报失败");
        }
    });
    return "";
}


//测试用例
function test_UploadPayInfo() {
    let channelUid = $("#channelUserId").val();
    let appId = $("#save_gameId").val();
    let serverId = $("#save_serverId").val();
    let channelId = $("#save_spId").val();
    let roleId = $("#roleId").val();
    let order_status = $("#payRecord_state").val();

    let accountId = $("#accountId").val();
    let channelOrderID = $("#oderid").val();
    let productID = "1";
    let productName = "大还丹";
    let productDesc = "使用立即回复满血";
    let money = $("#money").val();
    let realMoney = money;
    let roleID = roleId;
    let roleName = "测试账号1";
    let roleLevel = 1;
    let serverID = serverId;
    let serverName = "区服" + serverId;
    let extension = "";
    let status = order_status;
    let notifyUrl = "47.101.44.31";
    let signType = "MD5";
    let completeTime = 0;
    let sdkOrderTime = new Date().valueOf();
    if (status >= OrderStatus[4]) {
        completeTime = sdkOrderTime + 1000;
    }

    let sign = md5(accountId, channelId, channelUid, appId, channelOrderID,
        productID, productName, productDesc, money,
        roleID, roleName, roleLevel,
        serverID, serverName,
        realMoney, completeTime, sdkOrderTime,
        status, notifyUrl, signType);


    uploadPayInfo(accountId, channelId, channelUid, appId, channelOrderID,
        productID, productName, productDesc, money,
        roleID, roleName, roleLevel,
        serverID, serverName, realMoney,
        completeTime, sdkOrderTime,
        status, notifyUrl,
        signType, sign);
}

//md5加密
function md5(accountID, channelId, channelUid, appId, channelOrderID,
             productID, productName, productDesc, money,
             roleID, roleName, roleLevel,
             serverID, serverName,
             realMoney, completeTime, sdkOrderTime,
             status, notifyUrl,
             signType) {
    if (signType === "MD5") {
        let signString =
            "accountID=" + accountID + "&" +
            "channelID=" + channelId + "&" +
            "channelUid=" + channelUid + "&" +
            "appID=" + appId + "&" +
            "channelOrderID=" + channelOrderID + "&" +

            "productID=" + productID + "&" +
            "productName=" + productName + "&" +
            "productDesc=" + productDesc + "&" +
            "money=" + money + "&" +

            "roleID=" + roleID + "&" +
            "roleName=" + roleName + "&" +
            "roleLevel=" + roleLevel + "&" +

            "serverID=" + serverID + "&" +
            "serverName=" + serverName + "&" +

            "realMoney=" + realMoney + "&" +
            "completeTime=" + completeTime + "&" +
            "sdkOrderTime=" + sdkOrderTime + "&" +

            "status=" + status + "&" +
            "notifyUrl=" + notifyUrl;
        console.info("充值 sign " + signString);
        let urlSign = encodeURIComponent(signString);
        console.info("充值 sign url " + urlSign);
        return $.md5(urlSign);
    } else {
        return "";
    }
}

/**
 * 充值-上报数据接口
 * @param   {number}      accountID           指悦账号id
 * @param   {number}      channelId           渠道id
 * @param   {number}      channelUid          渠道账号id
 * @param   {number}      appId               游戏id
 * @param   {string}      channelOrderID      渠道订单号
 * @param   {string}      productID           当前商品ID
 * @param   {string}      productName         商品名称
 * @param   {string}      productDesc         商品描述
 * @param   {number}      money               商品价格,单位:分
 * @param   {number}      roleID              玩家在游戏服中的角色ID
 * @param   {string}      roleName            玩家在游戏服中的角色名称
 * @param   {number}      roleLevel           玩家等级
 * @param   {number}      serverID            玩家所在的服务器ID
 * @param   {string}      serverName          玩家所在的服务器名称
 * @param   {number}      realMoney           订单完成,实际支付金额,单位:分,未完成:-1
 * @param   {number}      completeTime        订单完成时间戳(毫秒，13位),未完成为:-1
 * @param   {number}      sdkOrderTime        订单创建时间戳(毫秒，13位)
 * @param   {number}      status              订单状态 请看OrderStatus、OrderStatusDesc
 * @param   {string}      notifyUrl           支付回调通知的游戏服地址
 * @param   {string}      signType            签名算法,RSA|MD5,默认MD5
 * @param   {string}      sign                签名
 * @return  {json}
 * */
function uploadPayInfo(accountID, channelId, channelUid, appId, channelOrderID,
                       productID, productName, productDesc, money,
                       roleID, roleName, roleLevel,
                       serverID, serverName,
                       realMoney, completeTime, sdkOrderTime,
                       status, notifyUrl,
                       signType, sign) {
    let isStatus = false;
    for (let orderStatus of status) {
        if (orderStatus === status) {
            isStatus = true;
            break;
        }
    }
    if (!isStatus) {
        console.error("订单状态错误 " + status);
        return;
    }
    let extension;
    if (status >= OrderStatus[3]) {

    }

    if (accountID == null || channelId == null || channelUid == null || appId == null ||
        accountID === "" || channelId === "" || channelUid === "" || appId === "") {
        console.error("账号或游戏参数为空 ");
        return;
    }
    if (productID == null || productName == null || productDesc == null || money == null ||
        productID === "" || productName === "" || productDesc === "") {
        console.error("商品参数为空 ");
        return;
    }
    if (roleID === null || roleName === null || roleName === "" || roleLevel === null) {
        console.error("角色参数为空 ");
        return;
    }
    if (serverID === null || serverName === null || serverName === "") {
        console.error("区服参数为空 ");
        return;
    }
    if (channelOrderID == null || channelOrderID === "" || realMoney == null || completeTime == null || sdkOrderTime == null) {
        console.error("订单参数为空 ");
        return;
    }
    if (signType == null || signType === "" || sign == null || sign === "") {
        console.error("签名数为空 ");
        return;
    }
    if (signType !== "MD5" && signType !== "RSA") {
        console.error("签名类型错误 ");
        return;
    }

    let data = {
        "accountID": accountID,
        "channelID": channelId,
        "channelUid": channelUid,
        "appID": appId,
        "channelOrderID": channelOrderID,

        "productID": productID,
        "productName": productName,
        "productDesc": productDesc,
        "money": money,

        "roleID": roleID,
        "roleName": roleName,
        "roleLevel": roleLevel,

        "serverID": serverID,
        "serverName": serverName,

        "realMoney": realMoney,
        "completeTime": completeTime,
        "sdkOrderTime": sdkOrderTime,

        "status": status,
        "notifyUrl": notifyUrl,
        "signType": signType,
        "sign": sign
    };
    console.info(data);
    $.ajax({
        url: "/ttt/payInfo",
        type: "post",
        dataType: 'json',
        contentType: "text/plain; charset=utf-8",
        data: JSON.stringify(data),
        async: false,
        success: function (result) {
            console.info("充值上报 recv " + result.resultCode);
            if (result.resultCode === 200) {
                console.info("充值上报 recv result:" + result.message);
                if (result.state !== 1) {
                    console.error("充值上报 recv " + OrderStateMsg[result.state]);
                } else {
                    console.info("充值上报 recv " + OrderStateMsg[result.state]);
                }
            }
            return result;
        },
        error: function () {
            console.error("系统提示", "充值数据上报失败");
        }
    });
}

function test_Register() {
    let auto = $("#auto").val();
    let appId = $("#save_gameId").val();
    let channelId = $("#save_spId").val();
    let channelUid = $("#channelUserId").val();

    let username = $("#username").val();
    let password = $("#password").val();
    let phone = "18571470846";
    let deviceCode = "PC";
    let imei = "PC";
    let addparm = "";

    let result = register(auto, appId,
        channelId, channelUid, "account_" + channelUid, "name_" + channelUid,
        username, password, phone, deviceCode, imei, addparm);

    $("#username").val(result.account);
    $("#password").val(result.password);
    $("#accountId").val(result.uid);
    $("#channelUserId").val(result.channelUid);
}

/**
 * 注册-接口
 * @param   {boolean}       auto             是否自动注册,无需账号密码
 * @param   {number}        appId            游戏id
 * @param   {number}        channelId        渠道id
 * @param   {string}        channelUid       渠道账号id
 * @param   {string}        channelUname     渠道账号名称
 * @param   {string}        channelUnick     渠道账号昵称
 * @param   {string}        username         指悦账号
 * @param   {string}        password         指悦账号密码
 * @param   {string}        phone            手机号
 * @param   {string}        deviceCode
 * @param   {string}        imei
 * @param   {string}        addparm          额外参数
 * @return  {json}
 * */
function register(auto, appId,
                  channelId, channelUid, channelUname, channelUnick,
                  username, password,
                  phone, deviceCode, imei,
                  addparm) {
    if (auto === "true") {
        if (channelId == null || channelId.toString() === "") {
            console.error("注册接口", "渠道id错误：" + channelId);
            return null;
        }
        if (channelId === "0") {
            //官方渠道
            channelUid = "0";
        } else {
            //其他渠道
            if (channelUid == null || channelUid === "") {
                console.error("注册接口", "渠道uid错误：" + channelUid);
                return null;
            }
        }
    } else {
        console.log("username：" + username);
        console.log("password：" + password);
        if (username == null || username === "" || password == null || password === "") {
            console.error("注册接口", "账号密码为空");
            return null;
        }
    }

    let data = {
        "auto": auto,
        "appId": appId,
        "channelId": channelId,
        "channelUid": channelUid,
        "channelUname": channelUname,
        "channelUnick": channelUnick,

        "username": username,
        "pwd": password,
        "phone": phone,
        "deviceCode": deviceCode,
        "imei": imei,
        "addparm": addparm
    };
    let response;
    $.ajax({
        url: "/ttt/register",
        type: "post",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(data),
        dataType: "json",
        async: false,
        success: function (result) {
            console.info(result);
            if (result.resultCode === 200) {
                if (result.data.message === messgae[0]) {
                    console.info("注册接口 recv ", result.data.reason);
                    console.info("注册接口 recv 账号 " + result.data.account);
                    console.info("注册接口 recv 密码 " + result.data.password);
                    console.info("注册接口 recv uid " + result.data.accountId);
                    console.info("注册接口 recv channelUid " + result.data.channelUid);
                    response = {
                        "account": result.data.account,
                        "password": result.data.password,
                        "uid": result.data.accountId,
                        "channelUid": result.data.channelUid,
                        "reason": result.data.reason,
                        "message": result.data.message
                    }
                } else {
                    console.error("注册接口 recv ", result.data.reason);
                    response = {
                        "reason": result.data.reason,
                        "message": result.data.message
                    }
                }
            }
        },
        error: function () {
            console.error("注册接口", "通信失败");
        }
    });
    return response;
}