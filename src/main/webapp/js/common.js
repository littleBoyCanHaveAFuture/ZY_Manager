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
    window.location.href = "../login.jsp";
}

function clearGameCookie() {
    delCookie("zy_appId");
    delCookie("zy_channelId");
    delCookie("zy_user");
    delCookie("zy_pwd");
    delCookie("zy_channelUid");
    delCookie("zy_uid");
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

/**
 * 初始化easyui datagrid
 * @param {datagrid}    dg
 * @param {array}       activeColumns
 * @param {function}    loadTab
 * */
function initDataGrid(dg, activeColumns, loadTab) {
    dg.datagrid({
        scrollbarSize: 0,
        pagination: true, //分页显示
        loadMsg: "正在加载，请稍后...",
        frozenColumns: [[]], columns: [
            activeColumns
        ],
    });

    let opts = dg.datagrid('options');
    let pager = dg.datagrid('getPager');

    pager.pagination({
        pageSize: 10,                                    //每页显示的记录条数，默认为10     //这里不设置的画分页页数选择函数会正确调用，否则每次点击下一页
        pageList: [5, 10, 15, 50],                       //可以设置每页记录条数的列表 　　　 //pageSize都会变回设置的值
        beforePageText: '第',                            //页数文本框前显示的汉字
        afterPageText: '页    共 {pages} 页',
        displayMsg: '当前显示 {from} - {to} 条记录   共 {total} 条记录',
        onChangePageSize: function () {
        },

        onSelectPage: function (pageNum, pageSize) {
            opts.pageNumber = pageNum;
            opts.pageSize = pageSize;
            loadTab();
        }
    });
    console.info("initDataGrid ok");
}

/**
 * easyui datagrid
 * 获取options
 * @param {datagrid}    dg
 * @return opts
 * */
function getDatagridOptions(dg) {
    let opts = dg.datagrid('options');
    return opts;
}

/**
 * @param {string} title 标题
 * @param {string}  message 内容
 * */
function tip(title, message) {
    $.messager.show({
        title: title,
        msg: message,
        timeout: 300,  //1秒后消失
        showType: 'slide',//弹出的方式。类似ppt里的图片弹出方式
        //弹出框的样式。居中显示
        style: {
            right: '',
            top: document.body.scrollTop + document.documentElement.scrollCenter,
            bottom: ''
        }
    });

}

/**
 * @return {string[]}
 */
function GetArgsFromHref(sHref, sArgName) {
    let args = sHref.split("?");
    let retval = "";

    if (args[0] === sHref) /*参数为空*/
    {
        return retval; /*无需做任何处理*/
    }
    let str = args[1];
    args = str.split("&");
    for (let i = 0; i < args.length; i++) {
        str = args[i];
        let arg = str.split("=");
        if (arg.length <= 1) continue;
        if (arg[0] === sArgName) retval = arg[1];
    }
    return retval;
}

//money 为元;20.01|20|20.1|20.10
function changeMoneyToFen(money) {
    let realmoney_fen = 0;
    let part_yuan = 0;
    let part_fen = 0;
    console.info(money);
    console.info("." + money.indexOf("."));
    if (money.indexOf(".") === -1) {
        //不存在小数点，分
        part_yuan = parseInt(money);
        part_fen = 0;
        realmoney_fen = part_yuan * 100;
    } else {
        let moneyArray = new Array();
        moneyArray = money.split(".");
        console.info("moneyArray=" + moneyArray);
        console.info("moneyArray s=" + moneyArray.length);
        if (moneyArray.length !== 2) {
            return null;
        } else {
            part_yuan = moneyArray[0];
            part_fen = moneyArray[1];
            console.info("part_yuan=" + part_yuan);
            console.info("part_fen=" + part_fen);
            let fen = 0;
            if (part_fen.length === 2) {
                let fen1 = parseInt(part_fen[0]);
                let fen2 = parseInt(part_fen[1]);
                if (fen1 !== 0)
                    fen += fen1 * 10;
                fen += fen2;
            } else {
                fen += part_fen * 10;
            }
            realmoney_fen = parseInt(part_yuan) * 100 + fen;
        }
    }
    return realmoney_fen;
}

//money 为分;10/100/1000/1000/100011
function changeMoneyToYuan(money) {
    let realmonet_yuan = 0;
    if (money.length > 2) {
        let fen1 = money.substr(0, money.length - 2);
        let fen2 = money.substr(money.length - 2, 2);
        console.info(fen1);
        console.info(fen2);
        realmonet_yuan = fen1 + "." + fen2;
    } else if (money.length === 2) {
        realmonet_yuan = "0." + money;
    } else if (money.length === 1) {
        realmonet_yuan = "0.0" + money;
    }
    return realmonet_yuan;
}
