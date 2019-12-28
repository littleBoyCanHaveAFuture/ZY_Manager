window.onload = function () {
    let name = getCookies("username");
    let pwd = getCookies("password");
    console.info(name);
    console.info(pwd);
};

function EnterGame() {
    window.location.href = "gamelogin.jsp";
}

function EnterGame_Cisha() {
    setCookie("appId", 2);
    // alert(2);
    EnterGame();
}
function EnterGame_SGYX() {
    setCookie("appId", 9999);
    // alert(9999);
    EnterGame();
}