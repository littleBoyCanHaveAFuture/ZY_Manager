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
        fitColumns: true,
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
    // dg2.datagrid('loadData', data);
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
    if (showTip === 0) {
        return '<a href="javascript:void(0)" style="color: blueviolet" onclick="openConfig(' + row.appid + ',' + row.channelid + ')">配置</a>' +
            '&nbsp;&nbsp;&nbsp;&nbsp;' +
            '<a style="color: grey">打包</a>';
    } else {
        return '<img style="width:12px;float:left;margin-top:5px;" src="/images/step_finish.png">' +
            '<a href="javascript:void(0)" style="color: grey" onclick="config(' + index + ')">已配置</a>&nbsp;&nbsp;' +
            '<a href="javascript:void(0)" style="color: blueviolet" onclick="openConfig(' + row.appid + ',' + row.channelid + ')">修改</a>&nbsp;&nbsp;' +
            '<a href="javascript:void(0)" style="color: blueviolet" onclick="config(' + index + ')">打包</a>&nbsp;&nbsp;' +
            '<a href="javascript:void(0)" style="color: blueviolet" onclick="config(' + index + ')">测试</a>&nbsp;&nbsp;' +
            '<a href="javascript:void(0)" style="color: blueviolet" onclick="config(' + index + ')">分发</a>&nbsp;&nbsp;';
    }
}

function openConfig(appId, channelId) {
    console.info("openConfig");
    let dlg = $("#dlg");
    dlg.dialog({
        title: '渠道配置',//弹出框的标题
        modal: true,//模态框
        closed: true,//默认弹出框关闭
        width: 1200,//弹出框宽度
        height: 600,//弹出框高度
        buttons: '#dlg-buttons',//弹出框底部按钮。#xx代表按钮所在的div。
    });
    dlg.dialog({
        onOpen: function () {
            loadChannelConfig(appId, channelId);
        }, onClose: function () {
            console.log("close");
        }
    });
    dlg.dialog("open");

}

