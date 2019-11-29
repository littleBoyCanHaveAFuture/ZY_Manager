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
