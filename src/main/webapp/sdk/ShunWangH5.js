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
    if (typeof (params) == 'object') {
        // window.PRODUCT_CODE = params.GameId;
    } else {
        // window.PRODUCT_CODE = getQueryString(params, 'GameId');
    }
}

function zyCallChannelPay(order) {
    console.log("zyCallChannelPay = " + JSON.stringify(order));

    let trade = JSON.parse(order.zhiyueOrder.channelOrder);
    let payData = trade.data;
    let domain = trade.domain;

    H5Pay().init({
        gameId: trade.gameId,
        data: trade.data,
        sign: trade.sign
    }, swPayCallBack);
}

/**
 * 展示二维码
 * todo
 * cp 应该提供一个二维码弹窗样式及关闭按钮
 * */
function swPayCallBack(json) {
    console.info(json.response.msg);
    console.info(json.response.qrcode);
    if (json.response.code === 0) {
        $("#id_qrcode").attr("src", "data:image/jpeg;base64," + json.response.qrcode);
    }
}

function zyCallUploadRole(roleInfo) {
    console.log(roleInfo);
    let data = {};

}
