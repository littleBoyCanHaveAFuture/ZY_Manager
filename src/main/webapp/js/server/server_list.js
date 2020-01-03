// 初始化内容 先加载完列表
$(function () {
    let activeColumns = [];
    let commonResult = {
        "编号": "id",
        "游戏id": "gameId",
        "游戏名": "gamename",
        "服务器id": "serverId",
        "渠道id": "spId",
        "开服时间": "openday",
        "md5秘钥": "secertKey",
        "登录地址": "loginUrl",
    };

    $.each(commonResult, function (index, value) {
        let column = {};
        column["field"] = value;
        column["title"] = index;
        column["align"] = 'center';
        // column["width"] = 50;
        if(value==="loginUrl"){
            column["width"] = 300;
        }
        activeColumns.push(column);
    });
    let dg = $("#serverTable");
    dg.datagrid({
        pagination: true, //分页显示
        loadMsg: "正在加载，请稍后...",
        frozenColumns: [[]], columns: [
            activeColumns
        ],
    });
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

    initFirstPage();
    initSelectList();
    initSpGameServer(1);
    initSpGameServer(2);
    initSpGameServer(3);
});

var url = "${pageContext.request.contextPath}/server/getServerList";
var method;
var type;

//查询服务器
function loadServerListTab() {
    let dg = $("#serverTable");
    let opts = dg.datagrid('options');
    let pager = dg.datagrid('getPager');

    let pageNumber = opts.pageNumber;
    let pageSize = opts.pageSize;

    let gameId = $("#gameid").val();
    let serverId = $("#serverid").val();
    let spId = $("#spid").val();
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
                $("#serverTable").datagrid("loadData", result);
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
    $("#dlg").dialog("open").dialog("setTitle", "添加服务器");
}

//打开dialog 修改服务器
function openServerModifyDialog() {
    type = 2;
    let selectedRows = $("#serverTable").datagrid('getSelections');
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
            $("#save_gameid").val(gameId);
            $("#save_serverid").val(serverId);
            $("#save_spid").val(spId);
            $("#save_loginurl").val(loginUrl);
            $('#save_openday').datebox('setValue', openday);
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
    let gameId = $("#save_gameid").val();
    let serverId = $("#save_serverid").val();
    let spId = $("#save_spid").val();
    let loginUrl = $("#save_loginurl").val();
    let openday = $("#save_openday").datetimebox("getValue");

    console.log("id" + gameid);
    let data = {"gameId": gameId, "serverId": serverId, "spId": spId, "loginUrl": loginUrl, "openday": openday};
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
                initFirstPage();
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
    let dataId = $("#save_id").val();
    let gameId = $("#save_gameid").val();
    let serverId = $("#save_serverid").val();
    let spId = $("#save_spid").val();
    let loginUrl = $("#save_loginurl").val();
    let openday = $("#save_openday").datetimebox("getValue");

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
                $("#serverTable").datagrid("reload");
                resetValue();
                initFirstPage();
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
    $("#save_openday").val("");
}

//删除服务器
function deleteServer() {
    let selectedRows = $("#serverTable").datagrid('getSelections');
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

function initFirstPage() {
    let gameId = null;
    let serverId = null;
    let spId = null;
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
}

//下拉菜单
function initSelectList() {
    console.log("initSelectList");
    //游戏id
    $.ajax({
        //获取下拉
        url: "/server/getGameList",
        type: "get",
        async: false,
        dataType: "json",
        success: function (result) {
            if (result.resultCode === 501) {
                relogin();
            } else if (result.resultCode === 200) {
                let select_gameId = $("#gameid");
                select_gameId.find("option").remove();
                select_gameId.append("<option value=-1 selected=selected>请选择</option>");
                for (let res = 0; res < result.total; res++) {
                    // console.log("<option value='" + result.rows[res].gameId + "'>" + result.rows[res].name + "</option>");
                    select_gameId.append("<option value='" + result.rows[res].gameId + "'>" + result.rows[res].name + "</option>");
                }
            }
        },
        error: function () {
            $.messager.alert("ERROR！", "获取游戏列表出错");
        }
    });
}

function initSpGameServer(type) {
    let gameId = $('#gameid').val();
    let serverId = $("#serverid").val();
    let spId = $("#spid").val();

    let data = {
        "gameId": gameId,
        "serverId": serverId,
        "spId": spId,
        "type": type
    };

    $.ajax({
        //获取下拉
        url: "/server/getDistinctServerInfo",
        type: "post",
        data: data,
        async: false,
        dataType: "json",
        success: function (result) {
            if (result.resultCode === 501) {
                relogin();
            } else if (result.resultCode === 200) {
                // console.log(result);
                if (type === 1) {
                    let select_spId = $("#spid");
                    select_spId.find("option").remove();
                    select_spId.append("<option value=-1 selected=selected>请选择</option>");
                    for (let res = 0; res < result.total; res++) {
                        select_spId.append("<option value='" + result.rows[res] + "'>" + result.rows[res] + "</option>");
                    }
                } else if (type === 2) {
                    let select_gameId = $("#gameid");
                    select_gameId.find("option").remove();
                    select_gameId.append("<option value=-1 selected=selected>请选择</option>");
                    for (let res = 0; res < result.total; res++) {
                        let gameid = result.rows[res].gameId;
                        let name = result.rows[res].name + "\t" + gameid;
                        select_gameId.append("<option  value='" + gameid + "'>" + name + "</option>");
                    }
                    let save_gameid = $("#save_gameid");
                    save_gameid.find("option").remove();
                    save_gameid.append("<option value=-1 selected=selected>请选择</option>");
                    for (let res = 0; res < result.total; res++) {
                        let gameid = result.rows[res].gameId;
                        let name = result.rows[res].name + "\t" + gameid;
                        save_gameid.append("<option  value='" + gameid + "'>" + name + "</option>");
                    }


                } else if (type === 3) {
                    let select_serverId = $("#serverid");
                    select_serverId.find("option").remove();
                    select_serverId.append("<option value=-1 selected=selected>请选择</option>");
                    for (let res = 0; res < result.total; res++) {
                        select_serverId.append("<option value='" + result.rows[res] + "'>" + result.rows[res] + "</option>");
                    }
                }

            }
        },
        error: function () {
            $.messager.alert("ERROR！", "获取游戏列表出错");
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
