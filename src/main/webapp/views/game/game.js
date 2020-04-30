// 初始化内容 先加载完列表
$(function () {
    let dg = $("#dg");
    let opts = dg.datagrid('options');
    let pager = dg.datagrid('getPager');

    pager.pagination({
        pageSize: 10,
        pageList: [5, 10, 15, 50],
        beforePageText: '第',
        afterPageText: '页    共 {pages} 页',
        displayMsg: '当前显示 {from} - {to} 条记录   共 {total} 条记录',
        singleSelect: true,
        onChangePageSize: function () {
        },

        onSelectPage: function (pageNum, pageSize) {
            opts.pageNumber = pageNum;
            opts.pageSize = pageSize;
            loadServerListTab();
        }
    });
    loadServerListTab();
    $('#save_gameid').attr('readonly', true);
});


let method;
let t_type;

//查询游戏
function loadServerListTab() {
    let dg = $("#dg");
    let opts = getDatagridOptions(dg);
    // let opts = dg.datagrid('options');
    let pager = dg.datagrid('getPager');

    let pageNumber = opts.pageNumber;
    let pageSize = opts.pageSize;

    let gameId = $("#gameid").val();
    let name = $("#name").val();

    if (checkParam(gameId)) {
        gameId = "-1";
    }
    if (checkParam(name)) {
        name = "";
    }
    if (checkParam(pageNumber) || pageNumber <= 0) {
        pageNumber = null;
    }
    if (checkParam(pageSize) || pageSize <= 0) {
        pageSize = null;
    }
    let param =
        "?gameId=" + gameId +
        "&name=" + name +
        "&page=" + pageNumber +
        "&rows=" + pageSize;

    $.ajax({
        url: "/game/getGameList" + param,
        type: "get",
        dataType: "json",
        async: false,
        success: function (result) {
            if (result.resultCode === 501) {
                relogin();
            } else if (result.resultCode === 200) {
                result = {
                    total: result.total,
                    rows: result.rows
                };
                if (result.total === 0) {
                    // tip("系统提示", "查询成功 无数据");
                    tip("系统提示", "查询成功 无数据");
                    $("#dg").datagrid('loadData', {total: 0, rows: []});
                } else {
                    tip("系统提示", "查询成功");
                    $("#dg").datagrid("loadData", result);
                }
            }
        },
        error: function () {
            tip("ERROR！", "查询失败");
        }
    });
}

//登录超时 重新返回到登录界面
function relogin() {
    // 登录失效
    console.log("登录失效");
    $.messager.confirm(
        "系统提示",
        "登录超时！",
        function (r) {
            if (r) {
                delCookie("userName");
                delCookie("roleName");
                parent.location.href = "../../login.jsp";
            }
        });
}

