// 初始化内容 先加载完列表
$(function () {
    var userName = null;
    var password = null;
    var id = null;
    var data = {"id": id, "password": password, "userName": userName}
    $.ajax({
        url: "/users/datagrid",
        type: "post",
        data: data,
        dataType: "json",
        async: false,
        success: function (result) {
            if (result.resultCode === 501) {
                relogin();
            } else if (result.resultCode === 200) {
                result = {
                    total: result.total,
                    rows: result.rows
                }
                console.log(result);
                $("#dg").datagrid("loadData", result);
            }
        },
        error: function () {
            $.messager.alert("ERROR！", "查询失败");
        }
    });
});
var url = "${pageContext.request.contextPath}/users";
var method;

function searchUser() {
    $("#dg").datagrid('load', {
        "userName": $("#s_userName").val()
    });
}

function deleteUser() {
    var selectedRows = $("#dg").datagrid('getSelections');
    if (selectedRows.length === 0) {
        $.messager.alert("系统提示", "请选择要删除的数据！");
        return;
    }
    var strIds = [];
    for (var i = 0; i < selectedRows.length; i++) {
        strIds.push(selectedRows[i].id);
    }
    var ids = strIds.join(",");
    $.messager.confirm("系统提示", "您确认要删除这<font color=red>" + selectedRows.length + "</font>条数据吗？",
        function (r) {
            if (r) {
                $.ajax({
                    type: "DELETE",//方法类型
                    dataType: "json",//预期服务器返回的数据类型
                    url: "/users/" + ids,//url
                    data: {},
                    success: function (result) {
                        console.log(result);//打印服务端返回的数据
                        if (result.resultCode === 200) {
                            $.messager.alert(
                                "系统提示",
                                "数据已成功删除！");
                            $("#dg").datagrid(
                                "reload");
                        } else if (result.resultCode === 501) {
                            relogin();
                        } else {
                            $.messager.alert(
                                "系统提示",
                                "数据删除失败！");
                        }
                    },
                    error: function () {
                        $.messager.alert("ERROR！");
                    }
                });
            }
        });

}

function openUserAddDialog() {
    $("#dlg").dialog("open").dialog("setTitle", "添加用户信息");
    method = "POST";
}

function saveUser() {
    var userName = $("#save_userName").val();
    var password = $("#save_password").val();
    var managerLv = $("#save_mamagerLv").val();
    var agents = $("#save_agents").val();
    var func = $("#save_func").val();

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

function openUserModifyDialog() {
    var selectedRows = $("#dg").datagrid('getSelections');
    if (selectedRows.length !== 1) {
        $.messager.alert("系统提示", "请选择一条要编辑的数据！");
        return;
    }
    var row = selectedRows[0];
    $("#dlg").dialog("open").dialog("setTitle", "编辑用户信息");
    $('#fm').form('load', row);
    $("#password").val("******");
    $("#userId").val(row.id);
    method = "PUT";
}

function resetValue() {
    $("#userName").val("");
    $("#password").val("");
}

function closeUserDialog() {
    $("#dlg").dialog("close");
    resetValue();
}

//登录超时 重新返回到登录界面
function relogin() {
    // 登录失效
    console.log("登录失效");
    $.messager.confirm(
        "系统提示",
        "登录超时！",
        function (r) {
            if (r) {
                delCookie("userName");
                delCookie("roleName");
                parent.location.href = "../login.jsp";
            }
        });
}
