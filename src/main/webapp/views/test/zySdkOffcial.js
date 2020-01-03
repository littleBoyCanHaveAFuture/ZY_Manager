/**
 * @author song minghua
 * @date 2019/12/31
 * @version 0.1.1
 */
// document.write("<script type='text/javascript' src='http://111.231.244.198:8080/try/login/jquery-3.4.1.min.js'></script>");
//md5 函数
let md5Script = document.createElement('script');
md5Script.setAttribute('type', 'text/javascript');
md5Script.setAttribute('src', 'http://111.231.244.198:8080/try/login/md5.js');
let head = document.getElementsByTagName('head')[0];
head.appendChild(md5Script);

const t_url = "http://47.101.44.31";
// const t_url = "http://localhost";
const t_domain = t_url + ":8080/ttt";
const t_domainPay = t_url + ":80/payInfo";
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

const appId_CS = 2;
const appId_SGYX = 9999;

/**
 * @param param 判断的参数
 * @return {boolean}
 * */
function checkParam(param) {
    return (param == null || param === "" || param === "undefined");
}

function test_Register() {
    let auto = $("#auto").val();
    let appId = $("#save_gameId").val();
    let channelId = $("#save_spId").val();
    let channelUid = $("#channelUid").val();

    let username = $("#username").val();
    let password = $("#password").val();
    let phone = "18571470846";
    let deviceCode = "PC";
    let imei = "PC";
    let addparm = "";

    let result = register(auto, appId,
        channelId, channelUid, "account_" + channelUid, "name_" + channelUid,
        username, password, phone, deviceCode, imei, addparm);

    console.info("test_Register");
    console.info(result);
    $("#username").val(result.account);
    $("#password").val(result.password);
    $("#accountId").val(result.uid);
    $("#channelUid").val(result.channelUid);
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
    let way = "注册-接口 ";
    if (auto === "true") {
        if (checkParam(channelId)) {
            console.error(way + "参数错误" + " channelId=" + channelId);
            return null;
        }
        if (channelId === "0") {
            //官方渠道
            channelUid = "0";
        } else {
            //其他渠道
            if (checkParam(channelUid)) {
                console.error(way + "参数错误" + " channelUid=" + channelUid);
                return null;
            }
        }
    } else {
        if (checkParam(username) || checkParam(password)) {
            console.error(way + "参数错误" + " username=" + username + " password=" + password);
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
        url: t_domain + "/register",
        type: "post",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(data),
        dataType: "json",
        async: false,
        success: function (result) {
            console.info(result);
            if (result.resultCode === 200) {
                if (result.data.message === messgae[0]) {
                    console.info(way + "recv ", result.data.reason);
                    console.info(way + "recv 账号 " + result.data.account);
                    console.info(way + "recv 密码 " + result.data.password);
                    console.info(way + "recv uid " + result.data.accountId);
                    console.info(way + "recv channelUid " + result.data.channelUid);
                    response = {
                        "account": result.data.account,
                        "password": result.data.password,
                        "uid": result.data.accountId,
                        "channelUid": result.data.channelUid,
                        "reason": result.data.reason,
                        "message": result.data.message
                    }
                } else {
                    console.error(way + "recv ", result.data.reason);
                    response = {
                        "reason": result.data.reason,
                        "message": result.data.message
                    }
                }
            }
        },
        error: function () {
            console.error(way + "通信失败");
        }
    });
    return response;
}

function test_Login() {
    let isChannel = $("#isChannel").val();
    let appId = $("#save_gameId").val();
    let channelId = $("#save_spId").val();
    let channelUid = $("#channelUid").val();
    let username = $("#username").val();
    let password = $("#password").val();
    let result = login(isChannel, appId, channelId, channelUid, username, password);
    console.info("test_Login");
    console.info(result);
    if (result.message === messgae[0]) {
        let loginResult = loginCheck(result.appid, result.uid, result.token, result.sign);
        console.info("test_Login");
        console.info(loginResult);
    }
}

