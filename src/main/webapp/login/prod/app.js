(function ($) {
    'use strict';

    $(function () {
        var $fullText = $('.admin-fullText');
        $('#admin-fullscreen').on('click', function () {
            $.AMUI.fullscreen.toggle();
        });

        $(document).on($.AMUI.fullscreen.raw.fullscreenchange, function () {
            $fullText.text($.AMUI.fullscreen.isFullscreen ? '退出全屏' : '开启全屏');
        });
    });
})(jQuery);

function setCopy(account, password) {
    let res = "账号: " + account + " 密码: " + password;
    copy.val(res);
}

/**
 * 展示主页界面
 * @param {boolean} val
 * */
function changeMainPage(val) {
    document.getElementById("myapp-main-login-form").hidden = val;
    document.getElementById("myapp-main-else-btn-tip").hidden = val;
    document.getElementById("myapp-main-else-btn1").hidden = val;
    document.getElementById("myapp-main-else-btn2").hidden = val;
    document.getElementById("myapp-main-bottom-tip").hidden = val;
}

/**
 * 展示游客一键注册界面
 * @param {boolean} val
 * */
function changeAutoRegPage(val) {
    document.getElementById("myapp-autoReg-form").hidden = val;
    document.getElementById("myapp-autoReg-else-btn").hidden = val;
    document.getElementById("myapp-autoReg-bottom-tip").hidden = val;
    document.getElementById("myapp-autoReg-else-btn1").hidden = val;
    document.getElementById("myapp-autoReg-else-btn2").hidden = val;
}

/**
 * 游客一键注册按钮
 * @param {boolean} val
 * */
function showMainPage(val) {
    changeMainPage(!val);
    changeAutoRegPage(val);

    // 隐藏下方 手机页面按钮
    document.getElementById("myapp-phone-else-btn1").hidden = true;
    document.getElementById("myapp-phone-else-btn2").hidden = true;

    DisplayAndHiddenBtn("myapp-phone-login-code-btn", false);
    document.getElementById("myapp-phone-login-change-code").hidden = true;
    document.getElementById("myapp-phone-login-change-password").hidden = true;

    document.getElementById("myapp-login-user-icon").className = "am-icon-user";


}

/*首页状态**/
function myapp_Init() {
    // 主页
    document.getElementById("myapp-main-login-form").hidden = false;
    //主页-手机登录-手机注册 账号输入框 icon
    document.getElementById("myapp-login-user-icon").className = "am-icon-user";//am-icon-phone
    // 主页-手机登录-手机密码登录 密码输入框
    document.getElementById("myapp-main-login-password-form").hidden = false;
    // 手机登录-手机验证码登录-手机注册 验证码输入框
    document.getElementById("myapp-phone-login-code-form").hidden = true;
    // 指悦网络科技游戏隐私保护协议
    document.getElementById("myapp-loign-select-protocol").hidden = false;
    // 手机登录-手机验证码登录 手机验证码
    DisplayAndHiddenBtn("myapp-phone-login-code-btn", false);

    // 主页 进入游戏按钮
    DisplayAndHiddenBtn("myapp-main-login-code-btn", true);
    // 手机登录-手机注册 手机注册按钮
    DisplayAndHiddenBtn("myapp-phone-register-btn", false);
    // 手机登录-手机注册 获取验证码按钮
    DisplayAndHiddenBtn("myapp-phone-register-code-btn", false);
    // 手机登录 进入游戏按钮
    DisplayAndHiddenBtn("myapp-login-code-btn", false);

    // 主页 复制账号密码
    document.getElementById("myapp-login-form-copy").hidden = false;
    // 手机登录-手机密码登录 切换验证码登录按钮
    document.getElementById("myapp-phone-login-change-code").hidden = true;
    // 手机登录-手机验证码登录 切换密码登录
    document.getElementById("myapp-phone-login-change-password").hidden = true;
    // 一键注册 界面
    document.getElementById("myapp-autoReg-form").hidden = true;
    // 一键注册 协议
    document.getElementById("myapp-autoReg-else-btn").hidden = true;
    // 主页分隔符
    document.getElementById("myapp-main-else-btn-tip").hidden = false;
    // 主页 功能按钮1
    document.getElementById("myapp-main-else-btn1").hidden = false;
    // 主页 功能按钮2
    document.getElementById("myapp-main-else-btn2").hidden = false;
    // 手机登录 功能按钮1
    document.getElementById("myapp-phone-else-btn1").hidden = true;
    // 手机登录 功能按钮2
    document.getElementById("myapp-phone-else-btn2").hidden = true;

    // 主页 最下方提示-客服QQ
    document.getElementById("myapp-main-bottom-tip").hidden = false;
    // 一键注册 最下方分隔符
    document.getElementById("myapp-autoReg-bottom-tip").hidden = true;
    // 一键注册 最下方 协议
    document.getElementById("myapp-autoReg-else-btn1").hidden = true;
    // 一键注册 最下方 返回
    document.getElementById("myapp-autoReg-else-btn2").hidden = true;
    // 一键注册 最下方 协议勾选按钮
    document.getElementById("myapp-autoReg-select-protocol").hidden = true;
}

