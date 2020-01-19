/**
 *  zy sdk 文件
 * 测试环境
 * @author song minghua
 * @date 2019/12/31
 * @version 0.1.1
 */
const OrderStatus = [1, 2, 3, 4, 5];
const OrderStatusDesc = [
    "选择充值方式界面  未选择充值方式（取消支付）",
    "支付宝微信界面   未支付（取消支付）",
    "支付成功 未发货",
    "支付成功 已发货(交易完成)",
    "支付成功 补单(交易完成)"
];

//服务器地址
// const zy_domain = "http://localhost:8080";
const zy_domain = "http://zyh5games.com:8080/";

let ZySDK = {
    GameId: null,
    GameKey: null,
    channelId: -1,
    channelUid: "",
    channelUsername: "",
    LoginKey: null,
    PayKey: null,
    SendKey: null,
    channelToken: "",
    debug: true,
    /**
     * 1.sdk初始化函数
     * @param sZyGameId         平台游戏id（后台自动分配 ）
     * @param sZyGameKey        平台秘钥（后台自动分配 ）zy_game
     * @param sZyChannelId      平台渠道id（后台自动分配 ）
     * @param callback          自定义回调函数
     * */
    init: function (sZyGameId, sZyGameKey, sZyChannelId, callback) {
        this.GameId = sZyGameId;
        this.GameKey = sZyGameKey;
        this.channelId = sZyChannelId;

        let params = {};
        params.GameId = this.GameId;
        params.GameKey = this.GameKey;
        params.channelId = this.channelId;
        params.debug = this.debug;

        doInit(params, function (state) {
            callback(state);
        });
    },
    /**
     * 2.初始化渠道用户信息
     * @param {string}  channelUid          用户渠道id
     * @param {string}  channelUsername     用户渠道登录名
     * @param callback
     * */
    initUser: function (channelUid, channelUsername, callback) {
        let rspObject = {};
        if (!ZySDK.checkZySdkParam(false)) {
            rspObject.state = false;
            rspObject.message = "initUser() 初始化渠道用户信息失败，请先调用Zysdk.init()";
            callback(rspObject);
            return;
        }
        if (checkParam(channelUid)) {
            rspObject.state = false;
            rspObject.message = "initUser() 初始化渠道用户信息失败，参数为空 channelUid channelUsername";
            callback(rspObject);
            return;
        }
        //获取zy平台uid
        this.channelUid = channelUid;
        this.channelUsername = channelUsername;
        rspObject.state = true;
        rspObject.message = "initUser()成功" + " channelUid = " + channelUid + " channelUsername=" + channelUsername;
        callback(rspObject);
    },
    /**
     * 3.指悦账号登录
     * 或者直接渠道账号登录 ：渠道id、渠道uid足以分辨
     * @param   {Object}         loginInfo              登录信息
     * @param   {function}       callback               回调函数
     * */
    zyLogin: function (loginInfo, callback) {
        sdk_ZyLogin(loginInfo, callback);
    },
    /**
     * 4.上传角色信息
     * @param   {string}        key             上报类型
     * @param   {Object}        roleInfo        角色信息
     * @param   {function}      callback        回调函数
     *  */
    uploadGameRoleInfo: function (key, roleInfo, callback) {
        sdk_ZyUploadGameRoleInfo(key, roleInfo, callback);
    },
    /**
     * 5.支付上报
     * @param {Object}      orderInfo       订单信息
     * @param {function}    callback
     * */
    pay: function (orderInfo, callback) {
        sdk_zyUploadPayInfo(orderInfo, callback);
    },
    /**
     * 6.指悦账号注册*/
    zyRegister: function (regInfo, callback) {
        sdk_ZyRegister(regInfo, callback);
    },
    /**
     * 检查sdk初始化的参数
     * @param {boolean} type
     * @return {boolean} result
     * */
    checkZySdkParam: function (type) {
        if (this.GameId == null || this.GameKey == null || this.channelId == null) {
            return false;
        }
        if (type === true) {
            if (this.channelUid == null || this.channelUsername == null) {
                return false;
            }
        }
        return true;
    }
};

