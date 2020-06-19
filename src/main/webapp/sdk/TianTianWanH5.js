function getQueryString(url, key) {
    let list = url.split("&");
    for (let i in list) {
        let arr = list[i].split("=");
        if (arr[0] === key) {
            return arr[1];
        }
    }
}

let ttwAppId = 0;
let ttwOpenId = "";
let ttwFirst = false;

function zyCallChannelInit(params) {
    ttwAppId = getQueryString(params, "appId");
    ttwOpenId = getQueryString(params, "openId");
    let config = getQueryString(params, "zhiyue_channel_config");
    if (config === "true") {
        ttwFirst = true;
    }
    let shareInfo = {
        title: '传奇青春，热血相伴！',
        imgUrl: 'cdn.xxx.com/img/shareIcon.jpg',
        desc: '多角色养成系统，全民BOSS争夺玩法等传奇经典玩法应有尽有，带您重新回味经典。极速升级，自动打怪，带给您不一样的极致享受。',
        openId: "dasdasdasdhjfhopenid",
        status: "serverid"
    };
    d2f.init(ttwAppId, function () {
        console.log('初始化完成');
    }, shareInfo, false);
}

/**
 *  quick登录,模拟quick正常登录回调，给QuickSDK赋值
 */
function zyCallChannelLogin(data) {

}

function zyCallChannelPay(order) {
    console.log("zyCallChannelPay=" + JSON.stringify(order));

    let trade = JSON.parse(order.zhiyueOrder.channelOrder);
    let payData = JSON.parse(trade.data);
    let orderData = JSON.parse(payData.orderData);

    console.log("zyCallChannelPay payData= " + trade.data);
    /**
     * amount 商品金额
     orderdata={
        openId:用户id,
        openKey:验证key,
        orderNo:研发订单id,
        ext:server id    //从1开始
        actor_id:角色id	//最长30位
        cproleid:游戏研发方唯一角色id     //最长30位 同actor_id
        subject:商品名}
     */
    d2f.pay(payData.amount, payData.orderData, function () {
        console.log('充值调起 成功');
    });

}

let hasSend = false;

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
    let type = roleInfo.datatype;
    if (type === 3) {
        d2f.reportData({
            action: "enterGame",            //进入游戏登录页时调用
            roleid: ttwOpenId,
            srvid: roleInfo.serverId,
            rolelevel: roleInfo.userRoleLevel,
            pfid: ttwAppId,
            rolename: roleInfo.userRoleName,
            power: roleInfo.gameRolePower,
            cproleid: roleInfo.userRoleId,//最长30位
            currency: 0,
        })
    } else if (type === 2) {
        d2f.reportData({
            action: "create_role", //创建角色
            roleid: ttwOpenId,
            srvid: roleInfo.serverId,
            rolelevel: roleInfo.userRoleLevel,
            cproleid: roleInfo.userRoleId,//最长30位
            pfid: ttwAppId,
            rolename: roleInfo.userRoleName,
        })
    } else if (roleInfo.datatype === 4) {
        d2f.reportData({
            action: "level_up", //创建角色
            roleid: ttwOpenId,
            srvid: roleInfo.serverId,
            rolelevel: roleInfo.userRoleLevel,
            cproleid: roleInfo.userRoleId,//最长30位
            pfid: ttwAppId,
            rolename: roleInfo.userRoleName,
        });
    } else {
        if (ttwFirst && !hasSend) {
            //用户第一次进入游戏时调用
            //（选服页 根据登录时传入的openid来做判断）
            d2f.reportData({
                action: "enterCreate",
                roleid: ttwOpenId,
                pfid: ttwAppId
            });
            hasSend = true;
        }
    }
}

function share() {
    //可以自定义分享内容，也可以不传参数，不传采用平台默认
}

function zyCallChannelLogout() {

}

