$(function () {
    initTableColumns();
    initGameList();

});

function exportToLocal() {
    $('#dg').datagrid('toExcel', 'dg.xls'); // export to excel
    // $('#dg').datagrid('print', 'DataGrid'); // print the datagrid
}

function search() {
    $("#ActivePlayer").val();
    $("#Recharge_Times").val();
    $("#Recharge_Number").val();
    $("#Recharge_Payment").val();
    $("#ActivePayRate").val();
    $("#ARPU_Paid").val();
    $("#ARPU_Active").val();
    $("#NOF_Payers").val();
    $("#NOF_Payment").val();
    $("#Registered_Payers").val();
    $("#Registered_Payment").val();
    $("#Registered_Payment_ARPU").val();

    $("#data").val();
    $("#NA_CreateAccount").val();
    $("#NA_CreateRole").val();
    $("#NA_CreateRole_ReomveOld").val();
    $("#CreateAccount_Rate").val();

    $.ajax({
        //获取下拉
        url: "",
        type: "post",
        async: false,
        data: {},
        dataType: "json",
        success: function (combox) {
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

function initTableColumns() {
    $('#save_startTime').datetimebox('setValue', '12/01/2019 00:00');
    let frozenColumns = [];
    let activeColumns = [];
    let commonResult = {
        "活跃玩家": "ActivePlayer",
        "充值次数": "Recharge_Times",
        "充值人数": "Recharge_Number",
        "充值金额": "Recharge_Payment",
        "活跃付费率": "ActivePayRate",
        "付费ARPU": "ARPU_Paid",
        "活跃ARPU": "ARPU_Active",
        "当日首次付费人数": "NOF_Payers",
        "当日首次付费金额": "NOF_Payment",
        "注册付费人数": "Registered_Payers",
        "注册付费金额": "Registered_Payment",
        "注册付费ARPU": "Registered_Payment_ARPU"

    };
    let activeResult = {
        "headers": [
            {
                "日期": "data",
                "新增创号": "NA_CreateAccount",
                "新增创角": "NA_CreateRole",
                "新增创角去除滚服": "NA_CreateRole_ReomveOld",
                "创角率": "CreateAccount_Rate"
            }
        ], "bodys": [
            // {"name": "LNG", "count": "50000", "Jan": "20000", "Feb": "30000"}
        ]
    };
    $.each(commonResult, function (index, value) {
        let column = {};
        column["field"] = value;
        column["title"] = index;
        column["align"] = 'center';
        column["width"] = 50;
        frozenColumns.push(column);
    });

    $.each(activeResult.headers[0], function (index, value) {
        let column = {};
        column["field"] = value;
        column["title"] = index;
        column["align"] = 'center';
        column["width"] = 50;
        activeColumns.push(column);//当需要formatter的时候自己添加就可以了,原理就是拼接字符串.
    });
    let dg = $("#dg");
    let opts = dg.datagrid('options');
    let pager = dg.datagrid('getPager');

    dg.datagrid({
        frozenColumns: [[]], columns: [
            activeColumns.concat(frozenColumns)
        ],
    }).datagrid('loadData', activeResult.bodys);

}

function initGameList() {
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
                let select_gameId = $("#save_gameId");
                select_gameId.find("option").remove();
                select_gameId.append("<option value=-1 selected=selected>请选择</option>");
                for (let res = 0; res < result.total; res++) {
                    select_gameId.append("<option  value='" + result.rows[res].gameId + "'>" + result.rows[res].name + "</option>");
                }
            }
        },
        error: function () {
            $.messager.alert("ERROR！", "获取游戏列表出错");
        }
    });
};

function initServerList(type) {
    let gameId = $('#save_gameId').val();
    let serverId = $("#save_serverId").val();
    let spId = $("#save_spId").val();

    let data = {
        "gameId": gameId,
        "serverId": serverId,
        "spId": spId,
        "type": type
    };
    console.log("data " + gameId);
    console.log("data " + serverId);
    console.log("data " + spId);
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
                console.log(result);
                if (type === 1) {
                    let select_serverId = $("#save_serverId");
                    select_serverId.find("option").remove();
                    select_serverId.append("<option value=-1 selected=selected>请选择</option>");
                    for (let res = 0; res < result.total; res++) {
                        select_serverId.append("<option value='" + result.rows[res] + "'>" + result.rows[res] + "</option>");
                    }
                } else {
                    let select_spId = $("#save_spId");
                    select_spId.find("option").remove();
                    select_spId.append("<option value=-1 selected=selected>请选择</option>");
                    for (let res = 0; res < result.total; res++) {
                        select_spId.append("<option value='" + result.rows[res] + "'>" + result.rows[res] + "</option>");
                    }
                }
            }
        },
        error: function () {
            $.messager.alert("ERROR！", "获取游戏列表出错");
        }
    });
}

function search() {
    let s = $('#save_serverId').val();
    console.log("ssssssss:" + s);
}