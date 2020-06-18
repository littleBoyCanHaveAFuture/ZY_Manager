var sdk = window.XIANXIA_GAME_SDK;
var hasInit = false;
var initParams = {
    gameId: "",
    debug: true,//设置为true,开启调试模式
    share: {
        success: function () {/*分享好友成功回调*/
            //alert("game tell success");
        }
    },
    pay: {
        success: function () {// 支付成功回调方法（仅针对于 当前页面的支付方式有效）
        }
    }
};

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
    let gameId = 0;
    if (typeof (params) == 'object') {
        gameId = params.gameId;
    } else {
        gameId = getQueryString(params, 'gameId');
    }
    initParams.gameId = gameId;

}

function zyCallChannelPay(order) {
    if (!hasInit) {
        sdk.config(initParams);//初始化
        hasInit = true;
    }

    console.log("zyCallChannelPay = " + JSON.stringify(order));
    let trade = JSON.parse(order.zhiyueOrder.channelOrder);
    let payData = JSON.parse(trade.data)
    console.log("zyCallChannelPay payData= " + trade.data);

    sdk.pay({
        uid: payData.uid,
        gameId: payData.gameId,
        time: payData.time,
        server: payData.server,
        role: payData.role,
        goodsId: payData.goodsId,
        goodsName: payData.goodsName,
        money: payData.money,
        cpOrderId: payData.cpOrderId,
        ext: payData.ext,
        sign: payData.sign,//签名见后面加密实例
        signType: payData.signType
    });
}


function zyCallUploadRole(roleInfo) {
    console.log(roleInfo);
    if (!hasInit) {
        sdk.config(initParams);//初始化
        hasInit = true;
    }
}

function share() {
    //可以自定义分享内容，也可以不传参数，不传采用平台默认
    sdk.showShare('分享给好友的标题', '分享给好友的描述', '分享给好友时的图标');
};

function authentication() {  //唤起实名认证窗口
    sdk.authentication({show: 'true'});
}


//上传类型填数字
function role() {//角色升级的时候上报角色等级，用于统计用户的活跃度
    sdk.role({
        roleEvent: "", //上传类型，1.online 2 create 3.levelup 4.offline 5. pay 6.delete
        roleId: "",
        roleName: "",
        serverId: "",
        serverName: "",
        roleLevel: ""
    });
}
