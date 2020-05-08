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
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.7.0/jquery.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.7.0/jquery.easyui.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.7.0/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/common.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/views/game/game.js?202004281101"></script>
</head>

<body style="margin:1px;height: 100%;" id="ff">

<div class="easyui-tabs" data-options="tools:'#tab-tools'" fit="true" id="tabs">
    <div title="游戏列表" fit="true">
        <table id="dg" class="easyui-datagrid" pagination="true" rownumbers="true" fit="true" showFooter="true"
               toolbar="#sp">
            <thead data-options="frozen:true">
            <tr>
                <th field="appId" align="center">APPID</th>
                <th field="appName" align="center">游戏名称</th>
                <th field="ownerId" align="center">创建者id</th>
                <th field="type" align="center">游戏类别</th>
                <th field="config" align="center" formatter="formatOpt">操作</th>
            </tr>
            </thead>
        </table>
    </div>
</div>


<div id="sp">
    <div>
        <label for="gameid" hidden="hidden">游戏id:</label>
        <input type="text" name="gameid" id="gameid" hidden="hidden">

        <label for="name"></label>
        <input type="text" name="name" id="name" placeholder="游戏名称" class="icon-search">
        <button onclick="loadServerListTab()" class="easyui-linkbutton"><span style="line-height: 24px">查询</span>
        </button>

        <a href="javascript:createGame()" class="easyui-linkbutton" iconCls="icon-add" plain="true"
           style="float: right">
            创建游戏</a>
    </div>
</div>

<script type="text/css">

</script>
<script type="text/javascript">
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

    function formatOpt(val, row, index) {
        return '<a href="#" onclick="editSp(' + index + ')">配置</a>';
    }

    //创建游戏
    function createGame() {
        let param = "?create=1&type=1";
        window.location.href = "/views/game/gameInfo.jsp" + param;
    }

    function editSp(index) {
        let dg = $('#dg');
        dg.datagrid('selectRow', index);
        let row = dg.datagrid('getSelected');
        if (row) {
            // type=0 配置 type=1 创建游戏
            let param = "?gameId=" + row.appId + "&name=" + row.appName + "&type=1" + "&create=" + 0;
            window.location.href = "/views/game/gameInfo.jsp" + param;
        }
    }
</script>
</body>

</html>