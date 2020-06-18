function getQueryString(url, key) {
    let list = url.split("&");
    for (let i in list) {
        let arr = list[i].split("=");
        if (arr[0] === key) {
            return arr[1];
        }
    }
}

function zyCallChannelInit(params) {

}

function zyCallChannelLogin(data) {

}

function zyCallChannelPay(order) {
    console.log("zyCallChannelPay=" + JSON.stringify(order));

    let trade = JSON.parse(order.zhiyueOrder.channelOrder);
    let payData = JSON.parse(trade.data);

    console.log("zyCallChannelPay payData= " + trade.data);

    // var params = {
    //     amount: 1, //金额(元，最低一元)
    //     roleid: 1, //游戏角色id
    //     serverid: 1, //充值服务器id
    //     productname: '充值的游戏名称', //充值的游戏名称
    //     attach: 'xxxx', //游戏方扩展参数
    //     productdesc: '商品描述', //充值的商品描述
    //     payCpSign: 'xxxx', // 充值加签处理 详情请看下面充值加签处理 md5加密
    //     cpUid: 'xxxx' // 游戏帐号ID
    // };
    YXFH5SDK.openPay(payData) //调起支付
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
    // console.log(roleInfo);

}

function share() {
    //可以自定义分享内容，也可以不传参数，不传采用平台默认
}
function zyCallChannelLogout() {

}

