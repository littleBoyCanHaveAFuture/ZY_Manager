function setCookie(name, value) {
    var Days = 30;
    var d = new Date();
    d.setTime(d.getTime() + (Days * 24 * 60 * 60 * 1000));
    var expires = "expires=" + d.toGMTString();
    document.cookie = name + "=" + value + "; " + expires;
}

function getCookies(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i].trim();
        if (c.indexOf(name) === 0) return c.substring(name.length, c.length);
    }
    return "";
}


function delCookie(name) {
    var exp = new Date();
    exp.setTime(exp.getTime() - 1);
    var cval = getCookies(name);
    if (cval != null)
        document.cookie = name + "=" + cval + ";expires=" + exp.toGMTString();
}


function checkCookies() {
    console.log("checkCookie");
    let userName = getCookies("userName");
    let roleName = getCookies("roleName");
    if (userName == null || userName === "" || roleName == null || roleName === "") {
        alert("未登录!");
        window.location.href = "login.jsp";
    }
}

function clearCookie() {
    delCookie("userName");
    delCookie("roleName");
    window.location.href = "login.jsp";
}

function clearGameCookie() {
    delCookie("username");
    delCookie("password");
    delCookie("channelUid");
    delCookie("accountid");
}

function formatterDate(date, type) {
    let day = date.getDate() > 9 ? date.getDate() : "0" + date.getDate();
    let month = (date.getMonth() + 1) > 9 ? (date.getMonth() + 1) : "0" + (date.getMonth() + 1);
    let hor = date.getHours();
    let min = date.getMinutes();
    let sec = (date.getSeconds() > 9) ? date.getSeconds() : "0" + date.getSeconds();

    if (type === 0) {
        return date.getFullYear() + '-' + month + '-' + day + " " + "00" + ":" + "00";
    } else {
        return date.getFullYear() + '-' + month + '-' + day + " " + hor + ":" + min;
    }
}

function getmax(arr) {
    let max = arr[0];
    for (let i = 0; i < arr.length - 1; i++) {
        max = max < arr[i + 1] ? arr[i + 1] : max
    }
    return max;
}

function checkParam(param) {
    return (param == null || param === "" || param === "undefined");
}