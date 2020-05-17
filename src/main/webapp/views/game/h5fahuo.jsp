<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport"
          content="width=device-width,initial-scale=1,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no"/>
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
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/datagrid-export/datagrid-export.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/common.js"></script>

</head>
<body style="margin:1px;">

<table id="dg" title="使用指南：请先选择游戏、渠道，再查询区服！" class="easyui-datagrid" pagination="true" fitcolumns="true"
       rownumbers="true" fit="true" showFooter="true" toolbar="#tb">
    <thead>
    </thead>
</table>

<div id="tb" fitcolumns="true">
    <div id="tbs">
        <label for="oid">玩家id：</label>
        <input type="text" id="oid" size="40" onkeydown=""/>

        <%--        <label for="save_startTime"></label>--%>
        <%--        <span style="color: blue;">开始时间:</span>--%>
        <%--        <input class="easyui-datetimebox" id="save_startTime" name="startTime"--%>
        <%--               data-options="required:true,showSeconds:false" style="width:150px">--%>
        <%----%>
        <%--        <label for="save_endTime"></label>--%>
        <%--        <span style="color: blue;margin-left:50px">结束时间:</span>--%>
        <%--        <input class="easyui-datetimebox" id="save_endTime" name="endTime"--%>
        <%--               data-options="required:true,showSeconds:false" style="width:150px">--%>

        <a href="javascript:search($('#oid').val(),null,null)" class="easyui-linkbutton" style="margin-left:10px"
           iconCls=" icon-search" plain="true">查询</a>


        <input type="text" id="ex_status" readonly="readonly" style="float: right;width: 50px"
               onclick="changeStatus();"/>
        <label for="ex_status" style="float: right">兑换功能：</label>
    </div>
</div>

</body>
<script type="text/javascript">
    $(function () {
        let dg = $("#dg");
        dg.datagrid({
            scrollbarSize: 0,
            rownumbers: true,
            columns: [[
                {field: 'id', title: '主键id', width: 180, align: 'center', hidden: false},
                {field: 'openId', title: '玩家id', width: 300, align: 'center'},
                {field: 'itemId', title: '商品id', width: 60, align: 'center'},
                {field: 'exchangeTime', title: '申请时间', width: 170, align: 'center'},
                {field: 'status', title: '申请状态', width: 90, align: 'center', formatter: showstatus},
                {field: 'message', title: '信息', width: 180, align: 'center', hidden: true},
                {field: 'finishedTime', title: '完成时间', width: 170, align: 'center'},
                {field: 'address', title: '收货地址', align: 'center'},
                {field: 'phone', title: '手机', width: 120, align: 'center'},
                {field: 'operation', title: '配置', align: 'center', formatter: config},
            ]],
            fit: true,
            showFooter: true,
            pagination: true,
            pageSize: 20,
            pageList: [10, 20]
        });


        // $('#save_startTime').datetimebox('setValue', formatterDate(new Date(), 0));
        // $('#save_endTime').datetimebox('setValue', formatterDate(new Date(), 1));

        let id = $('#oid');

        let opts = getDatagridOptions(dg);
        let pager = dg.datagrid('getPager');
        let pageNumber = opts.pageNumber;
        let pageSize = opts.pageSize;

        pager.pagination({
            pageSize: 10,                                    //每页显示的记录条数，默认为10     //这里不设置的画分页页数选择函数会正确调用，否则每次点击下一页
            pageList: [5, 10, 15, 50],                       //可以设置每页记录条数的列表 　　　 //pageSize都会变回设置的值
            beforePageText: '第',                            //页数文本框前显示的汉字
            afterPageText: '页    共 {pages} 页',
            displayMsg: '当前显示 {from} - {to} 条记录   共 {total} 条记录',
            onChangePageSize: function () {
            },

            onSelectPage: function (pageNum, pageSize) {
                opts.pageNumber = pageNum;
                opts.pageSize = pageSize;
                search($('#oid').val(), pageNum, pageSize);
            }
        });

        search("-1", pageNumber, pageSize);
        loadStatus();
    });

    function changeStatus() {
        $.ajax({
            url: "/h5/setExchangeStatus",
            type: "get",
            dataType: "json",//预期服务器返回的数据类型
            async: false,
            success: function (result) {
                console.log("changeStatus:" + result.status);
                let status;
                if (result.status === true || result.status === "true") {
                    status = "已开启";
                } else {
                    status = "已关闭";
                }
                $("#ex_status").val(status);
            },
            error: function () {
                tip("ERROR！", "查询失败");
            }
        });
    }

    function loadStatus() {
        $.ajax({
            url: "/h5/getExchangeStatus",
            type: "get",
            dataType: "json",//预期服务器返回的数据类型
            async: false,
            success: function (result) {
                console.log("loadStatus:" + result.status);
                let status;
                if (result.status === true || result.status === "true") {
                    status = "已开启";
                } else {
                    status = "已关闭";
                }
                $("#ex_status").val(status);
            },
            error: function () {
                tip("ERROR！", "查询失败");
            }
        });
    }

    function config(val, row, index) {
        if (row.status === 0) {
            return '<a href="javascript:void(0)" style="color: blueviolet" onclick="agree(\'' + row.id + '\',\'' + 1 + '\')">同意</a>'
                + '&nbsp;&nbsp;&nbsp;&nbsp;'
                + '<a href="javascript:void(0)" style="color: red" onclick="agree(\'' + row.id + '\',\'' + 2 + '\')">拒绝</a>'
        } else {
            return '<a href="javascript:void(0)" style="color:black" ">无需操作</a>';
        }

    }

    function showstatus(val, row, index) {
        if (row.status === 0) {
            return '<a href="javascript:void(0)" style="color: grey" ">待审核</a>';
        } else if (row.status === 1) {
            return '<a href="javascript:void(0)" style="color: blueviolet" ">已同意</a>';
        } else if (row.status === 2) {
            return '<a href="javascript:void(0)" style="color: red" ">已拒绝</a>';
        } else {
            return '<a href="javascript:void(0)" style="color:black" ">未知</a>';
        }
    }

    function agree(id, status) {
        $.ajax({
            url: "/h5/confirmExRecord?id=" + id + "&status=" + status,
            type: "post",
            dataType: "json",//预期服务器返回的数据类型
            contentType: "application/json; charset=utf-8",
            async: false,
            success: function (result) {
                console.log("opt:" + result);
                if (result.hasOwnProperty("resultCode") && result.resultCode === 501) {
                    relogin();
                    return;
                }
                search($('#oid').val(), 1, 10);
            },
            error: function () {
                tip("ERROR！", "查询失败");
            }
        });
    }

    function search(openid, page, rows) {
        if (openid == null || openid === "" || openid === "undefined") {
            openid = "-1";
        }
        if (page == null || page === "" || page === "undefined") {
            page = 1;
        }
        if (rows == null || rows === "" || rows === "undefined") {
            rows = 10;
        }
        let param = "?openid=" + openid + "&page=" + page + "&rows=" + rows;

        $.ajax({
            url: "/h5/getExRecordList" + param,
            type: "get",
            dataType: "json",
            async: false,
            success: function (result) {
                console.info(result);
                if (result.hasOwnProperty("resultCode") && result.resultCode === 501) {
                    relogin();
                    return;
                }
                if (result.num !== 0) {
                    result = {
                        rows: result.rows,
                        total: result.num
                    };
                    $("#dg").datagrid("loadData", result);
                }
            },
            error: function () {
                tip("ERROR！", "查询失败");
            }
        });
    }//登录超时 重新返回到登录界面
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

</script>
</html>
