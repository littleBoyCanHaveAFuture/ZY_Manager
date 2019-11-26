$(function () {
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

    $('#dg').datagrid({
        frozenColumns: [[]], columns: [
            activeColumns.concat(frozenColumns)
        ],
    }).datagrid('loadData', activeResult.bodys);

});

function exportToLocal() {
    $('#dg').datagrid('toExcel', 'dg.xls'); // export to excel
    // $('#dg').datagrid('print', 'DataGrid'); // print the datagrid
}

function search() {
    $('#dg').datagrid('toExcel', 'dg.xls'); // export to excel
    // $('#dg').datagrid('print', 'DataGrid'); // print the datagrid
}