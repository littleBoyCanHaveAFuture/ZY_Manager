let myChart;
$(function () {
    $('#save_startTime').datebox('setValue', formatterDate(new Date(new Date().getTime() - 60 * 60 * 1000), 1));
    $('#save_endTime').datebox('setValue', formatterDate(new Date(), 1));
    initSpGameServer(1);
    initSpGameServer(2);
    initSpGameServer(3);
    getLinesByDate(0);
});

function initChart() {
    if (myChart != null && myChart !== "" && myChart !== undefined) {
        myChart.dispose();
    }
    // 基于准备好的dom，初始化echarts实例
    myChart = echarts.init(document.getElementById('main'));
}
