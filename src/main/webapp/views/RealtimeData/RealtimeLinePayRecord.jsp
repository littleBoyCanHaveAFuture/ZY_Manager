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

    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.3.3/jquery.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.3.3/jquery.easyui.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.3.3/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/common.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/echarts.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/echarts.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/RealtimeData/RealtimeLinePayRecord.js"></script>
</head>
<body>
<!-- 为ECharts准备一个具备大小（宽高）的Dom -->
<div class="text-c">
    <label for="save_gameId"></label>
    <span style="color: blue; ">游戏:</span>
    <select title="选择游戏" id="save_gameId" name="gameId">
        <option value="-1" selected="selected">请选择</option>
    </select>

    <label for="save_serverId"></label>
    <span style="color: blue; margin-left:50px">区服:</span>
    <select title="选择区服" id="save_serverId" name="serverId">
        <option value="-1" selected="selected">请选择</option>
    </select>

    <label for="save_spId"></label>
    <span style="color: blue;margin-left:50px ">渠道:</span>
    <select title="选择渠道" id="save_spId" name="spId">
        <option value="-1" selected="selected">请选择</option>
    </select>
</div>
<div>
    <a href="javascript:initGameList()" class="easyui-linkbutton" style=""
       iconCls=" icon-search" plain="true">查询游戏</a>
    <a href="javascript:initServerList(1)" class="easyui-linkbutton" style="margin-left:50px"
       iconCls=" icon-search" plain="true">查询区服</a>
    <a href="javascript:initServerList(2)" class="easyui-linkbutton" style="margin-left:50px"
       iconCls=" icon-search" plain="true">查询渠道</a>
</div>
<label for="save_startTime">开始时间:</label>
<input class="easyui-datetimebox" id="save_startTime" name="startTime"
       data-options="required:true,showSeconds:false" style="width:150px">

<label for="save_endTime">结束时间:</label>
<input class="easyui-datetimebox" id="save_endTime" name="endTime"
       data-options="required:true,showSeconds:false" style="width:150px">

<a href="javascript:getLinesByDate(1)" class="easyui-linkbutton" style="margin-left:50px"
   iconCls=" icon-search" plain="true">查询统计数据</a>
</div>
<!-- 为ECharts准备一个具备大小（宽高）的Dom -->
<div id="main" style="width: 1000px;height:600px;"></div>
</body>

</html>