/**
 * 手机登录按钮
 * @param {boolean} val
 * */
function showPhoneLoginPage() {
    document.getElementById("myapp-main-else-btn1").hidden = true;
    document.getElementById("myapp-main-else-btn2").hidden = true;

    document.getElementById("myapp-phone-else-btn1").hidden = false;
    document.getElementById("myapp-phone-else-btn2").hidden = false;

    document.getElementById("myapp-login-form-copy").hidden = true;

    document.getElementById("myapp-phone-login-change-code").hidden = false;

    document.getElementById("myapp-login-user-icon").className = "am-icon-phone";//am-icon-user

    DisplayAndHiddenBtn("myapp-main-login-code-btn", false);
    DisplayAndHiddenBtn("myapp-login-code-btn", true);
}

/**
 * 手机验证码登录按钮
 * */
function showPhoneCodeLoginPage() {
    document.getElementById("myapp-main-login-password-form").hidden = true;
    document.getElementById("myapp-phone-login-change-code").hidden = true;

    document.getElementById("myapp-phone-login-code-form").hidden = false;

    document.getElementById("myapp-phone-login-change-password").hidden = false;
    DisplayAndHiddenBtn("myapp-phone-login-code-btn", true);
}

/**
 * 手机验证码登录 -切换手机密码登录
 * */
function showPhonePasswordLoginPage() {
    document.getElementById("myapp-main-login-password-form").hidden = false;
    document.getElementById("myapp-phone-login-change-code").hidden = false;


    document.getElementById("myapp-phone-login-code-form").hidden = true;

    document.getElementById("myapp-phone-login-change-password").hidden = true;

    DisplayAndHiddenBtn("myapp-phone-login-code-btn", false);

}

/**
 * 手机登录界面-切换到手机注册界面
 * */
function showPhoneRegPage() {
    document.getElementById("myapp-main-login-password-form").hidden = false;
    document.getElementById("myapp-phone-login-code-form").hidden = false;

    document.getElementById("myapp-phone-login-change-code").hidden = true;
    document.getElementById("myapp-phone-login-change-password").hidden = true;

    DisplayAndHiddenBtn("myapp-main-login-code-btn", false);
    DisplayAndHiddenBtn("myapp-phone-register-code-btn", true);
    DisplayAndHiddenBtn("myapp-phone-register-btn", true);
    DisplayAndHiddenBtn("myapp-phone-login-code-btn", false);
    DisplayAndHiddenBtn("myapp-login-code-btn", false);
}

function DisplayAndHiddenBtn(btnId, type) {
    let currentBtn = document.getElementById(btnId);
    if (type) {
        currentBtn.style.display = "block"; //style中的display属性
    } else {
        currentBtn.style.display = "none";
    }
}

let prod = true;
let url = "/zhiyue";
if (prod) {
    url = "https://zyh5games.com/zysdk/zhiyue";
}


/**
 * 渠道账号自动注册指悦账号
 * */