/**
 * 请求登录-接口
 * @param    {boolean}      isChannel        是否渠道自动注册
 * @param   {number}        appId            游戏id
 * @param   {number}        channelId        渠道id
 * @param   {string}        channelUid       渠道账号id
 * @param   {string}        username         指悦账号
 * @param   {string}        password         指悦账号密码
 * @return  {json}
 * */
function login(isChannel, appId,
               channelId, channelUid,
               username, password) {
    let way = "请求登录-接口 ";
    if (checkParam(appId)) {
        console.error(way + "参数错误" + " appId=" + appId);
        return null;
    }

    if (isChannel === "true") {
        if (checkParam(channelId)) {
            console.error(way + "参数错误" + " channelId=" + channelId);
            return null;
        }
        if (checkParam(channelUid)) {
            console.error(way + "参数错误" + " channelUid=" + channelUid);
            return null;
        }
    } else if (isChannel === "false") {
        if (checkParam(username) || checkParam(password)) {
            console.error(way + "参数错误" + " username=" + username + " password=" + password);
            return null;
        }
    } else {
        console.error(way + "参数错误" + " isChannel=" + isChannel);
        return null;
    }

    let data = {
        "isChannel": isChannel,
        "appId": appId,
        "channelId": channelId,
        "channelUid": channelUid,
        "name": username,
        "pwd": password
    };
    let response;
    $.ajax({
        url: t_domain + "/login",
        type: "post",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(data),
        dataType: "json",
        async: false,
        success: function (result) {
            console.info(result);
            if (result.message === messgae[0]) {
                console.info(way + "recv reason " + result.reason);
                console.info(way + "recv appid " + result.appid);
                console.info(way + "recv token " + result.token);
                console.info(way + "recv uid " + result.uid);
                console.info(way + "recv sign " + result.sign);
                response = {
                    "appid": result.appid,
                    "uid": result.uid,
                    "token": result.token,
                    "sign": result.sign,
                    "reason": result.reason,
                    "message": result.message
                };
            } else {
                console.error("recv ", result.reason);
                response = {
                    "reason": result.reason,
                    "message": result.message
                };
            }
        },
        error: function () {
            console.error(way + "请求登录接口失败");
        }
    });
    return response;
}

/**
 * 请求登录校验-接口
 * @param   {number}        appId           游戏id
 * @param   {number}        accountId       指悦uid
 * @param   {string}        token           登录token
 * @param   {string}        sign            签名
 * @return  {json}
 * */
function loginCheck(appId, accountId, token, sign) {
    let way = "请求登录校验-接口 ";
    if (checkParam(appId) || checkParam(accountId) || checkParam(token) || checkParam(sign)) {
        console.error(way + "参数错误" + " appId=" + appId + " accountId=" + accountId + " token=" + token + " sign=" + sign);
        return null;
    }
    let param = "appId=" + appId + "&uid=" + accountId + "&token=" + token + "&sign=" + sign;
    console.info(way + "send " + param);
    let response;
    $.ajax({
        url: t_domain + "/check?" + param,
        type: "get",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        async: false,
        success: function (result) {
            console.info(result);
            if (result.resultCode === 200) {
                let rdata = result.data;
                if (result.data.message === messgae[0]) {
                    console.info(way + "recv reason " + rdata.reason);
                    console.info(way + "recv uid " + rdata.accountId);

                    response = {
                        "accountId": rdata.accountId,
                        "channelUid": rdata.channelUid,
                        "reason": rdata.reason,
                        "message": rdata.message
                    }
                } else {
                    console.error(way + "recv ", rdata.reason);
                    response = {
                        "reason": rdata.reason,
                        "message": rdata.message
                    }
                }
            }
        },
        error: function () {
            console.error(way + "登录校验失败");
        }
    });
    return response;
}