/**
 * 获取平台的该游戏渠道参数
 * @param {Object}      params
 * @param {number}      params.GameId   平台游戏id
 * @param {string}      params.GameKey  平台游戏秘钥
 * @param {number}      params.SpId     平台渠道id
 * @param {boolean}     params.debug    是否·debug模式
 * @param {function}    callback        回调函数
 * */
function doInit(params, callback) {
    let requestUri = getNowHost();
    requestUri = requestUri.replace(/&amp;/g, '&');

    $.ajax({
        type: "GET",
        url: zy_domain + "/webGame/initApi",
        data: params,
        dataType: "json",
        success: function (data) {
            if (data.hasOwnProperty('state') && data.state === true) {
                //load channel lib
                loadSpLib(data, function () {
                    ZySDK.LoginKey = data.channelParams.login_key;
                    ZySDK.PayKey = data.channelParams.pay_key;
                    ZySDK.SendKey = data.channelParams.send_key;
                    callback(data.state);
                    setLog(JSON.stringify(data), 0);
                });
            } else {
                setLog(JSON.stringify(data), 1);
                callback(data.state);
            }
        }
    })
}

/**
 * 注册-接口
 * @param   {object}        regInfo                  注册信息
 * @param   {boolean}       regInfo.auto             是否无需账号密码注册
 * @param   {number}        regInfo.appId            游戏id
 * @param   {number}        regInfo.channelId        渠道id
 * @param   {string}        regInfo.channelUid       渠道账号id
 * @param   {string}        regInfo.channelUname     渠道账号名称
 * @param   {string}        regInfo.channelUnick     渠道账号昵称
 * @param   {string}        regInfo.username         指悦账号
 * @param   {string}        regInfo.password         指悦账号密码
 * @param   {string}        regInfo.phone            手机号
 * @param   {string}        regInfo.deviceCode
 * @param   {string}        regInfo.imei
 * @param   {string}        regInfo.addparm          额外参数
 * @param   {function}      callback

 * */
function sdk_ZyRegister(regInfo, callback) {
    let rspObject = {};
    if (!ZySDK.checkZySdkParam(false)) {
        rspObject.state = false;
        rspObject.message = "请先调用 ZySDK.init()";
        callback(rspObject);
        return;
    }
    regInfo.appId = Number(ZySDK.GameId);
    regInfo.channelId = Number(ZySDK.channelId);
    if (!regInfo.hasOwnProperty('auto')) {
        rspObject.message = "auto 参数为空";
        rspObject.state = false;
        callback(rspObject);
        return;
    }
    if (!regInfo.hasOwnProperty('appId')) {
        rspObject.message = "appId 参数为空";
        rspObject.state = false;
        callback(rspObject);
        return;
    }
    if (regInfo.auto === true) {
        let mustKey = ['channelId', 'channelUid'];
        if (regInfo.hasOwnProperty('channelId') && regInfo.channelId === 0) {

        } else {
            for (let keyIndex of mustKey) {
                if (!regInfo.hasOwnProperty(keyIndex)) {
                    rspObject.message = "渠道id 或 渠道uid 为空";
                    rspObject.state = false;
                    callback(rspObject);
                    return;
                }
            }
        }
    } else {
        let mustKey = ['channelId', 'username', 'password'];
        for (let keyIndex of mustKey) {
            if (!regInfo.hasOwnProperty(keyIndex)) {
                rspObject.message = "用户名 或 密码 为空";
                rspObject.state = false;
                callback(rspObject);
                return;
            }
        }
    }

    $.ajax({
        url: zy_domain + "/webGame/register",
        type: "post",
        contentType: "text/plain; charset=utf-8",
        data: JSON.stringify(regInfo),
        dataType: "json",
        async: false,
        success: function (result) {
            console.info(result);
            if (result.hasOwnProperty('state')) {
                if (result.state === true) {
                    rspObject.state = true;
                    rspObject.message = result.message;

                    rspObject.uid = result.accountId;
                    rspObject.account = result.account;
                    rspObject.password = result.password;
                    rspObject.channelUid = result.channelUid;
                } else {
                    rspObject.state = false;
                    rspObject.message = result.message;
                }
            } else {
                rspObject.state = false;
                rspObject.message = "通信失败";
            }
            callback(rspObject);
        },
        error: function () {
            rspObject.message = "通信失败";
            rspObject.state = false;
            callback(rspObject);
        }
    });
}

