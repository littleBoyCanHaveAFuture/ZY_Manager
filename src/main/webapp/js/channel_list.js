// 初始化内容 先加载完列表
$(function () {
    initTableColumns();
    loadSpListTab();

});

function initTableColumns() {
    let dg = $('#dg');
    dg.datagrid({
        rownumbers: true,
        columns: [[
            {field: 'iconUrl', title: '渠道图标', align: 'center', formatter: showPhoto},
            {field: 'parent', title: '父渠道', align: 'center', hidden: true},
            {field: 'spId', title: '渠道id', align: 'center'},
            {field: 'name', title: '渠道名称', align: 'center'},
            {field: 'state', title: '状态', align: 'center', hidden: true},
            {field: 'shareLinkUrl', title: '分享链接', align: 'center', hidden: true},
            {field: 'version', title: '版本号', align: 'center'},
            {field: 'code', title: '英文简称（渠道回调地址有关）', align: 'center'},
            {field: 'config', title: 'json配置', align: 'center', hidden: true},
        ]],
        pagination: true,
        pageSize: 20,
        // pageList: [10, 20],
        // onSelectPage: function (pageNum, pageSize) {
        //     opts.pageNumber = pageNum;
        //     opts.pageSize = pageSize;
        //     loadSpListTab();
        // }
    });
    let opts = getDatagridOptions(dg);
    let pager = dg.datagrid('getPager');
    pager.pagination({
        pageSize: 20,//每页显示的记录条数，默认为10        　　　　　　　　　　//这里不设置的画分页页数选择函数会正确调用，否则每次点击下一页pageSize都会变回设置的值
        pageList: [15, 20, 50, 100],//可以设置每页记录条数的列表 　　　　　　　　　　　　
        beforePageText: '第',//页数文本框前显示的汉字
        afterPageText: '页    共 {pages} 页',
        displayMsg: '当前显示 {from} - {to} 条记录   共 {total} 条记录',
        onChangePageSize: function () {
        },
        onSelectPage: function (pageNum, pageSize) {
            opts.pageNumber = pageNum;
            opts.pageSize = pageSize;
            loadSpListTab();
        }
    });
}

function showPhoto(value, row, index) {
    if (row.iconUrl) {
        return '<img src="' + row.iconUrl + '"style="height:30px;" alt="">'
    } else {
        return null;
    }
}

let url = "/server/getSpList";
let method;
let type;

