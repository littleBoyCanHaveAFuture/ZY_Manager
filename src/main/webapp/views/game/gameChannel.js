$(function () {
    let dlg = $('#dd');
    dlg.dialog({
        title: 'My Dialog',
        width: 1024,
        height: 500,
        closed: true,
        cache: false,
        minimizable: true,
        maximizable: true,
        resizable: true,
        // href: 'get_content.php',
        modal: true,
        buttons: [{
            text: '完成',
            iconCls: 'icon-save',
            handler: function () {
                dlg.dialog("close");
                loadGameSpTab(1);
            }

        }]

    });
    let dg1 = $('#dg1');
    dg1.datagrid({
        scrollbarSize: 0,
        rownumbers: true,
        columns: [[
            {field: 'appid', title: '游戏id', width: 180, align: 'center', hidden: true},
            {field: 'icon', title: '已选渠道图标', width: 180, align: 'center', formatter: showPhoto},
            {field: 'name', title: '渠道名称', width: 180, align: 'center'},
            {field: 'channelid', title: '渠道id', width: 180, align: 'center'},
            {field: 'version', title: '版本号', width: 180, align: 'center'},
            {field: 'status', title: '状态', width: 180, align: 'center', hidden: true},
            {field: 'operation', title: '配置', align: 'center', formatter: config},
        ]],
        fit: true,
        showFooter: true,
        pagination: true,
        pageSize: 20,
        pageList: [10, 20]
    });

    let dg2 = $('#dg2');
    dg2.datagrid({
        rownumbers: true,
        columns: [[
            {field: 'appid', title: '游戏id', width: 180, align: 'center', hidden: true},
            {field: 'icon', title: '渠道图标', width: 180, align: 'center', formatter: showPhoto},
            {field: 'name', title: '渠道名称', width: 180, align: 'center'},
            {field: 'channelid', title: '渠道id', width: 180, align: 'center'},
            {field: 'version', title: '版本号', width: 180, align: 'center'},
            {field: 'status', title: '状态', width: 180, align: 'center', hidden: true},
            {field: 'operation', title: '配置', width: 150, align: 'center', formatter: set_select},
        ]],
        pagination: true,
        pageSize: 10,
        pageList: [10, 20]
    });

    let rows = [];
    rows.push({
        icon: 1,
        name: 2,
        channelid: 3,
        version: 4,
        status: 0,
    });
    let data = {
        total: rows.length,
        rows: rows
    };
    dg2.datagrid('loadData', data);
    loadGameSpTab(1);
});

//查询游戏的渠道
function loadGameSpTab(type) {
    let url;
    let dg;
    if (type === 1) {
        url = "/server/getGameSpList";
        dg = $("#dg1");
    } else {
        url = "/server/getAllGameSpList";
        dg = $("#dg2");
    }
    let opts = getDatagridOptions(dg);
    let pageNumber = opts.pageNumber;
    let pageSize = opts.pageSize;

    let gameId = $("#appid").val();
    let serverId = $("#serverid").val();
    let name = $("#nav-search-input").val();

    if (checkParam(gameId)) {
        gameId = -1;
    }
    if (checkParam(name)) {
        name = null;
    }
    if (checkParam(pageNumber) || pageNumber <= 0) {
        pageNumber = null;
    }
    if (checkParam(pageSize) || pageSize <= 0) {
        pageSize = null;
    }


    let response;
    $.ajax({
        url: url,
        type: "post",
        data: {"gameId": gameId, "name": name, "page": pageNumber, "rows": pageSize},
        dataType: "json",
        async: false,
        success: function (result) {
            if (result.resultCode === 501) {
                relogin();
            } else if (result.resultCode === 200) {
                if (result.total === 0) {
                    tip("系统提示", "查询成功 无数据");
                }
                response = {
                    total: result.total,
                    rows: result.rows
                };
            }
        },
        error: function () {
            tip("ERROR！", "查询失败");
        }
    });
    console.info(response);
    dg.datagrid("loadData", response);
}

