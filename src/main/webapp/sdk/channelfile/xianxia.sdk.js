/**
 * Created by stupid-boy on 2017/8/14.
 */
var sdk = window.XIANXIA_GAME_SDK || {};
sdk.clientVersion = "1.0 build 20171129";
sdk.gameId = 0;
sdk.debug = true;
sdk.init = function () {

};
//加载配置信息
sdk.config = function (params) {
    sdk.gameId = params.gameId;
    if(params.debug){
        sdk.debug = true;
    }

    window.addEventListener("message", function (event) {
        switch (event.data.operation) {
            case"xianxia_share_success":
            {
                params.share.success(event.data.param);
                break
            }
            case"xianxia_pay_success":
            {
                params.pay.success(event.data.param);
                break
            }
        }
    }, false);
    sdk.sendMsgToParent({operation: "xianxia_config",param:{gameId:params.gameId}});
};
sdk.role = function (role) {

    if(sdk.debug){
        sdk.log("role start");
        if (!role.roleId) throw "缺少参数:roleId";
        if (!role.roleLevel) throw "缺少参数:roleLevel";
        if (!role.serverId) throw "缺少参数:serverId";
        if (!role.serverName) throw "缺少参数:serverName";
    }
    sdk.sendMsgToParent({operation: "xianxia_role",param:role});
}   ;
//唤起支付
sdk.pay = function (order) {
    if(sdk.debug){
        sdk.log("pay start");
        sdk.log(order);
    }

    if (!order.cpOrderId) throw "缺少参数:cpOrderId";
    if (!order.gameId) throw "缺少参数:gameId";
    if (!order.goodsId) throw "缺少参数:goodsId";
    if (!order.goodsName) throw "缺少参数:goodsName";
    if (!order.money) throw "缺少参数:money";
    if (!order.role) throw "缺少参数:role";
    if (!order.server) throw "缺少参数:server";
    if (!order.sign) throw "缺少参数:sign";
    if (!order.time) throw "缺少参数:time";

    sdk.sendMsgToParent({operation: "xianxia_pay",param:order});
};

//唤起分享
sdk.showShare = function (title,desc,imgUrl) {
    var shareInfo = {title:title,desc:desc,imgUrl:imgUrl};
    if(sdk.debug){
        sdk.log("share start");
        sdk.log(shareInfo);
    }

    sdk.sendMsgToParent({operation: "xianxia_share",param:shareInfo});
};

//唤起实名认证框
sdk.authentication = function () {
    var authInfo = { show:'true'};
    if(sdk.debug){
        sdk.log("authentication start");
        sdk.log(authInfo);
    }
    sdk.sendMsgToParent({ operation: "xianxia_authentication", param: authInfo });
};
sdk.log = function (info) {
    if( typeof info == 'object'){
        console.log(info);
    }else{
        console.log("%c"+info,"color:#41C8CC;font-size:22px;");
    }
};
sdk.sendMsgToParent = function (post) {
    /**
     * @desc 发送消息
     *@param message 要发送的信息（字符串和对象都可以）
     * @param targetOrigin 你要发送信息的目标域名
     * @param bool  transfer 可选参数
     * */
    window.parent.postMessage(post, '*')
};

sdk.init();
window.XIANXIA_GAME_SDK = sdk;

