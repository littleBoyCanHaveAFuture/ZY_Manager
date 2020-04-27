<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
    String str = request.getParameter("gameId");
    int gameId = Integer.parseInt(str);
%>
<!DOCTYPE html>
<html lang="utf-8">
<head>
    <meta charset="utf-8"/>
    <title></title>
    <meta name="description" content="overview & stats"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/jquery-easyui-1.7.0/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/jquery-easyui-1.7.0/themes/icon.css">
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/jquery-easyui-1.7.0/demo/demo.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/bootstrap-3.3.7-dist/css/bootstrap.min.css"/>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.7.0/jquery.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.7.0/jquery.easyui.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.7.0/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/bootstrap-3.3.7-dist/js/bootstrap.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/common.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/views/server/gameConfig.js?v202004201102"></script>
</head>
<body>

<label for="appname" hidden="hidden">游戏名称</label>
<input type="text" id="appname" size="20" hidden="hidden"/>
<label for="appid" hidden="hidden">游戏id</label>
<input type="text" id="appid" size="20" hidden="hidden"/>

<table id="dg1" title="" fit="true" class="easyui-datagrid" toolbar="#sp">

</table>

<div id="sp">
    <span class="input-icon">
        <label for="nav-search-input"></label>
        <input autocomplete="off" id="nav-search-input" type="text" name="KEYWORD" value="" placeholder="渠道名称"/>
        <i id="nav-search-icon" class="icon-search">
        </i>
	</span>

    <span onclick="loadServerListTab(1);" class="btn btn-small btn-primary" style="line-height:20px;">
        搜索
        <i class="icon-search icon-on-right">
        </i>
    </span>

    <div class="btn btn-small btn-primary create_game" onclick="sp_select();" id="selectSp">
        渠道选择
    </div>
    <div class="create_game" style="color: #3f89ec;line-height: 30px;" onclick="fee_manage();">
        计费模板管理
    </div>
</div>

<div id="dd">
    <table id="dg2" fit="true"></table>
</div>
</body>


<script>
    $("#appid").val("${param.gameId}");
    $("#appname").val("${param.name}");

    console.info($("#appid").val());
    console.info($("#appname").val());
</script>

<style type="text/css">
    .create_game {
        float: right;
        margin-right: 10px;
        text-align: center;
        line-height: 20px;
    }

    .inline div {
        color: #3f89ec;
        font-size: 13px;
        margin: 0 10px;
        text-align: center;
        float: left;
        cursor: pointer;
    }

    .box-cur a {
        text-decoration: none;
        font-seze: 13px;
        color: #666;
    }

    .box-cur a:hover {
        text-decoration: none;
        font-seze: 13px;
        color: #3f89ec;
    }

    .inline div {
        color: #ddd;
        pointer-events: none;
    }
</style>
</html>