function config(val, row, index) {
    if (row.configStatus === 0) {
        return '<a href="javascript:void(0)" style="color: blueviolet" onclick="openConfig(' + index + ')">配置</a>' +
            '&nbsp;&nbsp;&nbsp;&nbsp;' +
            '<a style="color: grey">打包</a>';
    } else {
        return '<img style="width:12px;float:left;margin-top:5px;" src="/images/step_finish.png">' +
            '<a href="javascript:void(0)" style="color: grey" onclick="config(' + index + ')">已配置</a>&nbsp;&nbsp;' +
            '<a href="javascript:void(0)" style="color: blueviolet" onclick="config(' + index + ')">修改</a>&nbsp;&nbsp;' +
            '<a href="javascript:void(0)" style="color: blueviolet" onclick="config(' + index + ')">打包</a>&nbsp;&nbsp;' +
            '<a href="javascript:void(0)" style="color: blueviolet" onclick="config(' + index + ')">测试</a>&nbsp;&nbsp;' +
            '<a href="javascript:void(0)" style="color: blueviolet" onclick="config(' + index + ')">分发</a>&nbsp;&nbsp;';
    }
}

function openConfig(index) {
    console.info("openConfig");
    let dlg = $("#dlg");
    dlg.dialog({
        title: '渠道配置',//弹出框的标题
        modal: true,//模态框
        closed: true,//默认弹出框关闭
        width: 800,//弹出框宽度
        height: 400,//弹出框高度
        buttons: '#dlg-buttons',//弹出框底部按钮。#xx代表按钮所在的div。
    });
    dlg.dialog({
        onOpen: function () {
        }
    });
    dlg.dialog("open");

}

function sp_select() {
    let dlg = $("#dd");
    dlg.dialog({
        onOpen: function () {
            loadGameSpTab(2);
        }
    });
    dlg.dialog("open").dialog("setTitle", "修改游戏-渠道信息");
}

function set_select(val, row, index) {
    if (row.status === 1) {
        return '<div id="sdk' + row.id + '" class="btn  btn-danger" onclick="editOpt(' + index + ')">取消</div>';
    } else {
        return '<div id="sdk' + row.id + '" class="btn  btn-primary" onclick="editOpt(' + index + ')">添加</div>';
    }
}

function editOpt(index) {
    let appid = $("#appid").val();

    let dg2 = $('#dg2');
    dg2.datagrid('selectRow', index);

    let row = dg2.datagrid('getSelected');
    if (row) {
        console.info(row);
        changeSdk(row);
        loadGameSpTab(2);
    }
    /*    let data = {
            "id": row.id,
            "gameId": $("#appid").val(),
            "spId": row.channelid,
            "loginUrl": row.loginUrl,
            "appId": row.appId,
            "appName": row.appName,
            "loginKey": row.loginKey,
            "payKey": row.payKey,
            "sendKey": row.sendKey,
            "type": type,
        };*/
}

function sdkCancel(app_id, channel_id) {
    let response = "fail";
    $.messager.confirm("系统提示", "确定要取消吗?您配置的数据将会被清空！",
        function (result) {
            if (result) {
                // $.ajax({
                //     type: 'post',
                //     url: 'product/deleteSdk',
                //     data: {'app_id': app_id, 'channel_id': channel_id},
                //     dataType: 'json',
                //     async: false,
                //     success: function (res) {
                // if (res.result === 'success') {
                //     response = res.result;
                // }

                // }
                // });
                response = "success";
            }
        });

    return response;
}

/**
 * 添加或删除渠道
 * @param row
 * */
function changeSdk(row) {
    let url = "/server/changeGameSp";
    let type = row.status === 0 ? 4 : 1;
    let gameId = $("#appid").val();
    let data = {
        "id": row.id,
        "gameId": gameId,
        "spId": row.channelid,
        "loginUrl": null,
        "appId": null,
        "appName": null,
        "loginKey": null,
        "payKey": null,
        "sendKey": null,
        "type": type,
    };

    $.ajax({
        url: url,
        type: "post",
        data: data,
        dataType: "json",
        async: false,
        success: function (result) {
            if (result.resultCode === 501) {
                relogin();
            } else if (result.resultCode === 200) {
                tip("系统提示", "修改成功");
            }
        },
        error: function () {
            tip("ERROR！", "修改失败");
        }
    });
}

