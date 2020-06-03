let game1758SDK = window.HLMY_SDK;
let authData = {};
let hasInit = false;

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
    authData.appKey = getQueryString(params, 'appKey');
    authData.hlmy_gw = getQueryString(params, 'hlmy_gw');
    authData.userToken = getQueryString(params, 'userToken');
}

function init1758Sdk() {
    if (!hasInit) {
        if (window.HLMY_SDK !== undefined) {
            game1758SDK = window.HLMY_SDK;
            game1758SDK.init({
                "gid": authData.uid,        //通过"用户验证"接口获取到的1758平台gid
                "appKey": authData.appKey,     //游戏的appkey
                "hlmy_gw": authData.hlmy_gw    //1758平台的自定义参数，CP通过授权回调地址后的参数获得
            });
            hasInit = true;
        }

    } else {
        console.log("init none");
    }
}

function zyCallChannelLogin(data) {
    console.log(data);
    authData.uid = data.uid;
    init1758Sdk();
}


function zyCallChannelPay(order) {
    console.log("zyCallChannelPay=" + JSON.stringify(order));
    init1758Sdk();
    let trade = JSON.parse(order.zhiyueOrder.channelOrder);
    let payData = JSON.parse(trade.data)

    console.log("zyCallChannelPay payData= " + trade.data);

    let reqpayData = {
        "paySafecode": payData.paySafeCode,//通过“统一下单接口”返回的支付安全码
        "callback": function (data) { //callback 不能保证一定会回调
            console.log(data);// data为object，格式为{status:1}
        }
    };
    HLMY_SDK.pay(reqpayData);
}

let isNewRole = false;

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
    init1758Sdk();
    console.log(roleInfo);
    if (roleInfo.datatype === 2) {
        isNewRole = true;
    }
    if (roleInfo.datatype === 3) {
        game1758SDK.roleInfo({
            "serverId": roleInfo.serverId,//区服id
            "serverName": roleInfo.serverName,//区服名称
            "isNewRole": isNewRole,//是否是新创建的角色
            "roleId": roleInfo.userRoleId,//角色id
            "roleName": roleInfo.userRoleName,//角色名称
            "roleLevel": roleInfo.userRoleLevel,//角色级别
            "roleCoins": roleInfo.userRoleBalance,//角色当前的财富值
            "roleCreateTime": roleInfo.roleCreateTime,//角色创建时间
            "gameRolePower": roleInfo.gameRolePower//角色战力值
        });
    }

}

function share() {
    //可以自定义分享内容，也可以不传参数，不传采用平台默认
}

function zyCallChannelLogout() {

}

