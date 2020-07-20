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
    console.log("zyCallChannelPay=" + JSON.stringify(order));
    let trade = JSON.parse(order.zhiyueOrder.channelOrder);
    let payData = JSON.parse(trade.data);
    console.log("zyCallChannelPay payData= " + trade.data);

    ziXiaToPay.GetToPayUrl(payData);
}

function zyCallUploadRole(roleInfo) {
    console.log(roleInfo);
}
