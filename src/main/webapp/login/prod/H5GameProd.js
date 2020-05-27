$(function () {
});
let url = "/zhiyue";

// url = "/zysdk/zhiyue"
/**
 * 渠道账号自动注册指悦账号
 * @param regInfo
 * @param regInfo.appId
 * @param regInfo.channelId
 * @param regInfo.appKey
 * @param regInfo.addParam
 * @param return
 * */
function zy_Register() {
    let regInfo = {};
    regInfo.appId = appId;
    regInfo.channelId = channelId;
    regInfo.appKey = appKey;
    regInfo.addParam = "巨龙战歌-官方注册";
    ZhiyueAutoReg(regInfo, function (data) {
        console.info("status = ", data.status);
        console.info("message = ", data.message);
        if (data.status) {
            let ZyUid = data.uid;
            let username = data.account;
            let password = data.password;
            let channelUid = data.channelUid;

            $("#uid").val(ZyUid);
            $("#username").val(username);
            $("#password").val(password);
            $("#channelUid").val(channelUid);

            setCookie("zy_account", username);
            setCookie("zy_password", password);
            setCookie("zy_channelUid", channelUid);
            showLoginPage();
            zy_Login();
        }
    });
}

function ZhiyueAutoReg(regInfo, callback) {
    console.info("autoReg = " + regInfo);
    let rspObj = {};
    $.ajax({
        url: url + "/autoReg",
        type: "post",
        contentType: "text/plain; charset=utf-8",
        data: JSON.stringify(regInfo),
        dataType: "json",
        async: false,
        success: function (result) {
            console.info(result);
            if (result.hasOwnProperty('status')) {
                if (result.status === true) {
                    rspObj.status = true;
                    rspObj.message = result.message;

                    rspObj.uid = result.zhiyueUid;
                    rspObj.account = result.account;
                    rspObj.password = result.password;
                    rspObj.channelUid = result.channelUid;
                } else {
                    rspObj.status = false;
                    rspObj.message = result.message;
                }
            } else {
                rspObj.status = false;
                rspObj.message = "通信失败";
            }
            callback(rspObj);
        },
        error: function () {
            rspObj.message = "通信失败";
            rspObj.status = false;
            callback(rspObj);
        }
    });
}

function zy_Login() {
    if (is_select === false) {
        alert("登录失败,请同意用户协议");
        return;
    }
    let loginInfo = {};
    loginInfo.channelId = channelId;
    loginInfo.appId = appId;
    loginInfo.appKey = appKey;
    loginInfo.username = $("#username").val();
    loginInfo.password = $("#password").val();

    console.info(loginInfo);

    ZhiyueLogin(loginInfo, function (callbackLoginData) {
        if (callbackLoginData.status === false) {
            alert("登录失败", callbackLoginData.message);
        } else {
            console.log("ZhiyueLogin " + callbackLoginData);

            let channelUid = callbackLoginData.channelUid;
            let loginUrl = callbackLoginData.loginUrl;

            $("#channelUid").val(callbackLoginData.channelUid);


            setCookie("zy_account", loginInfo.username);
            setCookie("zy_password", loginInfo.password);
            setCookie("zy_channelUid", channelUid);

            //iframe
            window.location.replace(loginUrl);
        }
    });
}

/**获取指悦Uid*/
function ZhiyueLogin(loginInfo, callback) {
    console.info("autoReg = " + loginInfo);
    let rspObj = {};

    $.ajax({
        url: url + "/autoLogin",
        type: "post",
        contentType: "text/plain; charset=utf-8",
        data: JSON.stringify(loginInfo),
        dataType: "json",
        async: false,
        success: function (result) {
            console.info(result);
            if (result.hasOwnProperty('status')) {
                if (result.status === true) {
                    rspObj.status = true;
                    rspObj.message = result.message;

                    rspObj.uid = result.channelUid;
                    rspObj.loginUrl = result.loginUrl;
                } else {
                    rspObj.status = false;
                    rspObj.message = result.message;
                }
            } else {
                rspObj.status = false;
                rspObj.message = "通信失败";
            }
            callback(rspObj);
        },
        error: function () {
            rspObj.message = "通信失败";
            rspObj.status = false;
            callback(rspObj);
        }
    });

}
