$(function () {
    initGameList();
    initServerList(2);
    initTableColumns();
    $('#payRecord_startTime').datebox('setValue', formatterDate(new Date(), 0));
    $('#payRecord_endTime').datebox('setValue', formatterDate(new Date(), 1));

    //初始化内容 下一页按钮
    let dg = $("#dg");
    let opts = dg.datagrid('options');
    let pager = dg.datagrid('getPager');
    pager.pagination({
        // pageSize: 10,//每页显示的记录条数，默认为10        　　　　　　　　　　//这里不设置的画分页页数选择函数会正确调用，否则每次点击下一页pageSize都会变回设置的值
        pageList: [5, 10, 15, 20],//可以设置每页记录条数的列表 　　　　　　　　　　　　
        // beforePageText: '第',//页数文本框前显示的汉字
        // afterPageText: '页    共 {pages} 页',
        // displayMsg: '当前显示 {from} - {to} 条记录   共 {total} 条记录',
        onChangePageSize: function () {
        },
        onSelectPage: function (pageNum, pageSize) {
            opts.pageNumber = pageNum;
            opts.pageSize = pageSize;
            selectPayRecord();
        }
    });
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

function initTableColumns() {
    let activeColumns = [];
    let commonResult = {
        "本地订单编号": "orderID",
        "游戏ID": "appID",
        "渠道ID": "channelID",
        "用户账号ID": "userID",
        "用户名": "username",

        "商品ID": "productID",
        "商品名称": "productName",
        "商品描述": "productDesc",
        "实际充值的金额": "money",
        "回调通知返回的金额": "realMoney",

        "币种": "currency",
        "角色ID": "roleID",
        "角色名称": "serverID",
        "服务器ID": "serverID",
        "服务器名称": "serverName",

        "订单状态": "state",
        "渠道订单号": "channelOrderID",
        // "扩展数据": "extension",
        "订单创建时间": "createdTime",
        "渠道SDK订单交易时间": "sdkOrderTime",

        "订单完成时间": "completeTime",
        "渠道回调游戏url": "notifyUrl"
    };
    $.each(commonResult, function (index, value) {
            let column = {};
            column["field"] = value;
            column["title"] = index;
            column["align"] = 'center';
            // column["width"] = 50;

            activeColumns.push(column);
        }
    );

    let dg = $("#dg");
    let opts = dg.datagrid('options');
    let pager = dg.datagrid('getPager');

    dg.datagrid({
        // frozenColumns: [[]],
        columns: [
            activeColumns
        ],
        // nowrap: true,
    })
    //.datagrid('loadData', ttt.bodys);
}

function selectPayRecord() {
    let dg = $("#dg");
    let opts = dg.datagrid('options');
    let pager = dg.datagrid('getPager');

    let pageNumber = opts.pageNumber;
    let pageSize = opts.pageSize;

    let orderId = $("#payRecordId").val();
    let payRecord_playerId = $("#payRecord_playerId").val();
    let pay_player_channel = $("#pay_player_channel").val();
    let pay_gameid = $("#pay_gameId").val();
    let payRecord_state = $("#payRecord_state").val();
    let payRecord_startTime = $("#payRecord_startTime").datetimebox("getValue");
    let payRecord_endTime = $("#payRecord_endTime").datetimebox("getValue");

    let data = {
        "orderID": null,
        "appID": pay_gameid,
        "channelID": pay_player_channel,
        "channelOrderID": orderId,
        "state": payRecord_state,
        "startTime": payRecord_startTime,
        "endTime": payRecord_endTime,
        "page": pageNumber,
        "rows": pageSize
    };
    $.ajax({
        //获取数据
        url: "/realtime/getPayRecord",
        type: "post",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(data),
        dataType: "json",
        async: false,
        success: function (result) {
            if (result.resultCode === 501) {
                relogin();
            } else if (result.resultCode === 200) {
                let rows = result.rows;
                let strState = [
                    "点开充值界面:未点充值按钮(取消支付)",
                    "选择充值方式界面:未选择充值方式(取消支付)",
                    "支付宝微信界面:未支付(取消支付)",
                    "支付成功:未发货",
                    "支付成功:已发货(交易完成)",
                    "支付成功:补单(交易完成)"
                ];

                rows = rows.map(function (row) {
                    if (row.state > strState.length || row.state < 0) {
                        row.state = "未知"
                    }
                    row.createdTime = formatterDate(new Date(row.createdTime.time), 1);
                    row.sdkOrderTime = formatterDate(new Date(row.sdkOrderTime.time), 1);
                    row.completeTime = formatterDate(new Date(row.completeTime.time), 1);
                    row.state = strState[row.state];
                    if (row.notifyUrl === "null") {
                        row.notifyUrl = "";
                    }

                    return row;
                });

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

                $("#dg").datagrid("loadData", result);
            }
        },
        error: function () {
            $.messager.alert("ERROR！", "查询失败");
        }
    });
}

function loadPayRecord() {

}

function exportPayRecord() {
    $('#dg').datagrid('toExcel', 'dg.xls'); // export to excel
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
                let select_gameId = $("#pay_gameId");
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
                if (type === 1) {
                    let select_serverId = $("#save_serverId");
                    select_serverId.find("option").remove();
                    select_serverId.append("<option value=-1 selected=selected>请选择</option>");
                    for (let res = 0; res < result.total; res++) {
                        select_serverId.append("<option value='" + result.rows[res] + "'>" + result.rows[res] + "</option>");
                    }
                } else {
                    let select_spId = $("#pay_player_channel");
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
