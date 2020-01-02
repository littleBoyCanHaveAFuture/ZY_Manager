$(function () {
    initSpGameServer(1);
    initSpGameServer(2);
    initSpGameServer(3);
});

function initSpGameServer(type) {
    let gameId = $('#save_gameId').val();
    let serverId = $("#save_serverId").val();
    let spId = $("#save_spId").val();

    let data = {
        "gameId": gameId,
        "serverId": serverId,
        "spId": spId,
        "type": type
    };

    $.ajax({
        //获取下拉
        url: "/server/getDistinctServerInfo",
        type: "post",
        data: data,
        async: false,
        dataType: "json",
        success: function (result) {
            if (result.resultCode === 501) {
                relogin();
            } else if (result.resultCode === 200) {
                // console.log(result);
                if (type === 1) {
                    let select_spId = $("#save_spId");
                    select_spId.find("option").remove();
                    select_spId.append("<option value=-1 selected=selected>请选择</option>");
                    for (let res = 0; res < result.total; res++) {
                        select_spId.append("<option value='" + result.rows[res] + "'>" + result.rows[res] + "</option>");
                    }
                } else if (type === 2) {
                    let select_gameId = $("#save_gameId");
                    select_gameId.find("option").remove();
                    select_gameId.append("<option value=-1 selected=selected>请选择</option>");
                    // console.info(result.rows);
                    for (let res = 0; res < result.total; res++) {

                        let gameid = result.rows[res].gameId;
                        let name = result.rows[res].name + "\t" + gameid;
                        select_gameId.append("<option  value='" + gameid + "'>" + name + "</option>");
                    }
                } else if (type === 3) {
                    let select_serverId = $("#save_serverId");
                    select_serverId.find("option").remove();
                    select_serverId.append("<option value=-1 selected=selected>请选择</option>");
                    for (let res = 0; res < result.total; res++) {
                        select_serverId.append("<option value='" + result.rows[res] + "'>" + result.rows[res] + "</option>");
                    }
                }

            }
        },
        error: function () {
            $.messager.alert("ERROR！", "获取游戏列表出错");
        }
    });
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
                parent.location.href = "../../login.jsp";
            }
        });
}