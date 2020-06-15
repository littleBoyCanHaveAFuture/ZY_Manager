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

function myappInit() {
    document.getElementById("myapp-main-login-form").hidden = true;
    document.getElementById("myapp-main-login-password-form").hidden = true;
    document.getElementById("myapp-phone-login-code-form").hidden = true;
    document.getElementById("myapp-loign-select-protocol").hidden = true;
    document.getElementById("myapp-phone-login-code-btn").hidden = true;

    document.getElementById("myapp-main-login-code-btn").hidden = true;
    document.getElementById("myapp-login-form-copy").hidden = true;
    document.getElementById("myapp-phone-login-change-code").hidden = true;
    document.getElementById("myapp-phone-login-change-password").hidden = true;
    document.getElementById("myapp-autoReg-else-btn").hidden = true;

    document.getElementById("myapp-main-else-btn-tip").hidden = true;
    document.getElementById("myapp-main-else-btn1").hidden = true;
    document.getElementById("myapp-login-form-phone-login").hidden = true;
    document.getElementById("myapp-login-form-password-login").hidden = true;
    document.getElementById("myapp-main-else-btn2").hidden = true;

    document.getElementById("myapp-main-bottom-tip").hidden = true;
    document.getElementById("myapp-autoReg-bottom-tip").hidden = true;
    document.getElementById("myapp-autoReg-else-btn1").hidden = true;
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


function DisplayAndHiddenBtn(btnId, type) {
    let currentBtn = document.getElementById(btnId);
    if (type) {
        currentBtn.style.display = "block"; //style中的display属性
    } else {
        currentBtn.style.display = "none";
    }
}
function enterGame() {

}