function sp_Select() {
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

let arr = {};
let bodyjson = {};
let parapame = {};
let showTip = "0";
let feeArr = [];
let channel_sdk_name = "";
let feeConfig;


//配置游戏的渠道参数
function loadChannelConfig(appId, channelId) {
    $("#dlg-appid").val(appId);
    $("#dlg-channelid").val(channelId);
    console.info($("#dlg-appid").val());
    console.info($("#dlg-channelid").val());

    hideCloumn();
    $.ajax({
        type: 'post',
        url: '/game/channelConfig',
        data: {
            "appId": appId,
            "channelId": channelId
        },
        dataType: 'json',
        success: function (res) {
            console.info(res);
            if (res.hasOwnProperty("resultCode") && res.resultCode === 501) {
                relogin();
                return;
            }
            // if (res.channel_id === "0") {
            //     $("#table_report").hide();
            // }else {
            //     $("#table_report").show();
            // }
            parapame = res;
            channel_sdk_name = res.channel_sdk_name;

            if (res.channel_config_key != null && res.channel_config_key !== "") {
                // arr = JSON.parse(parapame.channel_config_key);
                arr = parapame.channel_config_key;

                configKey(arr, "table_report");
                $("#gameTest").hide();
            }
            setValue(res);
        }
    });
}

function configKey(arr, id) {
    let c = "";
    for (let o in arr) {
        if (document.getElementById("tr_" + arr[o].name)) {
            continue;
        }
        let res = addTr(arr[o]);
        c += res;
        // console.log(res);
    }
    if (c !== "") {
        $("#" + id + "").append(c);
    }
    for (let o in arr) {
        if (document.getElementById("tr_" + arr[o].name)) {
            document.getElementById("tr_" + arr[o].name).hidden = false;
        }
    }
}

function addTr(o) {
    let valData = "";
    let valView = "";
    let valStyle = "style='width:94%;'";
    if (o["alert"]) showTip = o["alert"];
    if (o["val"]) {
        valData = o["val"];
        valView = "readonly='readonly'";
        valStyle = "style='width:94%;font-weight:bold;border: 0;color:#000;'";
    }

    return '<tr id=tr_' + o.name + '>' +
        '<td style="width:100px;text-align: left;padding-top: 13px;">' + o.showName + ':</td>' +
        '<td>' +
        '<input ' + valStyle + ' type="text" ' + valView + ' name="' + o.name + '" id="' + o.name + '" value="' + valData + '"  maxlength="100000000" title="' + o.showName + '" check="' + o.required + '">' +
        '<div><font color="#aaa">注：' + o.desc + '</font></div>' +
        '</td>' +
        '</tr>';
}

let save_Config = {};

function setValue(res) {
    let gotourl = "";
    if (res.h5_url) {
        gotourl = res.h5_url;
        gotourl = gotourl.replace("{game}", res.channel_sdk_code);
        if (gotourl.indexOf("?") < 0) {
            gotourl += "?";
        }
        if (gotourl.indexOf("GameId") < 0) {
            gotourl += "&GameId=" + res.app_id;
        }
        if (gotourl.indexOf("GameKey") < 0) {
            gotourl += "&GameKey=" + res.game_key;
        }
        if (gotourl.indexOf("ChannelCode") < 0) {
            gotourl += "&ChannelCode=" + res.sdkindex;
        }

        document.getElementById("url").value = gotourl;
    }
    if (res.channel_callback_url != null && res.channel_callback_url !== "") {
        $("#channel_callback_url").show();
        let callback = res.channel_callback_url;
        callback = callback.replace("{appid}", res.app_id);
        callback = callback.replace("{sdkindex}", res.sdkindex);
        callback = callback.replace("{channel_code}", res.channel_sdk_code);
        document.getElementById("callback_url").value = "" + callback + "";
    } else {
        $("#channel_callback_url").hide();
    }

    if (res.config_key != null && res.config_key !== "") {
        // let arr = JSON.parse(res.config_key);
        let arr = res.config_key;
        for (let o in arr) {
            if (document.getElementById(o)) {
                document.getElementById(o).value = arr[o];
            }
        }
        if (showTip === "1") {
            // $("#soeasyurl").html("注：提供给渠道的地址 <br>https://cn.soeasysdk.com/soeasysr/gameini/apps_conf_html/" + res.app_id + "/" + res.sdkindex + "/" + "<br>http://soeasysdk.com/soeasysr/gameini/apps_conf_html/" + res.app_id + "/" + res.sdkindex + "/");
        }
    } else {
        if (showTip === "1") {
            // $("#soeasyurl").html("注：提供给渠道的地址 <br>https://cn.soeasysdk.com/soeasysr/gameini/apps_conf_html/" + res.app_id + "/" + res.sdkindex + "/" + "<br>http://soeasysdk.com/soeasysr/gameini/apps_conf_html/" + res.app_id + "/" + res.sdkindex + "/");
        }
    }
    save_Config = res.c

}

function updateConfigKey() {
    console.info($("#dlg-appid").val());
    console.info($("#dlg-channelid").val());
    $.ajax({
        type: 'post',
        url: '/game/updateConfig',
        data: {
            "appId": $("#dlg-appid").val(),
            "channelId": $("#dlg-channelid").val()
        },
        dataType: 'json',
        success: function (res) {
            console.info(res);
            if (res.hasOwnProperty("resultCode") && res.resultCode === 501) {
                relogin();
                return;
            }
            parapame = res;
            channel_sdk_name = res.channel_sdk_name;

            if (res.channel_config_key != null && res.channel_config_key !== "") {
                // arr = JSON.parse(parapame.channel_config_key);
                arr = parapame.channel_config_key;
                configKey(arr, "table_report");
                $("#gameTest").hide();
            }
            setValue(res);
        }
    });
}

//保存
function save() {
    feeArr.length = 0;
    var flag = true;

    if (!checkParamS()) return false;
    if ($("#url").val() === "") {
        $("#url").tips({
            side: 3,
            msg: "渠道入口地址不能为空！",
            bg: '#AE81FF',
            time: 2
        });
        return false;
    } else if (IsURL($("#url").val()) === false) {
        $("#url").tips({
            side: 3,
            msg: '请填写正确的源地址，并以http或https开头',
            bg: '#AE81FF',
            time: 2
        });
        $("#url").focus();
        return false;
    } else if ($("#url").val().indexOf("GameId") < 0) {
        $("#url").tips({
            side: 3,
            msg: '请带上GameId参数',
            bg: '#AE81FF',
            time: 2
        });
        $("#url").focus();
        return false;
    } else if ($("#url").val().indexOf("GameKey") < 0) {
        $("#url").tips({
            side: 3,
            msg: '请带上GameKey参数',
            bg: '#AE81FF',
            time: 2
        });
        $("#url").focus();
        return false;
    } else if ($("#url").val().indexOf("ChannelCode") < 0) {
        $("#url").tips({
            side: 3,
            msg: '请带上ChannelCode参数',
            bg: '#AE81FF',
            time: 2
        });
        $("#url").focus();
        return false;
    }

    if (feeConfig === "1") {
        if (feeArr.length < 1) {
            if ($('#feelist').children().length === 0) {
                top.showInfo("请先填写计费点！");
                return false;
            }
        }
    }

    parapame.config_key = JSON.stringify(bodyjson);
    parapame.h5_url = $("#url").val().trim();
    let p = "";
    if ("" === "1") {
        parapame.t = "1";
    }
    if (flag === true) {
        $.ajax({
            type: 'post',
            url: '/game/updateChannelConfig',
            data: parapame,
            dataType: 'json',
            success: function (res) {
                $('#dlg').dialog('close');
            }
        });
    }

}

/**
 * @return {boolean}
 */
function IsURL(urlString) {
    let regExp = /(http|ftp|https):\/\/[\w]+(.[\w]+)([\w\-\.,@?^=%&:\/~\+#]*[\w\-\@?^=%&\/~\+#])/;
    return !!urlString.match(regExp);
}

function checkParamS() {
    for (var o in arr) {
        let val = document.getElementById(arr[o].name).value;
        let check = document.getElementById(arr[o].name).getAttribute("check");
        val = val.trim();
        if (val === "" && check === "1") {
            $("#" + arr[o].name).tips({
                side: 3,
                msg: arr[o].showName + "不能为空！",
                bg: '#AE81FF',
                time: 2
            });
            return false;
        } else {
            bodyjson[arr[o].name] = val;
        }
    }
    return true;
}

function hideCloumn() {
    if (arr == null) {
        return;
    }
    for (let o in arr) {
        let value = document.getElementById(arr[o].name).value;
        let check = document.getElementById(arr[o].name).getAttribute("check");
        document.getElementById("tr_" + arr[o].name).hidden = true;
        // $('#table_report').datagrid('hideColumn', "tr_" + arr[o].name);
    }

}
