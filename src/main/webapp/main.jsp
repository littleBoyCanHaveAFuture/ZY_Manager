<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    Cookie[] cookies = request.getCookies();
    String username = "";
    String roleName = "";
    if (cookies != null) {
        for (Cookie tmp : cookies) {
            if (tmp == null) {
                continue;
            }
            if (("userName").equals(tmp.getName())) {
                username = tmp.getValue();
            }
            if (("roleName").equals(tmp.getName())) {
                roleName = tmp.getValue();
            }
        }
    }
%>
<!DOCTYPE>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>指悦后台管理系统主页</title>
    <link rel="shortcut icon" type="image/x-icon"
          href="${pageContext.request.contextPath}/images/favicon.ico"/>
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
    <script src="${pageContext.request.contextPath}/js/common.js"></script>

<body class="easyui-layout" onload="load()">

<div region="north" style="height: 78px;background-color: #ffff">
    <table width="100%">
        <tr>
            <td valign="bottom" style="font-size: 20px;color:#8b004c;" align="left" width="50%">
                <span style="font-size: medium; ">
                    &nbsp;&nbsp;<strong>当前用户：</strong><%=username%>
                </span>[<%=roleName%>]
            </td>

            <td width="50%">
            </td>

            <td>
                <a href="javascript:logout()" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-man'"
                   style="width: 150px;">
                    安全退出
                </a>
            </td>
        </tr>
    </table>
</div>

<div region="center">
    <div class="easyui-tabs" fit="true" border="false" id="tabs">
        <div data-options="region:'center',iconCls:'icon-ok',border:false" title=""
             class="panel-body panel-body-noheader panel-body-noborder layout-body panel-noscroll" id=""
             style="width:100%;height:100%;background: url(${pageContext.request.contextPath}/images/home.jpg) no-repeat center top;background-size: 100%;">
        </div>
    </div>

</div>


<div id="closeMenu" class="easyui-menu" style="width: 150px;">
    <div id="refresh" iconcls="icon-arrow_refresh">刷新</div>
    <div class="menu-sep"></div>
    <div id="close" iconcls="icon-bin_closed">关闭</div>
    <div id="closeall" iconcls="icon-stop">全部关闭</div>
    <div id="closeother" iconcls="icon-bullet_cross">除此之外全部关闭</div>
    <div class="menu-sep"></div>
    <div id="closeright" iconcls="icon-door_in">关闭右侧标签</div>
    <div id="closeleft" iconcls="icon-door_out">关闭左侧标签</div>
    <div class="menu-sep"></div>
    <div id="exit" iconcls="icon-delete">退出</div>
