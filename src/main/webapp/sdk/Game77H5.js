function getQueryString(url, key) {
    let list = url.split("&");
    for (let i in list) {
        let arr = list[i].split("=");
        if (arr[0] === key) {
            return arr[1];
        }
    }
}

let loginParams = {};
let hasInit = false;

function game77SdkInit() {
    if (!hasInit) {
        console.info("game77SdkInit SbPulSdk = " + SbPulSdk == null || SbPulSdk === undefined);
        SbPulSdk.init(loginParams, function (channelSdk) {
            //cp放置一些渠道的特殊逻辑， 比如初始化渠道的分享配置等等
            hasInit = true;
            console.info("game77SdkInit success");
            //分享（微信初始化）
            channelSdk.shareConfig(function(){
                alert('7724分享成功回调cp');
            }, {'cp_p1' : 'cp自定义参数', 'cp_p2': '自定义参数会在登录回调的ext里面'});

            //分享
            $("#share_btn").click(function(){
                channelSdk.share(function(){
                    alert('7724分享成功回调cp');
                }, {'cp_p1' : 'cp自定义参数', 'cp_p2': '自定义参数会在登录回调的ext里面'});
            });

            //关注
            $("#7724follow").click(function(){
                //判断用户是否关注
                channelSdk.isSubscribe(function(subStatus){
                    if(subStatus === 1){
                        alert('你已经关注过啦');
                        return false;
                    }
                    //弹出关注
                    channelSdk.follow();
                });
            });
        });
    }

}


function zyCallChannelInit(params) {
    loginParams.qqesuid = getQueryString(params, 'qqesuid');
    loginParams.channelid = getQueryString(params, 'channelid');
    loginParams.channeluid = getQueryString(params, 'channeluid');
    loginParams.qqesnickname = getQueryString(params, 'qqesnickname');
    loginParams.qqesavatar = getQueryString(params, 'qqesavatar');
    loginParams.cpgameid = getQueryString(params, 'cpgameid');
    loginParams.ext = getQueryString(params, 'ext');
    loginParams.qqestimestamp = getQueryString(params, 'qqestimestamp');
    loginParams.sign = getQueryString(params, 'sign');
    game77SdkInit();
}


function zyCallChannelLogin(data) {
    game77SdkInit();
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
    let payData = JSON.parse(trade.data)

    console.log("zyCallChannelPay payData= " + trade.data);

    SbPulSdk.pay(payData);
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

}

function share() {
    //可以自定义分享内容，也可以不传参数，不传采用平台默认
}

function zyCallChannelLogout() {

}

