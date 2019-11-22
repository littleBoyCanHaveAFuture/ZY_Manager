// 初始化内容 先加载完列表
$(function () {
    var gameId = null;
    var serverId = null;
    var spId = null;
    $.ajax({
        url: "/server/getServerList",
        type: "post",
        data: {"gameId": gameId, "serverId": serverId, "spId": spId, "page": 1, "rows": 10},
        dataType: "json",
        async: false,
        success: function (result) {
            if (result.resultCode === 501) {
                relogin();
            } else if (result.resultCode === 200) {
                result = {
                    total: result.total,
                    rows: result.rows
                }
                if (result.total === 0) {
                    $.messager.alert("系统提示", "查询成功 无数据");
                }
                $("#serverTable").datagrid("loadData", result);
            }
        },
        error: function () {
            $.messager.alert("ERROR！", "查询失败");
        }
    });

});
//初始化内容 下一页按钮
$(function () {// 初始化内容
    var dg = $("#serverTable");
    var opts = dg.datagrid('options');
    var pager = dg.datagrid('getPager');
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
            // pager.pagination('refresh', {
            //     pageNumber: pageNum,
            //     pageSize: pageSize
            // });
            loadServerListTab();
        }
    });
});

var url = "${pageContext.request.contextPath}/server/getServerList";
var method;
var type;

//查询服务器
function loadServerListTab() {
    var pageNumber = $("#serverTable").datagrid('options').pageNumber;
    var pageSize = $("#serverTable").datagrid('options').pageSize;

    var gameId = $("#gameid").val();
    var serverId = $("#serverid").val();
    var spId = $("#spid").val();
    if (gameId === "" || gameId === undefined) {
        gameId = -1;
    }
    if (serverId === "" || serverId === undefined) {
        gameId = -1;
    }
    if (spId === "" || spId === undefined) {
        spId = null;
    }
    if (pageNumber === "" || pageNumber === undefined || pageNumber <= 0) {
        pageNumber = null;
    }
    if (pageSize === "" || pageSize === undefined || pageSize <= 0) {
        pageSize = null;
    }
    $.ajax({
        url: "/server/getServerList",
        type: "post",
        data: {"gameId": gameId, "serverId": serverId, "spId": spId, "page": pageNumber, "rows": pageSize},
        dataType: "json",
        async: false,
        success: function (result) {
            if (result.resultCode === 501) {
                relogin();
            } else if (result.resultCode === 200) {
                result = {
                    total: result.total,
                    rows: result.rows
                }
                if (result.total === 0) {
                    $.messager.alert("系统提示", "查询成功 无数据");
                }
                $("#serverTable").datagrid("loadData", result);
                // $('#serverTable').datagrid({loadFilter: pagerFilter}).datagrid('loadData', result);
            }

        },
        error: function () {
            $.messager.alert("ERROR！", "查询失败");
        }
    });
}

//页面过滤器
function pagerFilter(data) {
    if (typeof data.length == 'number' && typeof data.splice == 'function') {    // is array
        data = {
            total: data.length,
            rows: data
        }
    }
    var dg = $(this);
    var opts = dg.datagrid('options');
    var pager = dg.datagrid('getPager');
    pager.pagination({
        pageSize: 10,//每页显示的记录条数，默认为10        　　　　　　　　　　//这里不设置的画分页页数选择函数会正确调用，否则每次点击下一页
        pageList: [5, 10, 15],//可以设置每页记录条数的列表 　　　　　　　　　　　　 //pageSize都会变回设置的值
        beforePageText: '第',//页数文本框前显示的汉字
        afterPageText: '页    共 {pages} 页',
        displayMsg: '当前显示 {from} - {to} 条记录   共 {total} 条记录',
        onChangePageSize: function () {
        },
        onSelectPage: function (pageNum, pageSize) {
            console.log("onSelectPage");
            console.log(pageNum);
            console.log(pageSize);

            opts.pageNumber = pageNum;
            opts.pageSize = pageSize;
            pager.pagination('refresh', {
                pageNumber: pageNum,
                pageSize: pageSize
            });
            // dg.datagrid('loadData', data);
        }
    });
    if (!data.originalRows) {
        data.originalRows = (data.rows);
    }
    var start = (opts.pageNumber - 1) * parseInt(opts.pageSize);
    var end = start + parseInt(opts.pageSize);
    data.rows = (data.originalRows.slice(start, end));
    return data;
}