/**
 * 请求登录-接口
 * @param   {Object}        loginInfo
 * @param   {boolean}       loginInfo.isAuto           是否渠道自动注册
 * @param   {number}        loginInfo.GameId           游戏id
 * @param   {number}        loginInfo.channelId        渠道id
 * @param   {string}        loginInfo.channelUid       渠道账号id
 * @param   {string}        loginInfo.username         指悦账号
 * @param   {string}        loginInfo.password         指悦账号密码
 * @param   {number}        loginInfo.timestamp        时间戳
 * @param   {string}        loginInfo.sign             签名
 * @param   {function}      callback                   回调函数
 * */
function sdk_ZyLogin(loginInfo, callback) {
    let rspObject = {};
    if (!ZySDK.checkZySdkParam(true)) {
        rspObject.state = false;
        rspObject.message = "请先调用 ZySDK.init() 和 ZySDK.initUser()";
        callback(rspObject);
        return;
    }
    loginInfo.GameId = ZySDK.GameId;
    loginInfo.channelId = ZySDK.channelId;
    loginInfo.channelUid = ZySDK.channelUid;
    loginInfo.timestamp = new Date().valueOf();


    let mustKey = ['isAuto', 'GameId', 'channelId'];
    for (let keyIndex of mustKey) {
        if (!loginInfo.hasOwnProperty(keyIndex)) {
            rspObject.state = false;
            rspObject.message = "参数为空：isAuto GameId channelId";
            callback(rspObject);
            return;
        }
    }
    if (loginInfo.isAuto === true) {
        if (checkParam(loginInfo.channelId) || checkParam(loginInfo.channelUid)) {
            rspObject.state = false;
            rspObject.message = "参数为空：channelId  channelUid";
            callback(rspObject);
            return;
        }
    } else if (loginInfo.isAuto === false) {
        if (checkParam(loginInfo.username) || checkParam(loginInfo.password)) {
            rspObject.state = false;
            rspObject.message = "参数为空：username  password";
            callback(rspObject);
            return;
        }
    } else {
        rspObject.state = false;
        rspObject.message = "参数错误：isAuto";
        callback(rspObject);
        return;
    }
    let param = "isAuto" + "=" + loginInfo.isAuto + "&" +
        "GameId" + "=" + loginInfo.GameId + "&" +
        "channelId" + "=" + loginInfo.channelId + "&" +
        "channelUid" + "=" + loginInfo.channelUid + "&" +
        "username" + "=" + loginInfo.username + "&" +
        "password" + "=" + loginInfo.password + "&" +
        "timestamp" + "=" + loginInfo.timestamp;

    //md5 加密
    loginInfo.sign = md5(param, ZySDK.GameKey);

    let params = param + "&sign=" + loginInfo.sign;

    console.info(params);

    $.ajax({
        url: zy_domain + "/webGame/login?" + params,
        type: "get",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (result) {
            if (result.state === true) {
                rspObject.message = result.message;
                rspObject.state = result.state;

                rspObject.GameId = result.GameId;
                rspObject.channelId = result.channelId;
                rspObject.channelUid = result.channelUid;
                rspObject.channelToken = result.channelToken;
                rspObject.zyUid = result.zyUid;
                rspObject.username = result.username;
                rspObject.password = result.password;
                rspObject.loginUrl = result.loginUrl;
                rspObject.paybackUrl = result.paybackUrl;

                ZySDK.channelToken = result.channelToken;

                callback(rspObject);
            } else {
                rspObject.message = result.message;
                rspObject.state = result.state;
                callback(rspObject);
            }
        },
        error: function () {
            rspObject.message = "通信失败";
            rspObject.state = false;
            callback(rspObject);
        }
    });
}