//查询游戏
function loadSpListTab() {
    let dg = $("#dg");
    let opts = getDatagridOptions(dg);
    let pager = dg.datagrid('getPager');

    let pageNumber = opts.pageNumber;
    let pageSize = opts.pageSize;

    let spId = $("#spid").val();
    let name = $("#sname").val();

    if (pageNumber === "" || pageNumber === undefined || pageNumber <= 0) {
        pageNumber = null;
    }
    if (pageSize === "" || pageSize === undefined || pageSize <= 0) {
        pageSize = null;
    }
    if (checkParam(spId)) {
        spId = -1;
    }
    let param = "?spId=" + spId + "&name=" + name + "&page=" + pageNumber + "&rows=" + pageSize;

    $.ajax({
        url: "/server/getSpList" + param,
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

function saveSpType() {
    if (type === 3) {
        saveServer(3);
    } else if (type === 2) {
        saveServer(2);
    }
}

// 打开dialog 添加渠道
function openSpDialog() {
    type = 3;
    resetValue();
    $("#dlg").dialog("open").dialog("setTitle", "添加渠道");
}

//关闭 添加服务器 对话框
function closeSpDialog() {
    $("#dlg").dialog("close");
    resetValue();
}

//打开dialog 修改服务器
function openSpModifyDialog() {
    let dlg = $("#dlg");
    type = 2;
    let selectedRows = $("#dg").datagrid('getSelections');
    let row = selectedRows[0];
    if (selectedRows.length !== 1) {
        $.messager.alert("系统提示", "请选择一条要编辑的数据！");
        return;
    }
    let spId = row.spId;
    let name = row.name;
    let parent = row.parent;
    let state = row.state;
    let shareLinkUrl = row.shareLinkUrl;
    let iconUrl = row.iconUrl;
    let version = row.version;

    dlg.dialog({
        onOpen: function () {
            $("#save_spId").val(spId);
            $("#save_name").val(name);
            $("#save_parent").val(parent);
            $("#save_state").val(state);
            $("#save_shareLinkUrl").val(shareLinkUrl);
            $("#save_icon").val(iconUrl);
            $("#save_version").val(version);
        }
    });
    dlg.dialog("open").dialog("setTitle", "修改游戏信息");
}

function resetValue() {
    $("#save_spId").val("");
    $("#save_name").val("");
    $("#save_parent").val("");
    $("#save_state").val("");
    $("#save_shareLinkUrl").val("");
    $("#save_icon").val("");
    $("#save_version").val("");
}

//删除服务器
function deleteSp() {
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
    let row = selectedRows[0];

    let spId = row.spId;
    let name = row.name;
    let parent = row.parent;
    let state = row.state;
    let shareLinkUrl = row.shareLinkUrl;
    let iconUrl = row.iconUrl;
    let version = row.version;

    let param =
        "?type=" + type +
        "&spId=" + spId +
        "&name=" + name +
        "&parent=" + parent +
        "&state=" + state +
        "&shareLinkUrl=" + shareLinkUrl +
        "&iconUrl=" + iconUrl +
        "&version=" + version;

    $.messager.confirm("系统提示", "您确认要删除这" + "<font color=red>" + length + "</font>" + "条数据吗？",
        function (r) {
            if (r) {
                $.ajax({
                    url: "/server/changeSp" + param,
                    type: "get",
                    dataType: "json",
                    async: false,
                    success: function (result) {
                        if (result.resultCode === 501) {
                            relogin();
                        } else if (result.resultCode === 200) {
                            $.messager.alert(
                                "系统提示",
                                "数据已成功删除！");
                            loadSpListTab();
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
    let spId = $("#save_spId").val();
    let name = $("#save_name").val();
    let parent = $("#save_parent").val();
    let state = $("#save_state").val();
    let shareLinkUrl = $("#save_shareLinkUrl").val();
    let iconUrl = $("#save_icon").val();
    let version = $("#save_version").val();
    let code = $("#save_code").val();
    let param =
        "?type=" + type +
        "&spId=" + spId +
        "&name=" + name +
        "&parent=" + parent +
        "&state=" + state +
        "&shareLinkUrl=" + shareLinkUrl +
        "&iconUrl=" + iconUrl +
        "&version=" + version +
        "&code=" + code;

    console.info(url);

    $.ajax({
        url: "/server/changeSp" + param,
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
                loadSpListTab();
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
                parent.location.href = "../login.jsp";
            }
        });
}

function test() {
    var Ajax = {
        get: function (url, fn) {
            // XMLHttpRequest对象用于在后台与服务器交换数据
            let xhr = new XMLHttpRequest();
            xhr.open('GET', url, false);
            xhr.onreadystatechange = function () {
                // readyState == 4说明请求已完成
                if (xhr.readyState === 4) {
                    if (xhr.status === 200 || xhr.status === 304) {
                        console.log(xhr.responseText);
                        fn.call(xhr.responseText);
                    }
                }
            };
            xhr.send();
        },

        // data应为'a=a1&b=b1'这种字符串格式，在jq里如果data为对象会自动将对象转成这种字符串格式
        post: function (url, data, fn) {
            var xhr = new XMLHttpRequest();
            xhr.open('POST', url, false);
            // 添加http头，发送信息至服务器时内容编码类型
            xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
            xhr.onreadystatechange = function () {
                if (xhr.readyState === 4) {
                    if (xhr.status === 200 || xhr.status === 304) {
                        // console.log(xhr.responseText);
                        fn.call(xhr.responseText);
                    }
                }
            };
            xhr.send(data);
        }
    }
    Ajax.get(ZhiYue_domain + "/initApi?" + params,function () {

    })
}
