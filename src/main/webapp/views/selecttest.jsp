<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Hello, Multiple Select!</title>

    <%--        <link rel="stylesheet" href="https://unpkg.com/multiple-select@1.5.2/dist/multiple-select.min.css">--%>
    <%--        <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>--%>
    <%--        <script src="https://unpkg.com/multiple-select@1.5.2/dist/multiple-select.min.js"></script>--%>


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

    <style>
        select {
            width: 20%;
        }
    </style>
</head>
<body>
<button id="setSelectsBtn" class="btn btn-secondary">SetSelects</button>
<button id="getSelectsBtn" class="btn btn-secondary">GetSelects</button>
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
        <td>渠道id：</td>
        <td>
            <label for="save_agents"></label>
            <input type="text" id="save_agents" name="" class="easyui-validatebox" required="false"/>
        </td>
    </tr>
    <tr>
        <td>模块权限：</td>
        <td>
            <select title="选择模块" multiple="multiple" name="ddd" size="5" id="qqq">
            </select>
        </td>
    </tr>

    <tr>
        <td><a href="javascript:saveUsers()" class="easyui-linkbutton" iconCls="icon-ok">保存</a></td>
        <td><a href="javascript:custom_close()" class="easyui-linkbutton" iconCls="icon-cancel">关闭</a>
    </tr>

</table>


<script type="text/javascript">
    var $select = $('select');
    $(function () {
        // 初始化内容 先加载完列表
        $.ajax({
            //获取下拉
            url: "/users/getFuncList",
            type: "post",
            async: false,
            data: {},
            dataType: "json",
            success: function (combox) {//动态设置玩家渠道下拉列表
                var result = combox;
                //数据是有序的
                var times = 20;//大模块个数
                var start = 10000;
                var max = result[result.length - 1].id;//最大的数
                console.log("[max]----->" + max);

                for (var int = 1; int <= times; int++) {
                    var end = start + 10000;
                    console.log("[]----->" + start + ":" + end);
                    var id = new Array();
                    var name = new Array();
                    var num = 0;
                    if (start > max) {
                        return;
                    }
                    for (var i = 0; i < result.length; i++) {
                        var res = parseInt(result[i].id / 10000);
                        if (res === int) {
                            id[num] = result[i].id;
                            name[num] = result[i].name;
                            num++;
                        }
                    }
                    console.log(id);
                    console.log(name);
                    var selects = "";
                    for (var i = 1; i < num; i++) {
                        selects += "<option id=save_func value='" + id[i] + "'>" + name[i] + "</option>";
                    }
                    // $("#qqq").append("<optgroup label='" + name[0] + "'>" + selects + "</optgroup>");
                    $("#qqq").append(selects);
                    start = end;
                }
            }
        });

        $("#qqq").multipleSelect({
            placeholder: "请选择",
            width: 250,
            multiple: true,
            multipleWidth: 150,
        });
        // $('select').multipleSelect();

    });
    $('#setSelectsBtn').click(function () {
        $select.multipleSelect('setSelects', [1, 3])
    });

    $('#getSelectsBtn').click(function () {
        alert('Selected values: ' + $select.multipleSelect('getSelects'));
        alert('Selected texts: ' + $select.multipleSelect('getSelects', 'text'))
    });

    function saveUsers() {
        console.log("saveUser");
        var userName = $("#save_userName").val();
        var password = $("#save_password").val();
        var managerLv = $("#save_mamagerLv").val();
        var agents = $("#save_agents").val();
        var func = parseToString();
        var id = $("#userId").val();
        var data = {
            "userName": userName,
            "password": password,
            "func": func,
            "managerLv": managerLv,
            "agents": agents
        };
        console.log(data);
        $.ajax({
            url: "/users/addUser",
            type: "post",//方法类型
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(data),
            dataType: "json",//预期服务器返回的数据类型
            async: false,
            success: function (result) {
                console.log(result);//打印服务端返回的数据
                if (result.resultCode === 200) {
                    $.messager.alert("系统提示", "保存成功");
                    $("#dlg").dialog("close");
                    $("#dg").datagrid("reload");
                    resetValue();
                } else if (result.resultCode === 501) {
                    relogin();
                } else {
                    $.messager.alert("系统提示", "操作失败");
                    $("#dlg").dialog("close");
                    resetValue();
                }
            },
            error: function () {
                $.messager.alert("系统提示", "操作失败");
            }
        });
    }

    function parseToString() {
        var func = $select.multipleSelect('getSelects');
        var res = "";
        var first = false;
        for (k in func) {
            if (first) {
                res += ",";
            } else if (!first) {
                first = true;
            }
            res += func[k];
        }
        console.log(res);
        return res;
    }

    function custom_close() {
        window.opener = null;
        window.open('', '_self');
        window.close();
        window.location.href = "userManage.jsp";
    }
</script>

</body>
