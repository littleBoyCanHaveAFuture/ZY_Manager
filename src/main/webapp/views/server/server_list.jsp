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
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/server/server_list.js"></script>
</head>

<body style="margin:1px;" id="ff">

<table id="serverTable" title="服务器列表" class="easyui-datagrid" pagination="true"
       rownumbers="true" fit="true" showFooter="true" toolbar="#sp">
    <thead data-options="frozen:true">
    <tr>
        <th field="cb" checkbox="true" align="center"></th>
        <th field="id" width="70" align="center" hidden="true">编号</th>
        <th field="gameId" width="70" align="center" hidden="true">游戏id</th>
        <th field="gamename" width="70" align="center">游戏名</th>
        <th field="serverId" width="70" align="center">服务器id</th>
        <th field="spId" width="150" align="center">渠道id</th>
        <th field="loginUrl" width="300" align="center">登录地址</th>
        <th field="openday" width="300" align="center">开服时间</th>
    </tr>
    </thead>
</table>

<div id="sp">
    <div>
        <label for="spid"></label>
        <span style="color: blue;">渠道:</span>
        <select title="选择渠道" id="spid" name="spId">
            <option value="-1" selected="selected">请选择</option>
        </select>

        <label for="gameid"></label>
        <span style="color: blue; margin-left:50px ">游戏:</span>
        <select title="选择游戏" id="gameid" name="gameId">
            <option value="-1" selected="selected">请选择</option>
        </select>

        <label for="serverid"></label>
        <span style="color: blue; margin-left:50px">区服:</span>
        <select title="选择区服" id="serverid" name="serverId">
            <option value="-1" selected="selected">请选择</option>
        </select>

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
                <td>渠道id</td>
                <td>
                    <input type="text" name="save_spid" id="save_spid"
                           required="true" class="easyui-validatebox" validType="namerules"
                           missingMessage="渠道id不能为空">
                </td>
            </tr>

            <tr>
                <td>游戏id</td>
                <td>
                    <select title="选择游戏" id="save_gameid" name="save_gameid">
                        <option value="-1" selected="selected">请选择</option>
                    </select>
<%--                    <input type="text" name="save_gameid" id="save_gameid"--%>
<%--                           required="true" class="easyui-validatebox" validType="'number','length[5,10]'"--%>
<%--                           missingMessage="游戏id不能为空" ,invalidMessage="请输入数字">--%>
                </td>
            </tr>

            <tr>
                <td>区服id</td>
                <td>
                    <input type="text" name="save_serverid" id="save_serverid"
                           required="true" class="easyui-validatebox" validType="namerules"
                           missingMessage="服务器id不能为空">
                </td>
            </tr>

            <tr>
                <td>登录地址</td>
                <td>
                    <input type="text" name="save_loginurl" id="save_loginurl"
                           required="false">
                </td>
            </tr>
            <tr>
                <td>开服时间</td>
                <td>
                    <input class="easyui-datetimebox" id="save_openday" name="save_openday"
                           data-options="required:true,showSeconds:false" style="width:150px">
                </td>
            </tr>
        </table>
    </div>
</div>

</body>

</html>