</div>
<div region="west" style="width: 200px;height:500px;" title="导航菜单" split="true">

    <div id="aa" class="easyui-accordion">
        <%--        <div title="测试----->" data-options="selected:true,iconCls:'icon-wenzhangs'">--%>
        <%--            <a href="javascript:openTab('注册、登录、支付','test.jsp','icon-wenzhang')"--%>
        <%--               class="easyui-linkbutton"--%>
        <%--               data-options="plain:true,iconCls:'icon-wenzhang'"--%>
        <%--               style="width: 150px;">测试--%>
        <%--            </a>--%>
        <%--            <a href="javascript:openTab('quick','quick/login.jsp','icon-wenzhang')"--%>
        <%--               class="easyui-linkbutton"--%>
        <%--               data-options="plain:true,iconCls:'icon-wenzhang'"--%>
        <%--               style="width: 150px;">quick--%>
        <%--            </a>--%>
        <%--        </div>--%>

        <a href="javascript:openTab('全服概况','Detail/Day.jsp','icon-wenzhang')"
           class="easyui-linkbutton"
           data-options="plain:true,iconCls:'icon-wenzhang'"
           style="width: 150px;">全服概况
        </a>
        <a href="javascript:openTab('分渠道概况','Detail/Channel.jsp','icon-wenzhang')"
           class="easyui-linkbutton"
           data-options="plain:true,iconCls:'icon-wenzhang'"
           style="width: 150px;">分渠道概况
        </a>
        <div title="游戏概况" data-options="selected:true,iconCls:'icon-wenzhangs'">
            <a href="javascript:openTab('全服概况','Detail/DetailGame.jsp','icon-wenzhang')"
               class="easyui-linkbutton"
               data-options="plain:true,iconCls:'icon-wenzhang'"
               style="width: 150px;">全服概况
            </a>
            <a href="javascript:openTab('分渠道概况','Detail/DetailSp.jsp','icon-wenzhang')"
               class="easyui-linkbutton"
               data-options="plain:true,iconCls:'icon-wenzhang'"
               style="width: 150px;">分渠道概况
            </a>
            <a href="javascript:openTab('分服概况','Detail/DetailServer.jsp','icon-wenzhang')"
               class="easyui-linkbutton"
               data-options="plain:true,iconCls:'icon-wenzhang'"
               style="width: 150px;">分服概况
            </a>

        </div>

        <div title="实时数据" data-options="selected:true,iconCls:'icon-wenzhangs'">
            <a href="javascript:openTab('订单记录','RealtimeData/PayRecord.jsp','icon-wenzhang')"
               class="easyui-linkbutton"
               data-options="plain:true,iconCls:'icon-wenzhang'"
               style="width: 150px;">订单记录
            </a>
            <%--            <a href="javascript:openTab('实时充值','RealtimeData/RealtimeLinePayRecord.jsp','icon-large-chart')"--%>
            <%--               class="easyui-linkbutton"--%>
            <%--               data-options="plain:true,iconCls:'icon-large-chart'"--%>
            <%--               style="width: 150px;">实时充值--%>
            <%--            </a>--%>
            <%--            <a href="javascript:openTab('实时在线新增','RealtimeData/RealtimeLineActive.jsp','icon-large-chart')"--%>
            <%--               class="easyui-linkbutton"--%>
            <%--               data-options="plain:true,iconCls:'icon-large-chart'"--%>
            <%--               style="width: 150px;">实时在线新增--%>
            <%--            </a>--%>
        </div>

        <%--        <div title="玩家信息" data-options="selected:true,iconCls:'icon-wenzhangs'">--%>
        <%--        </div>--%>

        <%--        <div title="数据分析" data-options="selected:true,iconCls:'icon-wenzhangs'">--%>
        <%--        </div>--%>

        <%--        <div title="GM功能" data-options="selected:true,iconCls:'icon-wenzhangs'">--%>
        <%--        </div>--%>

        <div title="服务器管理" data-options="selected:true,iconCls:'icon-wenzhangs'">
            <a href="javascript:openTab('服务器管理-渠道配置','channel/channel_list.jsp','icon-wenzhang')"
               class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-wenzhang'"
               style="width: 150px;">支持的渠道列表
            </a>
            <%--            <a href="javascript:openTab('服务器管理-区服配置','server/server_list.jsp','icon-wenzhang')"--%>
            <%--               class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-wenzhang'"--%>
            <%--               style="width: 150px;">服务器列表--%>
            <%--            </a>--%>
            <a href="javascript:openTab('服务器管理-游戏折扣配置','server/discount_list.jsp','icon-wenzhang')"
               class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-wenzhang'"
               style="width: 150px;">游戏折扣列表
            </a>
            <a href="javascript:openTab('H5游戏','game/h5game.jsp','icon-wenzhang')"
               class="easyui-linkbutton"
               data-options="plain:true,iconCls:'icon-wenzhang'"
               style="width: 150px;">H5游戏
            </a>
        </div>
        <%--        <div title="游戏管理" data-options="selected:true,iconCls:'icon-wenzhangs'">--%>

        <%--        </div>--%>

        <div title="火锅H5" data-options="selected:true,iconCls:'icon-wenzhangs'">
            <a href="javascript:openTab('火锅乐翻天发货','game/h5fahuo.jsp','icon-wenzhang')"
               class="easyui-linkbutton"
               data-options="plain:true,iconCls:'icon-wenzhang'"
               style="width: 150px;">火锅乐翻天发货
            </a>
        </div>

        <div title="账号管理" data-options="selected:true,iconCls:'icon-wenzhangs'">
            <a href="javascript:openTab(' 管理员列表','userManage.jsp','icon-lxr')"
               class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-lxr'" style="width: 150px;">
                管理员列表
            </a>
        </div>
    </div>
</div>

<script type="text/javascript">
    $(function () {

    });

    function load() {
        checkCookies();
        let tab = $('#tabs');
        let text = ["game_list.jsp"];
        if (tab.tabs('exists', text[0])) {
            tab.tabs("close", text[0]);
        }
    }

    checkCookies();
    let url;

    function addTab(url, text, iconCls) {
        let content = "<iframe frameborder=0 scrolling='auto' style='width:100%;height:100%' " +
            "src='${pageContext.request.contextPath}/views/" + url + "'></iframe>";
        $("#tabs").tabs("add", {
            title: text,
            iconCls: iconCls,
            closable: true,
            content: content
        });
    }

    /**
     * 打开选项卡
     * @param text  选项卡标题
     * @param url   请求打开路径
     * @param icon  选项卡图标
     */
    function openTab(text, url, icon) {
        let tab = $('#tabs');
        //判断当前选项卡是否存在
        if (tab.tabs('exists', text)) {
            tab.tabs("close", text);
            // addTab(url, text, icon);
            // tab.tabs("select", text);
        } else {
            //如果不存在 则新建一个
            addTab(url, text, icon);
        }
    }

    function logout() {
        $.messager
            .confirm(
                "系统提示",
                "您确定要退出系统吗",
                function (r) {
                    if (r) {
                        clearCookie();
                    }
                });
    }

    $(document).keydown(function (e) {
        if (!e) {
            e = window.event;
        }
        if ((e.keyCode || e.which) === 13) {
            login();
        } else if ((e.keyCode || e.which) === 27) {
            logout();
        }
    });
</script>

<style type="text/css">

</style>

</body>
</html>
