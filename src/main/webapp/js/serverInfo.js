let baseDataGrid = {
    "日期": "date",
    "服务器": "serverId",
    "渠道id": "spId",
    "开服天数": "openDay",
    "新增玩家": "newaddplayer",
    "新增创号": "newAddCreateAccount",
    "新增创角": "newAddCreateRole",
    "新增创角去除滚服": "newAddCreateRoleRemoveOld",
    "创角率": "createAccountRate",
    "创号转化率": "createAccountTransRate",

    "活跃玩家": "activePlayer",
    "充值次数": "rechargeTimes",
    "充值人数": "rechargeNumber",
    "充值金额(分)": "rechargePayment",
    "活跃付费率": "activePayRate",
    "付费ARPU": "paidARPU",
    "活跃ARPU": "activeARPU",
    "当日首次付费人数": "nofPayers",
    "当日首次付费金额(分)": "nofPayment",
    "注册付费人数": "registeredPayers",
    "注册付费金额(分)": "registeredPayment",
    "注册付费ARPU": "registeredPaymentARPU",

    "累计充值": "totalPayment",
    "累计创角": "totalCreateRole",
    "累计充值人数": "totalRechargeNums",
    "总付费率": "totalRechargeRates",

    "注收比": "zhushoubi",
    "新增注收比": "addzhushoubi"
};

/**
 * @param {object}   commonResult
 * */
function initDatagrid(commonResult) {
    let activeColumns = [];
    $.each(commonResult, function (index, value) {
        let column = {};
        column["field"] = value;
        column["title"] = index;
        column["align"] = 'center';
        activeColumns.push(column);
    });

    let dg = $("#dg");
    initDataGrid(dg, activeColumns, null);
    $('#save_startTime').datebox('setValue', formatterDate(new Date(), 0));
    $('#save_endTime').datebox('setValue', formatterDate(new Date(), 1));
}

function exportToLocal() {
    $('#dg').datagrid('toExcel', 'dg.xls'); // export to excel
    // $('#dg').datagrid('print', 'DataGrid'); // print the datagrid
}

/*
 * @param {Date} date
 * @param {int} type
 * */
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

function initSpGameServer(type) {
    let select_spId = $("#save_spId");
    let select_gameId = $("#save_gameId");
    let select_serverId = $("#save_serverId");

    let spId = select_spId.val();
    let gameId = select_gameId.val();
    let serverId = select_serverId.val();

    let response;

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
                // console.info(result);
                response = result;
            }
        },
        error: function () {
            $.messager.alert("ERROR！", "获取游戏列表出错");
        }
    });

    switch (type) {
        case 1:
            select_spId.find("option").remove();
            select_spId.append("<option value=-1 selected=selected>请选择</option>");
            for (let res = 0; res < response.total; res++) {
                select_spId.append("<option value='" + response.rows[res] + "'>" + response.rows[res] + "</option>");
            }
            break;
        case 2:
            select_gameId.find("option").remove();
            select_gameId.append("<option value=-1 selected=selected>请选择</option>");
            for (let res = 0; res < response.total; res++) {
                let gameid = response.rows[res].id;
                let name = response.rows[res].name + "\t" + gameid;
                select_gameId.append("<option  value='" + gameid + "'>" + name + "</option>");
            }
            break;
        case 3:
            select_serverId.find("option").remove();
            select_serverId.append("<option value=-1 selected=selected>请选择</option>");
            for (let res = 0; res < response.total; res++) {
                select_serverId.append("<option value='" + response.rows[res] + "'>" + response.rows[res] + "</option>");
            }
            break;
        default:
            break;
    }
}

function search(type) {
    let gameId = $("#save_gameId").val();
    let serverId = $("#save_serverId").val();
    let spId = $("#save_spId").val();
    let startTime = $("#save_startTime").datetimebox("getValue");
    let endTime = $("#save_endTime").datetimebox("getValue");

    let data = {
        "type": type,
        "gameId": gameId,
        "serverId": serverId,
        "spId": spId.replace(/,/g, "|"),
        "startTime": startTime,
        "endTime": endTime
    };
    console.info(data);

    $.ajax({
        //获取数据
        url: "/rechargeSummary/searchRechargeSummary",
        type: "post",
        data: data,
        dataType: "json",
        async: false,
        success: function (result) {
            console.info(result);
            if (result.resultCode === 501) {
                relogin();
            } else if (result.resultCode === 200) {
                let useTime = result.time;
                if (result.total !== 0) {
                    let rows = result.rows;
                    rows = rows.map(function (row) {
                        row.date = row.date.substring(0, 4) + "-" + row.date.substring(4, 6) + "-" + row.date.substring(6);
                        row.createAccountRate = row.createAccountRate + "%";
                        row.activePayRate = row.activePayRate + "%";
                        return row;
                    });
                }
                result = {
                    total: result.total,
                    rows: result.rows
                };
                if (result.total === 0) {
                    $.messager.alert("系统提示", "查询成功 无数据");
                } else {
                    $.messager.alert("系统提示", "查询成功！ 一共 " + result.total + " 条数据" + "，耗时：" + useTime + " 秒");
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