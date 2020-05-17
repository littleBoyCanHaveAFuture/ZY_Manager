<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Insert title here</title>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/jquery-easyui-1.7.0/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/jquery-easyui-1.7.0/themes/icon.css">
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/multiple-select-1.5.2/multiple-select.css">
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.7.0/jquery.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.7.0/jquery.easyui.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.7.0/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/multiple-select-1.5.2/multiple-select.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/common.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/serverInfo.js"></script>
</head>

<body style="margin:1px;" id="ff">

<table id="dg" title="" class="easyui-datagrid" pagination="true" fitcolumns="true"
       rownumbers="true" fit="true" showFooter="true" toolbar="#tb">
    <thead>
    </thead>
</table>

<div id="tb">
    <div>
        <label for="save_gameId"></label>
        <span style="color: blue;">游戏:</span>
        <select title="选择游戏" id="save_gameId" name="gameId" onchange="initSpGameServer(1)">
            <option value=" -1" selected="selected">请选择</option>
        </select>

        <select title="选择渠道" id="save_spId" name="spId" onchange="initSpGameServer(3)" hidden="hidden">
            <option value="-1" selected="selected">请选择</option>
        </select>

        <label for="save_gamename"></label><input type="text" id="save_gamename">
        <button onclick=" loadDiscountData()" class="easyui-linkbutton" style="margin-left: 50px">查询</button>
    </div>
    <div>
        <a href="javascript:addDiscount()" class="easyui-linkbutton" iconCls="icon-add" plain="true">添加</a>
        <a href="javascript:modifyDiscount()" class="easyui-linkbutton" iconCls="icon-edit" plain="true">修改</a>
        <a href="javascript:deleteDiscount()" class="easyui-linkbutton" iconCls="icon-remove" plain="true">删除</a>
    </div>
</div>


<div id="dlg-buttons">
    <a href="javascript:saveDiscount()" class="easyui-linkbutton" iconCls="icon-ok">保存</a>
    <a href="javascript:closeDiscount()" class="easyui-linkbutton" iconCls="icon-cancel">关闭</a>
</div>

<div id="dlg" class="easyui-dialog" closed="true" buttons="#dlg-buttons"
     style="width: 600px;height:350px;padding: 10px 20px; position: relative; z-index:1000;">
    <div style="padding-top:50px;  float:left; width:95%; padding-left:30px;">
        <input type="hidden" name="save_id" id="save_id">
        <table>
            <tr>
                <td>
                    <input type="text" id="dlg_id" hidden="hidden">
                </td>
            </tr>
            <tr>
                <td>游戏id：</td>
                <td>
                    <select title="选择游戏" id="dlg_gameid" onchange="initDlgGameList(1)">
                        <option value="-1" selected="selected">请选择</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td>渠道id：</td>
                <td>
                    <select title="选择渠道" id="dlg_channelid">
                        <option value="-1" selected="selected">请选择</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td>折扣：</td>
                <td>
                    <select title="选择游戏" id="dlg_discount">
                        <option value="-1" selected="selected">请选择</option>
                    </select>
                </td>
            </tr>
        </table>
    </div>
</div>