/**
 * 角色-上报数据接口
 * @param   {string}        key
 * @param   {string}        key.createRole              创建新角色时调用
 * @param   {string}        key.enterGame               选择游戏内进入时调用
 * @param   {string}        key.levelUp                 玩家升级角色时调用
 * @param   {string}        key.exitGame                玩家退出游戏时调用
 * @param   {Object}        roleInfo                    角色信息
 * @param   {number}        roleInfo.appId              游戏id
 * @param   {number}        roleInfo.channelId          玩家渠道id
 * @param   {number}        roleInfo.channelUid         玩家渠道账号id
 * @param   {number}        roleInfo.roleId             当前登录的玩家角色ID，必须为数字
 * @param   {string}        roleInfo.roleName           当前登录的玩家角色名，不能为空，不能为null
 * @param   {number}        roleInfo.roleLevel          当前登录的玩家角色等级，必须为数字，且不能为0，若无，传入1
 * @param   {number}        roleInfo.zoneId             当前登录的游戏区服ID，必须为数字，且不能为0，若无，传入1
 * @param   {string}        roleInfo.zoneName           当前登录的游戏区服名称，不能为空，不能为null
 * @param   {number}        roleInfo.balance            用户游戏币余额，必须为数字，若无，传入0
 * @param   {number}        roleInfo.vip                当前用户VIP等级，必须为数字，若无，传入1
 * @param   {string}        roleInfo.partyName          当前角色所属帮派，不能为空，不能为null，若无，传入“无帮派”
 * @param   {number}        roleInfo.roleCTime          单位为毫秒，创建角色的时间
 * @param   {number}        roleInfo.roleLevelMTime     单位为毫秒，角色等级变化时间
 * @param   {function}      callback                    回调函数
 * */
function sdk_ZyUploadGameRoleInfo(key, roleInfo, callback) {
    let rspObject = {};
    if (!ZySDK.checkZySdkParam(true)) {
        rspObject.state = false;
        rspObject.message = "请先调用 ZySDK.init() 和 ZySDK.initUser()";
        callback(rspObject);
        return;
    }

    roleInfo.appId = ZySDK.GameId;
    roleInfo.channelId = ZySDK.channelId;
    roleInfo.channelUid = ZySDK.channelUid;

    let mustKeys = ['createRole', 'levelUp', 'enterGame', 'exitGame'];

    let hasKey = false;
    for (let keyIndex of mustKeys) {
        if (key === keyIndex) {
            hasKey = true;
            break;
        }
    }
    if (!hasKey) {
        rspObject.state = false;
        rspObject.message = "上传角色信息 key值错误 createRole levelUp enterServer enterGame";
        callback(rspObject);
        return;
    }
    let mustKeysValue = [
        'appId', 'channelId', 'channelUid',
        'roleId', 'roleName', 'roleLevel',
        'zoneId', 'zoneName', 'balance', 'vip',
        'partyName'];

    for (let keyIndex of mustKeysValue) {
        if (!roleInfo.hasOwnProperty(keyIndex)) {
            rspObject.state = false;
            rspObject.message = "上传角色信息 参数缺失 " + keyIndex;
            callback(rspObject);
            return;
        }
    }
    //参数值正确校验
    if (checkParam(roleInfo.appId) || checkParam(roleInfo.channelId) || checkParam(roleInfo.channelUid)) {
        rspObject.state = false;
        rspObject.message = "上传角色信息 参数为空 ：appId channelId channelUid";
        callback(rspObject);
        return;
    }
    if (checkParam(roleInfo.roleId) || checkParam(roleInfo.roleName) || checkParam(roleInfo.roleLevel)) {
        rspObject.state = false;
        rspObject.message = "上传角色信息 参数为空 ：roleId roleName roleLevel";
        callback(rspObject);
        return;
    }
    if (checkParam(roleInfo.zoneId) || checkParam(roleInfo.zoneName) || checkParam(roleInfo.balance)) {
        rspObject.state = false;
        rspObject.message = "上传角色信息 参数为空 ：zoneId zoneName balance";
        callback(rspObject);
        return;
    }
    if (checkParam(roleInfo.vip) || checkParam(roleInfo.partyName)) {
        rspObject.state = false;
        rspObject.message = "上传角色信息 参数为空 ：zoneId zoneName balance";
        callback(rspObject);
        return;
    }
    if (key === 'createRole' && !roleInfo.hasOwnProperty('roleCTime')) {
        rspObject.state = false;
        rspObject.message = "创建角色 参数缺失 " + 'roleCTime';
        callback(rspObject);
        return;
    }
    if (key === 'levelUp' && !roleInfo.hasOwnProperty('roleLevelMTime')) {
        rspObject.state = false;
        rspObject.message = "角色升级 参数缺失 " + 'roleLevelMTime';
        callback(rspObject);
        return;
    }

    let value = JSON.stringify(roleInfo);
    let data = {
        "key": key,
        "value": roleInfo
    };
    $.ajax({
        url: zy_domain + "/webGame/setData",
        type: "post",
        contentType: "text/plain; charset=utf-8",
        data: JSON.stringify(data),
        dataType: "json",
        success: function (result) {
            if (result.state === true) {
                rspObject.state = true;
                rspObject.message = result.message;

                rspObject.GameId = result.GameId;
                rspObject.channelId = result.channelId;
                rspObject.zoneId = result.zoneId;
                rspObject.roleId = result.roleId;
                rspObject.balance = result.balance;
            } else {
                rspObject.state = false;
                rspObject.message = result.message;
            }
            callback(rspObject);
        },
        error: function () {
            rspObject.state = false;
            rspObject.message = "通信失败";

            callback(rspObject);
        }
    });
}