function showPhoto(value, row, index) {
    if (row.icon) {
        return '<img src="' + row.icon + '"style="height:30px;" alt="">'
    } else {
        return null;
    }
}

//配置游戏的渠道参数
function loadChannelConfig() {
    let arr = {};
    let bodyjson = {};
    let parapame = {};
    let showTip = "0";
    let feeArr = [];
    let channel_sdk_name = "";
    let feeConfig;
    feeArr = {
        "screen": "0",
        "product_name": "测试122",
        "feeTempletList": [],
        "channel_sub_icon_url": "",
        "channel_callback_url": "http://cn.soeasysdk.com/ret/{channel_code}/{sdkindex}/{appid}",
        "h5_html_path": "",
        "h5_html_url": "",
        "meta_data": "",
        "channel_id": 806,
        "cate_type": "1",
        "id": 25923,
        "is_screen": "1",
        "sdkindex": 10654,
        "screen_url": "",
        "del_permission": "0",
        "reinforce": "0",
        "app_id": 3799,
        "channel_sdk_name": "yy渠道",
        "channel_config_key": "[{\"name\":\"key\",\"required\":\"1\",\"showName\":\"key\",\"desc\":\"渠道提供的key\",\"alert\":\"1\"},\r\n{\"name\":\"gamename\",\"required\":\"1\",\"showName\":\"gamename\",\"desc\":\"游戏名称\"}]",
        "h5_url": "https://source.huojianos.com/g1/game/index_zy_suyi.html",
        "channel_sdk_code": "h5_yy",
        "channel_feecode_config": "0"
    }

    $.ajax({
        type: 'post',
        url: '/game/channelConfig',
        data: {
            "id": "25923"
        },
        success: function (res) {
            console.info(JSON.stringify(res));
            parapame = res;
            channel_sdk_name = res.channel_sdk_name;
            if (res.channel_config_key != null && res.channel_config_key !== "") {
                arr = JSON.parse(parapame.channel_config_key);
                if (res.sdkindex === "315") {
                    $("#gameTest").show();
                    $("#testUrl").val("http://hlgame.mz30.cn/?code=" + res.fAppCode + "");
                    configZMKey(arr, res.fCpKey, res.fAppCode, res.fSecretKey, "table_report");
                    if (res.h5_html_path === "") {
                        $("#testTip1").show();
                    } else {
                        $("#testTip1").hide();
                    }
                } else {
                    configKey(arr, "table_report");
                    $("#gameTest").hide();
                }
            } else {
                $("#table_report").hide();
            }
            if (res.feeTempletList != null && res.feeTempletList != "") {
                var feeTempletList = res.feeTempletList;
                for (var i = 0; i < feeTempletList.length; i++) {
                    $("#feeUl").append(feeOption(feeTempletList[i]))
                }
            }
            feeConfig = res.channel_feecode_config;
            if (res.channel_feecode_config != null && res.channel_feecode_config != "") {
                if (res.channel_feecode_config == "1") {
                    $("#feeDiv").show();
                } else if (res.channel_feecode_config == "0") {
                    $("#feeDiv").hide();
                }
            } else {
                $("#feeDiv").hide();
            }
            setValue(res);

        },
        dataType: 'json'
    });
}

function configZMKey(arr, fCpKey, fAppCode, fSecretKey, id) {
    let c = "";
    for (let o in arr) {
        c += addZMTr(arr[o], fCpKey, fAppCode, fSecretKey);
    }
    $("#" + id + "").prepend(c);
}

function addZMTr(o, fCpKey, fAppCode, fSecretKey) {
    let str = "";
    if (o.showName === "APPCODE") {
        str = fAppCode;
    } else if (o.showName === "CPKEY") {
        str = fCpKey;
    } else if (o.showName === "secretkey") {
        str = fSecretKey;
    }
    return '<tr>' +
        '<td style="width:100px;text-align: left;padding-top: 13px;">' + o.showName + ':</td>' +
        '<td><input style="width:94%;" type="text" name="' + o.name + '" id="' + o.name + '" readonly="readonly" value="' + str + '" maxlength="1000" title="' + o.showName + '" check="' + o.required + '">' +
        '<div><font color="#aaa">注：' + o.desc + '</font></div>' +
        '</td>' +
        '</tr>';
}