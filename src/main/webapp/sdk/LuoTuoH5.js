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
    // parent.postMessage(trade.data, 'https://' + trade.domain);
    parent.postMessage(payData, '*');
}


function zyCallUploadRole(roleInfo) {
    console.log(roleInfo);
    let data = {};
    data.userToken = zySaveChannelParams;
    data.area = roleInfo.serverId;
    data.role_name = roleInfo.userRoleName;
    data.new_role = roleInfo.datatype === 2 ? 1 : 0;
    data.rank = roleInfo.userRoleLevel;
    data.money = roleInfo.userRoleBalance;
    data.appId = window.PRODUCT_CODE;
    data.channelId = 8;

    let gameUrl = "https://gameluotuo.com/index.php?g=Home&m=GameOauth&a=roles";

    getChannelSignature(data, function (result) {
        if (!result.status) return;

        let gameData = JSON.parse(result.data.content);
        gameData.sign = result.data.sign;
        jQuery.get(gameUrl, gameData, function (s) {
            console.log(s);
        });
    });
}

function getChannelSignature(Request, Callback) {
    jQuery.ajax({
        type: "POST",
        url: ZhiYue_domain + "/ajaxGetSignature",
        data: JSON.stringify(Request),
        dataType: "json",
        success: function (result) {
            Callback(result.data);
        }
    });
}
