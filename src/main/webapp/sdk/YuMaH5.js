function getQueryString(url, key) {
    let list = url.split("&");
    for (let i in list) {
        let arr = list[i].split("=");
        if (arr[0] === key) {
            return arr[1];
        }
    }
}

let hasInit = false;
let zyYuMaChannelUid = "";
let zyYuMaQuickData = "";
/**
 * @param  {string}     params.zhiyue_channel_config.ProductCode
 * @param  {string}     params.zhiyue_channel_config.ProductKey
 */

let quickConfig = {};

/**
 * @param  {string}     params
 * @param  {string}     params.zhiyue_channel_token
 * @param  {string}     params.zhiyue_channel_config
 * */
function zyCallChannelInit(params) {
    // 参数分割
    let channelUid = getQueryString(params, "zyYuMaChannelUid");
    let config = getQueryString(params, "zhiyue_channel_config");
    console.log(config);
    quickConfig = JSON.parse(config);

}

/**
 *  quick登录,模拟quick正常登录回调，给QuickSDK赋值
 */
function zyCallChannelLogin(data) {
    if (!hasInit) {
        let productCode = quickConfig.ProductCode;
        let productKey = quickConfig.ProductKey;
        QuickSDK.init(productCode, productKey, true, function () {
            console.log("quick init success");
            hasInit = true;
        })
    }
    if (data.hasOwnProperty("quickData")) {
        console.info("zyCallChannelLogin quick ---->doLoginCallback");
        doLoginCallback(data.quickData);
    } else {
        console.info("zyCallChannelLogin quick ---->fail");
        data.userData.uid = "";
    }
}

/**
 * @param {object}          order 指悦的订单参数+回调订单参数
 * @param {object}          order.orderNo 回调订单参数
 * @param {object}          order.zhiyueOrder 指悦返回的订单参数
 * @param {object}          order.zhiyueOrder.orderNo 与上面一样
 * @param {boolean}         order.zhiyueOrder.status 调起支付结果
 * @param {object}          order.zhiyueOrder.channelOrder 渠道订单信息 qucik
 * @param {object}          order.zhiyueOrder.channelOrder.data QuickSDK.pay() 支付返回的原生参数
 * */
function zyCallChannelPay(order) {
    console.log("zyCallChannelPay = " + JSON.stringify(order));
    // quick 回复的参数
    let trade = JSON.parse(order.zhiyueOrder.channelOrder);
    /**
     * @param {object} trade
     * @param {string} trade.reqData
     * @param {string} trade.rspData
     * */
    let orderData = JSON.parse(trade.reqData);
    let orderInfoJson = JSON.stringify(orderData);
    QuickSDK.pay(orderInfoJson,function(payStatusObject){
        console.log('zyCallChannelPay: quick下单通知' + JSON.stringify(payStatusObject));
    });
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
 * */
function zyCallUploadRole(roleInfo) {
    console.log("callUploadRole = " + roleInfo);
    let quickRoleInfo = {};
    quickRoleInfo.isCreateRole = roleInfo.datatype === 2;
    quickRoleInfo.roleCreateTime = roleInfo.roleCreateTime;
    quickRoleInfo.uid = roleInfo.uid;
    quickRoleInfo.username = roleInfo.username;
    quickRoleInfo.serverId = roleInfo.serverId;
    quickRoleInfo.serverName = roleInfo.serverName;
    quickRoleInfo.userRoleName = roleInfo.userRoleName;
    quickRoleInfo.userRoleId = roleInfo.userRoleId;
    quickRoleInfo.userRoleBalance = roleInfo.userRoleBalance;
    quickRoleInfo.vipLevel = roleInfo.vipLevel;
    quickRoleInfo.userRoleLevel = roleInfo.userRoleLevel;
    quickRoleInfo.partyId = roleInfo.partyId;
    quickRoleInfo.partyName = roleInfo.partyName;
    quickRoleInfo.gameRoleGender = roleInfo.gameRoleGender;
    quickRoleInfo.gameRolePower = roleInfo.gameRolePower;
    quickRoleInfo.partyRoleId = roleInfo.partyRoleId;
    quickRoleInfo.partyRoleName = roleInfo.partyRoleName;
    quickRoleInfo.professionId = roleInfo.professionId;
    quickRoleInfo.profession = roleInfo.profession;
    quickRoleInfo.friendlist = roleInfo.friendlist;
    let roleInfoJson = JSON.stringify(quickRoleInfo);
    QuickSDK.uploadGameRoleInfo(roleInfoJson, function (response) {
        if (response.status) {
            console.log('quick 提交信息成功');
        } else {
            console.log(response.message);
        }
    });
}

function zyCallChannelLogout() {
    QuickSDK.logout(function (logoutObject) {
        console.log('Game:成功退出游戏');
    })
}

