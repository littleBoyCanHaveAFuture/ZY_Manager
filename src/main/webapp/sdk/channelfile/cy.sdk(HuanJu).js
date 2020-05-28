/**
 * Created by stupid-boy on 2017/8/14.
 */
var sdk = window.CY_GAME_SDK || {};
sdk.clientVersion = "2.0 build 201705161940";
sdk.gameId = 0;
sdk.init = function () {

};
//加载配置信息
sdk.config = function (params) {
    sdk.gameId = params.gameId;

    window.addEventListener("message", function (event) {
        switch (event.data.operation) {
            case"onShare":
            {
                params.share.success(event.data.param);
                break
            }
            case"onPay":
            {
                params.pay.success(event.data.param);
                break
            }
            case"onRole":
            {
                params.uploadGameRole.success(event.data.param);
                break
            }
        }
    }, false);
    if(sdk.gameId==219){
        sendMsgToParent({operation: "config",param:{title:'',desc:'',imgUrl:''}});
    }

    //上报分享
    //sendMsgToParent({operation: "config",param:{title:params.share.title,desc:params.share.desc,imgUrl:params.share.imgUrl}});
};

//唤起支付
sdk.pay = function (order) {
    sendMsgToParent({operation: "pay",param:order});
};
//上报角色信息
sdk.uploadGameRole = function (param) {
    sendMsgToParent({operation: "uploadGameRole",param:param});
};

// //开始游戏
// sdk.startGame = function () {
//     sendMsgToParent({operation: "startGame"});
// };
//
// //选择区服
// sdk.selectServer = function () {
//     sendMsgToParent({operation: "selectServer"});
// };

//唤起分享引导页
sdk.showShare = function () {
    sendMsgToParent({operation: "share"});
};

//通知登录被顶
sdk.repeatLogin= function () {
    sendMsgToParent({operation: "repeatLogin"});
};

sdk.init();
window.CY_GAME_SDK = sdk;

function sendMsgToParent(post) {
    window.parent.postMessage(post, '*')
}
