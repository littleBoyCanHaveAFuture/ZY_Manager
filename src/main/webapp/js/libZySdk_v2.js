/**
 *  zy sdk 文件
 * 测试环境
 * @author song minghua
 * @date 2020/05/14
 * @version 0.2.7
 */
//服务器地址
const ZhiYue_domain = "http://localhost:8080/webGame2";
// const ZhiYue_domain = "https://zyh5games.com/zysdk/webGame2";

let ZhiYueSDK = {
    GameId: null,//游戏id
    GameKey: null,//游戏秘钥
    channelId: -1,//渠道id
    channelUid: "",//渠道uid
    channelCode: "",
    channelToken: "",
    debug: true,
    /**
     * 1.sdk初始化函数
     * @param GameId            平台游戏id（后台自动分配 ）
     * @param GameKey           平台游戏秘钥（后台自动分配 ）
     * @param channelId         平台渠道id（后台自动分配 ）其实不用这个参数 使用的是url的参数
     * @param callback          自定义回调函数
     * */
    init: function (GameId, GameKey, channelId, callback) {
        ZhiYueSDK.GameId = GameId;
        ZhiYueSDK.GameKey = GameKey;
        ZhiYueSDK.channelId = channelId;
        let params = {};
        params.debug = this.debug;
        params.GameId = this.GameId;
        params.GameKey = this.GameKey;
        params.channelId = this.channelId;

        zyDoInit(params, function (status) {
            callback(status);
        });
    },
    /**
     * 2.渠道账号登录->会获取当前页面的参数进行处理
     * @param callback          自定义回调函数
     * */
    login: function (callback) {
        zyLoginCallbackFunction = callback;
        zyAutoLogin();
    },
    /**
     * 3.调起支付-zyCallChannelPay 每个渠道需要重写，调起渠道支付页面
     * @param {Object}      orderInfo       订单信息
     * @param {function}    callback
     * */
    pay: function (orderInfo, callback) {
        zyUploadPayInfo(orderInfo, callback);
    },
    /**
     * 4.上传角色信息
     * @param   {objcet}      roleInfo
     * @param   {function}      callback        回调函数
     *  */
    uploadGameRoleInfo: function (roleInfo, callback) {
        let roleObject = {};
        this.dataPut("H5角色更新", "");
        //检查是否已初始化
        if (!ZhiYueSDK.checkZySdkParam(false)) {
            setLog('SDK未完成初始化', 1);
            return;
        }
        let roleChecker = zyCheckRoleObject(roleInfo);
        if (!roleChecker.result) {
            setLog(roleChecker.message, 1);
            return;
        }
        zyCallUploadRole(roleInfo);
        zyAjaxUploadGameRole(roleInfo, function (resultObject) {
            callback(resultObject);
        });
    },
    /**
     * 6.注销
     * */
    logout: function (callback) {
        if (typeof zyCallChannelLogout == 'function') {
            let logout = zyCallChannelLogout();
            callback(logout);
        }
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
            if (this.channelUid == null) {
                return false;
            }
        }
        return true;
    },
    dataPut: function () {
        //null
    },
};
// 临时存放 某些渠道的 token
let zySaveChannelParams = '';
let zyLoginCallbackFunction = null;
let zyCallbackPayFun = null;

/**
 * 获取平台的该游戏渠道参数
 * @param {Object}      params
 * @param {number}      params.GameId   平台游戏id
 * @param {string}      params.GameKey  平台游戏秘钥
 * @param {boolean}     params.debug    是否·debug模式
 * @param {function}    callback        回调函数
 * */
