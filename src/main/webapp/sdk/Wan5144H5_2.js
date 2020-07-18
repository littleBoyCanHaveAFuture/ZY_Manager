var hasInit = false;
var initParams = {
    pid: "",
    time: "",
    sign: ""
};
let wan5144_userInfo = "";

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
    let pid = 0;
    if (typeof (params) == 'object') {
        pid = params.gameId;
    } else {
        pid = getQueryString(params, 'pid');
    }
    let config = getQueryString(params, "zhiyue_channel_token");
    if (config === "") {
        console.info("sdk配置错误");
    } else {
        let param = config.split("|");
        initParams.pid = pid;
        initParams.time = param[0];
        initParams.sign = param[1]
        initSdk();
    }
}

function initSdk() {
    if (hasInit === false) {
        if ("undefined" === typeof play) {
            console.info("play undefined");

        } else {
            /**
             * {
             * code:“错误编码”，
             * msg:“提示信息”，
             * data:{
             * uid:“xxx”,   //我方玩家唯一标识
             * uname:“xxx”, //我方玩家账号
             * gid:“xxx”,   //我方游戏唯一标识
             * sid:“xxx”,   //我方游戏服唯一标识
             * }   
             */
            console.info("wan5144_userInfo init");
            play.sdk.init(initParams, function (result) {
                result = JSON.parse(result);
                console.info("5144 sdk init " + result.msg);
                console.info(result.data);
                if (result.code === "1" || result.code === 1) {
                    if (wan5144_userInfo.length === 0) {
                        wan5144_userInfo = result.data;
                        console.info("wan5144_userInfo = " + JSON.stringify(wan5144_userInfo));
                    }
                    hasInit = true;
                }
            });
        }
    }
}

/**
 * 充值-上报数据接口
 * @param   {Object}      order                     充值信息
 * @param   {string}      order.gameKey             QuickSDK后台自动分配的游戏参数
 * @param   {number}      order.channelId           QuickSDK后台自动分配的渠道参数
 * @param   {string}      order.channelUid          渠道UID
 * @param   {string}      order.username            渠道username
 * @param   {string}      order.userRoleId          游戏内角色ID
 * @param   {string}      order.userRoleName        游戏角色
 * @param   {string}      order.serverId            角色所在区服ID
 * @param   {string}      order.userServer          角色所在区服
 * @param   {number}      order.userLevel           角色等级
 * @param   {number}      order.cpOrderNo           游戏内的订单,SDK服务器通知中会回传
 * @param   {string}      order.amount              购买金额（元）
 * @param   {number}      order.count               购买商品个数
 * @param   {number}      order.quantifier          购买商品单位，如，个
 * @param   {string}      order.subject             道具名称
 * @param   {number}      order.desc                道具描述
 * @param   {number}      order.callbackUrl         Cp服务器通知地址
 * @param   {number}      order.extrasParams        透传参数,服务器通知中原样回传
 * @param   {number}      order.goodsId             商品ID
 * */
function zyCallChannelPay(order) {
    /**
     * 名称           参数名    类型    提供方    必填 说明
     * 商户ID         pid        string    我    是    贵方在我方唯一标识
     * 游戏ID         gid        string    我    是    贵方游戏在我方唯一标识
     * 游戏服ID       sid        string    我    是    贵方游戏服在我方唯一标识
     * 玩家id         uid        string    我    是    我方玩家唯一标识
     * 玩家账号       uname       string    我    是    我方玩家账号
     * 充值金额         money     string    贵    是    人民币，单位元，可为空，为空时玩家可以自己选择金额
     * 自定义参数        other    string    贵    是    贵方自定义参数值，如贵方订单号，我方会在充值回调里，传回给贵方，可为空
     * 游戏服id        gsid      string    贵    否    贵方服D，若服id，传空即可
     * 游戏服          gsname    string    贵    否    贵方服，若无服名，传空即可
     * 角色ID         roleid     string    贵    否    贵方角ID，若无角色名，传roleid即可
     * 角色名          role      string    贵    否    贵方角色名，若无角色名，传role即可
     * 商品ID         goodId     string    贵    否    商品ID，没有的话，传空
     * 商品名         goodname    string    贵    否    商品名称，没有的话，传空
     *
     */
    play.sdk.pay({
        pid: initParams.pid,
        gid: wan5144_userInfo.gid,
        sid: wan5144_userInfo.sid,
        uid: wan5144_userInfo.uid,
        uname: wan5144_userInfo.uname,
        money: order.amount,
        other: order.extrasParams,
        gsid: order.serverId,
        gsname: order.userServer,
        roleid: order.userRoleId,
        goodId: order.goodsId,
        goodname: order.subject,
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
 * @param   {function}           callback                    回调函数
 * */
function zyCallUploadRole(roleInfo) {
    initSdk();
    console.log(roleInfo);
    /**
     * 名称          参数名         类型       提供方    必填    说明
     * 商户ID         pid           string     我    是       贵方在我方唯一标识
     * 游戏ID         gid           string     我    是       贵方游戏在我方唯一标识
     * 游戏服ID        sid            string    我    是      贵方游戏服在我方唯一标识
     * 角色名称        rolename        string    贵    是     角色名称
     * 角色等级        level           Int       贵    否     角色等级
     * 职业            professional   string    贵    否      职业
     * 转生次数        conversion      string    贵    否     转生次数
     * 角色编号        rolenumber      String    贵    否    角色在贵方唯一标识
     * 贵方游戏服      gsid            String    贵    否    贵方游戏服唯一标识
     * 战斗力         fighting        String    贵    否    角色战斗力
     * 经验           experience      String    贵    否    角色经验值
     * 性别           sex             String    贵    否    性别
     * 金币           coins           String    贵    否    角色金币余额
     * 角色json       roledata        String    贵    否    其他角色信息 json字符串格式
     * 创建时间         createtime    Long      贵    否    角色创建时间,时间戳格式
     * 加密           sign            string   贵    是    加密规则:key我方提供md5(pid#key#time)
     * 时间戳         time             Long          是    时间戳(10位)
     * */
    console.log(roleInfo);
    let requestData = {};
    requestData.appId = roleInfo.GameId;
    requestData.channelId = roleInfo.channelId;
    requestData.pid = initParams.pid;

    getZyChannelSignature(requestData, function (data) {
        let roledata = {
            pid: initParams.pid,
            gid: wan5144_userInfo.gid,
            sid: wan5144_userInfo.sid,
            rolename: roleInfo.userRoleName,
            level: roleInfo.userRoleLevel,
            professional: roleInfo.profession,
            conversion: 0,
            rolenumber: roleInfo.userRoleId,
            gsid: roleInfo.serverId,
            fighting: roleInfo.gameRolePower,
            experience: 0,
            sex: roleInfo.gameRoleGender,
            coins: roleInfo.userRoleBalance,
            roledata: "",
            createtime: roleInfo.roleCreateTime,
            sign: data.sign,
            time: data.time,
        };
        play.sdk.role(roledata);
    });


}

function share() {
    //可以自定义分享内容，也可以不传参数，不传采用平台默认
    sdk.showShare('分享给好友的标题', '分享给好友的描述', '分享给好友时的图标');
}

function authentication() {  //唤起实名认证窗口
    sdk.authentication({show: 'true'});
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
