$(function () {
    initTableColumns();
    initGameList();
    // $('#save_startTime').datetimebox('setValue', '12/01/2019 00:00');
    $('#save_startTime').datebox('setValue', formatterDate(new Date(), 0));
    $('#save_endTime').datebox('setValue', formatterDate(new Date(), 1));
});

function formatterDate(date, type) {
    let day = date.getDate() > 9 ? date.getDate() : "0" + date.getDate();
    let month = (date.getMonth() + 1) > 9 ? (date.getMonth() + 1) : "0" + (date.getMonth() + 1);
    let hor = date.getHours();
    let min = date.getMinutes();
    let sec = (date.getSeconds() > 9) ? date.getSeconds() : "0" + date.getSeconds();
    if (type === 0) {
        return date.getFullYear() + '-' + month + '-' + "01" + " " + "00" + ":" + "00";
    } else {
        return date.getFullYear() + '-' + month + '-' + day + " " + hor + ":" + min;
    }
}

function exportToLocal() {
    $('#dg').datagrid('toExcel', 'dg.xls'); // export to excel
    // $('#dg').datagrid('print', 'DataGrid'); // print the datagrid
}

function search() {
    let gameId = $("#save_gameId").val();
    let serverId = $("#save_serverId").val();
    let spId = $("#save_spId").val();//此处逗号会产生问题
    let startTime = $("#save_startTime").val();
    let endTime = $("#save_endTime").val();
    let spIdstrs = new Array(); //定义一数组
    spIdstrs = spId.replace(/,/g, "|");
    let data = {
        "type": 2,
        "gameId": gameId,
        "serverId": serverId,
        "spId": spIdstrs,
        "startTime": startTime,
        "endTime": endTime
    };
    console.log("gameId:" + gameId);
    console.log("serverId:" + serverId);
    console.log("spId:" + spIdstrs);
    console.log("startTime:" + startTime);
    console.log("endTime:" + endTime);


    $.ajax({
        //获取数据
        url: "/rechargeSummary/searchRechargeSummary",
        type: "post",
        data: data,
        dataType: "json",
        async: false,
        success: function (result) {
            if (result.resultCode === 501) {
                relogin();
            } else if (result.resultCode === 200) {
                let useTime = result.time;
                result = {
                    total: result.total,
                    rows: result.rows
                };
                if (result.total === 0) {
                    $.messager.alert("系统提示", "查询成功 无数据");
                } else {
                    $.messager.alert("系统提示", "查询成功！ 一共 " + result.total + " 条数据" + "，耗时：" + useTime + " 秒");
                }
                if (result.total === 0) {

                }
                $("#dg").datagrid("loadData", result);
            }
        },
        error: function () {
            $.messager.alert("ERROR！", "查询失败");
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
    let activeColumns = [];
    let commonResult = {
        "服务器": "serverId",
        // "渠道id": "spId",
        "开服天数": "openDay",
        "新增玩家": "newaddplayer",
        "新增创号": "newAddCreateAccount",
        "新增创角": "newAddCreateRole",
        // "新增创角去除滚服": "newAddCreateRoleRemoveOld",
        // "创角率": "createAccountRate",
        // "创号转化率":"createAccountTransRate",

        "活跃玩家": "activePlayer",
        "充值次数": "rechargeTimes",
        "充值人数": "rechargeNumber",
        "充值金额": "rechargePayment",
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
//     "注收比": "zhushoubi",
//     "新增注收比": "addzhushoubi"
    };

    $.each(commonResult, function (index, value) {
        let column = {};
        column["field"] = value;
        column["title"] = index;
        column["align"] = 'center';
        column["width"] = 50;
        activeColumns.push(column);
    });

    let dg = $("#dg");
    let opts = dg.datagrid('options');
    let pager = dg.datagrid('getPager');

    dg.datagrid({
        frozenColumns: [[]], columns: [
            activeColumns
        ],
    });
    // .datagrid('loadData', activeResult.bodys);

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