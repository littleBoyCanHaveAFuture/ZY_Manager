<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title></title>
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
            src="${pageContext.request.contextPath}/js/common.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/views/channel/channel_list.js"></script>
</head>

<body style="margin:1px;" id="ff">

<table id="dg" title="渠道列表" class="easyui-datagrid" pagination="true"
       rownumbers="true" fit="true" showFooter="true" toolbar="#sp">
    <thead data-options="frozen:true">

    </thead>
</table>

<div id="sp">
    <div>
        <label for="spid">渠道id:</label>
        <input type="text" name="spid" id="spid">

        <label for="sname">渠道名称</label>
        <input type="text" name="sname" id="sname">
        <button onclick="loadSpListTab()" class="easyui-linkbutton">查询</button>
    </div>
    <div>
        <a href="javascript:openSpDialog()" class="easyui-linkbutton" iconCls="icon-add" plain="true">
            添加</a>
        <a href="javascript:openSpModifyDialog()" class="easyui-linkbutton" iconCls="icon-edit" plain="true">
            修改</a>
        <a href="javascript:deleteSp()" class="easyui-linkbutton" iconCls="icon-remove" plain="true">
            删除</a>
    </div>
</div>

<div id="dlg-buttons">
    <a href="javascript:saveSpType()" class="easyui-linkbutton" iconCls="icon-ok">保存</a>
    <a href="javascript:closeSpDialog()" class="easyui-linkbutton" iconCls="icon-cancel">关闭</a>
</div>
<div id="dlg" style="padding: 10px 20px; position: relative; z-index:1000;" hidden="hidden" class="easyui-dialog"
     closed="true" buttons="#dlg-buttons">
    <div style="padding-top:20px;  float:left; width:95%; padding-left:30px;">
        <input type="hidden" name="save_id" id="save_id">
        <table style="border:0;margin-bottom: 0;" id="table_top"
               class="table table-striped table-bordered table-hover">
            <tbody>
            <tr>
                <td style="width:100px;text-align: left;padding-top: 13px;">icon地址:</td>
                <td style="padding-top: 13px;color:#000;font-weight: bold;">
                    <label for="save_icon"></label>
                    <input type="text" name="save_icon" id="save_icon" style="width: 400px"
                           required="true" class="easyui-validatebox" validType="namerules"
                           missingMessage="图标地址">
                </td>
            </tr>
            <tr>
                <td style="width:100px;text-align: left;padding-top: 13px;">渠道id:</td>
                <td style="padding-top: 13px;color:#000;font-weight: bold;">
                    <label for="save_spId"></label>
                    <input type="text" name="save_spId" id="save_spId"
                           required="true" class="easyui-validatebox" validType="'number','length[5,10]'"
                           missingMessage="渠道id不能为空" ,invalidMessage="请输入数字">
                </td>
            </tr>
            <tr>
                <td style="width:100px;text-align: left;padding-top: 13px;">渠道名称:</td>
                <td style="padding-top: 13px;color:#000;font-weight: bold;">
                    <label for="save_name"></label>
                    <input type="text" name="save_name" id="save_name"
                           required="true" class="easyui-validatebox" validType="namerules"
                           missingMessage="渠道名称">
                </td>
            </tr>
            <tr hidden="hidden">
                <td style="width:100px;text-align: left;padding-top: 13px;">父渠道:</td>
                <td style="padding-top: 13px;color:#000;font-weight: bold;">
                    <label for="save_parent"></label>
                    <input type="text" name="save_parent" id="save_parent" value="0"
                           required="true" class="easyui-validatebox" validType="namerules"
                           missingMessage="父渠道">
                </td>
            </tr>
            <tr hidden="hidden">
                <td style="width:100px;text-align: left;padding-top: 13px;">状态:</td>
                <td style="padding-top: 13px;color:#000;font-weight: bold;">
                    <label for="save_state"></label>
                    <input type="text" name="save_state" id="save_state" value="0"
                           required="true" class="easyui-validatebox" validType="namerules"
                           missingMessage="状态">
                </td>
            </tr>
            <tr>
                <td style="width:100px;text-align: left;padding-top: 13px;">版本号:</td>
                <td style="padding-top: 13px;color:#000;font-weight: bold;">
                    <label for="save_version"></label>
                    <input type="text" name="save_version" id="save_version"
                           required="true" class="easyui-validatebox" validType="namerules"
                           missingMessage="sdk版本">
                </td>
            </tr>
            <tr hidden="hidden">
                <td style="width:100px;text-align: left;padding-top: 13px;">分享链接:</td>
                <td style="padding-top: 13px;color:#000;font-weight: bold;">
                    <label for="save_shareLinkUrl"></label>
                    <input type="text" name="save_shareLinkUrl" id="save_shareLinkUrl"
                           required="true" class="easyui-validatebox" validType="namerules"
                           missingMessage="分享链接">
                </td>
            </tr>
            <tr>
                <td style="width:100px;text-align: left;padding-top: 13px;">英文简称:</td>
                <td style="padding-top: 13px;color:#000;font-weight: bold;">
                    <label for="save_code"></label>
                    <input type="text" name="save_shareLinkUrl" id="save_code"
                           required="true" class="easyui-validatebox" validType="namerules"
                           missingMessage="jianxie ">
                </td>
            </tr>
            </tbody>
        </table>
    </div>
</div>
<script type="text/css">
    .table_report th, .table_report td {
        border-top: 0;
        padding: 8px;
        line-height: 20px;
        text-align: left;
        vertical-align: top;
    }

    .table_top th, .table_top td {
        border-top: 0;
        padding: 8px;
        line-height: 20px;
        text-align: left;
        vertical-align: top;
    }
</script>
</body>

</html>