function OneStepRegister() {
    let regInfo = {};
    regInfo.appId = $("#url_appId").val();
    regInfo.channelId = $("#url_channelId").val();
    regInfo.appKey = $("#url_appKey").val();
    regInfo.addParam = "巨龙战歌-官方注册";
    ZhiYueAutoReg(regInfo, function (data) {
        console.info("status = ", data.status);
        console.info("message = ", data.message);
        if (data.status) {
            let ZyUid = data.uid;
            let username = data.account;
            let password = data.password;
            let channelUid = data.channelUid;

            $("#loginname").val(username);
            $("#password").val(password);
            $("#url_channelUid").val(ZyUid);
            $("#url_uid").val(channelUid);

            setCookie("zy_account", username);
            setCookie("zy_password", password);
            setCookie("zy_channelUid", channelUid);
            myapp_Init();
            // zy_Login();
        }
    });
}

function enterGame() {
    if (mainSelect === false) {
        alert("登录失败,请同意用户协议");
        return;
    }
    let loginInfo = {};
    loginInfo.appId = $("#url_appId").val();
    loginInfo.channelId = $("#url_channelId").val();
    loginInfo.appKey = $("#url_appKey").val();
    loginInfo.username = $("#loginname").val();
    loginInfo.password = $("#password").val();

    console.log(loginInfo);

    ZhiYueLogin(loginInfo, function (callbackLoginData) {
        if (callbackLoginData.status === false) {
            alert("登录失败", callbackLoginData.message);
        } else {
            console.log("ZhiyueLogin " + callbackLoginData);

            let channelUid = callbackLoginData.uid;
            let loginUrl = callbackLoginData.loginUrl;
            let username = loginInfo.username;
            let password = loginInfo.password;

            setCookie("zy_account", loginInfo.username);
            setCookie("zy_password", loginInfo.password);
            setCookie("zy_channelUid", channelUid);

            $("#loginname").val(username);
            $("#password").val(password);
            $("#url_channelUid").val(channelUid);
            //iframe
            loadGameHtml(loginUrl);
        }
    });
}