</body>
<script type="text/javascript">
    let t_type = 4;
    $(function () {
        let commonResult = {
            "id": "id",
            "游戏id": "gameId",
            "游戏名称": "name",
            "渠道id": "channelId",
            "折扣(百分制)": "disCount",
        };
        initDatagrid(commonResult);
        initDlgGameList(2);
        let select_dlg_discount = $("#dlg_discount");
        select_dlg_discount.find("option").remove();
        select_dlg_discount.append("<option value=-1 selected=selected>请选择</option>");

        for (let res = 1; res <= 9; res++) {
            let value = res * 10;
            select_dlg_discount.append("<option  value='" + value + "'>" + value + "%" + "</option>");
        }
        loadDiscountData();
        initSpGameServer(2);
    });

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
    }

    function loadDiscountData() {
        let url = "/server/getAllGameGameDiscount";
        let dg = $("#dg");
        let opts = getDatagridOptions(dg);
        let pageNumber = opts.pageNumber;
        let pageSize = opts.pageSize;

        let gameId = $("#save_gameId").val();
        let name = $("#save_gamename").val();

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
                    // let rows = result.rows;
                    // rows = rows.map(function (row) {
                    //     return row;
                    // });
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

    function addDiscount() {
        initDlgGameList(2);
        $("#dlg").dialog("open").dialog("setTitle", "添加游戏折扣");
        t_type = 3;

    }

    //修改discount
    function modifyDiscount() {
        t_type = 2;
        let selectedRows = $("#dg").datagrid('getSelections');
        if (selectedRows.length === 0) {
            tip("系统提示", "请选择要修改的数据！");
            return false;
        }
        if (selectedRows.length !== 1) {
            tip("系统提示", "请选择要一条要修改的数据！");
            return false;
        }
        let id = selectedRows[0].id;
        let gameId = selectedRows[0].gameId;
        let channelId = selectedRows[0].channelId;
        let name = selectedRows[0].name;
        let discount = selectedRows[0].discount;

        $("#dlg_id").val(id);

        let select_dlg_gameid = $("#dlg_gameid");
        select_dlg_gameid.find("option").remove();
        select_dlg_gameid.append("<option value='" + gameId + "'>" + gameId + "</option>");


        let select_dlg_channelid = $("#dlg_channelid");
        select_dlg_channelid.find("option").remove();
        select_dlg_channelid.append("<option value='" + channelId + "'>" + channelId + "</option>");

        let all_options2 = document.getElementById("dlg_discount").options;
        for (let i = 0; i < all_options2.length; i++) {
            all_options2[i].selected = parseInt(all_options2[i].value) === parseInt(discount);
        }

        $("#dlg").dialog("open").dialog("setTitle", "添加游戏折扣");

    }

    function deleteDiscount() {
        t_type = 1;
        let selectedRows = $("#dg").datagrid('getSelections');
        if (selectedRows.length === 0) {
            tip("系统提示", "请选择要删除的数据！");
            return false;
        }
        if (selectedRows.length !== 1) {
            tip("系统提示", "请选择要一条要删除的数据！");
            return false;
        }
        let id = selectedRows[0].id;
        let gameId = selectedRows[0].gameId;
        let channelId = selectedRows[0].channelId;
        let name = selectedRows[0].name;
        let discount = selectedRows[0].discount;
        let data = {
            "id": id,
            "gameId": gameId,
            "channelId": channelId,
            "discount": discount,
            "type": t_type,
        };

        $.messager.confirm("系统提示", "您确认要删除这" + "<font color=red>" + 1 + "</font>" + "条数据吗？",
            function (r) {
                if (r) {
                    changeGameDiscount(data);
                    loadDiscountData();
                }
            });
    }

    function saveDiscount() {
        let id = $("#dlg_id").val();
        let gameId = $("#dlg_gameid").val();
        let discount = $("#dlg_discount").val();
        let channelid = $("#dlg_channelid").val();
        let data = {
            "id": id,
            "gameId": gameId,
            "channelId": channelid,
            "discount": discount,
            "type": t_type,
        };
        changeGameDiscount(data);
        t_type = 4;
        closeDiscount();
        loadDiscountData();
    }

    function closeDiscount() {
        $("#dlg").dialog("close");

        resetValue();
    }

    function resetValue() {
        $("#dlg_id").val("");
        let all_options = document.getElementById("dlg_gameid").options;
        for (let i = 0; i < all_options.length; i++) {
            all_options[i].selected = all_options[i].value === "-1";
        }
        all_options = document.getElementById("dlg_discount").options;
        for (let i = 0; i < all_options.length; i++) {
            all_options[i].selected = all_options[i].value === "-1";
        }
        all_options = document.getElementById("dlg_channelid").options;
        for (let i = 0; i < all_options.length; i++) {
            all_options[i].selected = all_options[i].value === "-1";
        }
    }

    function changeGameDiscount(data) {
        $.ajax({
            url: "/server/changeGameDiscount",
            type: "post",
            data: data,
            dataType: "json",
            async: false,
            success: function (result) {
                if (result.resultCode === 501) {
                    relogin();
                } else if (result.resultCode === 200) {
                    tip("系统提示", "修改成功");
                    console.info(result.state);
                    console.info(result.message);
                }
            },
            error: function () {
                tip("ERROR！", "修改失败");
            }
        });
    }

    function initDlgGameList(type) {
        let select_spId = $("#save_spId");
        let select_gameId = $("#save_gameId");
        let select_serverId = $("#save_serverId");
        let select_dlg_gameid = $("#dlg_gameid");
        let select_dlg_channelid = $("#dlg_channelid");
        let spId = select_spId.val();
        let gameId = select_gameId.val();
        let serverId = select_serverId.val();

        let response;

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
                select_dlg_channelid.find("option").remove();
                select_dlg_channelid.append("<option value=-1 selected=selected>请选择</option>");
                for (let res = 0; res < response.total; res++) {
                    select_spId.append("<option value='" + response.rows[res] + "'>" + response.rows[res] + "</option>");
                    select_dlg_channelid.append("<option value='" + response.rows[res] + "'>" + response.rows[res] + "</option>");
                }
                break;
            case 2:
                select_gameId.find("option").remove();
                select_gameId.append("<option value=-1 selected=selected>请选择</option>");
                select_dlg_gameid.find("option").remove();
                select_dlg_gameid.append("<option value=-1 selected=selected>请选择</option>");

                for (let res = 0; res < response.total; res++) {
                    let gameId = response.rows[res].id;
                    let name = response.rows[res].name + "\t" + gameId;
                    select_gameId.append("<option  value='" + gameId + "'>" + name + "</option>");
                    select_dlg_gameid.append("<option  value='" + gameId + "'>" + name + "</option>");
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


</script>
</html>
