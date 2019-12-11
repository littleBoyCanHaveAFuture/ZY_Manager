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
            src="${pageContext.request.contextPath}/js/common.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/multiple-select-1.5.2/multiple-select.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/datagrid-export/datagrid-export.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/RealtimeData/PayRecord.js"></script>
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

        <span style="color: blue;margin-left:50px"></span>
        <label for="pay_player_channel">&nbsp;玩家渠道:</label>
        <select title="选择渠道" id="pay_player_channel" name="spId">
            <option value="-1" selected="selected">请选择</option>
        </select>

        <span style="color: blue;margin-left:50px"></span>
        <label for="pay_gameId">&nbsp;游戏列表:</label>
        <select title="选择渠道" id="pay_gameId" name="spId">
            <option value="-1" selected="selected">请选择</option>
        </select>

        <span style="color: blue;margin-left:50px"></span>
        <label for="payRecord_state">&nbsp;支付状态:</label>
        <select title="选择订单状态" id="payRecord_state">
            <option value="">未选择</option>
            <option value="0">点开充值界面:未点充值按钮(取消支付)</option>
            <option value="1">选择充值方式界面:未选择充值方式(取消支付)</option>
            <option value="2">支付宝微信界面:未支付(取消支付)</option>
            <option value="3">支付成功:未发货</option>
            <option value="4" selected="selected">支付成功:已发货(交易完成)</option>
            <option value="5">支付成功:补单(交易完成)</option>
        </select>

        <span style="color: blue;margin-left:50px">开始时间:</span>
        <input class="easyui-datetimebox" id="payRecord_startTime" name="startTime"
               data-options="required:true,showSeconds:false" style="width:150px">

        <span style="color: blue;margin-left:50px">结束时间:</span>
        <input class="easyui-datetimebox" id="payRecord_endTime" name="endTime"
               data-options="required:true,showSeconds:false" style="width:150px">

        <a href="javascript:selectPayRecord()" class="easyui-linkbutton" style="margin-left:50px"
           iconCls=" icon-search" plain="true">查询统计数据</a>
        <a href="javascript:loadPayRecord()" class="easyui-linkbutton" style="float: right"
           iconCls="icon-save" plain="true">查询全部</a>
        <a href="javascript:exportPayRecord()" class="easyui-linkbutton" style="float: right"
           iconCls="icon-save" plain="true">导出excel表格</a>
    </div>

</div>

</body>
</html>