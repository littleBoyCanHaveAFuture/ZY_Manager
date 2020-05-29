/*! kuku.js 2020-05-28 */
var sdk = window.KUKU_JS_SDK || {};
sdk.version = "1.0.0 build 20180601.1000", sdk.host = "kuku168.cn", sdk.secure = !1, sdk.path = "/api", sdk.url = null, sdk.gameKey = null, sdk.token = null, sdk.userInfo = null, sdk.gameInfo = null, sdk.wxFollwoStatus = 0, sdk.onGetUserInfoCallBack = null, sdk.onGetGZHFollowStatusCallback = null, sdk.init = function () {
    sdk.token = sdk.getURLVar("token"), sdk.secure ? sdk.url = "https://" + sdk.host + sdk.path : sdk.url = "http://" + sdk.host + sdk.path
}, sdk.config = function (a, b, c) {
    sdk.gameKey = a, window.addEventListener("message", function (a) {
        if (a.source == window.parent) switch (a.data.cmd) {
            case"onShare":
                console.log("提醒：微信已经不支持获取分享的结果状态，此返回的状态仅仅为调用KUKUSDK分享状态"), "function" == typeof b && b(a.data.args);
                break;
            case"onFollowGZH":
                "function" == typeof c && c(a.data.args);
                break;
            case"onGetUserInfo":
                "function" == typeof sdk.onGetUserInfoCallBack && sdk.onGetUserInfoCallBack(a.data.args);
                break;
            case"onGetGZHFollowStatus":
                "function" == typeof sdk.onGetGZHFollowStatusCallback && sdk.onGetGZHFollowStatusCallback(a.data.args)
        }
    }, !1), window.parent.postMessage({cmd: "config", args: {gameKey: a}}, "*")
}, sdk.logout = function () {
    window.parent.postMessage({cmd: "logout"}, "*")
}, sdk.pay = function (a, b, c, d, e, f, g) {
    return sdk.isAllowPay ? void window.parent.postMessage({
        cmd: "pay",
        args: {
            gameKey: sdk.gameKey,
            productCost: a,
            productId: b,
            productName: c,
            gameUid: d,
            gameOrderNo: e,
            ext1: f,
            ext2: g
        }
    }, "*") : void window.parent.postMessage({cmd: "toast", args: {second: 2, text: "当前游戏不支持充值"}}, "*")
}, sdk.share = function (a, b, c, d, e, f) {
    window.parent.postMessage({
        cmd: "share",
        args: {gameKey: sdk.gameKey, title: a, desc: b, link: c, imgUrl: d, callbackUrl: f || "", closeMask: e || !1}
    }, "*")
}, sdk.followGZH = function () {
    window.parent.postMessage({cmd: "followGZH"}, "*")
}, sdk.toast = function (a, b) {
    window.parent.postMessage({cmd: "toast", args: {text: b, second: a}}, "*")
}, sdk.getGZHFollowStatus = function (a) {
    sdk.onGetGZHFollowStatusCallback = a, window.parent.postMessage({
        cmd: "GZHFollowStatus",
        args: {gameKey: sdk.gameKey}
    }, "*")
}, sdk.getUserInfo = function (a) {
    sdk.onGetUserInfoCallBack = a, window.parent.postMessage({cmd: "userInfo", args: {gameKey: sdk.gameKey}}, "*")
}, sdk.getURLVar = function (a) {
    var b = new RegExp("(^|&)" + a + "=([^&]*)(&|$)", "i"), c = window.location.search.substr(1).match(b);
    return null != c ? decodeURIComponent(c[2]) : null
}, sdk.isAllowPay = function () {
    return -1 == navigator.userAgent.indexOf("Nopay")
}, sdk.isQQ = function () {
    return "qq" == navigator.userAgent.toLowerCase().match(/\bqq\b/i)
}, sdk.isWeixin = function () {
    return "micromessenger" == navigator.userAgent.toLowerCase().match(/MicroMessenger/i)
}, sdk.isWeibo = function () {
    return "weibo" == navigator.userAgent.toLowerCase().match(/weibo/i)
}, sdk.isAndroid = function () {
    return -1 < navigator.userAgent.indexOf("Android") || -1 < navigator.userAgent.indexOf("Linux")
}, sdk.isiOS = function () {
    return !!navigator.userAgent.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/)
}, sdk.isPCWeixin = function () {
    return "windowswechat" == navigator.userAgent.toLowerCase().match(/WindowsWechat/i)
}, sdk.isMobile = function () {
    for (var a = navigator.userAgent.toLowerCase(), b = ["android", "iphone", "symbianos", "windows phone", "ipad", "ipod"], c = 0; c < b.length; c++) if (0 < a.indexOf(b[c])) return !0;
    return !1
}, sdk.isYLAPP = function () {
    var a = navigator.userAgent.toLowerCase();
    return -1 != a.indexOf("ylapp")
}, sdk.init(), window.KUKU_JS_SDK = sdk;
