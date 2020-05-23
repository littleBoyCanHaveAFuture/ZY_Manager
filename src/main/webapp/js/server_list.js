// 初始化内容 先加载完列表
$(function () {
    let commonResult = {
        "编号": "id",
        "游戏id": "gameId",
        "游戏名": "gamename",
        "服务器id": "serverId",
        "渠道id": "spId",
        "开服时间": "openday",
    };
    let activeColumns = [];
    $.each(commonResult, function (index, value) {
        let column = {};
        column["title"] = index;
        column["field"] = value;
        column["align"] = 'center';
        if (value === "loginUrl") {
            column["width"] = 300;
        }
        activeColumns.push(column);
    });

    let dg = $("#dg");
    initDataGrid(dg, activeColumns, loadServerListTab);

    loadServerListTab();
    initSelectList();
    initSpGameServer(1);
    initSpGameServer(2);
    initSpGameServer(3);
});

//对话框下拉菜单
function initSelectList() {
    console.log("initSelectList");
    //游戏id
    $.ajax({
        //获取下拉
        url: "/server/getGameList",
        type: "get",
        dataType: "json",
        success: function (result) {
            if (result.resultCode === 501) {
                relogin();
            } else if (result.resultCode === 200) {1``
                let select_gameId = $("#dlg_gameid");
                select_gameId.find("option").remove();
                select_gameId.append("<option value=-1 selected=selected>请选择</option>");
                for (let res = 0; res < result.total; res++) {
                    select_gameId.append("<option value='" + result.rows[res].appId + "'>" + result.rows[res].appName + "</option>");
                }
            }
        },
        error: function () {
            $.messager.alert("ERROR！", "获取游戏列表出错");
        }
    });
}

let method;
let type;

//查询服务器
function loadServerListTab() {
    let dg = $("#dg");
    let opts = getDatagridOptions(dg);
    let pager = dg.datagrid('getPager');

    let pageNumber = opts.pageNumber;
    let pageSize = opts.pageSize;

    let gameId = $("#gameid").val();
    let serverId = $("#save_serverId").val();
    let spId = $("#save_spId").val();
    if (gameId === "" || gameId === undefined) {
        gameId = -1;
    }
    if (checkParam(gameId)) {
        gameId = -1;
    }
    if (checkParam(spId)) {
        spId = null;
    }
    if (checkParam(pageNumber) || pageNumber <= 0) {
        pageNumber = null;
    }
    if (checkParam(pageSize) || pageSize <= 0) {
        pageSize = null;
    }
    $.ajax({
        url: "/server/getServerList",
        type: "post",
        data: {"gameId": gameId, "serverId": serverId, "spId": spId, "page": pageNumber, "rows": pageSize},
        dataType: "json",
        async: false,
        success: function (result) {
            console.info(result);
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

// 打开dialog 添加渠道
function openServerDialog() {
    type = 1;
    let openday = $("#dlg_openday").datetimebox("getValue");
    if (checkParam(openday)) {

    }
    $("#dlg").dialog("open").dialog("setTitle", "添加服务器");
}

//打开dialog 修改服务器
function openServerModifyDialog() {
    type = 2;
    let selectedRows = $("#dg").datagrid('getSelections');
    let row = selectedRows[0];
    if (selectedRows.length !== 1) {
        $.messager.alert("系统提示", "请选择一条要编辑的数据！");
        return;
    }
    let dataId = row.id;
    let gameId = row.gameId;
    let serverId = row.serverId;
    let spId = row.spId;
    let loginUrl = row.loginUrl;
    let openday = row.openday;


    $("#dlg").dialog({
        onOpen: function () {
            $("#save_id").val(dataId);
            $("#dlg_gameid").val(gameId);
            $("#save_serverid").val(serverId);
            $("#dlg_spid").val(spId);
            $("#dlg_loginurl").val(loginUrl);
            $('#dlg_openday').datebox('setValue', openday);
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
    let gameId = $("#dlg_gameid").val();
    let serverId = $("#dlg_serverid").val();
    let spId = $("#dlg_spid").val();
    let loginUrl = $("#dlg_loginurl").val();
    let openday = $("#dlg_openday").datetimebox("getValue");
    if (gameId === "-1") {
        $.messager.alert("系统提示", "请选择游戏");
        return;
    }
    let data = {"gameId": gameId, "serverId": serverId, "spId": spId, "loginUrl": loginUrl, "openday": openday};
    $.ajax({
        url: "/server/addServer",
        type: "post",
        data: JSON.stringify(data),
        dataType: "json",//预期服务器返回的数据类型
        contentType: "application/json; charset=utf-8",
        async: false,
        success: function (result) {
            $("#dlg").dialog("close");
            if (result.resultCode === 501) {
                relogin();
            } else if (result.resultCode === 200) {
                $.messager.alert("系统提示", "保存成功");
                resetValue();
                loadServerListTab();
            } else {
                $.messager.alert("系统提示", "操作失败");
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
    let dataId = $("#save_id").val();
    let gameId = $("#dlg_gameid").val();
    let serverId = $("#dlg_serverid").val();
    let spId = $("#dlg_spid").val();
    let loginUrl = $("#dlg_loginurl").val();
    let openday = $("#dlg_openday").datetimebox("getValue");

    var data = {
        "id": dataId,
        "gameId": gameId,
        "serverId": serverId,
        "spId": spId,
        "loginUrl": loginUrl,
        "openday": openday
    };
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
                $("#dg").datagrid("reload");
                resetValue();
                loadServerListTab();
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
    $("#dlg_serverid").val("");
    $("#dlg_spid").val("");
    $("#dlg_loginurl").val("");
    $("#dlg_openday").val("");
}

//删除服务器
function deleteServer() {
    let selectedRows = $("#dg").datagrid('getSelections');
    if (selectedRows.length === 0) {
        $.messager.alert("系统提示", "请选择要删除的数据！");
        return;
    }
    let strIds = [];
    for (var i = 0; i < selectedRows.length; i++) {
        strIds.push(selectedRows[i].id);
    }
    let ids = strIds.join(",");
    let length = selectedRows.length;
    $.messager.confirm("系统提示", "您确认要删除这" + "<font color=red>" + length + "</font>" + "条数据吗？",
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
                parent.location.href = "../login.jsp";
            }
        });
}