function zyDoInit(params, callback) {
    let requestUri = zyGetNowHost();
    // console.info("doInit requestUri=" + requestUri);
    requestUri = requestUri.replace(/\&amp;/g, '&');
    try {
        var params = requestUri.split('?')[1];
        // console.info("doInit params=" + params);
    } catch (e) {
        var params = '';
    }

    $.ajax({
        type: "GET",
        url: ZhiYue_domain + "/initApi?" + params,
        dataType: "json",
        success: function (data) {
            if (data.hasOwnProperty('status') && data.status === true) {
                //load channel lib
                zyLoadSpLib(data, function () {
                    let initParam = params + '&zhiyue_channel_token=' + data.channelToken;
                    if (data.channelData.hasOwnProperty("config")) {
                        initParam += "&zhiyue_channel_config=" + data.channelData.config;
                    }
                    zyCallChannelInit(initParam);
                    callback(data.status);
                    if (data.hasOwnProperty('channelToken')) {
                        zySaveChannelParams = data.channelToken;
                    }
                    if (data.hasOwnProperty('channelCode')) {
                        ZhiYueSDK.channelCode = data.channelCode;
                    }
                });
            } else {
                setLog(JSON.stringify(data), 1);
                data.status = false;
                callback(data.status);
            }
        }
    })
}

/**
 * 渠道登录
 * */
function zyAutoLogin() {
    let requestUri = zyGetNowHost();
    requestUri = requestUri.replace(/\&amp;/g, '&');
    try {
        var params = requestUri.split('?')[1];
    } catch (e) {
        var params = '';
    }
    $.ajax({
        type: "GET",
        url: ZhiYue_domain + "/loginApi?" + params,
        dataType: "json",
        success: function (data) {
            zyDoLoginCallback(data);
        }
    })
}

let zyUserData = {};

/**
 * @param {object}  data
 * @param {object}  data.userData                   用户
 * @param {string}  data.userData.uid                   渠道uid
 * @param {string}  data.userData.username              渠道username
 * @param {boolean} data.userData.isLogin               是否游客,登录后此值为true
 * @param {string}  data.userData.time                  当前时间戳 单位：秒
 * @param {string}  data.userData.token
 * @param {string}  data.userData.channelId             渠道ID
 * @param {object}  data.channelData                渠道
 * @param {string}  data.channelData.channel_id
 * @param {string}  data.channelData.channel_name
 * @param {boolean} data.status                     登录结果
 * @param {string}  data.message                    提示内容
 * */
function zyDoLoginCallback(data) {
    let t_userData = data.userData;
    let channelData = data.channelData;

    let channelId = channelData.channel_id;
    let channelName = channelData.channel_name;

    ZhiYueSDK.channelId = channelData.channel_id;
    zyUserData = t_userData;

    if (typeof zyCallChannelLogin == 'function') {
        zyCallChannelLogin(data.userData);
    }

    if (zyLoginCallbackFunction != null) {
        let returnObject = {};
        returnObject.data = data.userData;
        returnObject.status = data.userData.uid !== '';
        returnObject.message = data.message;
        if (returnObject.status)
            ZhiYueSDK.channelUid = data.userData.uid;
        zyLoginCallbackFunction(returnObject);
    }
}

/**
 * 充值-上报数据接口
 * @param   {Object}      orderInfo                     充值信息
 * @param   {string}      orderInfo.gameKey             QuickSDK后台自动分配的游戏参数
 * @param   {number}      orderInfo.channelId           QuickSDK后台自动分配的渠道参数
 * @param   {string}      orderInfo.channelUid          渠道UID
 * @param   {string}      orderInfo.username            渠道username
 * @param   {string}      orderInfo.userRoleId          游戏内角色ID
 * @param   {string}      orderInfo.userRoleName        游戏角色
 * @param   {string}      orderInfo.serverId            角色所在区服ID
 * @param   {string}      orderInfo.userServer          角色所在区服
 * @param   {number}      orderInfo.userLevel           角色等级
 * @param   {number}      orderInfo.cpOrderNo           游戏内的订单,SDK服务器通知中会回传
 * @param   {string}      orderInfo.amount              购买金额（元）
 * @param   {number}      orderInfo.count               购买商品个数
 * @param   {number}      orderInfo.quantifier          购买商品单位，如，个
 * @param   {string}      orderInfo.subject             道具名称
 * @param   {number}      orderInfo.desc                道具描述
 * @param   {number}      orderInfo.callbackUrl         Cp服务器通知地址
 * @param   {number}      orderInfo.extrasParams        透传参数,服务器通知中原样回传
 * @param   {number}      orderInfo.goodsId             商品ID
 * */
