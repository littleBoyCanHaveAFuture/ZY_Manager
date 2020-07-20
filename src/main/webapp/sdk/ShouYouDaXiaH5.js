let sydxGameId = "";

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
    // 参数分割
    let config = getQueryString(params, "zhiyue_channel_config");
    console.log(config);
    config = JSON.parse(config);
    sydxGameId = config.gameId;
}

function zyCallChannelPay(order) {
    console.log("zyCallChannelPay=" + JSON.stringify(order));
    let trade = JSON.parse(order.zhiyueOrder.channelOrder);
    let payData = JSON.parse(trade.data);
    console.log("zyCallChannelPay payData= " + trade.data);

    xgGame.h5paySdk(payData, function (data) {
        console.log(data);
    });
}

function zyCallUploadRole(roleInfo) {
    console.log(roleInfo);
    /*
        参数 参数说明
        user_id 运营方登录时传递的 user_id
        game_appid 游戏编号----运营方为游戏分配的唯一编号
        server_id 区服 id
        server_name 区服名称
        role_id 角色 id
        role_name 角色名
        level 角色等级
        sign 按照上方签名机制进行签名
    */
    let jsonData = {};
    jsonData.appId = roleInfo.GameId;
    jsonData.channelId = roleInfo.channelId;
    jsonData.user_id = roleInfo.uid;
    jsonData.game_appid = sydxGameId;
    jsonData.server_id = roleInfo.serverId;
    jsonData.server_name = roleInfo.serverName;
    jsonData.role_id = roleInfo.userRoleId;
    jsonData.role_name = roleInfo.userRoleName;
    jsonData.level = roleInfo.userRoleLevel;
    getZyChannelSignature(jsonData, function (data) {
        xgGame.jointCreateRole(data)
    });
}


function zyShare() {
    xgGame.shareSdk({
        game_appid: "游戏 appid",
        title: "自定义标题",
        desc: "自定义内容"
    }, function (data) {//分享结果 status 1 分享成功 0 分享失败
        console.log(data);
        alert(data.status);
    })
}

/**
 * @param Request
 * @param {function} Callback
 * */
function getZyChannelSignature(Request, Callback) {
    $.ajax({
        type: "POST",
        url: ZhiYue_domain + "/ajaxGetSignature",
        contentType: "text/plain; charset=utf-8",
        data: JSON.stringify(Request),
        dataType: "json",
        async: true,
        success: function (result) {
            console.log(result);
            if (result.status) {
                console.log("getChannelSignature success" + result);
                Callback(result.data);
            } else {
                console.log("getChannelSignature error" + result);
            }

        }, error: function (result) {
            console.log("getChannelSignature error" + result);
        }
    });
}