//测试用例
function test_CreateRole() {
    // let appId = $("#save_gameId").val();
    let appId=9999;
    let serverId = $("#save_serverId").val();
    let channelId = $("#save_spId").val();
    let channelUid = $("#channelUid").val();
    let roleId = $("#roleId").val();

    // let timestamp = (new Date()).valueOf();
    let timestamp = "1577788132";
    let roleLevelMTime = "1577788132";
    let result = createRole(keys[0], appId, channelId, channelUid,
        roleId, "Role_" + roleId, 1,
        serverId, "Server_" + serverId, 100, 1,
        "NONE", timestamp, 0);
    console.info("test_CreateRole");
    console.info(result);
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
    let way = "角色-上报数据接口 ";
    let hasKey = false;
    for (let aKey of keys) {
        console.info(aKey);
        if (aKey === key) {
            hasKey = true;
            break;
        }
    }

    if (!hasKey) {
        console.error(way + "参数错误" + " key= " + key);
        return null;
    }
    if (checkParam(appId) || checkParam(channelId) || checkParam(channelUid)) {
        console.error(way + "参数错误" + " appId=" + appId + " channelId=" + channelId + " channelUid=" + channelUid);
        return null;
    }
    if (checkParam(roleId) || checkParam(roleName) || checkParam(roleLevel)) {
        console.error(way + "参数错误" + " roleId=" + roleId + " roleName=" + roleName + " roleLevel=" + roleLevel);
        return null;
    }
    if (checkParam(zoneId) || checkParam(zoneName)) {
        console.error(way + "参数错误" + " zoneId=" + zoneId + " zoneName=" + zoneName);
        return null;
    }
    if (checkParam(balance) || checkParam(vip) || checkParam(partyName)) {
        console.error(way + "参数错误" + " balance=" + balance + " vip=" + vip + " partyName=" + partyName);
        return null;
    }
    if (key === keys[0] && checkParam(roleCTime)) {
        console.error(way + "参数错误" + "创建角色时，创建时间不能为空" + " roleCTime = " + roleCTime);
        return null;
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
        if (checkParam(value[key])) {
            console.error(way + "参数错误 " + key + "=" + value[key]);
            return null;
        }
    }

    let response;
    let ss = JSON.stringify(value);
    let data = {
        "key": key,
        "value": ss
    };
    console.info(way + "send " + ss);
    $.ajax({
        url: t_domain + "/setdata",
        type: "post",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(data),
        dataType: "json",
        async: false,
        success: function (result) {
            console.info(result);
            if (result.message === messgae[0]) {
                console.info(way + "recv result ", result.reason);
                console.info(way + "recv message ", result.message);
                console.info(way + "recv data " + result.data);

                let rdata = result.data;
                response = {
                    "channelId": rdata.channelId,
                    "appId": rdata.appId,
                    "zoneId": rdata.zoneId,
                    "roleId": rdata.roleId,
                    "balance": rdata.balance,
                    "reason": result.reason,
                    "message": result.message
                };
            } else {
                console.error(way + "recv result ", result.reason);
                console.error(way + "recv message ", result.message);
                response = {
                    "reason": result.reason,
                    "message": result.message
                };
            }
        },
        error: function () {
            console.error(way + "上传角色数据失败");
            response = {
                "message": messgae[1]
            };
        }
    });
    console.info(response);
    return response;
}

//测试用例
function test_EnterGame() {
    let channelUid = $("#channelUid").val();
    let appId = $("#save_gameId").val();
    let serverId = $("#save_serverId").val();
    let channelId = $("#save_spId").val();
    let roleId = $("#roleId").val();
    let result = enterGame(appId, serverId, channelId, channelUid, roleId);
    console.info("test_EnterGame");
    console.info(result);
}

