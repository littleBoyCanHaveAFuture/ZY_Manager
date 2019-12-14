###接口文档
### 目录
- 1、注册指悦平台账号接口
- 2、支付宝整合到ssm环境
- 3、微信支付整合到ssm环境

### 一、注册指悦平台账号接口
    Request：
    1.url=47.101.44.31/manager/ttt/register
    2.
    boolean     auto          自动注册(限渠道账号,无需username,pwd)*
    int         appId         游戏id*
    int         channelId     渠道id*
    string      channelUid    渠道账号id*
    string      channelUname  渠道账号登录名*
    string      channelUnick  渠道账号昵称*
    string      username      指悦账户登录名(为空即可)
    string      pwd           指悦账号密码(为空即可)
    string      phone         手机号*
    string      deviceCode    硬件设备号*
    string      imei          国际移动设备识别码*
    string      addparm       额外参数(为空即可)
    
    注：1.*不可为空
    
    js例子：
    let data = {
        "auto": "true",
        "appId": appId,
        "channelId": channelId,
        "channelUid": channelUid,
        "channelUname": "546546",
        "channelUnick": "10",

        "username": username,
        "pwd": password,
        "phone": "18571470846",
        "deviceCode": "PC",
        "imei": "PC",
        "addparm": ""
    };

    $.ajax({
        url: "/ttt/register",
        type: "post",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(data),
        dataType: "json",
        async: false,
        success: function (result) {
            if (result.resultCode === 200) {
                if (result.data.status != 1) {
                    $.messager.alert("注册失败：", result.data.err);
                } else {
                    t_accountid = result.data.account;
                    t_pwd = result.data.pwd;
                    let ss = "账号:" + result.data.account + "\n密码：" + result.data.pwd;
                    $.messager.alert("注册成功：", ss);
                }

            }
        },
        error: function () {
            $.messager.alert("系统提示", "操作失败");
        }
    });