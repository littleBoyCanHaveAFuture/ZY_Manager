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
        timeout: 200,  //1秒后消失
        showType: 'slide',//弹出的方式。类似ppt里的图片弹出方式
        //弹出框的样式。居中显示
        style: {
            right: '',
            top: document.body.scrollTop + document.documentElement.scrollCenter,
            bottom: ''
        }
    });

}

