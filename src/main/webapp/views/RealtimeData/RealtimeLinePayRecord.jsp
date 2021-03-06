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
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.7.0/jquery.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.7.0/jquery.easyui.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.7.0/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/echarts.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/echarts.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/common.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/serverInfo.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/RealtimeData/RealtimeLinePayRecord.js?v=202002282209"></script>
</head>

<body>

<div style="width: 90%">
    <div>
        <label for="save_startTime" style="margin-left: 50px">开始时间:</label>
        <input class="easyui-datetimebox" id="save_startTime" name="startTime"
               data-options="required:true,showSeconds:false">

        <label for="save_endTime">结束时间:</label>
        <input class="easyui-datetimebox" id="save_endTime" name="endTime"
               data-options="required:true,showSeconds:false">

        <label for="save_gameId"></label>
        <span style="color: blue; margin-left:50px ">游戏:</span>
        <select title="选择游戏" id="save_gameId" name="gameId" onchange="initSpGameServer(1)">
            <option value="-1" selected="selected">请选择</option>
        </select>

        <label for="save_spId"></label>
        <span style="color: blue; margin-left:50px  ">渠道:</span>
        <select title="选择渠道" id="save_spId" name="spId" onchange="initSpGameServer(3)">
            <option value="-1" selected="selected">请选择</option>
        </select>

        <label for="save_serverId"></label>
        <span style="color: blue; margin-left:50px">区服:</span>
        <select title="选择区服" id="save_serverId" name="serverId">
            <option value="-1" selected="selected">请选择</option>
        </select>

        <a href="javascript:loadData(2)" class="easyui-linkbutton" style="margin-left:50px"
           iconCls=" icon-search" plain="true">查询统计数据</a>
    </div>
</div>

<!-- 为ECharts准备一个具备大小（宽高）的Dom -->
<div id="main" style="width: 90%;height:1000px;margin-left:50px;">

</div>
</body>
<script type="text/javascript">
    let myChart;
    $(function () {
        initSpGameServer(2);
        $('#save_startTime').datetimebox('setValue', formatterDate(new Date(new Date(new Date().toLocaleDateString()).getTime())));
        $('#save_endTime').datetimebox('setValue', formatterDate(new Date(new Date().getTime() + 30 * 60 * 1000), 1));
        initChart();
        initOption();
    });

    function initChart() {
        if (myChart != null && myChart !== "" && myChart !== undefined) {
            myChart.dispose();
        }
        // 基于准备好的dom，初始化echarts实例
        myChart = echarts.init(document.getElementById('main'));
    }

</script>
</html>
