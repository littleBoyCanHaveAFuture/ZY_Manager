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

function formatOpt(val, row, index) {
    return '<a href="#" onclick="editSp(' + index + ')">配置</a>';
}

function editSp(index) {
    let dg = $('#dg');
    dg.datagrid('selectRow', index);
    let row = dg.datagrid('getSelected');
    if (row) {
        let tab = $('#tabs');
        let param = "?gameId=" + row.id + "&name=" + row.name;
        // openTab(tab, "渠道配置", "server/gameConfig.jsp" + param, "icon-ok");
        openTab(tab, "渠道配置", "server/game_index.jsp" + param, "icon-ok");
    }
}

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
        url: "/server/getGameList" + param,
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
                }
                $("#dg").datagrid("loadData", result);
            }

        },
        error: function () {
            tip("ERROR！", "查询失败");
        }
    });
}

function saveServerType() {
    saveServer(t_type);
}

// 打开dialog 添加渠道
function openServerDialog() {
    t_type = 3;
    resetValue();
    $("#save_gameid").hide();//隐藏
    $("#dlg").dialog("open").dialog("setTitle", "添加游戏");
}

//关闭 添加服务器 对话框
function closeServerDialog() {
    $("#dlg").dialog("close");
    resetValue();
}

//打开dialog 修改服务器
function openServerModifyDialog() {
    $("#save_gameid").show();//隐藏
    let dlg = $("#dlg");
    t_type = 2;
    let selectedRows = $("#dg").datagrid('getSelections');
    let row = selectedRows[0];
    if (selectedRows.length !== 1) {
        tip("系统提示", "请选择一条要编辑的数据！");
        return;
    }
    let gameId = row.id;
    let name = row.name;
    let loginUrl = row.loginUrl;
    let paycallbackUrl = row.paycallbackUrl;

    dlg.dialog({
        onOpen: function () {
            $("#save_gameid").val(gameId);
            $("#save_name").val(name);
            $("#save_loginurl").val(loginUrl);
            $("#save_paybackurl").val(paycallbackUrl);
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
    t_type = 1;
    let selectedRows = $("#dg").datagrid('getSelections');
    if (selectedRows.length === 0) {
        tip("系统提示", "请选择要删除的数据！");
        return;
    }
    if (selectedRows.length !== 1) {
        tip("系统提示", "请选择要一条要删除的数据！");
        return;
    }

    let length = selectedRows.length;
    let gameId = selectedRows[0].id;
    let name = selectedRows[0].name;
    let uid = selectedRows[0].uid;
    let secertKey = selectedRows[0].secertKey;
    let loginUrl = selectedRows[0].loginUrl;
    let paybackUrl = selectedRows[0].paycallbackUrl;

    let param = "?gameId=" + gameId + "&name=" + name + "&type=" + t_type
        + "&loginUrl=" + loginUrl + "&paybackUrl=" + paybackUrl;


    $.messager.confirm("系统提示", "您确认要删除这" + "<font color=red>" + length + "</font>" + "条数据吗？",
        function (r) {
            if (r) {
                $.ajax({
                    type: "get",
                    dataType: "json",
                    url: "/server/gamedata" + param,
                    async: false,
                    success: function (result) {
                        if (result.resultCode === 501) {
                            relogin();
                        } else if (result.resultCode === 200) {
                            // tip(
                            //     "系统提示",
                            //     "数据已成功删除！");
                            loadServerListTab();
                        } else {
                            tip(
                                "系统提示",
                                "数据删除失败！");
                        }
                    },
                    error: function () {
                        tip("ERROR！");
                    }
                });
            }
        });
}

//保存
function saveServer(type) {
    let gameId = $("#save_gameid").val();
    let name = $("#save_name").val();
    let loginUrl = $("#save_loginurl").val();
    let paybackUrl = $("#save_paybackurl").val();

    let param = "?gameId=" + gameId + "&name=" + name + "&type=" + type
        + "&loginUrl=" + loginUrl + "&paybackUrl=" + paybackUrl;

    $.ajax({
        url: "/server/gamedata" + param,
        type: "get",
        dataType: "json",
        async: false,
        success: function (result) {
            if (result.resultCode === 501) {
                relogin();
            } else if (result.resultCode === 200) {
                tip("系统提示", "保存成功");
                $("#dlg").dialog("close");
                $("#serverTable").datagrid("reload");
                resetValue();
                loadServerListTab();
            } else {
                tip("系统提示", "操作失败");
                $("#dlg").dialog("close");
                resetValue();
            }
        },
        error: function () {
            tip("ERROR！", "操作失败");
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

