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

function zyCallChannelPay(order) {
    console.log("zyCallChannelPay = " + JSON.stringify(order));

    let trade = JSON.parse(order.zhiyueOrder.channelOrder);
    let data = trade.data;
    let channelUid = getQueryString(window.location.href, "ChannelUid");

    loadZyPayHtml(
        data.orderId,
        data.body,
        data.subject,
        data.totalAmount,
        data.productId,
        data.passBackParams,
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
    let param = "?v=" + new Date().getTime()
        + "&orderId=" + orderId
        + "&body=" + body
        + "&subject=" + subject
        + "&totalAmount=" + totalAmount
        + "&productId=" + productId
        + "&passBackParams=" + passBackParams
        + "&appId=" + ZhiYueSDK.GameId + "&spId=" + spId;

    let payUrl = "https://zyh5games.com/pay/static/pay.html" + encodeURI(param);

    openPayHtml(payUrl);
}

let openWin = function (payUrl) {
    // alert("苹果打开支付2");
    //打开一个新窗口
    let winRef = window.open('', "_blank");
    //假装获取请求支付参数
    $.ajax({
        type: 'get',
        url: "https://zyh5games.com/zysdk/test/hello",
        success: function () {
            //设置新窗口的跳转地址
            // winRef.location.href = "www.baidu.com";
            window.location.href = payUrl;
        }
    })
};

function newWin(url, id) {
    // window.location.href = url;
    let a = document.createElement('zyOpenWin');
    a.setAttribute('href', url);
    a.setAttribute('target', '_blank');
    a.setAttribute('id', id);
    // 防止反复添加
    if (!document.getElementById(id)) document.body.appendChild(a);
    a.onclick = openWin(url);

    a.click();
}

function zyCallUploadRole(roleInfo) {
    console.log(roleInfo);

}


function openPayHtml(payUrl) {
    //苹果浏览器
    let isSafari = navigator.vendor && navigator.vendor.indexOf('Apple') > -1 &&
        navigator.userAgent &&
        navigator.userAgent.indexOf('CriOS') === -1 &&
        navigator.userAgent.indexOf('FxiOS') === -1;
    if (isSafari) {
        // alert("苹果");
        newWin(payUrl, "zyOpenWin");
    } else {
        // alert("其他");
        window.open(payUrl);
    }
}

