//初始化SDK
let ylSdk;
let hasInit = false;
let YinLiGameKey = "";

//加载KUKU-JSSDK，并完成设置项
function initYLSDK() {
    ylSdk = window.KUKU_JS_SDK;
    //初始化SDK
    ylSdk.config(
        YinLiGameKey,//KUKU平台分配游戏方的游戏标识
        function (args) {//微信分享的状态回调函数
            let result = document.getElementById("result");
            result.innerText = JSON.stringify(args)
        },
        function () {
        }
    );
    console.log('sdk加载完成', ylSdk);
    hasInit = true;
}

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
    let config = getQueryString(params, "zhiyue_channel_config");
    console.log(config);
    let YinLiConfig = JSON.parse(config);
    YinLiGameKey = YinLiConfig.GameKey;
}

function zyCallChannelPay(order) {
    if (!hasInit) {
        initYLSDK();
    }

    console.log("zyCallChannelPay=" + JSON.stringify(order));
    let trade = JSON.parse(order.zhiyueOrder.channelOrder);
    let payData = JSON.parse(trade.data);
    console.log("zyCallChannelPay payData= " + trade.data);

    /*
        API002:游戏调用此函数完成支付页面调起
        参数说明
        productCost:1000,必填，道具支付金额，单位分
        productId:'PRO_1234',必填，游戏道具ID
        productName:'100元宝',必填，游戏道具名称
        gameUid:'123456',可选，游戏方用户ID
        gameOrderNo:'ORD_123456',可选，游戏方订单ID
        ext1:'123456',可选，扩展字段1，支付回调游戏方时原样返回
        ext2:'7890',可选，扩展字段2，支付回调游戏方时原样返回
    */
    console.log("zyCallChannelPay 开始调起 引力支付");
    ylSdk.pay(
        payData.productCost,
        payData.productId,
        payData.productName,
        payData.gameUid,
        payData.gameOrderNo,
        payData.ext1,
        payData.ext2);
}

function zyCallUploadRole(roleInfo) {
    console.log(roleInfo);
    if (!hasInit) {
        initYLSDK();
    }
}

//Toast接口API-005【可选】
function toast(msg) {
    ylSdk.toast(3, msg || '我是一个弹窗测试消息')
}

//分享接口API-003【可选】
function share() {
    ylSdk.share(
        'KUKU娱乐测试分享', //分享标题
        '我是KUKU娱乐测试分享的详情描述，你可以多写点哦', //分享描述
        'https://baidu.com/', //分享出去打开的链接
        'https://imgweb.kuku168.cn/1a2465e642ed4d698f3e7ca7c47d0c52', //分享的图标
        false, //是否关闭分享引导
        'https://m.kuku168.cn/Simulate/simulateGameCallBack' //获取分享链接点击回调，null则不回调
    )
}
