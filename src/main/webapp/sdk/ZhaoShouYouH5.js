function getQueryString(url, key) {
    let list = url.split("&");
    for (let i in list) {
        let arr = list[i].split("=");
        if (arr[0] === key) {
            return arr[1];
        }
    }
}

let ZhaoShouYouParam = {};

function zyCallChannelInit(params) {
    ZhaoShouYouParam.game_id = getQueryString(params, 'game_id');
    ZhaoShouYouParam.user_id = getQueryString(params, 'user_id');
}

/**
 *  quick登录,模拟quick正常登录回调，给QuickSDK赋值
 */
function zyCallChannelLogin(data) {
    // if (!hasInit) {
    //     let productCode = quickConfig.ProductCode;
    //     let productKey = quickConfig.ProductKey;
    //     QuickSDK.init(productCode, productKey, true, function () {
    //         console.log("quick init success");
    //         hasInit = true;
    //     })
    // }
    // if (data.hasOwnProperty("quickData")) {
    //     console.info("zyCallChannelLogin quick ---->doLoginCallback");
    //     doLoginCallback(data.quickData);
    // } else {
    //     console.info("zyCallChannelLogin quick ---->fail");
    //     data.userData.uid = "";
    // }
}

function zyCallChannelPay(order) {
    console.log("zyCallChannelPay=" + JSON.stringify(order));

    let trade = JSON.parse(order.zhiyueOrder.channelOrder);
    let payData = trade.data

    console.log("zyCallChannelPay payData= " + trade.data);
    let payUrl = "https://api.sy12306.com/game/h5gamepay_9";
    payUrl += "?" + payData;
    $.ajax({
        url: payUrl,
        type: 'get',
        success: function (res) {
            console.log(res);
        },
        error: function (e) {
            console.log(e)
        }
    })

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
     * role.role_id         String      是    角色id
     * role.role_name       String      是    角色名
     * role.server_id       Int         是    区服id
     * role.server_name     String      是    区服名
     * role.event           Int         是    角色事件1--角色登录 2--角色创建3--角色升级 4--角色下线
     * role.role_level      Int         是    角色等级
     * role.role_vip        Int         是    角色vip等级
     * role.combat_num      String      是    战力  使用字符串表示数字，最大9223372036854775807，系统有合法判断
     * format               String      是    固定为 jsonp
     * extra                String      否    附加信息 可以为空
     * app_id               Int         是    就是登陆时传入的game_id参数
     * user_id              Int         是    就是登陆时传入的user_id参数
     */
    let datatype = roleInfo.datatype;
    if (roleInfo.datatype === 3) {
        datatype = 1;
    } else if (roleInfo.datatype === 2) {
        datatype = 2;
    } else if (roleInfo.datatype === 4) {
        datatype = 3;
    } else if (roleInfo.datatype === 5) {
        datatype = 4;
    } else {
        return;
    }
    let role = {
        role_id: roleInfo.userRoleId,
        role_name: roleInfo.userRoleName,
        server_id: roleInfo.serverId,
        server_name: roleInfo.serverName,
        event: datatype,
        role_level: roleInfo.userRoleLevel,
        role_vip: roleInfo.vipLevel,
        combat_num: roleInfo.gameRolePower
    };

    let data = {
        role: role,
        format: 'jsonp',
        extra: '',
        app_id: ZhaoShouYouParam.game_id,
        user_id: ZhaoShouYouParam.user_id
    };

    $.ajax({
        url: 'https://api.sy12306.com/v8/user/uprole',
        type: 'post',
        data: data,
        dataType: 'jsonp',
        success: function (res) {
            console.log(res);
            if (res.code === 200) {
                console.log(res.msg);
            }
        },
        error: function (e) {
            console.log(e)
        }
    })

}

function share() {
    //可以自定义分享内容，也可以不传参数，不传采用平台默认
}

function zyCallChannelLogout() {

}

