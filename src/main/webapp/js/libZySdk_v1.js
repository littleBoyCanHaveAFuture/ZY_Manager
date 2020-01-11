/**
 *  zy sdk 文件
 * 测试环境
 * @author song minghua
 * @date 2019/12/31
 * @version 0.1.1
 */
//服务器地址
const domain = "http://localhost:8080";
let SpParams = {};
let saveSpParams;
let ZySDK = {
    GameId: null,
    GameKey: null,
    SpId: -1,
    PayKey: null,
    debug: true,
    /**
     * 1.sdk初始化函数
     * @param sZyGameId 平台游戏id（后台自动分配 ）
     * @param sZyGameKey 平台秘钥（后台自动分配 ）zy_game
     * @param sZySpId    平台渠道id（后台自动分配 ）
     * @param callback 自定义回调函数
     * */
    init: function (sZyGameId, sZyGameKey, sZySpId, callback) {
        this.GameId = sZyGameId;
        this.GameKey = sZyGameKey;
        this.SpId = sZySpId;

        let params = {};
        params.GameId = this.GameId;
        params.GameKey = this.GameKey;
        params.SpId = this.SpId;
        params.debug = this.debug;

        doInit(params, function () {
            callback();
        });
    },
    /**
     * 2.sdk登录函数
     * @param callback 自定义回调函数
     * */
    gameLogin: function (callback) {
        loginCallbackFunction = callback;
        autoLogin();
    },
    /**
     * 3.上传角色信息*/
    uploadGameRoleInfo: function (roleInfo, callback) {

    },
    /**
     * 4.支付上报
     * @param {Object}      orderInfo 订单信息
     * @param {function}    callback
     * */
    pay: function (orderInfo, callback) {
        //检查订单对象
        let returnObject = {};

        let orderChecker = checkOrderObject(orderInfo);
        if (!orderChecker.result) {
            setLog(orderChecker.message, 1);
        }

        getZySDKOrderData(orderInfo, function (getOrderData) {

            if (getOrderData.status === false || !getOrderData.hasOwnProperty('orderNo')) {

                //0支付成功 1支付失败 2取消支付 3下单失败
                let orderType = {};
                orderType.payStatus = 3;

                returnObject.status = false;
                returnObject.data = orderType;
                returnObject.message = '下单失败:' + getOrderData.message;

                setLog(returnObject.message, 1);

                // QuickSDK.dataPut("H5下单失败", "");

                if (callback != null) {
                    callback(returnObject);
                }
            }

            orderInfo.orderNo = getOrderData.orderNo;
            orderInfo.quickOrder = getOrderData;
            // callChannelPay(orderInfo);
        });

        // uploadPayInfo(orderInfo, function (resultObject) {
        //     callback(resultObject);
        // });
    },
    /**
     * 5.登出*/
    gameLogout: function (callback) {

    },
    /**
     * 6.指悦账号登录
     * 或者直接渠道账号登录 ：渠道id、渠道uid足以分辨
     *
     * */
    zyLogin: function (callback) {

    },
    /**
     * 7.指悦账号注册*/
    zyRegister: function (callback) {

    }
};

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

/**加载需要的js文件
 * @param {string}      src         js文件路径+文件名称
 * @param {function}    callback
 * */
