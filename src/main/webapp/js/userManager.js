var $select = $('select');

$(function () {
    // 初始化内容 先加载完列表
    initUserTable();
    //模块权限
    initUserFunc();
    $("#save_func").multipleSelect({
        placeholder: "请选择",
        width: 400,
        multiple: true,
        multipleWidth: 150,
    });
    // $('select').multipleSelect();
});

var method;

//搜索用户
function searchUser() {
    $("#dg").datagrid('load', {
        "userName": $("#s_userName").val()
    });
}

//删除用户
function deleteUser() {
    let selectedRows = $("#dg").datagrid('getSelections');
    if (selectedRows.length === 0) {
        $.messager.alert("系统提示", "请选择要删除的数据！");
        return;
    }
    let strIds = [];
    for (let i = 0; i < selectedRows.length; i++) {
        strIds.push(selectedRows[i].id);
    }
    var ids = strIds.join(",");
    $.messager.confirm("系统提示", "您确认要删除这" +
        "<font color=red>" + selectedRows.length + "</font>"
        + "条数据吗？",
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

//修改用户信息
function openUserAddDialog() {
    $("#dlg").dialog("open").dialog("setTitle", "添加用户信息");
    method = "POST";
}

//上传用户信息
function saveUser() {
    let userName = $("#save_userName").val();
    let password = $("#save_password").val();
    let managerLv = $("#save_mamagerLv").val();
    let agents = $("#save_agents").val();
    let func = $("#save_func").val();

    let id = $("#userId").val();
    let data = {
        "id": id,
        "userName": userName,
        "password": password,
        "func": parseToString(func),
        "managerLv": managerLv,
        // "spId": spId,
        "agents": agents
    };
    let url = "";
    console.log(data);
    if (method === "PUT") {
        url = "/users/updateUser";
    } else if (method === "POST") {
        url = "/users/addUser";
    }
    $.ajax({
        url: url,
        type: method,//方法类型
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(data),
        dataType: "json",//预期服务器返回的数据类型
        async: false,
        success: function (result) {
            console.log("trd" + result);//打印服务端返回的数据
            if (result.resultCode === 200) {
                $.messager.alert("系统提示", "保存成功");
                $("#dlg").dialog("close");
                $("#dg").datagrid("reload");
                resetValue();
            } else if (result.resultCode === 501) {
                relogin();
            } else {
                $.messager.alert("系统提示", result.message);
                $("#dlg").dialog("close");
                resetValue();
            }
        },
        error: function () {
            $.messager.alert("系统提示", "操作失败");
        }
    });
}

//打开 修改用户信息 对话框
function openUserModifyDialog() {
    let selectedRows = $("#dg").datagrid('getSelections');
    if (selectedRows.length !== 1) {
        $.messager.alert("系统提示", "请选择一条要编辑的数据！");
        return;
    }
    let row = selectedRows[0];
    $("#dlg").dialog("open").dialog("setTitle", "编辑用户信息");
    $('#fm').form('load', row);
    $("#password").val("******");
    $("#userId").val(row.id);

    method = "PUT";
}

function resetValue() {
    $("#userName").val("");
    $("#roleName").val("");
    $("#mamagerLv").val("");
    $("#agents").val("");
    $("#spId").val("");
    $("#password").val("");
    $("#func").val("");
}

function closeUserDialog() {
    console.log("close");
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

function parseToString(func) {
    let res = "";
    let first = false;
    for (let k in func) {
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

function initUserTable() {
    let data = {"id": null, "password": null, "userName": null};
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
                };
                console.log(result);
                $("#dg").datagrid("loadData", result);
            }
        },
        error: function () {
            $.messager.alert("ERROR！", "查询失败");
        }
    });
}

function initUserFunc() {
    $.ajax({
        //获取下拉
        url: "/users/getFuncList",
        type: "post",
        async: false,
        data: {},
        dataType: "json",
        success: function (combox) {//动态设置玩家渠道下拉列表
            let result = combox;
            //数据是有序的
            let times = 20;//大模块个数
            let start = 10000;
            let max = result[result.length - 1].id;//最大的数
            console.log("[max]----->" + max);

            for (let int = 1; int <= times; int++) {
                let end = start + 10000;
                console.log("[]----->" + start + ":" + end);
                let id = [];
                let name = [];
                let num = 0;
                if (start > max) {
                    return;
                }
                for (let i = 0; i < result.length; i++) {
                    let res = parseInt(result[i].id / 10000);
                    if (res === int) {
                        id[num] = result[i].id;
                        name[num] = result[i].name;
                        num++;
                    }
                }
                console.log(id);
                console.log(name);
                let selects = "";
                for (let i = 1; i < num; i++) {
                    selects += "<option id=save_func value='" + id[i] + "'>" + name[i] + "</option>";
                }
                $("#save_func").append("<optgroup label='" + name[0] + "'>" + selects + "</optgroup>");
                // $("#qqq").append(selects);
                start = end;
            }
        }
    });
}
