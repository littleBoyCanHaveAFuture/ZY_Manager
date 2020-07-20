var _manbah5sdk;
var _manbah5_config = {};

function getQueryString(url, key) {
    let list = url.split("&");
    for (let i in list) {
        let arr = list[i].split("=");
        if (arr[0] === key) {
            return arr[1];
        }
    }
}

function initSdk() {
    // 参数分割
    if (_manbah5sdk === null || _manbah5sdk === undefined) {
        if (typeof manbah5sdk === 'function') {
            _manbah5sdk = new manbah5sdk();
            let config = _manbah5sdk.init();
            _manbah5_config = {
                'mbGameId': config.mbGameId,
                'mbUserId': config.mbUserId,
                'mbToken': config.mbToken
            };
            console.info("man ba sdk init success")
        }
    }
}

function zyCallChannelInit(params) {
    initSdk();
}

function zyCallChannelPay(order) {
    initSdk();
    console.log("zyCallChannelPay=" + JSON.stringify(order));
    let trade = JSON.parse(order.zhiyueOrder.channelOrder);
    let payData = JSON.parse(trade.data);
    console.log("zyCallChannelPay payData= " + trade.data);

    _manbah5sdk.pay(payData);
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
    console.log(roleInfo);
    initSdk();
    let data = {
        roleid: roleInfo.userRoleId,//角色ID 必传
        rolename: roleInfo.userRoleName,//角色名称
        rolelevel: roleInfo.userRoleLevel,//角色等级
        zoneid: roleInfo.serverId,//区服ID
        zonename: roleInfo.serverName,//区服名称
        balance: roleInfo.userRoleBalance,//游戏币
        vip: roleInfo.vipLevel,//VIP等级
        partyname: roleInfo.partyRoleId,//是否有工会
        attach: '',//如果可以提供 用JSON字符串
    };
    _manbah5sdk.gradeReport(data);
}

// 曼巴分享回调：
// manbah5sdk.prototype.MbShaerCall= function(){
//     console.info('分享回调');
// }
// 曼巴支付回调（页面端）:
// //支付回调，不能用于真实回调，以服务器通知为准
// //result 是否支付成功 string cp attach参数或者错误信息
// manbah5sdk.prototype.MbPayorder = function(result,string){
//
//     Console.info(‘支付回调’);
// }
