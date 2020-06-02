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
            src="${pageContext.request.contextPath}/js/common.js?v=20200228"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/serverInfo.js?v=20200228"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/RealtimeData/PayRecord.js?v=1438"></script>
</head>
<body style="margin:1px;">

<table id="dg" title="注：需要先查询游戏、区服、渠道后方可查询" class="easyui-datagrid" pagination="true" fitcolumns="true"
       rownumbers="true" fit="true" showFooter="true" toolbar="#tb">
    <thead>
    </thead>
</table>

<div id="tb" fitcolumns="true">
    <div id="tbs">
        <label for="payRecordId">渠道订单号:</label>
        <input id="payRecordId" type="text" class="easyui-textbox" style="width:105px;height: 20px"/>

        <label for="payRecord_playerId">&nbsp;玩家ID:</label>
        <input id="payRecord_playerId" type="text" class="easyui-textbox" style="width:105px;height: 20px"/>

        <label for="save_gameId"></label>
        <span style="color: blue;margin-left:30px">游戏:</span>
        <select title="选择游戏" id="save_gameId" name="gameId" onchange="initSpGameServer(1)">
            <option value="-1" selected="selected">请选择</option>
        </select>

        <label for="save_spId"></label>
        <span style="color: blue; margin-left:30px">渠道:</span>
        <select title="选择渠道" id="save_spId" name="spId" onchange="initSpGameServer(3)">
            <option value="-1" selected="selected">请选择</option>
        </select>

        <label for="save_serverId"></label>
        <span style="color: blue; margin-left:30px">区服:</span>
        <select title="选择区服" id="save_serverId" name="serverId">
            <option value="-1" selected="selected">请选择</option>
        </select>

        <span style="color: blue;margin-left:30px"></span>
        <label for="payRecord_state">&nbsp;支付状态:</label>
        <select title="选择订单状态" id="payRecord_state">
            <option value="-1">所有订单</option>
<%--            <option value="0">无</option>--%>
<%--            <option value="1">选择商品-未支付</option>--%>
            <option value="2">选择支付方式-未支付</option>
            <option value="3">支付成功-未发货</option>
            <option value="4" selected="selected">支付成功-交易完成</option>
            <option value="5">支付成功-补单完成</option>
        </select>

        <span style="color: blue;margin-left:30px">开始时间:</span>
        <label for="payRecord_startTime"></label>
        <input class="easyui-datetimebox" id="payRecord_startTime" name="startTime"
               data-options="required:true,showSeconds:false" style="width:150px">

        <span style="color: blue;margin-left:30px">结束时间:</span>
        <label for="payRecord_endTime"></label>
        <input class="easyui-datetimebox" id="payRecord_endTime" name="endTime"
               data-options="required:true,showSeconds:false" style="width:150px">

        <a href="javascript:selectPayRecord()" class="easyui-linkbutton" style="margin-left:50px"
           iconCls=" icon-search" plain="true">查询订单</a>

        <a href="javascript:exportPayRecord()" class="easyui-linkbutton" style="float: right"
           iconCls="icon-save" plain="true">导出excel表格</a>
    </div>

</div>

</body>

</html>
