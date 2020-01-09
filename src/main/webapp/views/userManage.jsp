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
            src="${pageContext.request.contextPath}/js/common.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/multiple-select-1.5.2/multiple-select.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/userManager.js"></script>
</head>
<body style="margin:1px;">

<table id="dg" title="用户管理" class="easyui-datagrid" pagination="true" fitcolumns="true"
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
</div>

<div id="dlg" class="easyui-dialog" data-options="modal:true"
     style="width: 850px;height:400px;padding: 10px 20px; position: relative; z-index:1000;"
     closed="true" buttons="#dlg-buttons">
    <button id="setSelectsBtn" class="btn btn-secondary">SetSelects</button>
    <button id="getSelectsBtn" class="btn btn-secondary">GetSelects</button>
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
                    <input type="text" id="save_password" name="" class="easyui-validatebox"/>
                    <span style="color: red; ">不修改可不填</span>
                </td>
            </tr>
            <tr>
                <td>管理员权限：</td>
                <td>
                    <label for="save_mamagerLv"></label>
                    <select title="选择管理员权限" id="save_mamagerLv" name="func">
                        <option value="1000">超级管理员</option>
                        <option value="500">渠道管理员</option>
                        <option value="100">渠道成员</option>
                        <option value="0">普通成员</option>
                    </select>
                    <span style="color: red; ">单选</span>
                </td>
            </tr>
            <tr>
                <td>代理商id：</td>
                <td>
                    <label for="save_agents"></label>
                    <select title="选择代理商" id="save_agents" name="func">
                        <option value="0">无</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td>模块权限：</td>
                <td>
                    <select title="选择模块" id="save_func" name="func" multiple="multiple" size="5">
                    </select>
                    <span style="color: red; ">*多选</span>
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