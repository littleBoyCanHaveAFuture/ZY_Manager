$(function () {
    let appId = 14;
    let GameKey = "u6d3047qbltix34a9l0g2bvs5e8q82ol";
    let channelId = 0;

    let newURl = updateQueryStringParameter(window.location.href, 'GameId', appId);
    newURl = updateQueryStringParameter(newURl, 'GameKey', GameKey);
    newURl = updateQueryStringParameter(newURl, 'channelId', channelId);
    //向当前url添加参数，没有历史记录
    window.history.replaceState({
        path: newURl
    }, '', newURl);

    let zy_account = getCookies("zy_account");
    let zy_password = getCookies("zy_password");
    let zy_channelUid = getCookies("zy_channelUid");

    console.info("zy_account=" + zy_account);
    console.info("zy_password=" + zy_password);
    console.info("zy_channelUid=" + zy_channelUid);

    if (channelId === 0) {
        // 指悦官方渠道-无账号自动注册
        if (zy_account === "" || zy_password === "" || zy_channelUid === "") {
            let channelUid = getZyNextUid();
            let regInfo = {};
            regInfo.appId = appId;
            regInfo.channelId = channelId;
            regInfo.channelUid = channelUid;
            regInfo.channelUname = "";
            regInfo.channelUnick = "";
            regInfo.phone = "";
            regInfo.deviceCode = "PC";
            autoReg(regInfo);
        } else {
            newURl = updateQueryStringParameter(newURl, 'zy_channelUid', zy_channelUid);
            newURl = updateQueryStringParameter(newURl, 'zy_account', zy_account);
            newURl = updateQueryStringParameter(newURl, 'zy_password', zy_password);
            //向当前url添加参数，没有历史记录
            window.history.replaceState({
                path: newURl
            }, '', newURl);

        }
    } else {
        let regInfo = {};
        regInfo.appId = appId;
        regInfo.channelId = channelId;
        regInfo.channelUid = getRndInteger(8, 10);
        regInfo.channelUname = "";
        regInfo.channelUnick = "";
        regInfo.phone = "";
        regInfo.deviceCode = "PC";
        autoReg(regInfo);
    }


    let res = "账号: " + zy_account + " 密码: " + zy_password;
    $('#copy').val(res);

    sdkInit(appId, GameKey, channelId);
});


// 自动注册账号
function autoReg(regInfo) {
    sdk_AutoReg(regInfo, function (callbackData) {
        if (callbackData.state === false) {
            console.log(callbackData.message);
            alert(callbackData.message);
        } else {
            console.info(callbackData.message);
            console.info(JSON.stringify(callbackData));

            let ZyUid = callbackData.uid;
            let username = callbackData.account;
            let password = callbackData.password;
            let channelUid = callbackData.channelUid;

            // $("#uid").val(ZyUid);
            // $("#username").val(username);
            // $("#password").val(password);
            // $("#channelUid").val(channelUid);
            //
            // $("#reg_username").val("");
            // $("#reg_password").val("");
            //
            let res = "账号: " + username + " 密码: " + password + " Uid: " + channelUid;
            $('#copy').val(res);
            setCookie("zy_account", username);
            setCookie("zy_password", password);
            setCookie("zy_channelUid", channelUid);
            // showLoginPage();
        }
    })
}

/**
 * 渠道账号自动注册指悦账号
 * @param regInfo
 * @param regInfo.appId
 * @param regInfo.channelId
 * @param regInfo.channelUid
 * @param regInfo.channelUname
 * @param regInfo.channelUnick
 * @param regInfo.phone
 * @param regInfo.deviceCode
 * @param callback
 * */
function sdk_AutoReg(regInfo, callback) {
    console.log(regInfo);
    let rspObject = {};
    let mustKey = ['appId', 'channelId', 'channelUid'];
    for (let keyIndex of mustKey) {
        if (!regInfo.hasOwnProperty(keyIndex)) {
            rspObject.message = "参数:" + mustKey[keyIndex] + " 为空";
            rspObject.state = false;
            callback(rspObject);
            return;
        }
    }

    $.ajax({
        url: "/webGame2/autoReg",
        type: "post",
        contentType: "text/plain; charset=utf-8",
        data: JSON.stringify(regInfo),
        dataType: "json",
        async: false,
        success: function (result) {
            console.info(result);
            if (result.hasOwnProperty('state')) {
                if (result.state === true) {
                    rspObject.state = true;
                    rspObject.message = result.message;

                    rspObject.uid = result.accountId;
                    rspObject.account = result.account;
                    rspObject.password = result.password;
                    rspObject.channelUid = result.channelUid;
                } else {
                    rspObject.state = false;
                    rspObject.message = result.message;
                }
            } else {
                rspObject.state = false;
                rspObject.message = "通信失败";
            }
            callback(rspObject);
        },
        error: function () {
            rspObject.message = "通信失败";
            rspObject.state = false;
            callback(rspObject);
        }
    });
}

function getRndInteger(min, max) {
    return Math.floor(Math.random() * (max - min)) + min;
}

function resetLogin() {
    delCookie("zy_account");
    delCookie("zy_password");
    delCookie("zy_channelUid");
}

/**指悦账号注册 获取下一位uid*/
function getZyNextUid() {
    let id = "0";
    $.ajax({
        url: "/webGame2/getId",
        type: "get",
        async: false,
        success: function (result) {
            console.info("nextId=" + result);
            id = result;
        }, error: function () {

        }
    });
    return id;
}


function updateQueryStringParameter(uri, key, value) {
    if (value === "" || value === undefined) {
        return uri;
    }
    let re = new RegExp("([?&])" + key + "=.*?(&|$)", "i");
    let separator = uri.indexOf('?') !== -1 ? "&" : "?";
    if (uri.match(re)) {
        return uri.replace(re, '$1' + key + "=" + value + '$2');
    } else {
        return uri + separator + key + "=" + value;
    }
}

function checkUserInfo(info) {
    let param = "token=" + info.data.token +
        "&gameKey=" + ZySDK.GameKey +
        "&uid=" + info.data.uid +
        "&channelId=" + info.data.channelId;
    $.ajax({
        url: "/webGame2/checkUserInfo?" + param,
        type: "get",
        async: false,
        success: function (result) {
            console.info("checkUserInfo=" + result);
            //验证成功 进入游戏
        }, error: function (result) {
            console.log("checkUserInfo=" + result);
        }
    });
}