/**
 * 进入游戏-上报数据接口
 * @param   {number}     appId           游戏id
 * @param   {number}     serverId        当前登录的游戏区服ID，必须为数字，且不能为0，若无，传入1
 * @param   {number}     channelId       玩家渠道id
 * @param   {number}     channelUid      玩家渠道账号id
 * @param   {number}     roleId          当前登录的玩家角色ID，必须为数字
 * @return  {json}
 * */
function enterGame(appId, serverId, channelId, channelUid, roleId) {
    let way = "进入游戏-上报数据接口 ";
    if (checkParam(appId) || checkParam(serverId) || checkParam(channelId)) {
        console.error(way + "参数错误" + "appId=" + appId + " serverId=" + serverId + " channelId=" + channelId);
        return null;
    }
    if (checkParam(channelUid) || checkParam(roleId)) {
        console.error(way + "参数错误" + "channelUid=" + channelUid + " channelUid=" + roleId);
        return null;
    }

    let param = "appId=" + appId +
        "&serverId=" + serverId +
        "&channelId=" + channelId +
        "&channelUid=" + channelUid +
        "&roleId=" + roleId;

    console.info(way + "send " + param);

    let response;
    $.ajax({
        url: t_domain + "/enter?" + param,
        type: "get",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        async: false,
        success: function (result) {
            console.info(result);
            if (result.message === messgae[0]) {
                console.info(way + "recv reason " + result.reason);
                console.info(way + "recv message " + result.message);
                console.info(way + "recv data " + result.data);
                let rdata = JSON.parse(result.data);
                response = {
                    "channelId": rdata.channelId,
                    "appId": rdata.appId,
                    "serverId": rdata.serverId,
                    "roleId": rdata.roleId,
                    "reason": result.reason,
                    "message": result.message
                }
            } else {
                console.error(way + "recv " + result.reason);
                console.error(way + "recv " + result.message);
                response = {
                    "reason": result.reason,
                    "message": result.message
                }
            }

        },
        error: function () {
            console.error(way + "上报失败");
        }
    });
    return response;
}

//测试用例
function test_ExitGame() {
    let channelUid = $("#channelUid").val();
    let appId = $("#save_gameId").val();
    let serverId = $("#save_serverId").val();
    let channelId = $("#save_spId").val();
    let roleId = $("#roleId").val();
    let result = exitGame(appId, serverId, channelId, channelUid, roleId);
    console.info("test_ExitGame");
    console.info(result);
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
    let way = "退出游戏-上报数据接口 ";
    if (checkParam(appId) || checkParam(serverId) || checkParam(channelId)) {
        console.error(way + "参数错误" + " appId=" + appId + " serverId=" + serverId + " channelId=" + channelId);
        return null;
    }
    if (checkParam(channelUid) || checkParam(roleId)) {
        console.error(way + "参数错误" + " channelUid=" + channelUid + " channelUid=" + roleId);
        return null;
    }
    let param = "appId=" + appId +
        "&serverId=" + serverId +
        "&channelId=" + channelId +
        "&channelUid=" + channelUid +
        "&roleId=" + roleId;

    console.info(way + "send " + param);

    let response;
    $.ajax({
        url: t_domain + "/exit?" + param,
        type: "get",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        async: false,
        success: function (result) {
            if (result.message === messgae[0]) {
                console.info(way + "recv " + result.reason);
                console.info(way + "recv " + result.message);
                console.info(way + "recv " + result.data);
                let rdata = JSON.parse(result.data);
                response = {
                    "channelId": rdata.channelId,
                    "appId": rdata.appId,
                    "serverId": rdata.serverId,
                    "roleId": rdata.roleId,
                    "reason": result.reason,
                    "message": result.message
                }
            } else {
                console.error(way + "recv " + result.reason);
                console.error(way + "recv " + result.message);
                response = {
                    "reason": result.reason,
                    "message": result.message
                }
            }
        },
        error: function () {
            console.error(way + "退出游戏上报失败");
        }
    });
    return response;
}


