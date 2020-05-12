<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
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
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/common.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/serverInfo.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/views/server/server_list.js"></script>
</head>

<body style="margin:1px;" id="ff">

<table id="dg" title="服务器列表" class="easyui-datagrid" pagination="true"
       rownumbers="true" fit="true" showFooter="true" toolbar="#sp">
    <thead data-options="frozen:true">
    <tr>
        <th field="cb" checkbox="true" align="center"></th>
    </tr>
    </thead>
</table>

<div id="sp">
    <div>
        <label for="spid"></label>
        <span style="color: blue;">渠道:</span>
        <select title="选择渠道" id="save_spId" name="save_spId">
            <option value="-1" selected="selected">请选择</option>
        </select>

        <label for="save_gameId"></label>
        <span style="color: blue; margin-left:50px ">游戏:</span>
        <select title="选择游戏" id="save_gameId" name="save_gameId">
            <option value="-1" selected="selected">请选择</option>
        </select>

        <label for="save_serverId"></label>
        <span style="color: blue; margin-left:50px">区服:</span>
        <select title="选择区服" id="save_serverId" name="save_serverId">
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
                    <label for="save_spid"></label>
                    <input type="text" name="dlg_spid" id="dlg_spid"
                           required="true" class="easyui-validatebox" validType="namerules"
                           missingMessage="渠道id不能为空">
                </td>
            </tr>

            <tr>
                <td>游戏id</td>
                <td>
                    <select title="选择游戏" id="dlg_gameid" name="dlg_gameid">
                        <option value="-1" selected="selected">请选择</option>
                    </select>
                </td>
            </tr>

            <tr>
                <td>区服id</td>
                <td>
                    <label for="dlg_serverid"></label>
                    <input type="text" name="dlg_serverid" id="dlg_serverid"
                           required="true" class="easyui-validatebox" validType="namerules"
                           missingMessage="服务器id不能为空">
                </td>
            </tr>

            <tr>
                <td>登录地址</td>
                <td>
                    <label for="dlg_loginurl"></label>
                    <input type="text" name="dlg_loginurl" id="dlg_loginurl"
                           required="false">
                </td>
            </tr>
            <tr>
                <td>开服时间</td>
                <td>
                    <label for="dlg_openday"></label>
                    <input class="easyui-datetimebox" id="dlg_openday" name="dlg_openday"
                           data-options="required:true,showSeconds:false" style="width:150px">
                </td>
            </tr>

        </table>
    </div>
</div>

</body>

</html>
