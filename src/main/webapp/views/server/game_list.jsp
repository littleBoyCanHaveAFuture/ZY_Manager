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
    <script type="text/javascript" src="${pageContext.request.contextPath}/ueditor/ueditor.config.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/ueditor/ueditor.all.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/common.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/server/game_list.js"></script>
</head>

<body style="margin:1px;" id="ff">

<table id="dg" title="游戏列表" class="easyui-datagrid" pagination="true"
       rownumbers="true" fit="true" showFooter="true" toolbar="#sp">
    <thead data-options="frozen:true">
    <tr>
        <th field="gameId" width="70" align="center">游戏id</th>
        <th field="name" width="70" align="center">游戏名</th>
    </tr>
    </thead>
</table>

<div id="sp">
    <div>
        <label for="gameid">游戏id:</label>
        <input type="text" name="gameid" id="gameid">

        <label for="name">游戏名称</label>
        <input type="text" name="name" id="name">
        <button onclick="loadServerListTab()" class="easyui-linkbutton">查询</button>
    </div>
    <div>
        <a href="javascript:openServerDialog()" class="easyui-linkbutton" iconCls="icon-add" plain="true">
            添加</a>
        <a href="javascript:openServerModifyDialog()" class="easyui-linkbutton" iconCls="icon-edit" plain="true">
            修改</a>
        <a href="javascript:deleteServer()" class="easyui-linkbutton" iconCls="icon-remove" plain="true">
            删除</a>
    </div>
</div>

<div id="dlg-buttons">
    <a href="javascript:saveServerType()" class="easyui-linkbutton" iconCls="icon-ok">保存</a>
    <a href="javascript:closeServerDialog()" class="easyui-linkbutton" iconCls="icon-cancel">关闭</a>
</div>

<div id="dlg" class="easyui-dialog" closed="true" buttons="#dlg-buttons"
     style="width: 600px;height:350px;padding: 10px 20px; position: relative; z-index:1000;">
    <div style="padding-top:50px;  float:left; width:95%; padding-left:30px;">
        <input type="hidden" name="save_id" id="save_id">
        <table>
            <tr>
                <td>游戏id:</td>
                <td>
                    <label for="save_gameid"></label>
                    <input type="text" name="save_gameid" id="save_gameid"
                           required="true" class="easyui-validatebox" validType="'number','length[5,10]'"
                           missingMessage="游戏id不能为空" ,invalidMessage="请输入数字">
                </td>
            </tr>
            <tr>
                <td>游戏名称:</td>
                <td>
                    <label for="save_name"></label>
                    <input type="text" name="save_name" id="save_name"
                           required="true" class="easyui-validatebox" validType="namerules"
                           missingMessage="游戏名称">
                </td>
            </tr>
        </table>
    </div>
</div>

</body>

</html>