//测试用例
function test_UploadPayInfo() {
    let channelUid = $("#channelUid").val();
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
    let result = uploadPayInfo(accountId, channelId, channelUid, appId, channelOrderID,
        productID, productName, productDesc, money,
        roleID, roleName, roleLevel,
        serverID, serverName,
        realMoney, completeTime, sdkOrderTime,
        status, notifyUrl,
        signType, sign);
    console.info("test_UploadPayInfo");
    console.info(result);
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
        // console.info("充值 sign " + signString);
        let urlSign = encodeURIComponent(signString);
        // console.info("充值 sign url " + urlSign);
        console.info(hex_md5(urlSign));
        return hex_md5(urlSign);
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
    let way = "充值上报 ";
    let isStatus = false;
    for (let orderStatus of status) {
        if (orderStatus === status) {
            isStatus = true;
            break;
        }
    }
    if (!isStatus) {
        console.error("订单状态错误 " + status);
        return null;
    }

    if (status >= OrderStatus[3]) {
        console.info("订单当前状态 " + OrderStatusDesc[status]);
    }

    if (checkParam(accountID) || checkParam(channelId) || checkParam(channelUid) || checkParam(appId)) {
        console.error(way + "账号或游戏参数为空" + " accountID=" + accountID + " channelId=" + channelId + " channelUid=" + channelUid + " appId=" + appId);
        return null;
    }
    if (checkParam(productID) || checkParam(productName) || checkParam(productDesc)) {
        console.error(way + "商品参数为空 " + " productID=" + productID + " productName=" + productName + " productDesc=" + productDesc);
        return null;
    }
    if (checkParam(roleID) || checkParam(roleName) || checkParam(roleLevel)) {
        console.error(way + "角色参数为空 " + " roleID=" + roleID + " roleName=" + roleName + " roleLevel=" + roleLevel);
        return null;
    }
    if (checkParam(serverID) || checkParam(serverName)) {
        console.error(way + "区服参数为空 " + " serverID=" + serverID + " serverName=" + serverName);
        return null;
    }
    if (checkParam(channelOrderID) || checkParam(realMoney)) {
        console.error(way + "订单参数为空 " + " channelOrderID=" + channelOrderID + " realMoney=" + realMoney);
        return null;
    }
    if (checkParam(completeTime) || checkParam(sdkOrderTime)) {

    }
    if (checkParam(sign) || checkParam(signType)) {
        console.error(way + "签名数为空 ");
        return null;
    }
    if (signType !== "MD5" && signType !== "RSA") {
        console.error(way + "签名类型错误 ");
        return null;
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
    let response;
    $.ajax({
        url: t_domain + "/payInfo",
        type: "post",
        dataType: 'json',
        contentType: "text/plain; charset=utf-8",
        data: JSON.stringify(data),
        async: false,
        success: function (result) {
            console.info(way + "recv " + result.resultCode);
            if (result.resultCode === 200) {
                console.info(way + "recv result " + result.message);
                if (result.state !== 1) {
                    console.error(way + "recv 订单当前状态 " + OrderStateMsg[result.state]);
                    response = {
                        "message": result.message,
                        "state": result.state
                    }
                } else {
                    console.info(way + "recv 订单当前状态 " + OrderStateMsg[result.state]);
                    response = {
                        "message": result.message,
                        "orderId": result.orderId,
                        "state": result.state
                    }
                }
            }
        },
        error: function () {
            console.error("系统提示", "充值数据上报失败");
        }
    });
    return response;
}


//测试用例
function test_PayInfo() {
    let channelUid = $("#channelUid").val();
    let appId = $("#save_gameId").val();
    let serverId = $("#save_serverId").val();
    let channelId = $("#save_spId").val();
    let roleId = $("#roleId").val();
    let order_status = $("#payRecord_state").val();

    let accountId = $("#accountId").val();
    let channelOrderID = "";
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

    let sign = md5(accountId, channelId, channelUid, appId, "",
        productID, productName, productDesc, money,
        roleID, roleName, roleLevel,
        serverID, serverName,
        realMoney, completeTime, sdkOrderTime,
        status, notifyUrl, signType);
    let result = PayInfo(accountId, channelId, channelUid, appId,
        productID, productName, productDesc, money,
        roleID, roleName, roleLevel,
        serverID, serverName,
        realMoney, completeTime, sdkOrderTime,
        status, notifyUrl,
        signType, sign);
    console.info("test_PayInfo");
    console.info(result);

    $("#oderid").val(result.orderId);

}

/**
 * 充值-生成官方订单数据接口
 * @param   {number}      accountID           指悦账号id
 * @param   {number}      channelId           渠道id
 * @param   {number}      channelUid          渠道账号id
 * @param   {number}      appId               游戏id
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
function PayInfo(accountID, channelId, channelUid, appId,
                 productID, productName, productDesc, money,
                 roleID, roleName, roleLevel,
                 serverID, serverName,
                 realMoney, completeTime, sdkOrderTime,
                 status, notifyUrl,
                 signType, sign) {
    let way = "充值 ";
    let isStatus = false;
    for (let orderStatus of status) {
        if (orderStatus === status) {
            isStatus = true;
            break;
        }
    }
    if (!isStatus) {
        console.error("订单状态错误 " + status);
        return null;
    }

    if (status >= OrderStatus[3]) {
        console.info("订单当前状态 " + OrderStatusDesc[status]);
    }

    if (checkParam(accountID) || checkParam(channelId) || checkParam(channelUid) || checkParam(appId)) {
        console.error(way + "账号或游戏参数为空" + " accountID=" + accountID + " channelId=" + channelId + " channelUid=" + channelUid + " appId=" + appId);
        return null;
    }
    if (checkParam(productID) || checkParam(productName) || checkParam(productDesc)) {
        console.error(way + "商品参数为空 " + " productID=" + productID + " productName=" + productName + " productDesc=" + productDesc);
        return null;
    }
    if (checkParam(roleID) || checkParam(roleName) || checkParam(roleLevel)) {
        console.error(way + "角色参数为空 " + " roleID=" + roleID + " roleName=" + roleName + " roleLevel=" + roleLevel);
        return null;
    }
    if (checkParam(serverID) || checkParam(serverName)) {
        console.error(way + "区服参数为空 " + " serverID=" + serverID + " serverName=" + serverName);
        return null;
    }
    if (checkParam(realMoney)) {
        console.error(way + "订单参数为空 " + " realMoney=" + realMoney);
        return null;
    }
    if (checkParam(completeTime) || checkParam(sdkOrderTime)) {

    }
    if (checkParam(sign) || checkParam(signType)) {
        console.error(way + "签名数为空 ");
        return null;
    }
    if (signType !== "MD5" && signType !== "RSA") {
        console.error(way + "签名类型错误 ");
        return null;
    }

    let data = {
        "accountID": accountID,
        "channelID": channelId,
        "channelUid": channelUid,
        "appID": appId,

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
    let response;
    $.ajax({
        url: t_domainPay + "/wap",
        type: "post",
        dataType: 'json',
        contentType: "text/plain; charset=utf-8",
        data: JSON.stringify(data),
        async: false,
        success: function (result) {
            console.info(way + "recv " + result.resultCode);
            console.info(way + "recv result " + result.message);
            if (result.state !== 1) {
                console.error(way + "recv 订单当前状态 " + OrderStateMsg[result.state]);
                response = {
                    "message": result.message,
                    "state": result.state
                }
            } else {
                console.info(way + "recv 订单当前状态 " + OrderStateMsg[result.state]);
                response = {
                    "message": result.message,
                    "orderId": result.orderId,
                    "state": result.state
                }
            }

        },
        error: function () {
            console.error("系统提示", "充值数据上报失败");
        }
    });
    return response;
}