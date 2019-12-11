var myChart;
$(function () {
    $('#save_startTime').datebox('setValue', formatterDate(new Date(new Date().getTime() - 60 * 60 * 1000), 1));
    $('#save_endTime').datebox('setValue', formatterDate(new Date(), 1));
    initGameList();
    getLinesByDate(0);
});

function initChart() {
    if (myChart != null && myChart !== "" && myChart !== undefined) {
        myChart.dispose();
    }
    // 基于准备好的dom，初始化echarts实例
    myChart = echarts.init(document.getElementById('main'));
}

function getLinesByDate(type) {
    initChart();
    myChart.setOption({
        //图表标题
        title: {
            text: '玩家实时数据折线图',
            subtext: '数据来自指悦平台Redis数据库',
            x: 'center'
        },
        //坐标轴触发提示框，多用于柱状、折线图中
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                animation: false
            }
        },
        //支持鼠标滚轮缩放
        dataZoom: [
            {
                show: true,
                type: 'slider',
                realtime: true,
                start: 0,
                end: 100//默认数据初始缩放范围为10%到90%
            },
            {
                //支持单独的滑动条缩放
                type: 'inside',
                realtime: true,
                start: 0,
                end: 100//默认数据初始缩放范围为10%到90%
            },
            {
                type: 'inside'
            }
        ],
        grid: [{
            left: 50,
            right: 0,
            height: '80%'
        }],
        legend: {	//图表上方的类别显示
            show: true,
            data: ['新增', '在线', '收入'],
            x: 'left'
        },
        color: [
            '#FF3333',	//新增
            '#53FF53',	//在线
            '#AC00AC',	//收入
        ],
        toolbox: {
            feature: {
                dataZoom: {
                    yAxisIndex: 'none'
                },
                dataView: {show: true, readOnly: false},
                magicType: {show: true, type: ['line', 'bar']},
                restore: {show: true},
                saveAsImage: {show: true}
            }
        },

        axisPointer: {
            link: {xAxisIndex: 'all'}
        },

        xAxis: {	//X轴
            type: 'category',
            boundaryGap: false,
            data: []	//先设置数据值为空，后面用Ajax获取动态数据填入
        },
        yAxis: [
            {
                min: 0, //y轴的最小值
                max: 100, //y轴最大值
                interval: 10, //值之间的间隔
                type: 'value',
                axisLabel: {
                    formatter: '{value} 人'	//控制输出格式
                }
            }

        ],
        //系列（内容）列表
        series: [
            //设置折线图中表示每个坐标点的符号；emptycircle：空心圆；emptyrect：空心矩形；circle：实心圆；emptydiamond：菱形
            {
                name: '新增',
                type: 'line',	//折线图表示（生成温度曲线）
                symbol: 'emptycircle',
                data: []		//数据值通过Ajax动态获取
            },
            {
                name: '在线',
                type: 'line',
                symbol: 'emptycircle',
                data: []
            },
            {
                name: '收入',
                type: 'line',
                symbol: 'emptycircle',
                data: []
            }
        ]
    });
    // 开始加载数据

    if (type !== 1) {
        return;
    }
    myChart.showLoading();


    let gameId = $("#save_gameId").val();
    let serverId = $("#save_serverId").val();
    let spId = $("#save_spId").val();
    let startTime = $("#save_startTime").datetimebox("getValue");
    let endTime = $("#save_endTime").datetimebox("getValue");

    //endTime 大于当前时间 处理下

    let send = {
        "startTime": startTime,
        "endTime": endTime,
        "spId": spId,
        "gameId": gameId,
        "serverId": serverId
    };
    console.info(send);
    // 异步加载数据，并显示
    $.ajax({
        type: "post",
        async: true,
        url: "/realtime/realtimedata",
        data: send,
        dataType: "json",
        success: function (result) {
            if (result.resultCode === 200) {
                let newadd = [];
                let online = [];
                let money = [];
                let times = [];

                newadd = result.newadd;
                online = result.onlines;
                money = result.money;
                times = result.times;

                console.info("newadd" + newadd);
                console.info("online" + online);
                console.info("money" + money);
                times = times.map(function (str) {
                    let yyyy = str.substring(0, 4);
                    let MM = str.substring(4, 6);
                    let dd = str.substring(6, 8);
                    let mm = str.substring(8, 10);
                    let ss = str.substring(10, 12);
                    return yyyy + "-" + MM + "-" + dd + " " + mm + ":" + ss;
                });

                let maxValue = 2 * getmax([getmax(newadd), getmax(online), getmax(money)]);
                let ymax = (maxValue === 0) ? 10 : maxValue;
                if (ymax < 10) {
                    ymax = 10;
                } else if (ymax < 50) {
                    ymax = 50;
                } else if (ymax < 100) {
                    ymax = 100;
                } else if (ymax < 500) {
                    ymax = 500;
                } else if (ymax < 1000) {
                    ymax = 1000;
                } else if (ymax < 5000) {
                    ymax = 5000;
                } else if (ymax < 10000) {
                    ymax = 10000;
                }
                let interval = (maxValue === 0) ? 1 : ymax / 10;

                //隐藏加载动画
                myChart.hideLoading();
                //载入数据
                myChart.setOption({
                    //填入X轴数据
                    xAxis: {
                        data: times
                    },
                    // 根据名字对应到相应的系列
                    //填入系列（内容）数据
                    series: [
                        {
                            name: '新增',
                            data: newadd
                        },
                        {
                            name: '在线',
                            data: online
                        },
                        {
                            name: '收入',
                            data: money
                        }
                    ],
                    yAxis: [
                        {
                            min: 0, //y轴的最小值
                            max: ymax, //y轴最大值
                            interval: interval, //值之间的间隔
                            type: 'value',
                            axisLabel: {
                                formatter: '{value} 人'	//控制输出格式
                            }
                        }
                    ],
                });
            } else if (result.resultCode === 501) {
                relogin();
            } else {
                $.messager.alert("ERROR！", result.err);
                // layerUIAlert(result.msg, 5);
                myChart.hideLoading();
            }
        },
        error: function (errorMsg) {
            // layerUIAlert("图表请求数据失败", 5);
            myChart.hideLoading();
        }
    });
}

function initGameList() {
    $.ajax({
        //获取下拉
        url: "/server/getGameList",
        type: "get",
        async: false,
        dataType: "json",
        success: function (result) {
            if (result.resultCode === 501) {
                relogin();
            } else if (result.resultCode === 200) {
                let select_gameId = $("#save_gameId");
                select_gameId.find("option").remove();
                select_gameId.append("<option value=-1 selected=selected>请选择</option>");
                for (let res = 0; res < result.total; res++) {
                    select_gameId.append("<option  value='" + result.rows[res].gameId + "'>" + result.rows[res].name + "</option>");
                }
            }
        },
        error: function () {
            $.messager.alert("ERROR！", "获取游戏列表出错");
        }
    });
}

function initServerList(type) {
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
                    let select_serverId = $("#save_serverId");
                    select_serverId.find("option").remove();
                    select_serverId.append("<option value=-1 selected=selected>请选择</option>");
                    for (let res = 0; res < result.total; res++) {
                        select_serverId.append("<option value='" + result.rows[res] + "'>" + result.rows[res] + "</option>");
                    }
                } else {
                    let select_spId = $("#save_spId");
                    select_spId.find("option").remove();
                    select_spId.append("<option value=-1 selected=selected>请选择</option>");
                    for (let res = 0; res < result.total; res++) {
                        select_spId.append("<option value='" + result.rows[res] + "'>" + result.rows[res] + "</option>");
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
