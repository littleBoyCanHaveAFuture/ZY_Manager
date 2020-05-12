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
    "充值金额(元)": "rechargePayment",
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

    $('#save_startTime').datetimebox('setValue', formatterDate(new Date(), 0));
    $('#save_endTime').datetimebox('setValue', formatterDate(new Date(), 1));
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
    let times;
    if (type === 0) {
        times = date.getFullYear() + '-' + month + '-' + "01" + " " + "00" + ":" + "00";
    } else {
        times = date.getFullYear() + '-' + month + '-' + day + " " + hor + ":" + min;
    }

    return times;
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

    let url = "";
    if (type === 1) {
        url = "/channel/getAllChannel";
        url += "?gameId=" + gameId;
    } else if (type === 2) {
        //查询游戏
        url = "/channel/getAllGame";
    } else {
        url = "/channel/getAllServerId";
        url += "?gameId=" + gameId;
        url += "&spId=" + spId;
    }
    $.ajax({
        //获取下拉
        url: url,
        type: "get",
        async: false,
        dataType: "json",
        success: function (result) {
            if (result.resultCode === 501) {
                relogin();
            } else if (result.resultCode === 200) {
                console.info(result);
                response = result;
            }
        },
        error: function () {
            tip("ERROR！", "获取游戏列表出错");
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
                let gameId = response.rows[res].appId;
                let name = response.rows[res].appName + "\t" + gameId;
                select_gameId.append("<option  value='" + gameId + "'>" + name + "</option>");
            }
            break;
        case 3:
        case 4:
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
        "channelId": spId.replace(/,/g, "|"),
        "serverId": serverId,
        "startTime": startTime,
        "endTime": endTime
    };
    console.info(data);

    $.ajax({
        //获取数据
        // url: "/rechargeSummary/searchRechargeSummary",
        url: "/rechargeSummary/getRS",
        type: "post",
        data: data,
        dataType: "json",
        async: true,
        beforeSend: function () {
            console.info("beforeSend");
            $('#loadrs').html('(加载中...,请勿再次点击)');
        },
        success: function (result) {
            console.info(result);
            $('#loadrs').html('(查询完毕...)');
            if (result.resultCode === 501) {
                relogin();
            } else {
                if (result.state === false) {
                    tip("系统提示", result.message);
                    return;
                }
                let useTime = result.time;
                if (result.total !== 0) {
                    let rows = result.rows;
                    rows = rows.map(function (row) {
                        row.date = row.date.substring(0, 4) + "-" + row.date.substring(4, 6) + "-" + row.date.substring(6);
                        row.createAccountRate = row.createAccountRate + "%";
                        row.activePayRate = row.activePayRate + "%";
                        let money = row.rechargePayment;
                        row.rechargePayment = changeMoneyToYuan(money);
                        return row;
                    });
                }
                result = {
                    total: result.total,
                    rows: result.rows
                };
                if (result.total === 0) {
                    tip("系统提示", "查询成功 无数据");
                } else {
                    tip("系统提示", "查询成功！ 一共 " + result.total + " 条数据" + "，耗时：" + useTime + " 秒");
                }
                $("#dg").datagrid("loadData", result);


            }
        },
        error: function () {
            tip("ERROR！", "查询失败");
        }
    });
}

function changeMoneyToYuan(tmoney) {
    let money = tmoney.toString();
    let realmonet_yuan = 0;
    if (money.length > 2) {
        let fen1 = money.substr(0, money.length - 2);
        let fen2 = money.substr(money.length - 2, 2);
        realmonet_yuan = fen1 + "." + fen2;
    } else if (money.length === 2) {
        realmonet_yuan = "0." + money;
    } else if (money.length === 1) {
        realmonet_yuan = "0.0" + money;
    }
    return realmonet_yuan;
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