function ZhiYueAutoReg(regInfo, callback) {
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

/**获取指悦Uid*/
function ZhiYueLogin(loginInfo, callback) {
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


function ZhiYuePhoneCode(val, type, callback) {
    let phone = $("#loginname").val();
    let appId = $("#url_appId").val();
    let url = "https://zyh5games.com/zysdk/zhiYueSms/PhoneCodeReg" + "?" + "phone=" + phone + "&appId=" + appId;
    // url = "/zhiYueSms/PhoneCodeReg" + "?" + "phone=" + phone + "&appId=" + appId;
    if (type === 1) {
        url = "https://zyh5games.com/zysdk/zhiYueSms/PhoneCodeLogin" + "?" + "phone=" + phone + "&appId=" + appId;
        // url = "/zhiYueSms/PhoneCodeLogin" + "?" + "phone=" + phone + "&appId=" + appId;
    }
    let rspObj = {};

    $.ajax({
        url: url,
        type: "get",
        dataType: "json",
        async: false,
        success: function (result) {
            console.log(result);
            if (result.hasOwnProperty('status')) {
                callback(result.status);
            } else {
                callback(false);
            }
            alert(result.message);
        }
    });
}

function zy_phone_Login() {
    if (mainSelect === false) {
        alert("登录失败,请同意用户协议");
        return;
    }

    let loginInfo = {};
    loginInfo.appId = $("#url_appId").val();
    loginInfo.channelId = $("#url_channelId").val();
    loginInfo.appKey = $("#url_appKey").val();
    loginInfo.phone = $("#loginname").val();
    loginInfo.password = $("#password").val();
    loginInfo.code = $("#code").val();

    console.info(loginInfo);
    phone_Login(loginInfo, function (callbackLoginData) {
        if (callbackLoginData.status === false) {
            alert("登录失败", callbackLoginData.message);
        } else {
            console.log("ZhiyueLogin " + callbackLoginData);

            let channelUid = callbackLoginData.uid;
            let loginUrl = callbackLoginData.loginUrl;

            $("#url_channelId").val(callbackLoginData.channelUid);


            setCookie("zy_account", loginInfo.phone);
            setCookie("zy_password", loginInfo.password);
            setCookie("zy_channelUid", channelUid);

            //iframe
            loadGameHtml(loginUrl);
        }
    });
}

function phone_Login(loginInfo, callback) {
    console.info("autoReg = " + loginInfo);
    let rspObj = {};
    let url = "https://zyh5games.com/zysdk/zhiYueSms/PhoneLogin";

    $.ajax({
        url: url,
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

function phoneReg() {
    phone_Register(function (data) {
        console.info("status = ", data.status);
        console.info("message = ", data.message);
        if (data.status) {
            let ZyUid = data.uid;
            let username = data.account;
            let password = data.password;
            let channelUid = data.channelUid;

            $("#loginname").val(username);
            $("#password").val(password);
            $("#url_channelUid").val(channelUid);
            $("#url_uid").val(ZyUid);

            setCookie("zy_account", username);
            setCookie("zy_password", password);
            setCookie("zy_channelUid", channelUid);
            myapp_Init();
            // zy_Login();
        } else {
            alert(data.message);
        }
    })
}

function phone_Register(callback) {
    let regInfo = {};
    regInfo.appId = $("#url_appId").val();
    regInfo.channelId = $("#url_channelId").val();
    regInfo.appKey = $("#url_appKey").val();
    regInfo.phone = $("#loginname").val();
    regInfo.password = $("#password").val();
    regInfo.code = $("#code").val();

    console.info("autoReg = " + regInfo);

    let url = "https://zyh5games.com/zysdk/zhiYueSms/PhoneReg";
    let rspObj = {};
    $.ajax({
        url: url,
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
                callback(rspObj);
            } else {
                rspObj.status = false;
                rspObj.message = "通信失败";
                callback(rspObj);
            }
        },
        error: function () {
            rspObj.message = "通信失败";
            rspObj.status = false;
            callback(rspObj);
        }
    });
}

function getCookies(cname) {
    let name = cname + "=";
    let cookies = document.cookie;
    if (cookies === undefined || cookies == null || cookies === "") {
        return "";
    }
    let ca = cookies.split(';');

    for (let i = 0; i < ca.length; i++) {
        let c = ca[i].trim();
        if (c.indexOf(name) === 0) return c.substring(name.length, c.length);
    }
    return "";
}

function delCookie(name) {
    let exp = new Date();
    exp.setTime(exp.getTime() - 1);
    let cval = getCookies(name);
    if (cval != null)
        document.cookie = name + "=" + cval + ";expires=" + exp.toGMTString();
}

function setCookie(name, value) {
    let Days = 30;
    let d = new Date();
    d.setTime(d.getTime() + (Days * 24 * 60 * 60 * 1000));
    let expires = "expires=" + d.toGMTString();
    document.cookie = name + "=" + value + "; " + expires;
}

function loadGameHtml(payUrl) {
    //苹果浏览器
    let isSafari = navigator.vendor && navigator.vendor.indexOf('Apple') > -1 &&
        navigator.userAgent &&
        navigator.userAgent.indexOf('CriOS') === -1 &&
        navigator.userAgent.indexOf('FxiOS') === -1;
    if (isSafari) {
        newWin(payUrl, "zyOpenWin");
    } else {
        // alert("其他");
        window.open(payUrl);
    }
}

let openWin = function (payUrl) {
    //打开一个新窗口
    let winRef = window.open('', "_blank");
    //假装获取请求支付参数
    $.ajax({
        type: 'get',
        url: "https://zyh5games.com/zysdk/test/hello",
        success: function () {
            //设置新窗口的跳转地址
            // winRef.location.href = "www.baidu.com";
            window.location.href = payUrl;
        }
    })
};

function newWin(url, id) {
    // window.location.href = url;
    let a = document.createElement('zyOpenWin');
    a.setAttribute('href', url);
    a.setAttribute('target', '_blank');
    a.setAttribute('id', id);
    // 防止反复添加
    if (!document.getElementById(id)) document.body.appendChild(a);
    a.onclick = openWin(url);

    a.click();
}
