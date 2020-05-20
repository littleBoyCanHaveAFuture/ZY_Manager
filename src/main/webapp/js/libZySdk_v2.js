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
     * @param channelId         平台渠道id（后台自动分配 ）
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

        doInit(params, function (status) {
            callback(status);
        });
    },
    /**
     * 2.渠道账号登录->会获取当前页面的参数进行处理
     * @param callback          自定义回调函数
     * */
    login: function (callback) {
        loginCallbackFunction = callback;
        autoLogin();
    },
    /**
     * 3.调起支付-callChannelPay 每个渠道需要重写，调起渠道支付页面
     * @param {Object}      orderInfo       订单信息
     * @param {function}    callback
     * */
    pay: function (orderInfo, callback) {
        sdk_zyUploadPayInfo(orderInfo, callback);
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
        let roleChecker = checkRoleObject(roleInfo);
        if (!roleChecker.result) {
            setLog(roleChecker.message, 1);
            return;
        }
        callUploadRole(roleInfo);
        ajaxUploadGameRole(roleInfo, function (resultObject) {
            callback(resultObject);
        });
    },
    /**
     * 6.注销
     * */
    logout: function (callback) {
        if (typeof callChannelLogout == 'function') {
            let logout = callChannelLogout();
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
let saveChannelParams = '';
let loginCallbackFunction = null;
let CallbackPayFun = null;

/**
 * 获取平台的该游戏渠道参数
 * @param {Object}      params
 * @param {number}      params.GameId   平台游戏id
 * @param {string}      params.GameKey  平台游戏秘钥
 * @param {boolean}     params.debug    是否·debug模式
 * @param {function}    callback        回调函数
 * */
function doInit(params, callback) {
    let requestUri = getNowHost();
    console.info("doInit requestUri=" + requestUri);
    requestUri = requestUri.replace(/\&amp;/g, '&');
    try {
        var params = requestUri.split('?')[1];
        console.info("doInit params=" + params);
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
                loadSpLib(data, function () {
                    callChannelInit(params + '&zhiyue_channel_token=' + data.channelToken);
                    callback(data.status);
                    if (data.hasOwnProperty('channelToken')) {
                        saveChannelParams = data.channelToken;
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
function autoLogin() {
    let requestUri = getNowHost();
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
            doLoginCallback(data);
        }
    })
}

let userData = {};

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
function doLoginCallback(data) {
    let t_userData = data.userData;
    let channelData = data.channelData;

    let channelId = channelData.channel_id;
    let channelName = channelData.channel_name;

    ZhiYueSDK.channelId = channelData.channel_id;
    userData = t_userData;

    if (loginCallbackFunction != null) {
        let returnObject = {};
        returnObject.data = data.userData;
        returnObject.status = data.userData.uid !== '';
        returnObject.message = data.message;
        if (returnObject.status)
            ZhiYueSDK.channelUid = data.userData.uid;
        loginCallbackFunction(returnObject);
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
function sdk_zyUploadPayInfo(orderInfo, callback) {
    let rspObject = {};
    let orderChecker = checkOrderObject(orderInfo);
    if (orderChecker.state === false) {
        setLog(orderChecker.message, 1);
        return
    }

    //向 ZhiYueSDK 下单获取 ZhiYueSDK 订单
    let channelToken = getParamsExtQuick('channelToken');
    //从渠道驱动js里获取channelToken
    if (!channelToken || channelToken === '') {
        if (typeof getChannelToken == 'function') {
            channelToken = getChannelToken();
        }
    }
    if (channelToken === '') {
        channelToken = saveChannelParams;
    }
    orderInfo.channelToken = channelToken;
    getQuickSDKOrderData(orderInfo, function (getOrderData) {
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
        callChannelPay(orderInfo);
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

    if (ZhiYueSDK.debug) {
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
 * @param {function}  callback
 * */
function loadSpLib(data, callback) {
    let libUrl = data.channelPlatform.libUrl;
    let spLib = "https://zyh5games.com/sdk/channel/" + data.channelData.name + ".js";
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

/**
 * @param param 判断的参数
 * @return {boolean}
 * */
function checkParam(param) {
    return (param == null || param === "" || param === "undefined");
}

let getParamsArr = null;

function getParamsExtQuick(key) {

    if (getParamsArr == null) {
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
        getParamsArr = getParamsData;
    }

    return getParamsArr.hasOwnProperty(key) ? getParamsArr[key] : '';

}

function getQuickSDKOrderData(orderData, callback) {

    orderData.gameKey = ZhiYueSDK.GameKey;
    orderData.channelId = ZhiYueSDK.channelId;

    if (orderData.channelId === -1) {
        return setLog('必须登录后方可调用支付', 1);
    }

    if (!orderData.hasOwnProperty('username') || orderData.username.length <= 0) {
        orderData.username = orderData.uid;
    }

    let rebackObj = {};
    rebackObj.status = false;
    rebackObj.orderNo = '';

    $.ajax({
        url: ZhiYue_domain + "/ajaxGetOrderNo",
        type: "post",
        contentType: "text/plain; charset=utf-8",
        data: JSON.stringify(orderData),
        dataType: "json",
        // /**
        //  * @param {object} rspData
        //  * @param {string} rspData.message
        //  * @param {boolean} rspData.status
        //  * @param {string} rspData.orderNo
        //  * @param {object} rspData.data
        //  * @param {object} rspData.data.amount
        //  * @param {object} rspData.data.orderNo
        //  * @param {object} rspData.data.channelOrderNo
        //  * */
        success: function (rspData) {
            if (rspData == null || typeof (rspData) !== 'object') {
                rebackObj.message = '请求接口失败无法获取响应';
                return callback(rebackObj);
            }
            if (!rspData.hasOwnProperty('status') || rspData.status === false) {
                if (rebackObj.message !== undefined || rebackObj.message === '' || !rspData.hasOwnProperty('message')) {
                    rebackObj.message = '请求接口失败';
                } else {
                    rebackObj.message = rspData.message;
                }
                return callback(rebackObj);
            }
            rebackObj.orderNo = rspData.data.orderNo;
            rebackObj.status = true;
            rebackObj.channelOrder = rspData.data.channelOrderNo;
            return callback(rebackObj);
        },
        error: function () {
            rebackObj.status = false;
            rebackObj.message = "接口请求失败";
            return callback(rebackObj);
        }
    });
}

/**测试代码-模拟cp服务器请求登录验证*/
function checkUserInfo(info) {
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

function checkRoleObject(roleInfo) {
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

    return result;
}

/**
 * 角色-上报数据接口
 *  @param  {object}             roleInfo
 *  @param  {boolean}            roleInfo.datatype
 *  @param  {string}             roleInfo.roleCreateTime
 *  @param  {}                   roleInfo.uid
 *  @param  {}                   roleInfo.username
 *  @param  {}                   roleInfo.serverId
 *  @param  {}                   roleInfo.serverName
 *  @param  {}                   roleInfo.userRoleName
 *  @param  {}                   roleInfo.userRoleId
 *  @param  {}                   roleInfo.userRoleBalance
 *  @param  {}                   roleInfo.vipLevel
 *  @param  {}                   roleInfo.userRoleLevel
 *  @param  {}                   roleInfo.partyId
 *  @param  {}                   roleInfo.partyName
 *  @param  {}                   roleInfo.gameRoleGender
 *  @param  {}                   roleInfo.gameRolePower
 *  @param  {}                   roleInfo.partyRoleId
 *  @param  {}                   roleInfo.partyRoleName
 *  @param  {}                   roleInfo.professionId
 *  @param  {}                   roleInfo.profession
 *  @param  {}                   roleInfo.friendlist
 * @param   {function}           callback                    回调函数
 * */
function ajaxUploadGameRole(roleInfo, callback) {
    let rspObject = {};
    rspObject.status = false;
    rspObject.data = '';
    rspObject.message = "";

    let roleObject = roleInfo;

    roleObject.GameId = ZhiYueSDK.GameId;
    roleObject.GameKey = ZhiYueSDK.GameKey;
    roleObject.channelCode = ZhiYueSDK.channelId;

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

/**测试 渠道支付回调*/
function testChannelPayCallback() {
    let data = {};
    data.channelCode = ZhiYueSDK.channelCode;

    let param =
        "/" + ZhiYueSDK.channelCode +
        "/" + ZhiYueSDK.channelId +
        "/" + ZhiYueSDK.GameId;
    let url = ZhiYue_domain + "/callbackPayInfo" + param;
    console.info("testChannelPayCallback = " + url);

    if (ZhiYueSDK.channelCode === "h5_ziwan") {
        $.post(url + "/x-www-form-urlencoded", {
            "openid": "aaa",
            "price": "bbb",
            "other": "",
            "item_id": "",
            "orderid": "",
            "sign": ""
        })
    }

    // $.ajax({
    //     url: url,
    //     type: "post",
    //     data: JSON.stringify(data),
    //     dataType: "json",
    //     async: false,
    //     success: function (result) {
    //         console.info(result);
    //
    //     },
    //     error: function () {
    //
    //     }
    // });
}
