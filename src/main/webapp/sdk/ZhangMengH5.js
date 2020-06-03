let soeasyUserInfo = {};
let hasInit = false;
let soeasyUrl = "https://cn.soeasysdk.com/soeasysr/zm_engine_v2.js?" + new Date().getTime();
//sdk加载完毕回调
window.zmInitSucc = function () {
    console.log("cp start work");
    //TODO必须在这里或者该方法调用之后进行sdk调用
    ZmSdk.getInstance().init(function (data) {
        //初始化成功之后调用其他sdk能力如获取用户信息、支付、角色上报、设置分享信息、分享等...
        //示例：
        if (data.retcode === "0") {
            let userinfo = ZmSdk.getInstance().getUserInfo();
            soeasyUserInfo.time = userinfo.userdata.t;
            soeasyUserInfo.uid = userinfo.userdata.uid;
            soeasyUserInfo.name = userinfo.userdata.name;
            soeasyUserInfo.sign = userinfo.userdata.sign;
            console.log("zm init success");
            console.log(userinfo);
            console.log(soeasyUserInfo);
            ZhiYueSDK.login(function (callbackData) {
                if (callbackData.status) {
                    JWJHsdk.login(callbackData.data);
                } else {
                    console.log('GameDemo:ZhiYueSDK登录失败:' + callbackData.message);
                }
            });
        } else if (data.retcode === "1") {
            //初始化失败处理
            console.log("zm init fail");
        }
    });
};
zyLoadAsyncScript(soeasyUrl, function () {
    console.log("load soeasyUrl");
});

/**
 * 特殊渠道 增加参数
 * @param requestUri
 * @param {string}      requestUri.wlh          原本地址
 * @param {string}      requestUri.channelWLH   此函数赋值
 * */
function zyExtraParam(requestUri) {
    if (!soeasyUserInfo.hasOwnProperty("uid")) {
        console.log("no uid----------------------------------------");
        return;
    }
    let channelWLH = requestUri.wlh;
    let time = getQueryString(channelWLH, "zysyTime");
    if (time === null || time === undefined) {
        channelWLH += "&zysyTime=" + soeasyUserInfo.time;
    } else {
        //替换 todo
    }
    let uid = getQueryString(channelWLH, "zysyUid");
    if (uid === null || uid === undefined) {
        channelWLH += "&zysyUid=" + soeasyUserInfo.uid;
    } else {
        //替换 todo
    }
    let name = getQueryString(channelWLH, "zysyName");
    if (name === null || name === undefined) {
        channelWLH += "&zysyName=" + soeasyUserInfo.name;
    } else {
        //替换 todo
    }
    let sign = getQueryString(channelWLH, "zysySign");
    if (sign === null || sign === undefined) {
        channelWLH += "&zysySign=" + soeasyUserInfo.sign;
    } else {
        //替换 todo
    }
    requestUri.channelWLH = channelWLH;
    console.log("channelWLH" + channelWLH);
}

function getQueryString(url, key) {
    let list = url.split("&");
    for (let i in list) {
        let arr = list[i].split("=");
        if (arr[0] === key) {
            return arr[1];
        }
    }
    return null;
}

function zyCallChannelInit(params) {
    let gameId = 0;
    if (typeof (params) == 'object') {
        window.PRODUCT_CODE = params.productCode;
        gameId = params.gameId;
    } else {
        window.PRODUCT_CODE = getQueryString(params, 'productCode');
    }
    soeasyUserInfo.appId = ZhiYueSDK.GameId;
    soeasyUserInfo.channelId = ZhiYueSDK.channelId;


}

/**
 *  soeasy 登录,模拟 soeasy 正常登录回调，给uid
 */
function zyCallChannelLogin(data) {

}

function zyCallChannelPay(order) {
    console.log("zyCallChannelPay=" + JSON.stringify(order));

    let trade = JSON.parse(order.zhiyueOrder.channelOrder);
    let payData = JSON.parse(trade.data)

    console.log("zyCallChannelPay payData= " + trade.data);
    ZmSdk.getInstance().pay(payData, function (data) {
        console.log("调起支付：" + data.msg);
        if (data.retcode === 0) {
            console.log("调起支付成功：" + data.msg);
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
//ZmSdk.getInstance().reportRoleDetail(roleInfoJSON);
//     roleInfoJSON:{"
//     datatype":"必填1.选择服务器2.创建角色3.进入游戏4.等级提升5.退出游戏",
//     "serverid":"服务器id",
//     "servername":"服务器名称",
//     "roleid":"角色id",
//     "rolename":"游戏角色昵称",
//     "rolelevel":"角色等级",
//     "fightvalue":"战力",(以上字段为必填字段)
//     "moneynum":"游戏币",
//     "partyname":"工会",
//     "rolecreatetime":"角色创建时间",
//     "rolelevelmtime":"角色升级时间",
//     "gender":"角色性别,可传'男'、'女'",
//     "professionid":"职业id",
//     "profession":"职业名称”,
//     "vip":"vip等级",
//     "partyid":"所在帮派id",
//     "partyname":"所在帮派名称",
//     "partyroleid":"帮派称号id",
//     "partyrolename":"帮派称号名称",
//     "friendlist":[{"roleid":"关系角色id","intimacy":"亲密度","nexusid":"关系id,可填数字1:夫妻2:结拜3:情侣4:师徒5:仇人6:其它"
//     }

    let roleInfoJSON = {};
    roleInfoJSON.datatype = roleInfo.datatype;
    roleInfoJSON.serverid = roleInfo.serverId;
    roleInfoJSON.servername = roleInfo.serverName;
    roleInfoJSON.roleid = roleInfo.userRoleId;
    roleInfoJSON.rolename = roleInfo.userRoleName;
    roleInfoJSON.rolelevel = roleInfo.userRoleLevel;
    roleInfoJSON.fightvalue = roleInfo.gameRolePower;
    roleInfoJSON.moneynum = roleInfo.userRoleBalance;
    roleInfoJSON.partyname = roleInfo.partyName;
    roleInfoJSON.rolecreatetime = roleInfo.roleCreateTime;
    roleInfoJSON.rolelevelmtime = roleInfo.roleCreateTime;
    roleInfoJSON.gender = roleInfo.gameRoleGender;//存疑
    roleInfoJSON.professionid = roleInfo.professionId;
    roleInfoJSON.profession = roleInfo.profession;
    roleInfoJSON.vip = roleInfo.vipLevel;
    roleInfoJSON.partyid = roleInfo.partyRoleId;
    roleInfoJSON.partyname = roleInfo.partyName;
    roleInfoJSON.partyroleid = roleInfo.partyRoleId;
    roleInfoJSON.partyrolename = roleInfo.partyRoleName;
    roleInfoJSON.friendlist = roleInfo.friendlist;
    roleInfoJSON.friendlist = "";
    ZmSdk.getInstance().reportRoleStatus(roleInfoJSON);
}

function share() {
    //可以自定义分享内容，也可以不传参数，不传采用平台默认
}

function zyCallChannelLogout() {

}

