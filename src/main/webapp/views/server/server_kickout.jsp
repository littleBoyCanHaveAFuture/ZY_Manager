<%--
  Created by IntelliJ IDEA.
  User: tgzwmkkkk
  Date: 2019/11/21
  Time: 17:28
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
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
            src="${pageContext.request.contextPath}/ueditor/ueditor.config.js">
    </script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/ueditor/ueditor.all.min.js">
    </script>
    <script src="${pageContext.request.contextPath}/js/common.js" type=""></script>
    <script src="${pageContext.request.contextPath}/js/server/server_kickout.js" type=""></script>
</head>

<body style="margin:1px;" id="ff">

<table id="serverTable" title="服务器列表" class="easyui-datagrid" pagination="true"
       rownumbers="true" fit="true" showFooter="true" toolbar="#sp">
    <thead data-options="frozen:true">
    <tr>
        <th field="cb" checkbox="true" align="center"></th>
        <th field="id" width="70" align="center" hidden="true">编号</th>
        <th field="gameId" width="70" align="center">游戏id</th>
        <th field="serverId" width="70" align="center">服务器id</th>
        <th field="spId" width="150" align="center">渠道id</th>
        <th field="loginUrl" width="300" align="center">登录地址</th>
    </tr>
    </thead>
</table>
</body>
</html>
