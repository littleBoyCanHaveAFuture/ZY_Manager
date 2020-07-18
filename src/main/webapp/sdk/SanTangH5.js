function getQueryString(url, key) {
    let list = url.split("&");
    for (let i in list) {
        let arr = list[i].split("=");
        if (arr[0] === key) {
            return arr[1];
        }
    }
}

let santangUserId = "";

function zyCallChannelInit(params) {
    santangUserId = getQueryString(params, "userid");
}

/**
 *  quick登录,模拟quick正常登录回调，给QuickSDK赋值
 */
function zyCallChannelLogin(data) {

}

function zyCallChannelPay(order) {
    console.log("zyCallChannelPay=" + JSON.stringify(order));

    let trade = JSON.parse(order.zhiyueOrder.channelOrder);
    let payData = JSON.parse(trade.data)

    console.log("zyCallChannelPay payData= " + trade.data);
    /*
     * 参数名        是否必须       备注                   说明
     * userid         是           三唐用户名               三唐开发平台分配的
     * gid            是           游戏                    APPID
     * sid            是           游戏区服                不分区服默认 1
     * money          是           用户充值金额             Money 单位是元，比如 6 元则传值 6
     * gamename       是           游戏名字                中文需要 URLEncode编码
     * cp_trade_no    是           CP 方订单编号
     * openid         是           三唐开放平台openid
     * method         是           消息类型固定值 pay       注意本参数不参与签名
     * item           是           购买物品                例：购买 60 元宝,中文需要 URLEncode编码
     * gamerate       否           游戏兑换比例             例如如果兑换比例是1 元 10 元宝，则gamerate=10
     * ybcn           否           游戏虚拟货币名称         例：元宝、龙晶,中文需要 URLEncode编码
     * roleid         是           玩家角色 ID
     * rolename       是           玩家游戏角色名          中文需要 URLEncode编码
     * sign           是           md5(gid&sid&openid&userid&money& APPKEY)注：&是变量连接符,不要放到加密里。APPKEY 由三唐平台分配
     *
     */
    // window.parent.postMessage(payData, '*');
    window.top.postMessage(payData, '*');
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
    /**
     * 字段                   类型           说明
     *  method                必传           固定值 report，平台用以区分消息类型
     * isCreateRole           必传           是创建角色就写 1，其他写 0
     * roleCreateTime         非必须          unixTime
     * userid                 必传           三唐用户名 userid
     * openid                 必传           三唐 openid
     * fightvalue             必传           玩家角色战力
     * serverId               必传           区服 ID
     * serverName             必传           区服名称
     * userRoleId             必传           游戏角色 ID，需跟角色上报的一致，不然拉不起支付
     * userRoleName           必传           游戏角色
     * userRoleLevel          必传           角色等级
     * userRoleBalance        非必传         角色游戏内货币余额
     * vipLevel               非必传         角色 VIP 等级
     * partyId                非必传         公会/社团 ID
     * partyName              非必传         公会/社团名称
     */
    let roleInfoJson = {
        "method": "report",//注意方法名不能错
        "isCreateRole": 1,//是创建角色就写 1，其他写 0
        "roleCreateTime": roleInfo.roleCreateTime,//unixTime
        "userid": santangUserId,//三唐用户名 userid
        "open id": roleInfo.uid,//三唐用户名 openid
        "fightvalue": roleInfo.gameRolePower,
        "serverId": roleInfo.serverId,
        "serverName": roleInfo.serverName,
        "userRoleName": roleInfo.userRoleName,
        "userRoleId": roleInfo.userRoleId,
        "userRoleBalance": roleInfo.userRoleBalance,
        "vipLevel": roleInfo.vipLevel,
        "userRoleLevel": roleInfo.userRoleLevel,
        "partyId": roleInfo.partyId,
        "partyName": roleInfo.partyName
    };
    window.parent.postMessage(roleInfoJson, '*');
}

function share() {
    //可以自定义分享内容，也可以不传参数，不传采用平台默认
}

function zyCallChannelLogout() {

}

