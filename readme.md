###接口文档
### 目录
- 1、注册指悦平台账号接口
- 2、支付宝整合到ssm环境
- 3、微信支付整合到ssm环境

### 一、注册指悦平台账号接口
- 1.注册账号
- 2.进入游戏
- 3.创建角色
- 4.离开游戏
- 5.充值上报
- 6.wap充值

#### 1.注册账号

###### 1.1 存储玩家账户：精确到，渠道-游戏
###### BITMAP：UserInfo:spid:*:gid:*#G_AC_ANUMS

###### 1.2 存储新增创号：精确到，渠道-游戏-当月天数
###### BITMAP：UserInfo:spid:*:gid:*:date:yyyyMMdd#NA_CA


    url: "47.101.44.31/ttt/register",
    type: "post",
    contentType: "application/json; charset=utf-8",
    data: JSON.stringify(data),
    dataType: "json",
    async: false,
        
    Request：
    let data = {
        "auto": "true",
        "appId": "9999",
        "channelId": 9999,
        "channelUid": "1000099",
        "channelUname": "test20191217",
        "channelUnick": "测试账号1000099",
        "username": "",
        "pwd": "",
        "phone": "11100003333",
        "deviceCode": "PC",
        "imei": "PC",
        "addparm": ""
    };
    
    Response ：
        body = {
            "resultCode":"200",
            "message":"SUCCESS",//SUCCESS|FAIL
            "data":""
        }
    FAIL：
        data={
            "err":"参数非法："+"key"+"为空";//key为Request data的键
        }
    SUCCESS：
        data={
            "message":"";//注册结果描述
            "account":"",
            "pwd":"",
            "status":"1"//1，注册成功|0，注册失败
        }
    

#### 2.进入游戏
###### 2.1 存储活跃账号：精确到，渠道-游戏-区服-当天天数
###### BITMAP：UserInfo:spid:*:gid:*:sid:*:date:yyyyMMdd#ACT_PL

###### 2.2 存储在线账号：精确到，渠道-游戏-区服-当天天数
###### BITMAP：UserInfo:spid:*:gid:*:sid:*:date:yyyyMMdd#ON_PL

###### 2.3 存储实时在线账号：精确到，渠道-游戏-区服-当前分钟
###### BITMAP：RTS:spid:*:gid:*:sid:*:date:yyyyMMddHHmm#REAL_ONLINE_AC

###### API:spid:*:gid:*:sid:*:date:yyyyMMdd#
###### RTS:spid:*:gid:*:sid:*:date:yyyyMMdd#

#### 3.创建角色
###### 3.1 创建过角色的账号（可判断滚服账号）：精确到，渠道-游戏
###### BITMAP：UserInfo:spid:*:gid:*#G_AC_SRole

###### 3.1 新增创角：精确到，渠道-游戏-区服-当天天数
###### BITMAP：UserInfo:spid:*:gid:*:sid:*:date:yyyyMMdd#NA_CR

###### 3.2 新增创角去除滚服：精确到，渠道-游戏-区服-当天天数
###### BITMAP：UserInfo:spid:*:gid:*:sid:*:date:yyyyMMdd#NA_CR_RM_OLD

###### 3.3 累计创角：精确到，渠道-游戏-区服
###### Sorted SET：UserInfo:spid:*:gid:*:sid:*#AC_INFO GACC_CR

###### 3.4 实时新增创角（每分钟的创角数目）：精确到，渠道-游戏-当天 
###### BITMAP：RTS:spid:*:gid:*:sid:*:date:yyyyMMdd#NA_CR yyyyMMddHHmm

#### 4.离开游戏
###### 4.1 移除在线账号：精确到，渠道-游戏-区服-当天天数
###### BITMAP：UserInfo:spid:*:gid:*:sid:*:date:yyyyMMdd#ON_PL

####5.充值上报
###### 5.1 累计充值金额：精确到，渠道-游戏-区服
###### Sorted SET：API:spid:*:gid:*:sid:*#RE_TO_INFO GACC_RE_AM

###### 5.2 充值次数：精确到，渠道-游戏-区服-当天天数
###### Sorted SET：API:spid:*:gid:*:sid:*:date:yyyyMMdd#RE_INFO RE_TS

###### 5.3 充值人数：精确到，渠道-游戏-区服-当天天数
###### Sorted SET：API:spid:*:gid:*:sid:*date:yyyyMMdd#RE_INFO RE_PL

###### 5.3 充值金额：精确到，渠道-游戏-区服-当天天数
###### Sorted SET：API:spid:*:gid:*:sid:*date:yyyyMMdd#RE_INFO RE_AM

###### 5.4 当日首次付费金额：精确到，渠道-游戏-区服-当天天数
###### Sorted SET：API:spid:*:gid:*:sid:*date:yyyyMMdd#RE_INFO RE_FAM

###### 5.4 当日首次付费人数：精确到，渠道-游戏-区服-当天天数
###### Sorted SET：API:spid:*:gid:*:sid:*date:yyyyMMdd#RE_AC 

###### 5.4 实时充值金额（每分钟的充值金额）：精确到，渠道-游戏-区服-当天天数
###### Sorted SET：RTS:spid:*:gid:*:sid:*date:yyyyMMdd#REAL_RE_AM yyyyMMddHHmm

###### 5.5 注册付费玩家：精确到，渠道-游戏-区服-当天天数
###### BITMAP：API:spid:*:gid:*:sid:*date:yyyyMMdd#RE_AC_NA_CA 

###### 5.6 注册付费金额（每分钟的充值金额）：精确到，渠道-游戏-区服-当天天数
###### Sorted SET：RTS:spid:*:gid:*:sid:*date:yyyyMMdd#REAL_RE_AM RE_AM_NA_CA


