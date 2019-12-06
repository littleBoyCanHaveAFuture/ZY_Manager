<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%
    Cookie[] cookies = request.getCookies();
    String username = "";
    String roleName = "";
    if (cookies != null) {
        for (Cookie tmp : cookies) {
            if (tmp != null) {
//                System.out.println(tmp.getName() + ":" + tmp.getValue());
                if (("userName").equals(tmp.getName())) {
                    username = tmp.getValue();
                }
                if (("roleName").equals(tmp.getName())) {
                    roleName = tmp.getValue();
                }
            }
        }
    }
    if (username.isEmpty() || roleName.isEmpty()) {

    }
%>
<!DOCTYPE html PUBLIC "-//W3C//Dtd HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>指悦后台管理系统主页</title>
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
    <script src="${pageContext.request.contextPath}/js/common.js"></script>

    <script type="text/javascript">
        checkCookies();
        var url;

        function addTab(url, text, iconCls) {
            var content = "<iframe frameborder=0 scrolling='auto' style='width:100%;height:100%' " +
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
            //判断当前选项卡是否存在
            if ($('#tabs').tabs('exists', text)) {
                $("#tabs").tabs("close", text);
                // addTab(url, text, iconCls);
                // $("#tabs").tabs("select", text);
                //如果存在 显示
                $("#tabs").tabs("select", text);
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
    </script>

<body class="easyui-layout">
<div region="north" style="height: 78px;background-color: #ffff">
    <table width="100%">
        <tr>
            <td width="50%">
            </td>
            <td valign="bottom" style="font-size: 20px;color:#8B8B8B;font-family: '楷体',serif;"
                align="right" width="50%">
                <font size="3">&nbsp;&nbsp;<strong>当前用户：</strong><%=username%>
                </font>【<%=roleName%>】
            </td>
        </tr>
        <tr>
            <td width="50%">
            <td valign="bottom" style="font-size: 20px;color:#8B8B8B;font-family: '楷体',serif;"
                align="right" width="50%">
                <a href="javascript:logout()" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-exit'"
                   style="width: 150px;">
                    安全退出
                </a>
            </td>

        </tr>
    </table>
</div>

<div region="center">
    <div class="easyui-tabs" fit="true" border="false" id="tabs">
        <div title="首页" data-options="iconCls:'icon-home'">
            <img src="${pageContext.request.contextPath}/images/home.jpg"
                 alt="">
        </div>
    </div>
</div>

<div region="west" style="width: 200px;height:500px;" title="导航菜单"
     split="true">
    <div class="easyui-accordion">
        <div title="测试----->"
             data-options="selected:true,iconCls:'icon-wenzhangs'"
             style="padding: 10px;height:10px;">
            <a href="javascript:openTab('注册、登录、支付','test/test.jsp','icon-wenzhang')"
               class="easyui-linkbutton"
               data-options="plain:true,iconCls:'icon-wenzhang'"
               style="width: 150px;">测试
            </a>
        </div>
        <div title="游戏概况"
             data-options="selected:true,iconCls:'icon-wenzhangs'"
             style="padding: 10px;height:10px;">
            <a href="javascript:openTab('全服概况','Detail/DetailGame.jsp','icon-wenzhang')"
               class="easyui-linkbutton"
               data-options="plain:true,iconCls:'icon-wenzhang'"
               style="width: 150px;">全服概况
            </a>
            <a href="javascript:openTab('分服概况','Detail/DetailServer.jsp','icon-wenzhang')"
               class="easyui-linkbutton"
               data-options="plain:true,iconCls:'icon-wenzhang'"
               style="width: 150px;">分服概况
            </a>
            <a href="javascript:openTab('分渠道概况','Detail/DetailSp.jsp','icon-wenzhang')"
               class="easyui-linkbutton"
               data-options="plain:true,iconCls:'icon-wenzhang'"
               style="width: 150px;">分渠道概况
            </a>
        </div>
        <div title="实时数据"
             data-options="selected:true,iconCls:'icon-wenzhangs'"
             style="padding: 10px;height:10px;">

        </div>
        <div title="玩家信息"
             data-options="selected:true,iconCls:'icon-wenzhangs'"
             style="padding: 10px;height:10px;">

        </div>
        <div title="数据分析"
             data-options="selected:true,iconCls:'icon-wenzhangs'"
             style="padding: 10px;height:10px;">

        </div>

        <div title="GM功能"
             data-options="selected:true,iconCls:'icon-wenzhangs'"
             style="padding: 10px;height:10px;">

        </div>
        <div title="服务器管理"
             data-options="selected:true,iconCls:'icon-wenzhangs'"
             style="padding: 10px;height:10px;">
            <a href="javascript:openTab('服务器列表','server/server_list.jsp','icon-wenzhang')"
               class="easyui-linkbutton"
               data-options="plain:true,iconCls:'icon-wenzhang'"
               style="width: 150px;">服务器列表
            </a>
            <a href="javascript:openTab('踢人下线','server/server_kickout.jsp','icon-wenzhang')"
               class="easyui-linkbutton"
               data-options="plain:true,iconCls:'icon-wenzhang'"
               style="width: 150px;">踢人下线
            </a>
        </div>

        <div title="账号管理" data-options="iconCls:'icon-item'" style="padding:10px;border:none;">
            <a href="javascript:openTab(' 管理员列表','userManage.jsp','icon-lxr')"
               class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-lxr'" style="width: 150px;">
                管理员列表
            </a>
            <a href="javascript:openTab(' 测试','selecttest.jsp','icon-lxr')"
               class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-lxr'" style="width: 150px;">
                测试
            </a>
        </div>

        <div title="-------------"
             data-options="selected:true,iconCls:'icon-wenzhangs'"
             style="padding: 10px;height:10px;">

        </div>

        <div title="文章管理"
             data-options="selected:true,iconCls:'icon-wenzhangs'"
             style="padding: 10px;height:10px;">
            <a href="javascript:openTab(' 文章管理-ue','articleManage-ue.jsp','icon-wenzhang')"
               class="easyui-linkbutton"
               data-options="plain:true,iconCls:'icon-wenzhang'"
               style="width: 150px;">UEditor
            </a>
            <a href="javascript:openTab(' 文章管理-ke','articleManage-ke.jsp','icon-wenzhang')"
               class="easyui-linkbutton"
               data-options="plain:true,iconCls:'icon-wenzhang'"
               style="width: 150px;"> kindEditor(推荐)
            </a>
        </div>

        <div title="图片管理" data-options="iconCls:'icon-shouye'"
             style="padding:10px">
            <a href="javascript:openTab(' 图片设置','pictureManage.jsp?type=1&grade=1','icon-tupians')"
               class="easyui-linkbutton"
               data-options="plain:true,iconCls:'icon-tupian'"
               style="width: 150px;"> 图片设置</a>
        </div>
    </div>
</div>

</body>
</html>