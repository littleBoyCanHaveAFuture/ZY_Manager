<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport"
          content="width=device-width,initial-scale=1,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no"/>
    <title>Insert title here</title>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/jquery-easyui-1.3.3/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/jquery-easyui-1.3.3/themes/icon.css">
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/multiple-select-1.5.2/multiple-select.css">
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.3.3/jquery.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.3.3/jquery.easyui.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.3.3/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/multiple-select-1.5.2/multiple-select.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/datagrid-export/datagrid-export.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/common.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/serverInfo.js"></script>
</head>
<body style="margin:1px;">

<table id="dg" title="使用指南：请先选择渠道，然后查询对应的游戏、区服！" class="easyui-datagrid" pagination="true" fitcolumns="true"
       rownumbers="true" fit="true" showFooter="true" toolbar="#tb">
    <thead>

    </thead>
</table>

<div id="tb" fitcolumns="true">
    <div id="tbs">
        <label for="save_spId"></label>
        <span style="color: blue; ">渠道:</span>
        <select title="选择渠道" id="save_spId" name="spId">
            <option value="-1" selected="selected">请选择</option>
        </select>

        <label for="save_gameId"></label>
        <span style="color: blue;margin-left:50px  ">游戏:</span>
        <select title="选择游戏" id="save_gameId" name="gameId">
            <option value="-1" selected="selected">请选择</option>
        </select>

        <label for="save_serverId"></label>
        <span style="color: blue; margin-left:50px">区服:</span>
        <select title="选择区服" id="save_serverId" name="serverId">
            <option value="-1" selected="selected">请选择</option>
        </select>

        <label for="save_startTime"></label>
        <span style="color: blue;margin-left:50px">开始时间:</span>
        <input class="easyui-datetimebox" id="save_startTime" name="startTime"
               data-options="required:true,showSeconds:false" style="width:150px">

        <label for="save_endTime"></label>
        <span style="color: blue;margin-left:50px">结束时间:</span>
        <input class="easyui-datetimebox" id="save_endTime" name="endTime"
               data-options="required:true,showSeconds:false" style="width:150px">

        <a href="javascript:search(3)" class="easyui-linkbutton" style="margin-left:50px"
           iconCls=" icon-search" plain="true">查询统计数据</a>
        <a href="javascript:exportToLocal()" class="easyui-linkbutton" style="float: right"
           iconCls="icon-save" plain="true">导出excel表格</a>
    </div>
    <div>
        <a href="javascript:initSpGameServer(1)" class="easyui-linkbutton" style=""
           iconCls=" icon-search" plain="true">查询渠道</a>
        <a href="javascript:initSpGameServer(2)" class="easyui-linkbutton" style="margin-left:50px"
           iconCls=" icon-search" plain="true">查询游戏</a>
        <a href="javascript:initSpGameServer(3)" class="easyui-linkbutton" style="margin-left:50px"
           iconCls=" icon-search" plain="true">查询区服</a>
    </div>
</div>

</body>
<script type="text/javascript">
    $(function () {
        let commonResult = {
            "渠道id": "spId",

            "新增创号": "newAddCreateAccount",
            "新增创角": "newAddCreateRole",
            "新增创角去除滚服": "newAddCreateRoleRemoveOld",
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
            "注册付费金额": "registeredPayment",
            "注册付费ARPU": "registeredPaymentARPU",

            "累计充值(分)": "totalPayment",
            "累计创角": "totalCreateRole",
            "累计充值人数": "totalRechargeNums",
            "总付费率": "totalRechargeRates",

            "注收比": "zhushoubi",
        };
        initDatagrid(commonResult);
        initSpGameServer(1);
    });
</script>
</html>