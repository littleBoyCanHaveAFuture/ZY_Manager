$(function () {
    initTableColumns();
    initGameList();

});

function exportToLocal() {
    $('#dg').datagrid('toExcel', 'dg.xls'); // export to excel
    // $('#dg').datagrid('print', 'DataGrid'); // print the datagrid
}

function search(type) {

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
        "活跃玩家": "activePlayer",
        "充值次数": "rechargeTimes",
        "充值人数": "rechargeNumber",
        "充值金额": "RechargePayment",
        "活跃付费率": "activePayRate",
        "付费ARPU": "paidARPU",
        "活跃ARPU": "activeARPU",
        "当日首次付费人数": "nofPayers",
        "当日首次付费金额": "nofPayment",
        "注册付费人数": "registeredPayers",
        "注册付费金额": "registeredPayment",
        "注册付费ARPU": "registeredPaymentARPU",
//分服
        "累计充值": "totalPayment",
        "累计创角": "totalCreateRole",
        "累计充值人数": "totalRechargeNums",
        "总付费率": "totalRechargeRates",
//渠道
        "注收比": "zhushoubi",
        "新增注收比": "addzhushoubi"
    };
    let activeResult = {
        "headers": [
            {
                "渠道id": "spId",
                "新增创号": "newAddCreateAccount",
                "新增创角": "newAddCreateRole",
                "新增创角去除滚服": "newAddCreateRoleRemoveOld",
                "创角率": "createAccountRate",
                "创号转化率":"createAccountTransRate"
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
}

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