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
    if (typeof (params) == 'object') {
        window.PRODUCT_CODE = params.productCode;
    } else {
        window.PRODUCT_CODE = getQueryString(params, 'productCode');
    }
}

function callChannelPay(order) {
    console.log("callChannelPay=" + JSON.stringify(order));

    let trade = JSON.parse(order.zhiyueOrder.channelOrder);
    let payData = trade.data;
    let domain = trade.domain;
    console.log("trade[" + typeof trade + "]=" + trade);
    console.log("payData[" + typeof payData + "]=" + payData);
    // parent.postMessage(trade.data, 'https://' + trade.domain);
    parent.postMessage(payData, '*');
}


function callUploadRole(roleInfo) {
    console.log(roleInfo);

    // let role = JSON.parse(roleInfo);
    // let data = {};
    // data.userToken = userData.userToken;
    // data.area = role.serverId;
    // data.role_name = role.userRoleName;
    // data.new_role = role.isCreateRole ? 1 : 0;
    // data.rank = role.userRoleLevel;
    // data.money = role.userRoleBalance;
    // data.productCode = window.PRODUCT_CODE;
    // data.channelCode = 8;
    //
    // var gameUrl = "https://gameluotuo.com/index.php?g=Home&m=GameOauth&a=roles";
    //
    // getChannelSignature(data, function (result) {
    //     gameData = JSON.parse(result.content);
    //     gameData.sign = result.signature;
    //     jQuery.get(gameUrl, gameData, function (s) {
    //         console.log(s);
    //     });
    // });
}


function getChannelSignature(Request, Callback) {
    jQuery.ajax({
        type: "POST",
        url: ZhiYue_domain + "/ajaxGetSignature",
        data: Request,
        dataType: "json",
        success: function (result) {
            Callback(result.data);
        }
    });
}
