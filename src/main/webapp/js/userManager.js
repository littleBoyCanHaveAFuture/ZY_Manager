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
    // initDoubleChoice();
    //权限复选下拉框 初始化
    $.ajax({
        //获取下拉
        url: "/users/getFuncList",
        type: "post",
        async: false,
        data: {},
        dataType: "json",
        success: function (combobox) {//动态设置玩家渠道下拉列表
            for (var int = 0; int < combobox.length; int++) {
                $("#authority").append("<option value='" + combobox[int].id + "'>" + combobox[int].name + "</option>");
            }
        }
    });
    $("#authority").multipleSelect({
        placeholder: "请选择",
        width: 500,
        multiple: true,
        multipleWidth: 150

    });
    $('select').multipleSelect()
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

var rankNameData = [
    // {key: "所有", value: -1},
    {key: "10000 ", value: 10000},
    {key: "20000 ", value: 20000},
    {key: "30000 ", value: 30000},
    {key: "40000 ", value: 40000},
    {key: "50000 ", value: 50000},
    {key: "60000 ", value: 60000},
];

//给复选框赋值
function initDoubleChoice() {
    var t = document.getElementById("addUserTable");
    if (rankNameData.length > 0) {
        var r = t.insertRow(t.rows.length);//创建新的行
        var c = r.insertCell();//创建新的列
        c.innerHTML = "开放模块：";

        for (var i = 0; i < rankNameData.length; i++) {
            //tr
            if (i % 4 === 0) {
                var r = t.insertRow(t.rows.length);//创建新的行
            }
            //td
            var c = r.insertCell(); 			   //创建新的列
            c.innerHTML = "<input type=checkbox value='" + rankNameData[i].value + "' name=checks onclick=oneChoice()>" + rankNameData[i].key;
            // $("#rankType").append("<option value='" + rankNameData[i].value + "'>" + rankNameData[i].key + "</option>");
            $("#rankType").append("<option >" + "<input type=checkbox value='" + rankNameData[i].value + "' name=checks onclick=oneChoice()>" + rankNameData[i].key + "</option>");
        }
    } else {
        var r = t.insertRow();
        var c = r.insertCell();
        c.innerHTML = "暂无主题列表";
    }
    // document.getElementById('addUserTable').appendChild(t);
}

//声明函数
function beginGet() {
    //首先我们要得到多选框中有一些什么样的值
    var elementsByName = document.getElementsByName;
    var date = elementsByName("save_mamagerLv");
    //然后我们去得到这个多选框的长度
    //使用字符串数组，用于存放选择好了的数据
    var str = "";
    for (var i = 0; i < date.length; i++) {
        if (date[i].checked === true) {

            str += date[i].value;//这个是获取多选框中的值
            if (i !== date.length - 1) {
                str += ",";
            }
        }
    }
}

//下拉选复选框单选事件
function oneChoice() {
    var obj = $('[name="checks"]');
    var check_val = [];
    for (k in obj) {
        if (obj[k].checked && obj[k].value !== "0")
            check_val.push(obj[k].value);
    }
    console.log(check_val);
    // $('[name="save_func"]').val()
}

$("#authority").multipleSelect({
    placeholder: "请选择",
    width: 500,
    multiple: true,
    multipleWidth: 150

});

function show() {
    $("#col-sm-10").show();
}