// 打开dialog 添加渠道
function openServerDialog() {
    type = 1;
    $("#dlg").dialog("open").dialog("setTitle", "添加服务器");
}

//打开dialog 修改服务器
function openServerModifyDialog() {
    type = 2;
    var selectedRows = $("#serverTable").datagrid('getSelections');
    var row = selectedRows[0];
    if (selectedRows.length !== 1) {
        $.messager.alert("系统提示", "请选择一条要编辑的数据！");
        return;
    }
    var dataId = row.id;
    var gameId = row.gameId;
    var serverId = row.serverId;
    var spId = row.spId;
    var loginUrl = row.loginUrl;


    $("#dlg").dialog({
        onOpen: function () {
            $("#save_id").val(dataId);
            $("#save_gameid").val(gameId);
            $("#save_serverid").val(serverId);
            $("#save_spid").val(spId);
            $("#save_loginurl").val(loginUrl);
        }
    });
    $("#dlg").dialog("open").dialog("setTitle", "修改服务器信息");

}

function saveServerType() {
    if (type === 1) {
        saveServer();
    } else {
        updateServer();
    }
}

//保存
function saveServer() {

    var gameId = $("#save_gameid").val();
    var serverId = $("#save_serverid").val();
    var spId = $("#save_spid").val();
    var loginUrl = $("#save_loginurl").val();

    console.log("id" + gameid);
    var data = {"gameId": gameId, "serverId": serverId, "spId": spId, "loginUrl": loginUrl};
    $.ajax({
        url: "/server/addServer",
        type: "post",
        data: JSON.stringify(data),
        async: false,
        dataType: "json",//预期服务器返回的数据类型
        contentType: "application/json; charset=utf-8",

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

//修改数据
function updateServer() {
    var dataId = $("#save_id").val();
    var gameId = $("#save_gameid").val();
    var serverId = $("#save_serverid").val();
    var spId = $("#save_spid").val();
    var loginUrl = $("#save_loginurl").val();

    var data = {"id": dataId, "gameId": gameId, "serverId": serverId, "spId": spId, "loginUrl": loginUrl};
    console.log(data);
    $.ajax({
        url: "/server/updateServer",
        type: "put",
        data: JSON.stringify(data),
        async: false,
        dataType: "json",//预期服务器返回的数据类型
        contentType: "application/json; charset=utf-8",

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

//关闭 添加服务器 对话框
function closeServerDialog() {
    $("#dlg").dialog("close");
    resetValue();
}

function resetValue() {
    $("#save_id").val("");
    $("#save_gameid").val("");
    $("#save_serverid").val("");
    $("#save_spid").val("");
    $("#save_loginurl").val("");
}

//删除服务器
function deleteServer() {
    var selectedRows = $("#serverTable").datagrid('getSelections');
    if (selectedRows.length === 0) {
        $.messager.alert("系统提示", "请选择要删除的数据！");
        return;
    }
    var strIds = [];
    for (var i = 0; i < selectedRows.length; i++) {
        strIds.push(selectedRows[i].id);
    }
    var ids = strIds.join(",");
    var length = selectedRows.length;
    $.messager.confirm("系统提示", "您确认要删除这<font color=red>" + length + "</font>条数据吗？",
        function (r) {
            if (r) {
                $.ajax({
                    type: "DELETE",//方法类型
                    dataType: "json",//预期服务器返回的数据类型
                    url: "/server/" + ids,//url
                    data: {},
                    success: function (result) {
                        console.log(result);//打印服务端返回的数据
                        if (result.resultCode === 200) {
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