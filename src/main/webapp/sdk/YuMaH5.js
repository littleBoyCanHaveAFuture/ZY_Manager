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