/**
 * 充值-上报数据接口
 * @param   {Object}      orderInfo           充值信息
 * @param   {number}      orderInfo.accountID           指悦账号id
 * @param   {number}      orderInfo.channelId           渠道id
 * @param   {number}      orderInfo.channelUid          渠道账号id
 * @param   {number}      orderInfo.appId               游戏id
 * @param   {string}      orderInfo.channelOrderID      渠道订单号
 * @param   {string}      orderInfo.productID           当前商品ID
 * @param   {string}      orderInfo.productName         商品名称
 * @param   {string}      orderInfo.productDesc         商品描述
 * @param   {number}      orderInfo.money               商品价格,单位:分
 * @param   {number}      orderInfo.roleID              玩家在游戏服中的角色ID
 * @param   {string}      orderInfo.roleName            玩家在游戏服中的角色名称
 * @param   {number}      orderInfo.roleLevel           玩家等级
 * @param   {number}      orderInfo.serverID            玩家所在的服务器ID
 * @param   {string}      orderInfo.serverName          玩家所在的服务器名称
 * @param   {number}      orderInfo.realMoney           订单完成,实际支付金额,单位:分,未完成:-1
 * @param   {number}      orderInfo.completeTime        订单完成时间戳(毫秒，13位),未完成为:-1
 * @param   {number}      orderInfo.sdkOrderTime        订单创建时间戳(毫秒，13位)
 * @param   {number}      orderInfo.status              订单状态 请看OrderStatus、OrderStatusDesc
 * @param   {string}      orderInfo.notifyUrl           支付回调通知的游戏服地址
 * @param   {string}      orderInfo.signType            签名算法,RSA|MD5,默认MD5
 * @param   {string}      orderInfo.sign                签名
 * @param   {function}    callback                      回调函数
 * */
function sdk_zyUploadPayInfo(orderInfo, callback) {
    let rspObject = checkOrderObject(orderInfo);
    if (rspObject.state === false) {
        callback(rspObject);
        return;
    }

    orderInfo.sign = orderSign(orderInfo);

    $.ajax({
        url: zy_domain + "/webGame/payInfo",
        type: "post",
        contentType: "text/plain; charset=utf-8",
        data: JSON.stringify(orderInfo),
        dataType: "json",
        success: function (result) {
            console.info(result);
            if (result.state === true) {
                rspObject.state = true;
                rspObject.message = result.message;
                rspObject.orderId = result.orderId;
            } else {
                rspObject.state = false;
                rspObject.message = result.message;
            }
            callback(rspObject);
        },
        error: function () {
            rspObject.state = false;
            rspObject.message = "通信失败";
            callback(rspObject);
        }
    })
}

