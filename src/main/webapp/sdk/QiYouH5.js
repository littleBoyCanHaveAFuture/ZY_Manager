let qiyouSDK = window.DSSDK;
let hasInit = false;
let qiyouUserInfo = {};

function getQueryString(url, key) {
    let list = url.split("&");
    for (let i in list) {
        let arr = list[i].split("=");
        if (arr[0] === key) {
            return arr[1];
        }
    }
}

function initQiYouSDK() {
    console.log("initQiYouSDK start");
    console.log(hasInit);
    console.log(window.DSSDK === null);
    console.log(window.DSSDK === undefined);
    let ok = true;
    if (hasInit) {
        ok = false;
    }
    if (window.DSSDK === null) {
        ok = false;
    }
    if (window.DSSDK === undefined) {
        ok = false;
    }
    console.log("initQiYouSDK end");
    if (ok) {
        /**
         * code         状态          0 - 登录／注册成功|-1 - 登录／注册失败
         * message      消息          当code=-1时返回失败的原因
         * data         帐号信息      当code=0时同时返回一个js对象
         * data={
         *      user_id:用户ID
         *      account:帐号名
         *      token:用户的token
         *      }
         * 请使用user_id作为我方用户的唯一ID，account仅作为显示使用。
         * token用来做二次校验用，由研发服务器向我方服务器发起，
         * 通过token校验后，玩家登录才算成功，校验接口详见下方接口2.2
         */
        console.log("initQiYouSDK gogogo");
        window.DSSDK.login(function (data) {
            console.info(data);

            if (data.code === -1) {
                console.error("奇游SDK 登录失败 = " + data.message);
                return;
            } else if (data.code === 0) {
                let userInfo = data.data;
                qiyouUserInfo.user_id = userInfo.user_id;
                qiyouUserInfo.account = userInfo.account;
                qiyouUserInfo.token = userInfo.token;
                hasInit = true;
                console.info("login succecss");
            }
        })
    } else {
        console.log("fail initQiYouSDK");
    }

}

function zyCallChannelInit(params) {
    initQiYouSDK();
    if (!hasInit && window.DSSDK !== null && window.DSSDK !== undefined) {
        window.DSSDK.logLoadingFinish();
    }
}

function zyExtraParam(requestUri) {
    initQiYouSDK();
    if (!qiyouUserInfo.hasOwnProperty("user_id")) {
        console.log("no user_id----------------------------------------");
        return;
    }
    let channelWLH = requestUri.wlh;
    let user_id = getQueryString(channelWLH, "user_id");
    if (user_id === null || user_id === undefined) {
        channelWLH += "&zyqyUserId=" + qiyouUserInfo.user_id;
    }
    let account = getQueryString(channelWLH, "account");
    if (account === null || account === undefined) {
        channelWLH += "&zyqyAccount=" + qiyouUserInfo.account;
    }
    let token = getQueryString(channelWLH, "token");
    if (token === null || token === undefined) {
        channelWLH += "&zyqyToken=" + qiyouUserInfo.token;
    }
    requestUri.channelWLH = channelWLH;
    console.log("channelWLH = " + channelWLH);
}

function zyCallChannelLogout() {
    window.DSSDK.logout(function (data) {
        console.log("logout code = " + data.code + " = " + data.message);
    })
}

/**
 *  quick登录,模拟quick正常登录回调，给QuickSDK赋值
 */
function zyCallChannelLogin(data) {
    console.log(data);
    console.log("login");

    initQiYouSDK();
    if (!data.hasOwnProperty("uid") && qiyouUserInfo.hasOwnProperty("user_id")) {
        ZhiYueSDK.login(function (callbackData) {
            if (callbackData.status) {
                JWJHsdk.login(callbackData.data);
            } else {
                console.log('GameDemo:ZhiYueSDK登录失败:' + callbackData.message);
            }
        });
    }
    console.log("login");
}

function zyCallChannelPay(order) {
    console.log("zyCallChannelPay=" + JSON.stringify(order));

    let trade = JSON.parse(order.zhiyueOrder.channelOrder);
    let payData = JSON.parse(trade.data)

    console.log("zyCallChannelPay payData= " + trade.data);

    window.DSSDK.pay(payData, function (rs) {
        if (rs.code === 1) {
            console.log("奇游支付 支付成功");
        }
        if (rs.code === -1) {
            console.log("奇游支付 支付失败");
        }
        if (rs.code === -2) {
            console.log("奇游支付 支付取消");
        }
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
    console.log(roleInfo);
    if (roleInfo.datatype === 2) {
        window.DSSDK.logCreateRole(roleInfo.serverId, roleInfo.serverName, roleInfo.userRoleId,
            roleInfo.userRoleName, roleInfo.userRoleLevel);
    } else if (roleInfo.datatype === 3) {
        window.DSSDK.logEnterGame(roleInfo.serverId, roleInfo.serverName, roleInfo.userRoleId,
            roleInfo.userRoleName, roleInfo.userRoleLevel);
    } else if (roleInfo.datatype === 4) {
        window.DSSDK.logRoleUpLevel(roleInfo.serverId, roleInfo.serverName, roleInfo.userRoleId,
            roleInfo.userRoleName, roleInfo.userRoleLevel);
    }

}

function share() {
    //可以自定义分享内容，也可以不传参数，不传采用平台默认
}
