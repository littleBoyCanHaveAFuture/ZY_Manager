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
    $("#dlg").dialog("open").dialog("setTitle", "添加游戏");
    // $('#dd3').dialog('open').dialog({
    //     title: "添加流程",
    //     width: 400,
    //     height: 300,
    //     iconCls: 'icon-add',                 //弹出框图标
    //     modal: true,
    // });
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

/*
function fee_manage() {
    top.jzts();
    var diag = new top.Dialog();
    diag.Drag = true;
    diag.Title = "计费模板管理";
    diag.URL = 'http://dev.soeasysdk.com:80/product/feeManage.do?sdk_type=3&app_id=' + 3616;
    diag.Width = 1024;
    diag.Height = 500;
    diag.CancelEvent = function () { //关闭事件
        window.location.href = 'product/h5sdk.do?app_id=3616';
        diag.close();
    };
    diag.show();
}

*/

/*    function doSearch() {
        search();
        return false;
    }

    function search() {
        getlist();
    }

    getlist();

    function getlist() {
        $.ajax({
            type: 'post',
            url: 'product/appChannel',
            data: {app_id: 3616, KEYWORD: $("#nav-search-input").val()},
            success: function (res) {
                var choosedList = res.choosedList;
                $("#t_body").html("");
                var fg = 0;
                var boolIndex = false;
                if (choosedList.length > 0) {
                    for (var i = 0; i < choosedList.length; i++) {
                        fg += choosedList[i].fg;
                        $("#t_body").append(Tbody(choosedList[i]));
                        if (choosedList[i].sdkindex == "315") {
                            boolIndex = true;
                        }
                    }
                } else {
                    $("#t_body").append("<tr class='main_info'>" +
                        "<td colspan='100' class='center'>没有相关数据</td>" +
                        "</tr>")
                }
                if (fg > 0) {
                    if (boolIndex == true) {
                        $(".btn-group").css("width", "85%");
                    } else {
                        $(".btn-group").css("width", "73%");
                    }
                    top.showPackBtn("h5", 3616);
                }
            },
            dataType: 'json'
        });
    }


    function Tbody(tab) {
        var down_color;
        var zmStr;
        var oprateStr;
        if (tab.appExist == "0" || tab.h5_html_path == "") {
            down_color = "down_color";
        } else {
            down_color = "";
        }
        if (tab.sdkindex == "315") {
            zmStr = "<div class='" + down_color + "' onclick=addH5GameCenter('" + tab.appCode + "')>测试</div><div class='" + down_color + "' onclick=addH5Feng('" + tab.app_id + "')>分发</div>";
            $(".btn-group").css("width", "73%");
        } else {
            zmStr = "";
        }
        if (tab.config_key != null && tab.config_key != "") {
            oprateStr += "<img style='width:12px;float:left;margin-top:5px;' src='http://res.soeasysdk.com/soeasy/images/step_finish.png'>";
            oprateStr += "<div style='color:#888;'>已配置</div>";
            oprateStr += "<div onclick=addParam('" + tab.id + "')>修改</div>";
        } else {
            oprateStr = "<div onclick=addParam('" + tab.id + "')>配置</div>";
        }
        return "<tr>" +
            "<td class='center' style='width: 50px;'>" +
            "<img id='sdkImg' style='height:30px;cursor: pointer;' src='" + tab.sdk_icon_url + "'>" +
            "</td>" +
            "<td class='center' style='width: 30px;'>" + tab.channel_sdk_name + "</td>" +
            "<td class='center' style='width: 6px;'>" + tab.sdkindex + "</td>" +
            "<td class='center' class='center'style='width: 30px;'>" + tab.channel_sdk_ver + "</td>" +
            "<td style='width: 70px;' class='center'>" +
            "<div class='btn-group' style='text-align:right;'>" +
            "<div class='inline position-relative' style='vertical-align: middle;cursor：pointer;'>" +
            oprateStr + "" +
            "<div class='" + down_color + "' onclick=sdkPackage('" + tab.id + "')>打包</div>" +
            zmStr
        "</div>" +
        "</div>" +
        "</td>" +
        "</tr>";
    }

    function addParam(id) {
        if ("" == "1")
            id += "&t=1";
        top.jzts();
        var diag = new top.Dialog();
        diag.Drag = true;
        diag.Title = "渠道配置";
        diag.URL = 'http://dev.soeasysdk.com:80/product/qudaoParamH5.do?id=' + id;
        diag.Width = 1024;
        diag.Height = 500;
        diag.CancelEvent = function () { //关闭事件
            getlist();
            diag.close();
        };
        diag.show();
    }

    function sdkPackage(id) {
        top.jzts();
        var diag = new top.Dialog();
        diag.Drag = true;
        diag.Title = "填写打包内容";
        diag.URL = 'http://dev.soeasysdk.com:80/product/sdkPackage.do?id=' + id;
        diag.Width = 512;
        diag.Height = 300;
        diag.CancelEvent = function () { //关闭事件
            if (diag.innerFrame.contentWindow.document.getElementById('goNextPage').value != "") {
                //window.location.href="pack/h5_goAppDownList.do?app_id="+3616;
                //top.getAppid("h5",app_id,"3");
                var pid = diag.innerFrame.contentWindow.document.getElementById('pid').value;
                var sdkindex = diag.innerFrame.contentWindow.document.getElementById('sdkin').value;
                addTask(pid, sdkindex);
            } else {
                location.reload();
            }
            diag.close();
        };
        diag.show();
    }

    function addTask(pid, sdkindex) {
        $.ajax({
            type: "POST",
            url: 'http://dev.soeasysdk.com:80//pack/addTaskH5.do?tm=' + new Date().getTime(),
            data: {appsdklist: pid},
            dataType: 'json',
            //beforeSend: validateData,
            cache: false,
            success: function (res) {
                if (res.result == "200") {
                    doTask(sdkindex);
                } else if (res.result == "900") {
                    top.getAppid("h5", "3616", "3");
                    window.location.href = "http://dev.soeasysdk.com:80//pack/h5_goAppDownList.do?app_id=3616";
                    parent.parent.showInfo("任务中已经在打包！");
                }
            }
        });
    }

    function doTask(text) {
        $.ajax({
            type: "POST",
            url: 'http://dev.soeasysdk.com:80//pack/doTaskH5.do?tm=' + new Date().getTime(),
            data: {app_id: "3616", text: text},
            dataType: 'json',
            //beforeSend: validateData,
            cache: false,
            success: function (res) {
                top.getAppid("h5", 3616, "3");
                window.location.href = "http://dev.soeasysdk.com:80//pack/h5_goAppDownList.do?app_id=3616";
            }
        });
    }

    function download(id) {
        window.location.href = 'http://dev.soeasysdk.com:80//product/down.do?id=' + id;
    }

    function addH5GameCenter(appCode) {
        window.open("http://hlgame.mz30.cn/?test=true&code=" + appCode);
    }

    function addH5Feng(app_id) {
        $.ajax({
            type: 'post',
            url: 'product/goFengFa',
            data: {"app_id": "" + app_id + ""},
            success: function (res) {
                if ("success" == res.result) {
                    top.location.href = "distribute_index";
                }
            },
            dataType: 'json'
        });
    }*/
