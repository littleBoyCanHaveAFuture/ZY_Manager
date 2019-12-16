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

####1.注册账号
RedisKey
#####该渠道-游戏 所有账号
######BITMAP：UserInfo:spid:*:gid:*#G_AC_ANUMS
#####该渠道-游戏 当日-新增创号 
######BITMAP：UserInfo:spid:*:gid:*:date:yyyyMMdd#NA_CA

###### UserInfo:spid:*:gid:*:sid:*:date:yyyyMMdd#
###### API:spid:*:gid:*:sid:*:date:yyyyMMdd#
###### RTS:spid:*:gid:*:sid:*:date:yyyyMMdd#




####5.充值上报

###### UserInfo:spid:*:gid:*:sid:*:date:yyyyMMdd#
###### API:spid:*:gid:*:sid:*:date:yyyyMMdd#
###### RTS:spid:*:gid:*:sid:*:date:yyyyMMdd#

###### UserInfo:spid:*:gid:*:sid:*:date:yyyyMMdd#ON_PL
###### UserInfo:spid:*:gid:*:sid:*:date:yyyyMMdd#ON_PL
###### RTS:spid:*:gid:*:sid:*:date:yyyyMMddHHmm#REAL_ONLINE_AC