function zyUploadPayInfo(orderInfo, callback) {
    let rspObject = {};
    let orderChecker = zyCheckOrderObject(orderInfo);
    if (orderChecker.state === false) {
        setLog(orderChecker.message, 1);
        return
    }

    //向 ZhiYueSDK 下单获取 ZhiYueSDK 订单
    let channelToken = zyGetParamsExtQuick('channelToken');
    //从渠道驱动js里获取channelToken
    if (!channelToken || channelToken === '') {
        if (typeof getChannelToken == 'function') {
            channelToken = getChannelToken();
        }
    }
    if (channelToken === '') {
        channelToken = zySaveChannelParams;
    }
    orderInfo.channelToken = channelToken;
    getZhiYueSDKOrderData(orderInfo, function (getOrderData) {
        if (getOrderData.status === false || !getOrderData.hasOwnProperty('orderNo')) {

            //0支付成功 1支付失败 2取消支付 3下单失败
            let orderType = {};
            orderType.payStatus = 3;

            rspObject.status = false;
            rspObject.data = orderType;
            rspObject.message = '下单失败:' + getOrderData.message;

            setLog(rspObject.message, 1);

            ZhiYueSDK.dataPut("H5下单失败", "");
            if (callback != null) {
                callback(rspObject);
            }
        }

        orderInfo.orderNo = getOrderData.orderNo;
        orderInfo.zhiyueOrder = getOrderData;
        zyCallChannelPay(orderInfo);
    });
}

/*
 * 日志
 * @param {string} str 消息内容
 * @param {number} level 等级
 * */
function setLog(str, level) {
    if (ZhiYueSDK.debug === true) {
        if (level === 1) {
            console.log('%cZhiYueSDK致命错误:' + str, 'color:red');
        } else if (level === 2) {
            console.log('%cZhiYueSDK警告错误:' + str, 'color:gray');
        } else if (level === 0) {
            console.log('%cZhiYueSDK运行日志:' + str, 'color:gray');
        } else {
            console.log('%cZhiYueSDK运行日志:' + str, 'color:gray');
        }
    }
}

/**
 * 加载需要的js文件
 * @param {string}      src         js文件路径+文件名称
 * @param {function}    callback
 * */
