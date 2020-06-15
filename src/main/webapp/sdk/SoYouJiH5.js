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

/**
 *  quick登录,模拟quick正常登录回调，给QuickSDK赋值
 */
function zyCallChannelLogin(data) {

}

function zyCallChannelPay(order) {
    console.log("zyCallChannelPay=" + JSON.stringify(order));

    let trade = JSON.parse(order.zhiyueOrder.channelOrder);
    let payData = trade.data;

    console.log("zyCallChannelPay payData= " + payData);
    let url = "https://api.sooyooj.com/index/pay/141?" + payData;
    window.parent.postMessage(
        {
            event: 'pay',
            url: url
        }, '*')
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
     * uid          用户在汇米网络的用户ID            # length <= 20
     * nonce        随机字符串，可为空                # length <= 64
     * time         操作发生时的UNIX时间戳，精确到秒   # length = 10
     * serverid     游戏区服ID                      # length <= 20
     * server_name  游戏区服名                      # length <= 16
     * roleid       用户在游戏中的角色ID             # length <= 20
     * role_name    游戏角色名                     # length <= 16
     * level        用户游戏角色等级                # length <= 10
     * sign         签名，用于请求合法性校验
     */
    let data = {};
    data.appId = roleInfo.GameId;
    data.channelId = roleInfo.channelId;
    data.uid = roleInfo.uid;
    data.nonce = "";
    data.time = "";
    data.serverid = roleInfo.serverId;
    data.server_name = roleInfo.serverName;
    data.roleid = roleInfo.userRoleId;
    data.role_name = roleInfo.userRoleName;
    data.level = roleInfo.userRoleLevel;

    getZyChannelSignature(data, function (data) {
        let url = "https://api.sooyooj.com/index/role/141";
        url += "?" + data.param;
        $.ajax({
            url: url,
            type: "get",
            success: function (result) {
                console.log(result);
            },
        });
    })
}

/**
 * @param Request
 * @param {function} Callback
 * */
function getZyChannelSignature(Request, Callback) {
    $.ajax({
        type: "POST",
        url: ZhiYue_domain + "/ajaxGetSignature",
        contentType: "text/plain; charset=utf-8",
        data: JSON.stringify(Request),
        dataType: "json",
        async: true,
        success: function (result) {
            console.log(result);
            if (result.status) {
                console.log("getChannelSignature success" + result);
                Callback(result.data);
            } else {
                console.log("getChannelSignature error" + result);
            }

        }, error: function (result) {
            console.log("getChannelSignature error" + result);
        }
    });
}

