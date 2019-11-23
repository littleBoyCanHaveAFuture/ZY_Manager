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
            src="${pageContext.request.contextPath}/js/userManager.js"></script>


</head>
<body style="margin:1px;">

<table id="dg" title="用户管理" class="easyui-datagrid" pagination="true"
       rownumbers="true" fit="true" showFooter="true" toolbar="#tb">
    <thead>
    <tr>
        <th field="cb" checkbox="true" align="center"></th>
        <th field="id" width="50" align="center">编号</th>
        <th field="userName" width="100" align="center">用户名</th>
        <th field="roleName" width="150" align="center">权限名</th>
        <th field="mamagerLv" width="100" align="center" hidden="true">权限等级</th>
        <th field="agents" width="100" align="center">所属渠道</th>
        <th field="spId" width="400" align="center">可查看的渠道</th>
        <th field="password" width="100" hidden="true" align="center">密码</th>
        <th field="func" width="400" fit="true" align="center">开放模块</th>
    </tr>
    </thead>
</table>

<div id="tb">
    <div>
        <a href="javascript:openUserAddDialog()" class="easyui-linkbutton" iconCls="icon-add" plain="true">添加</a>
        <a href="javascript:openUserModifyDialog()" class="easyui-linkbutton" iconCls="icon-edit" plain="true">修改</a>
        <a href="javascript:deleteUser()" class="easyui-linkbutton" iconCls="icon-remove" plain="true">删除</a>
    </div>
    <div>用户名：
        <label for="s_userName"></label>
        <input type="text" id="s_userName" size="20" onkeydown="if(event.keyCode===13) searchUser()"/>
        <a href="javascript:searchUser()" class="easyui-linkbutton" iconCls="icon-search" plain="true">搜索</a>
    </div>
    <div class="col-sm-10">
        <label>
            <select multiple="multiple" class="multiple-select" id="mu">
                <optgroup label="Group 1">
                    <option value="1">Option 1</option>
                    <option value="2">Option 2</option>
                    <option value="3">Option 3</option>
                </optgroup>
                <optgroup label="Group 2">
                    <option value="4">Option 4</option>
                    <option value="5">Option 5</option>
                    <option value="6">Option 6</option>
                </optgroup>
                <optgroup label="Group 3">
                    <option value="7">Option 7</option>
                    <option value="8">Option 8</option>
                    <option value="9">Option 9</option>
                </optgroup>
            </select>
        </label>
    </div>
</div>

<div id="dlg" class="easyui-dialog" data-options="modal:true"
     style="width: 850px;height:555px;padding: 10px 20px; position: relative; z-index:1000;"
     closed="true" buttons="#dlg-buttons">
    <form id="fm" method="post">
        <table id=addUserTable cellspacing="8px">
            <tr>
                <td>用户名：</td>
                <td>
                    <label for="save_userName"></label>
                    <input type="text" id="save_userName" name="userName" class="easyui-validatebox" required="true"/>
                    <span style="color: red; ">*</span>
                    <input type="hidden" id="userId" value="0">
                </td>
            </tr>
            <tr>
                <td>密码：</td>
                <td>
                    <label for="save_password"></label>
                    <input type="text" id="save_password" name="" class="easyui-validatebox" required="false"/>
                </td>
            </tr>
            <tr>
                <td>管理员权限：</td>
                <td>
                    <label for="save_mamagerLv"></label>
                    <%--                    <input type="text" id="save_mamagerLv" name="mamagerLv" class="easyui-validatebox" required="true"/>--%>
                    <input type="radio" id="save_mamagerLv" name="mamagerLv" value="1000" checked/>超级管理员
                    <input type="radio" id="save_mamagerLv" name="mamagerLv" value="500" checked/>渠道管理员
                    <input type="radio" id="save_mamagerLv" name="mamagerLv" value="100" checked/>渠道成员
                    <input type="radio" id="save_mamagerLv" name="mamagerLv" value="0" checked/>普通成员
                </td>
            </tr>
            <tr>
                <td>sss：</td>
                <td>
                    <div style="margin-bottom:20px">

                        <input id="authority" name="authority" style="width: 150px;" class="easyui-combobox">

                    </div>
                </td>
            </tr>
        </table>
    </form>
</div>

<div id="dlg-buttons">
    <a href="javascript:saveUser()" class="easyui-linkbutton" iconCls="icon-ok">保存</a>
    <a href="javascript:closeUserDialog()" class="easyui-linkbutton" iconCls="icon-cancel">关闭</a>
</div>

</body>
</html>