function loadAsyncScript(src, callback) {
    if (checkParam(src)) {
        callback();
        return;
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
 * @param {Object} info 加密内容
 * @param secretKey md5秘钥
 * */
function md5(info, secretKey) {
    let strInfo = "";
    for (let key in info) {
        strInfo += key;
        strInfo += "=";
        strInfo += info[key];
        strInfo += "&";
    }
    strInfo += secretKey;

    let sign_uri = encodeURIComponent(strInfo);
    let hex_sign_uri = hex_md5(sign_uri);

    if (ZySDK.debug) {
        console.info(strInfo);
        console.info(hex_sign_uri);
    }
    return hex_sign_uri;
}

/**检查订单参数
 * @param    {Object}      orderInfo           充值信息
 * @return   {Object}      result
 * */
function checkOrderObject(orderInfo) {
    let result = {
        result: true,
        message: ""
    };
    //检查必须参数
    let mustKey = [
        'appId', 'channelId', 'channelUid',
        'serverID', 'serverName',
        'roleID', 'roleName', 'roleLevel',
        'productID', 'productName', 'productDesc', 'money',
        'sdkOrderTime', 'notifyUrl',
        'signType', 'sign'
    ];

    for (let keyIndex in mustKey) {
        let keyName = mustKey[keyIndex];
        if (!orderInfo.hasOwnProperty(keyName)) {
            result.result = false;
            result.message = '调用uploadPayInfo()缺少必须参数:' + keyName;
            return result;
        }
    }

    //检查参数是否合法 gameKey是否一致
    // if (orderInfo.appId !== ZySDK.ZyGameId) {
    //     result.result = false;
    //     result.message = '调用pay()时如下参数:appId 与 ZySDK.ZyGameId 不一致';
    //     return result;
    // }
    //平台参数:游戏-渠道
    // if (checkParam(orderInfo.appId) || checkParam(orderInfo.channelId)) {
    //     result.result = false;
    //     result.message = '调用pay()时如下参数:appId channelId  不能为空';
    //     return result;
    // }
    //平台参数:平台账号、渠道账号
    if (checkParam(orderInfo.channelUid)) {
        result.result = false;
        result.message = '调用pay()时如下参数: channelUid 不能为空';
        return result;
    }
    // //渠道订单:渠道订单号、渠道账号
    // if (checkParam(orderInfo.channelOrderID)) {
    //     result.result = false;
    //     result.message = '调用pay()时如下参数:channelOrderID   不能为空';
    //     return result;
    // }
    //渠道订单:区服
    if (checkParam(orderInfo.serverID) || checkParam(orderInfo.serverName)) {
        result.result = false;
        result.message = '调用pay()时如下参数:serverID serverName  不能为空';
        return result;
    }
    //渠道订单:角色信息
    if (checkParam(orderInfo.roleID) || checkParam(orderInfo.roleName) || checkParam(orderInfo.roleLevel)) {
        result.result = false;
        result.message = '调用pay()时如下参数:roleID roleName roleLevel    不能为空';
        return result;
    }
    //渠道订单:商品信息
    if (checkParam(orderInfo.productID) || checkParam(orderInfo.productName) || checkParam(orderInfo.productDesc)) {
        result.result = false;
        result.message = '调用pay()时如下参数:productID productName  productDesc   不能为空';
        return result;
    }
    //渠道订单:商品信息
    if (checkParam(orderInfo.productID) || checkParam(orderInfo.productName) || checkParam(orderInfo.productDesc)) {
        result.result = false;
        result.message = '调用pay()时如下参数:productID productName  productDesc   不能为空';
        return result;
    }
    //渠道订单:金额，金额需为整数或浮点数
    if (parseFloat(orderInfo.money) !== orderInfo.amount && parseInt(orderInfo.money) !== orderInfo.money) {
        result.result = false;
        result.message = '调用pay()时下单金额需为整数或浮点数';
        return result;
    }
    if (checkParam(orderInfo.signType) || !(orderInfo.signType === "MD5" || orderInfo.signType === "RSA2")) {
        result.result = false;
        result.message = '调用pay()时 signType 需要为 MD5 或 RSA2';
        return result;
    }

    // let status = orderInfo.status;
    // //订单状态
    // let isStatus = OrderStatus.findIndex(function (value, index, arr) {
    //     return value === status;
    // });
    //
    // if (isStatus === -1) {
    //     result.result = false;
    //     result.message = "订单状态错误：status[" + status + "]=" + OrderStatusDesc[status];
    //     return result;
    // }
    return result;
}

/**
 * 获取平台的该游戏渠道参数
 * @param {Object}  params
 * @param {number}  params.GameId
 * @param {string}  params.GameKey
 * @param {number}  params.SpId
 * @param {boolean}  params.debug
 * @param {function} callback 回调函数
 * */
function doInit(params, callback) {
    let requestUri = getNowHost();
    requestUri += "?q=1&w=2"
    requestUri = requestUri.replace(/&amp;/g, '&');

    console.info(params);
    console.info(requestUri);

    $.ajax({
        type: "GET",
        url: domain + "/webGame/initApi",
        data: params,
        dataType: "json",
        success: function (data) {
            //load channel lib
            loadSpLib(data, function () {
                SpParams = data.channelParams;
                callback();
                if (data.hasOwnProperty('channelToken')) {
                    saveSplParams = data.channelToken;
                }
                setLog(data, 0);
            });
        }
    })

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

/**
 * 充值-上报数据接口
 * @param   {Object}      orderInfo           充值信息
 * @param   {function}    callback            回调函数
 * @return  {json}
 * */
function uploadPayInfo(orderInfo, callback) {
    let rspObject = {
        status: false,
        data: '',
        message: ""
    };
    let way = "充值上报 ";

    setLog(orderInfo, 0);

    $.ajax({
        url: t_domain + "/payInfo",
        type: "post",
        dataType: 'json',
        contentType: "text/plain; charset=utf-8",
        data: JSON.stringify(orderInfo),
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

/**
 * 获取zy平台订单数据
 * @param {Object} orderData 订单信息
 * @param callback
 * */
function getZySDKOrderData(orderData, callback) {
    let way = "充值上报 ";

    orderData.appID = ZySDK.ZyGameId;
    orderData.channelID = ZySDK.ZySpId;

    if (orderData.channelUid === -1) {
        return setLog('必须登录后方可调用支付', 1);
    }
    let rspObject = {};

    $.ajax({
        url: t_domain + "/payInfo",
        type: "post",
        dataType: 'json',
        contentType: "text/plain; charset=utf-8",
        data: JSON.stringify(orderData),
        async: false,
        success: function (result) {
            setLog(way + "recv " + result, 0);

            if (result == null || typeof (result) != 'object') {
                rspObject.message = '请求接口失败无法获取响应';
                return callback(rspObject);
            }
            if (!respData.hasOwnProperty('resultCode')) {
                rspObject.message = '请求接口失败';
                return callback(rspObject);
            }

            if (result.resultCode === 200) {
                setLog(way + "recv result " + result.message, 0);

                if (result.state !== 1) {
                    setLog(way + "recv 订单当前状态 " + OrderStateMsg[result.state], 1);
                    rspObject.message = result.message;
                    rspObject.state = result.state;
                } else {
                    setLog(way + "recv 订单当前状态 " + OrderStateMsg[result.state], 0);
                    rspObject.message = result.message;
                    rspObject.state = result.state;
                    rspObject.orderId = result.orderId;
                }
            }
        },
        error: function () {
            rspObject.state = false;
            rspObject.message = "接口请求失败";
            return callback(rspObject);
        }
    });
    return response;
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
    let repObject = {};
    if (!regInfo.hasOwnProperty('auto')) {
        repObject.message = "auto 参数为空";
        repObject.state = false;
        callback(repObject);
        return;
    }
    if (regInfo.auto === true) {
        let mustKey = ['appId', 'channelId', 'channelUid', 'channelUname'];
        if (regInfo.hasOwnProperty('channelId') && regInfo.channelId === 0) {

        } else {
            for (let keyIndex of mustKey) {
                if (!regInfo.hasOwnProperty(mustKey[keyIndex])) {
                    repObject.message = "渠道id 或 渠道uid 为空";
                    repObject.state = false;
                    callback(repObject);
                    return;
                }
            }
        }
    } else {
        let mustKey = ['appId', 'channelId', 'username', 'password'];
        for (let keyIndex of mustKey) {
            if (!regInfo.hasOwnProperty(mustKey[keyIndex])) {
                repObject.message = "用户名 或 密码 为空";
                repObject.state = false;
                callback(repObject);
                return;
            }
        }
    }

    $.ajax({
        url: t_domain + "/register",
        type: "post",
        contentType: "text/plain; charset=utf-8",
        data: JSON.stringify(regInfo),
        dataType: "json",
        async: false,
        success: function (result) {
            if (result.hasOwnProperty('data') && result.data.hasOwnProperty('state')) {
                if (result.data.state === true) {
                    repObject = {
                        "state": true,
                        "account": result.data.account,
                        "password": result.data.password,
                        "uid": result.data.accountId,
                        "channelUid": result.data.channelUid,
                        "message": result.data.reason
                    };
                } else {
                    repObject = {
                        "state": false,
                        "message": result.data.reason
                    }
                }
            } else {
                repObject.state = false;
                repObject.message = "失败";
            }
            callback(repObject);
        },
        error: function () {
            repObject.message = "失败";
            repObject.state = false;
            callback(repObject);
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