function zyLoadAsyncScript(src, callback) {
    if (zyCheckParam(src)) {
        callback();
        return;
    }
    console.info(src);
    if (src.search("jquery") !== -1) {
        if (typeof (jQuery) == "undefined") {
            console.info("jQuery is not imported");
        } else {
            console.info("jQuery is imported");
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

/**
 * 检查订单参数
 * @param       {Object}        orderInfo           充值信息
 * @return      {Object}        rspObject
 * */
function zyCheckOrderObject(orderInfo) {
    let rspObject = {};

    if (!ZhiYueSDK.checkZySdkParam(true)) {
        rspObject.state = false;
        rspObject.message = "请先调用 ZhiYueSDK.init() 和 ZhiYueSDK.login()";
        return rspObject;
    }

    let mustKey = ["gameKey", "uid", "username", "userRoleId", "userRoleName",
        "serverId", "userServer", "userLevel", "cpOrderNo", "amount",
        "count", "quantifier", "subject", "desc", "callbackUrl",
        "goodsId"];

    for (let keyIndex in mustKey) {
        let keyName = mustKey[keyIndex];
        if (!orderInfo.hasOwnProperty(keyName)) {
            rspObject.state = false;
            rspObject.message = '调用ZhiYueSDK pay()缺少必须参数:' + keyName;
            return rspObject;
        }
    }
    //检查参数是否合法 gameKey是否一致
    if (orderInfo.gameKey !== ZhiYueSDK.GameKey) {
        setLog('调用ZhiYueSDK 下单的产品与初始化的产品不一致', 2);
    }

    //uid userRoleId userRoleName userServer userLevel是否为空
    if (orderInfo.uid.length <= 0 || orderInfo.userRoleId.length <= 0 ||
        orderInfo.userRoleName.length <= 0 || orderInfo.userServer.length <= 0 ||
        orderInfo.userLevel.length <= 0) {
        rspObject.state = false;
        rspObject.message = '调用ZhiYueSDK pay()时如下参数:uid userRoleId userRoleName userServer userLevel 不能为空';
        return rspObject;
    }

    //amount金额需为整数或浮点数
    if (parseFloat(orderInfo.amount) !== orderInfo.amount && parseInt(orderInfo.amount) !== orderInfo.amount) {
        rspObject.state = false;
        rspObject.message = '调用ZhiYueSDK pay()时下单金额需为整数或浮点数';
        return rspObject;
    }
    //subject为必传
    if (orderInfo.subject.length <= 0) {
        rspObject.state = false;
        rspObject.message = '调用ZhiYueSDK pay()时下单如下参数:subject 不能为空';
        return rspObject;
    }
    rspObject.state = true;
    return rspObject;
}

/**
 * 从服务器获取js路径
 * @param {object}  data
 * @param {object}  data.channelPlatform
 * @param {string}  data.channelPlatform.libUrl
 * @param {string}  data.channelPlatform.playUrl
 * @param {string}  data.channelData.name
 * @param {string}  data.channelData.config
 * @param {function}  callback
 * */
function zyLoadSpLib(data, callback) {
    let libUrl = data.channelPlatform.libUrl;
    let spLib = "https://zyh5games.com/sdk/channel/" + data.channelData.name + ".js";
    let name = "";
    if (!data.channelData.hasOwnProperty("name")) {
        spLib = null;
    }

    zyLoadAsyncScript(spLib, function () {
        let channelLib = libUrl;
        if (channelLib == null) {
            callback();
        }

        if (typeof (channelLib) == 'object') {
            let hasSend = false;
            for (let i in channelLib) {
                let thisLib = channelLib[i];
                zyLoadAsyncScript(thisLib, function () {
                    setLog('load channel lib success');
                    if (!hasSend) {
                        callback();
                        hasSend = true;
                    }
                });
            }
        } else {
            zyLoadAsyncScript(channelLib, function () {
                setLog('load channel lib success');
                callback();
            });
        }
    });
}

function zyGetNowHost() {
    let requestUri = {};
    requestUri.wlh = window.location.href;
    let url = requestUri.wlh;
    //?次数
    var temp = url.split("?");
    if (temp.length !== 2) {
        //多次?
        url = url.replace('?', '|tempCut|');
        url = url.replace(/\?/g, '&');
        url = url.replace('|tempCut|', '?');
    }
    if (typeof zyExtraParam == "function") {
        zyExtraParam(requestUri);
    }
    if (requestUri.hasOwnProperty("channelWLH")) {
        return requestUri.channelWLH;
    } else {
        return requestUri.wlh;
    }
}

/**
 * @param param 判断的参数
 * @return {boolean}
 * */
function zyCheckParam(param) {
    return (param == null || param === "" || param === "undefined");
}

let zyGetParamsArr = null;

function zyGetParamsExtQuick(key) {
    if (zyGetParamsArr == null) {
        var domain = window.location.href;
        domain = domain.replace('??', "?");
        var urlParams = domain.split("?");
        var channelToken = '';
        var getParamsData = new Object();
        if (urlParams.length >= 2) {
            urlParamsArr = urlParams[1];
            var keyVal = urlParamsArr.split("&");
            for (var ki in keyVal) {
                var thisKeyVal = keyVal[ki].split("=");
                if (thisKeyVal.length >= 2) {
                    var paramsKey = thisKeyVal[0];
                    var paramsVal = thisKeyVal[1];
                    getParamsData[paramsKey] = paramsVal;
                }
            }
        }
        zyGetParamsArr = getParamsData;
    }
    return zyGetParamsArr.hasOwnProperty(key) ? zyGetParamsArr[key] : '';
}

function getZhiYueSDKOrderData(orderData, callback) {

    orderData.gameKey = ZhiYueSDK.GameKey;
    orderData.channelId = ZhiYueSDK.channelId;

    if (orderData.channelId === -1) {
        return setLog('必须登录后方可调用支付', 1);
    }

    if (!orderData.hasOwnProperty('username') || orderData.username.length <= 0) {
        orderData.username = orderData.uid;
    }

    let rspObj = {};
    rspObj.status = false;
    rspObj.orderNo = '';

    $.ajax({
        url: ZhiYue_domain + "/ajaxGetOrderNo",
        type: "post",
        contentType: "text/plain; charset=utf-8",
        data: JSON.stringify(orderData),
        dataType: "json",
        // @param {object} rspData
        // @param {string} rspData.message
        // @param {boolean} rspData.status
        // @param {string} rspData.orderNo
        // @param {object} rspData.data
        // @param {object} rspData.data.amount
        // @param {object} rspData.data.orderNo
        // @param {object} rspData.data.channelOrderNo
        success: function (rspData) {
            if (rspData == null || typeof (rspData) !== 'object') {
                rspObj.message = '请求接口失败无法获取响应';
                return callback(rspObj);
            }
            if (!rspData.hasOwnProperty('status') || rspData.status === false) {
                if (rspObj.message !== undefined || rspObj.message === '' || !rspData.hasOwnProperty('message')) {
                    rspObj.message = '请求接口失败';
                } else {
                    rspObj.message = rspData.message;
                }
                return callback(rspObj);
            }
            //指悦订单id
            rspObj.orderNo = rspData.data.orderNo;
            rspObj.status = true;
            // 渠道订单信息
            rspObj.channelOrder = rspData.data.channelOrderNo;
            return callback(rspObj);
        },
        error: function () {
            rspObj.status = false;
            rspObj.message = "接口请求失败";
            return callback(rspObj);
        }
    });
}

/** 测试代码-模拟cp服务器请求登录验证*/
function zyCheckUserInfo(info) {
    let param = "token=" + info.data.token + "&gameKey=" + ZhiYueSDK.GameKey + "&uid=" + info.data.uid + "&channelId=" + info.data.channelId;
    $.ajax({
        url: ZhiYue_domain + "/checkUserInfo?" + param,
        type: "get",
        async: false,
        success: function (result) {
            console.info("checkUserInfo=" + result);
            //验证成功 进入游戏
        }, error: function (result) {
            console.log("checkUserInfo=" + result);
        }
    });
}

function zyCheckRoleObject(roleInfo) {
    let roleJson = roleInfo;
    let result = {};
    result.result = true;

    //检查必须参数
    let mustKey = ['serverId', 'serverName', 'userRoleId', 'userRoleName', 'userRoleBalance', 'vipLevel', 'userRoleLevel', 'partyName'];
    for (let keyIndex in mustKey) {
        let keyName = mustKey[keyIndex];
        if (!roleJson.hasOwnProperty(keyName)) {
            result.result = false;
            result.message = '调用updateGameRoleInfo()缺少必须参数:' + keyName;
            return result;
        }
    }

    //判断类型 vipLevel userRoleBalance userRoleLevel需为int
    if (parseInt(roleJson.vipLevel) !== roleJson.vipLevel) {
        result.result = false;
        result.message = '调用updateGameRoleInfo()时传递vipLevel类型需为int';
        return result;
    }

    if (parseInt(roleJson.userRoleBalance) !== roleJson.userRoleBalance) {
        result.result = false;
        result.message = '调用updateGameRoleInfo()时传递userRoleBalance类型需为int';
        return result;
    }

    if (parseInt(roleJson.userRoleLevel) !== roleJson.userRoleLevel) {
        result.result = false;
        result.message = '调用updateGameRoleInfo()时传递userRoleLevel类型需为int';
        return result;
    }
    roleInfo.GameId = ZhiYueSDK.GameId;
    roleInfo.GameKey = ZhiYueSDK.GameKey;
    roleInfo.channelId = ZhiYueSDK.channelId;
    return result;
}

/**
 * 角色-上报数据接口
 *  @param  {object}             roleInfo
 *  @param  {boolean}            roleInfo.datatype          1.选择服务器 2.创建角色 3.进入游戏 4.等级提升 5.退出游戏"
 *  @param  {string}             roleInfo.roleCreateTime    角色创建时间 时间戳 单位 秒
 *  @param  {string}             roleInfo.uid               渠道UID
 *  @param  {string}             roleInfo.username          渠道账号昵称
 *  @param  {string}             roleInfo.serverId          区服ID
 *  @param  {string}             roleInfo.serverName        区服名称
 *  @param  {string}             roleInfo.userRoleName      游戏内角色名
 *  @param  {string}             roleInfo.userRoleId        游戏角色ID
 *  @param  {string}             roleInfo.userRoleBalance   角色游戏内货币余额
 *  @param  {string}             roleInfo.vipLevel          角色VIP等级
 *  @param  {string}             roleInfo.userRoleLevel     角色等级
 *  @param  {string}             roleInfo.partyId           公会/社团ID
 *  @param  {string}             roleInfo.partyName         公会/社团名称
 *  @param  {string}             roleInfo.gameRoleGender    角色性别
 *  @param  {string}             roleInfo.gameRolePower     角色战力
 *  @param  {string}             roleInfo.partyRoleId       角色在帮派中的ID
 *  @param  {string}             roleInfo.partyRoleName     角色在帮派中的名称
 *  @param  {string}             roleInfo.professionId      角色职业ID
 *  @param  {string}             roleInfo.profession        角色职业名称
 *  @param  {string}             roleInfo.friendlist        角色好友列表
 * @param   {function}           callback                    回调函数
 * */
function zyAjaxUploadGameRole(roleInfo, callback) {
    let rspObject = {};
    rspObject.status = false;
    rspObject.data = '';
    rspObject.message = "";

    let roleObject = roleInfo;


    if (!roleObject.hasOwnProperty('uid') || roleObject.uid.length <= 0) {
        rspObject.message = "调用失败:缺少uid";
        return callback(rspObject);
    }

    if (!roleObject.hasOwnProperty('username') || roleObject.username.length <= 0) {
        roleObject.username = roleObject.uid;
    }
    ZhiYueSDK.dataPut("H5角色Vip" + roleObject.vipLevel, "");
    let data = JSON.stringify(roleObject);
    $.ajax({
        url: ZhiYue_domain + "/ajaxUploadGameRoleInfo",
        type: "post",
        contentType: "text/plain; charset=utf-8",
        data: data,
        dataType: "json",
        async: false,
        success: function (respData) {
            if (respData == null || typeof (respData) != 'object') {
                rspObject.message = '请求接口失败无法获取响应';
                return callback(rspObject);
            }
            if (!respData.hasOwnProperty('status') || respData.status === false) {
                if (rspObject.message !== undefined || rspObject.message === '' || !respData.hasOwnProperty('message')) {
                    rspObject.message = '请求接口失败';
                } else {
                    rspObject.message = respData.message;
                }
                return callback(rspObject);
            }
            rspObject.data = respData;
            rspObject.status = true;
            return callback(rspObject);
        },
        error: function () {
            rspObject.status = false;
            rspObject.message = "接口请求失败";
            return callback(rspObject);
        }
    });
}
