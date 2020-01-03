/**
 * yy sdk
 * @author sonh minghua
 * */
const Appid = "MCS";
const Appkey = "F43083865E22418A92E8E653FB682884F3A14ADF822D9A6CE3FF338C21408D56";
const Channel = "YY_YY_Debug";
const url ="http://11wan.yy.com/games/?appId=MCS&channel=YY_YY_Debug";

let loginCallbackFunction = null;
let CallbackPayFun = null;
let InitCallback = null;
let ZySDK = {
    productCode: null,
    productKey: null,
    channelCode: 0,
    debug: true,
    init: function (sproductCode, sproductKey, isDebug, callback) {
        this.productCode = sproductCode;
        this.productKey = sproductKey;
        let params = {};
        params.productCode = this.productCode;
        params.productKey = this.productKey;
        params.debug = this.debug;
        doInit(params, function () {
            callback();
        });
    },
    login: function (callback) {
        loginCallbackFunction = callback;
        autoLogin();
    },

    //注销
    logout: function (callback) {

    },

};
$(function () {
    ZySDK.init(null, null, true, null);
    ZySDK.login(null);
});

function doInit() {
    init_YY();
}

function autoLogin() {
    sdkLogin_YY();
}

function init_YY() {
    window.WanGameH5sdk.init();
    window.WanGameH5sdk.config({
        share: {    // 邀请参数配置
            success: function () {
                // 邀请成功
                console.info("success");
            },
            cancel: function () {
                // 取消邀请
                console.info("cancel");
            }
        },
        focus: {   // 关注状态配置
            success: function () {
                // 异步通知关注成功
                console.info("success async");
            }
        }
    });

}

function sdkLogin_YY() {
    window.WanGameH5sdk.login({
        success: function (data) {
            // 登录成功回调
            console.log(data);    // data: {"sid": "foo"}
        },
        fail: function (data) {
            // 登录失败回调
            console.log(data.status); // 失败状态码
        }
    });
}