<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE>
<html>
<head>
    <meta name="description" content="overview & stats"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title></title>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/jquery-easyui-1.7.0/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/jquery-easyui-1.7.0/themes/icon.css">
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/jquery-easyui-1.7.0/demo/demo.css">
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.7.0/jquery.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.7.0/jquery.easyui.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.7.0/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/common.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/server/game_list.js"></script>
</head>

<body style="margin:1px;height: 100%;" id="ff">

<div class="easyui-tabs" data-options="tools:'#tab-tools'" fit="true" id="tabs">
    <div title="游戏列表" fit="true">
        <table id="dg" class="easyui-datagrid" pagination="true" rownumbers="true" fit="true" showFooter="true"
               toolbar="#sp">
            <thead data-options="frozen:true">
            <tr>
                <th field="id" width="80" align="center">游戏id</th>
                <th field="name" width="80" align="center">游戏名称</th>
                <th field="uid" width="80" align="center">创建者id</th>
                <th field="secertKey" align="center">app秘钥</th>
                <th field="loginUrl">登陆地址</th>
                <th field="paycallbackUrl" align="center">支付回调地址</th>
                <th field="config" width="60" align="center" formatter="formatOpt">配置</th>
            </tr>
            </thead>
        </table>
    </div>
    <%--    <div title="Ajax" data-options="href:'_content.html',closable:true" style="padding:10px"></div>--%>
    <%--    <div title="Iframe" data-options="closable:true" style="overflow:hidden">--%>
    <%--        <iframe scrolling="yes" frameborder="0" src="${pageContext.request.contextPath}/views/gameConfig.jsp"--%>
    <%--                style="width:100%;height:100%;">--%>
    <%--        </iframe>--%>
    <%--    </div>--%>

</div>


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
     style="width: 700px;height:350px;padding: 10px 20px; position: relative; z-index:1000;">
    <div style="padding-top:50px;  float:left; width:95%; padding-left:30px;">
        <input type="hidden" name="save_id" id="save_id">
        <table>
            <tr>
                <td>游戏id：</td>
                <td>
                    <label for="save_gameid"></label>
                    <input type="text" name="save_gameid" id="save_gameid" class="easyui-validatebox">
                </td>
            </tr>
            <tr>
                <td>游戏名称：</td>
                <td>
                    <label for="save_name"></label>
                    <input type="text" name="save_name" id="save_name"
                           required="required" class="easyui-validatebox" validType="namerules"
                           missingMessage="游戏名称">
                </td>
            </tr>
            <tr>
                <td>
                    <label for="save_uid"></label>
                    <input type="text" name="save_uid" id="save_uid"
                           required="required" class="easyui-validatebox" hidden="hidden"
                           missingMessage="创建者id">
                </td>
            </tr>
            <tr>
                <td>游戏地址：</td>
                <td>
                    <label for="save_loginurl"></label>
                    <input type="text" name="save_loginurl" id="save_loginurl" style="width: 400px">
                </td>
            </tr>
            <tr>
                <td>支付回调地址：</td>
                <td>
                    <label for="save_paybackurl"></label>
                    <input type="text" name="save_paybackurl" id="save_paybackurl" style="width: 400px">
                </td>
            </tr>
        </table>
    </div>
</div>

<script type="text/javascript">

    function addTab(url, text, icon) {
        let content = "<iframe frameborder=0 scrolling='auto' style='width:100%;height:100%' " +
            "src='${pageContext.request.contextPath}/views/" + url + "'></iframe>";
        $("#tabs").tabs("add", {
            title: text,
            iconCls: icon,
            closable: true,
            content: content
        });
    }

    /**
     * 打开选项卡
     * @param tab easyui-tabs
     * @param text  选项卡标题
     * @param url   请求打开路径
     * @param icon  选项卡图标
     */
    function openTab(tab, text, url, icon) {
        if (tab.tabs('exists', text)) {
            tab.tabs("close", text);
            addTab(url, text, icon);
            tab.tabs("select", text);
        } else {
            addTab(url, text, icon);
        }
    }
</script>
</body>

</html>