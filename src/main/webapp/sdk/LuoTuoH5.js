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
        window.PRODUCT_CODE = params.GameId;
    } else {
        window.PRODUCT_CODE = getQueryString(params, 'GameId');

    }
}

function zyCallChannelPay(order) {
    console.log("zyCallChannelPay = " + JSON.stringify(order));

    let trade = JSON.parse(order.zhiyueOrder.channelOrder);
    let payData = trade.data;
    let domain = trade.domain;
    console.log("trade[" + typeof trade + "]=" + trade);
    console.log("payData[" + typeof payData + "]=" + payData);
    payData = JSON.parse(payData);
    // parent.postMessage(trade.data, 'https://' + trade.domain);
    parent.postMessage(payData, '*');
}


function zyCallUploadRole(roleInfo) {
    console.log(roleInfo);
    let data = {};
    data.appId = roleInfo.GameId;
    data.channelId = roleInfo.channelId;
    data.userToken = zySaveChannelParams;
    data.area = roleInfo.serverId;
    data.role_name = roleInfo.userRoleName;
    data.new_role = roleInfo.datatype === 2 ? 1 : 0;
    data.rank = roleInfo.userRoleLevel;
    data.money = roleInfo.userRoleBalance;
    data.uid = roleInfo.uid;
    let gameUrl = "https://gameluotuo.com/index.php?g=Home&m=GameOauth&a=roles";

    getZyChannelSignature(data, function (result) {
        console.info(result);
        if (!result.status) return;

        let gameData = result.data;
        jQuery.get(gameUrl, gameData, function (s) {
            console.log(s);
        });
    });
}

function getZyChannelSignature(Request, Callback) {
    $.ajax({
        type: "POST",
        url: ZhiYue_domain + "/ajaxGetSignature",
        contentType: "text/plain; charset=utf-8",
        data: JSON.stringify(Request),
        dataType: "json",
        async: true,
        success: function (result) {
            console.log("getChannelSignature success" + result);
            Callback(result);
        }, error: function (result) {
            console.log("getChannelSignature error" + result);
        }
    });
}
