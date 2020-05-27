function getQueryString(url, key) {
    let list = url.split("&");
    for (let i in list) {
        let arr = list[i].split("=");
        if (arr[0] === key) {
            return arr[1];
        }
    }
}

function callChannelInit(params) {
    let gameId = 0;
    if (typeof (params) == 'object') {
        window.PRODUCT_CODE = params.productCode;
    } else {
        window.PRODUCT_CODE = getQueryString(params, 'productCode');
    }

}

function callChannelPay(order) {
    console.log("callChannelPay=" + JSON.stringify(order));

    let trade = JSON.parse(order.zhiyueOrder.channelOrder);
    let data = trade.data;
    loadZyPayHtml(data.orderId, data.body, data.subject,
        data.totalAmount, data.productId, data.passBackParams,
        trade.spId);

}

/**
 * @param orderId           订单id
 * @param body              商品详细描述
 * @param subject           商品名称
 * @param totalAmount       商品金额：元
 * @param productId         商品id
 * @param passBackParams    透传参数
 * */
function loadZyPayHtml(orderId, body, subject, totalAmount, productId, passBackParams, spId) {
    let param = "?orderId=" + orderId
        + "&body=" + body
        + "&subject=" + subject
        + "&totalAmount=" + totalAmount
        + "&productId=" + productId
        + "&passBackParams=" + passBackParams
        + "&appId=" + ZhiYueSDK.GameId + "&spId=" + spId;

    let payUrl = "http://zy.hysdgame.cn/pay/static/pay.html" + encodeURI(param);
    payUrl = "http://zyh5games.com/pay/static/pay.html" + encodeURI(param);
    //苹果浏览器
    let isSafari = navigator.vendor && navigator.vendor.indexOf('Apple') > -1 &&
        navigator.userAgent &&
        navigator.userAgent.indexOf('CriOS') === -1 &&
        navigator.userAgent.indexOf('FxiOS') === -1;
    if (isSafari) {
        // alert("苹果");
        openWin(payUrl);
    } else {
        // alert("其他");
        window.open(payUrl);
    }
}

let openWin = function (payUrl) {
    //打开一个新窗口
    let winRef = window.open('', "_blank");
    //假装获取请求支付参数
    $.ajax({
        type: 'get',
        url: "https://zyh5games.com/zysdk/test/hello",
        success: function () {
            //设置新窗口的跳转地址
            // winRef.location.href = "www.baidu.com";
            winRef.location.href = payUrl;
        }
    })
};

function callUploadRole(roleInfo) {
    console.log(roleInfo);

}


