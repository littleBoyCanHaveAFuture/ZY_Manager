// 初始化内容 先加载完列表
$(function () {
    loadServerListTab();
});
//初始化内容 下一页按钮
$(function () {// 初始化内容
    let dg = $("#dg");
    let opts = dg.datagrid('options');
    let pager = dg.datagrid('getPager');
    pager.pagination({
        // pageSize: 10,//每页显示的记录条数，默认为10        　　　　　　　　　　//这里不设置的画分页页数选择函数会正确调用，否则每次点击下一页
        pageList: [5, 10, 15],//可以设置每页记录条数的列表 　　　　　　　　　　　　 //pageSize都会变回设置的值
        beforePageText: '第',//页数文本框前显示的汉字
        afterPageText: '页    共 {pages} 页',
        displayMsg: '当前显示 {from} - {to} 条记录   共 {total} 条记录',
        onChangePageSize: function () {
        },
        onSelectPage: function (pageNum, pageSize) {
            opts.pageNumber = pageNum;
            opts.pageSize = pageSize;
            loadServerListTab();
        }
    });
});

var url = "${pageContext.request.contextPath}/server/getServerList";
var method;
var type;

//查询游戏
function loadServerListTab() {
    let dg = $("#dg");
    let opts = dg.datagrid('options');
    let pager = dg.datagrid('getPager');

    let pageNumber = opts.pageNumber;
    let pageSize = opts.pageSize;

    let gameId = $("#gameid").val();
    let name = $("#name").val();

    if (gameId === "" || gameId === undefined) {
        gameId = "-1";
    }
    if (name === "" || name === undefined) {
        name = "";
    }
    if (pageNumber === "" || pageNumber === undefined || pageNumber <= 0) {
        pageNumber = null;
    }
    if (pageSize === "" || pageSize === undefined || pageSize <= 0) {
        pageSize = null;
    }
    let url = "/server/getGameList" +
        "?gameId=" + gameId +
        "&name=" + name +
        "&page=" + pageNumber +
        "&rows=" + pageSize;

    $.ajax({
        url: url,
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
                    $.messager.alert("系统提示", "查询成功 无数据");
                }
                $("#dg").datagrid("loadData", result);
            }

        },
        error: function () {
            $.messager.alert("ERROR！", "查询失败");
        }
    });
}

function saveServerType() {
    if (type === 3) {
        saveServer(3);
    } else if (type === 2) {
        saveServer(2);
    }
}

// 打开dialog 添加渠道
function openServerDialog() {
    type = 3;
    resetValue();
    $("#dlg").dialog("open").dialog("setTitle", "添加游戏");
}

//关闭 添加服务器 对话框
function closeServerDialog() {
    $("#dlg").dialog("close");
    resetValue();
}

//打开dialog 修改服务器
function openServerModifyDialog() {
    let dlg = $("#dlg");
    type = 2;
    let selectedRows = $("#dg").datagrid('getSelections');
    let row = selectedRows[0];
    if (selectedRows.length !== 1) {
        $.messager.alert("系统提示", "请选择一条要编辑的数据！");
        return;
    }
    let gameId = row.gameId;
    let name = row.name;


    dlg.dialog({
        onOpen: function () {
            $("#save_gameid").val(gameId);
            $("#save_name").val(name);
        }
    });
    dlg.dialog("open").dialog("setTitle", "修改游戏信息");
}

function resetValue() {
    $("#save_gameid").val("");
    $("#save_name").val("");
}

//删除服务器
function deleteServer() {
    type = 1;
    let selectedRows = $("#dg").datagrid('getSelections');
    if (selectedRows.length === 0) {
        $.messager.alert("系统提示", "请选择要删除的数据！");
        return;
    }
    if (selectedRows.length !== 1) {
        $.messager.alert("系统提示", "请选择要一条要删除的数据！");
        return;
    }

    let length = selectedRows.length;
    let gameId = selectedRows[0].gameId;
    let name = selectedRows[0].name;


    let url = "/server/gamedata" +
        "?gameId=" + gameId +
        "&name=" + name +
        "&type=" + type;
    $.messager.confirm("系统提示", "您确认要删除这" + "<font color=red>" + length + "</font>" + "条数据吗？",
        function (r) {
            if (r) {
                $.ajax({
                    type: "get",
                    dataType: "json",
                    url: url,
                    success: function (result) {
                        if (result.resultCode === 501) {
                            relogin();
                        } else if (result.resultCode === 200) {
                            $.messager.alert(
                                "系统提示",
                                "数据已成功删除！");
                            loadServerListTab();
                        } else {
                            $.messager.alert(
                                "系统提示",
                                "数据删除失败！");
                        }
                    },
                    error: function () {
                        $.messager.alert("ERROR！");
                    }
                });
            }
        });
}

//保存
function saveServer(type) {
    let gameId = $("#save_gameid").val();
    let name = $("#save_name").val();

    let url = "/server/gamedata" +
        "?gameId=" + gameId +
        "&name=" + name +
        "&type=" + type;
    console.info(url);
    $.ajax({
        url: url,
        type: "get",
        dataType: "json",
        async: false,
        success: function (result) {
            if (result.resultCode === 501) {
                relogin();
            } else if (result.resultCode === 200) {
                $.messager.alert("系统提示", "保存成功");
                $("#dlg").dialog("close");
                $("#serverTable").datagrid("reload");
                resetValue();
            } else {
                $.messager.alert("系统提示", "操作失败");
                $("#dlg").dialog("close");
                resetValue();
            }
        },
        error: function () {
            $.messager.alert("ERROR！", "操作失败");
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