//下面 工具函数 放在服务器

/**
 * 加载需要的js文件
 * @param {string}      src         js文件路径+文件名称
 * @param {function}    callback
 * */
function loadAsyncScript(src, callback) {
    if (checkParam(src)) {
        callback();
        return;
    }
    console.info(src);
    if (src.search("jquery") !== -1) {
        if (typeof (jQuery) == "undefined") {
            console.info("jQuery is not imported");
        } else {
            console.info("jQuery is imported");
            callback();
            return;
        }
    }
    let scriptElement = document.createElement('script');
    scriptElement.setAttribute('type', 'text/javascript');
    scriptElement.setAttribute('src', src);
    let head = document.getElementsByTagName('head')[0];
    head.appendChild(scriptElement);

    if (scriptElement.readyState) {
        //ie
        scriptElement.onreadystatechange = function () {
            let state = this.readyState;
            if (state === 'loaded' || state === 'complete') {
                callback();
            }
        }
    } else {
        //Others: Firefox, Safari, Chrome, and Opera
        scriptElement.onload = function () {
            callback();
        }
    }
}

function setLog(str, level) {
    if (ZySDK.debug === true) {
        if (level === 1) {
            console.log('%cZYSDK致命错误:' + str, 'color:red');
        } else if (level === 2) {
            console.log('%cZYSDK警告错误:' + str, 'color:gray');
        } else if (level === 0) {
            console.log('%cZYSDK运行日志:' + str, 'color:gray');
        } else {
            console.log('%cZYSDK运行日志:' + str, 'color:gray');
        }
    }
}

/**
 * md5加密
 * @param {string} info 加密内容
 * @param secretKey md5秘钥
 * */
function md5(info, secretKey) {
    let strInfo = info;
    // for (let key in info) {
    //     strInfo += key;
    //     strInfo += "=";
    //     strInfo += info[key];
    //     strInfo += "&";
    // }
    strInfo += "&" + secretKey;

    let sign_uri = encodeURIComponent(strInfo);
    let hex_sign_uri = hex_md5(sign_uri);

    if (ZySDK.debug) {
        console.info(strInfo);
        console.info(hex_sign_uri);
    }
    return hex_sign_uri;
}

/**
 * 检查订单参数
 * @param       {Object}        orderInfo           充值信息
 * @return      {Object}        rspObject
 * */
function checkOrderObject(orderInfo) {
    let rspObject = {};

    if (!ZySDK.checkZySdkParam(true)) {
        rspObject.state = false;
        rspObject.message = "请先调用 ZySDK.init() 和 ZySDK.initUser()";
        return rspObject;
    }
    let isStatus = false;
    if (orderInfo.status > 5 || orderInfo.status < 1) {
        rspObject.state = false;
        rspObject.message = "订单状态错误:" + orderInfo.status;
        return rspObject;
    }
    orderInfo.appId = ZySDK.GameId;
    orderInfo.channelId = ZySDK.channelId;
    orderInfo.channelUid = ZySDK.channelUid;

    let mustKeys = [
        'accountID', 'channelId', 'channelUid', 'appId', 'channelOrderID',
        'productID', 'productName', 'productDesc', 'money', 'roleID', 'roleName',
        'roleLevel', 'serverID', 'serverName', 'sdkOrderTime', 'status', 'notifyUrl',
        'signType'];
    for (let keyIndex of mustKeys) {
        if (!orderInfo.hasOwnProperty(keyIndex)) {
            rspObject.state = false;
            rspObject.message = "参数为空：" + keyIndex;
            return rspObject;
        }
    }
    if (checkParam(orderInfo.accountID) || checkParam(orderInfo.channelId) || checkParam(orderInfo.channelUid) || checkParam(orderInfo.appId)) {
        rspObject.state = false;
        rspObject.message = "参数为空：accountID channelId channelUid appId";
        return rspObject;
    }
    if (checkParam(orderInfo.productID) || checkParam(orderInfo.productName) || checkParam(orderInfo.productDesc)) {
        rspObject.state = false;
        rspObject.message = "参数为空：productID productName productDesc ";
        return rspObject;
    }
    if (checkParam(orderInfo.roleID) || checkParam(orderInfo.roleName) || checkParam(orderInfo.roleLevel)) {
        rspObject.state = false;
        rspObject.message = "参数为空：roleID roleName roleLevel ";
        return rspObject;
    }
    if (checkParam(orderInfo.serverID) || checkParam(orderInfo.serverName)) {
        rspObject.state = false;
        rspObject.message = "参数为空：serverID serverName ";
        return rspObject;
    }
    if (checkParam(orderInfo.channelOrderID || checkParam(orderInfo.money))) {
        rspObject.state = false;
        rspObject.message = "参数为空：channelOrderID money ";
        return rspObject;
    }
    if (orderInfo.status === 1 && checkParam(orderInfo.sdkOrderTime)) {
        rspObject.state = false;
        rspObject.message = "参数为空：sdkOrderTime ";
        return rspObject;
    }
    if (checkParam(orderInfo.signType)) {
        rspObject.state = false;
        rspObject.message = "参数为空：signType ";
        return rspObject;
    }
    if (orderInfo.signType !== "MD5" && orderInfo.signType !== "RSA") {
        rspObject.state = false;
        rspObject.message = "签名类型错误：signType " + orderInfo.signType;
        return rspObject;
    }
    rspObject.state = true;
    return rspObject;
}

