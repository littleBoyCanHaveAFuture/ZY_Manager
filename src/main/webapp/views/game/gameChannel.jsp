<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
//    String str = request.getParameter("appId");
//    int gameId = Integer.parseInt(str);
//
//    String name = request.getParameter("appName");
//
//    String str3 = request.getParameter("type");
//    int type = Integer.parseInt(str3);
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
            src="${pageContext.request.contextPath}/views/game/gameChannel.js?v202005071"></script>
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


<div id="dlg" style="padding: 10px 20px; position: relative; z-index:1000;">
    <div style="padding-top:20px;  float:left; width:95%; padding-left:30px;">
        <table cellpadding="0" cellspacing="0" style="width:100%" class="cc">
            <tr>
                <td style="width:100px;text-align: left;padding-top: 13px;">渠道支付回调:</td>
                <td style="padding-top: 13px;color:#000;font-weight: bold;">
                    <input type="hidden" name="callback_url" id="callback_url">http://cn.soeasysdk.com/ret/h5_yy/10654/3799
                </td>
            </tr>
            <tr>
                <td style="width:100px;text-align: left;padding-top: 13px;">渠道入口地址:</td>
                <td style="width:100px;text-align: left;padding-top: 13px;">
                    <input class="showNormal" style="width:94%;font-weight: bold;color:#000;"
                           type="text" name="url"
                           id="url" maxlength="500" title="渠道入口地址">
                    <div id="soeasyurl" class="showNormal" style="color:red;">
                        注：提供给渠道的地址
                        <br>https://cn.soeasysdk.com/soeasysr/gameini/apps_conf_html/3799/10654/
                        <br>http://soeasysdk.com/soeasysr/gameini/apps_conf_html/3799/10654/
                    </div>
                </td>
            </tr>
            <tr>
                <td style="width:100px;text-align: left;padding-top: 13px;">key:</td>
                <td>
                    <input style="width:94%;" type="text" name="key" id="key" value="" maxlength="100000000" title="key"
                           check="1">
                    <div>
                        <font color="#aaa">注：渠道提供的key</font>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</div>

<div id="dlg-buttons">
    <a href="#" class="easyui-linkbutton" iconCls="icon-save" onclick="javascript:alert('save')">Save</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-cancel"
       onclick="javascript:$('#dlg').dialog('close')">Close</a>
</div>

</body>
<script>
    $("#appid").val("${param.appId}");
    $("#appname").val("${param.appName}");

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


    .cc th, .cc td {
        border-top: 0;
        padding: 8px;
        line-height: 20px;
        text-align: left;
        vertical-align: top;
    }
</style>
</html>