/**
 * 订单签名
 * @param       {Object}        orderInfo           充值信息
 * @return      {string}        sign                签名数据
 * */
function orderSign(orderInfo) {
    let signString =
        "accountID=" + orderInfo.accountID + "&" +
        "channelID=" + orderInfo.channelId + "&" +
        "channelUid=" + orderInfo.channelUid + "&" +
        "appID=" + orderInfo.appId + "&" +
        "channelOrderID=" + orderInfo.channelOrderID + "&" +

        "productID=" + orderInfo.productID + "&" +
        "productName=" + orderInfo.productName + "&" +
        "productDesc=" + orderInfo.productDesc + "&" +
        "money=" + orderInfo.money + "&" +

        "roleID=" + orderInfo.roleID + "&" +
        "roleName=" + orderInfo.roleName + "&" +
        "roleLevel=" + orderInfo.roleLevel + "&" +

        "serverID=" + orderInfo.serverID + "&" +
        "serverName=" + orderInfo.serverName + "&" +

        "realMoney=" + orderInfo.realMoney + "&" +
        "completeTime=" + orderInfo.completeTime + "&" +
        "sdkOrderTime=" + orderInfo.sdkOrderTime + "&" +

        "status=" + orderInfo.status + "&" +
        "notifyUrl=" + orderInfo.notifyUrl + "&" +
        ZySDK.GameKey;

    let urlSign = encodeURIComponent(signString);
    let hex_sign_uri = hex_md5(urlSign);
    return hex_sign_uri;
}

/**
 * 从服务器获取js路径
 * @param {object}  data
 * @param {function}  callback
 * */
function loadSpLib(data, callback) {
    let libUrl = data.channelPlatform.libUrl;
    // let spLib = "http://111.231.244.198:8080/lib/Channels/Drivers/" + data.channelData.name + ".js"
    let spLib = null;
    loadAsyncScript(spLib, function () {
        let channelLib = libUrl;
        if (channelLib == null) {
            callback();
        }

        if (typeof (channelLib) == 'object') {
            let hasSend = false;
            for (let i in channelLib) {
                let thisLib = channelLib[i];
                loadAsyncScript(thisLib, function () {
                    setLog('load channel lib success');
                    if (!hasSend) {
                        callback();
                        hasSend = true;
                    }
                });
            }
        } else {
            loadAsyncScript(channelLib, function () {
                setLog('load channel lib success');
                callback();
            });
        }
    });
}

function getNowHost() {
    let requestUri = window.location.href;
    //?次数
    var temp = requestUri.split("?");
    if (temp.length !== 2) {
        //多次?
        requestUri = requestUri.replace('?', '|tempCut|');
        requestUri = requestUri.replace(/\?/g, '&');
        requestUri = requestUri.replace('|tempCut|', '?');
    }
    return requestUri;
}
