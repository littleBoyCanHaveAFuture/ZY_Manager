package com.ssm.promotion.core.jedis;

public class JedisKey {
    //键值参考：账号id 一定要唯一
/*
    新增创号    BitMap
    UserInfo:spid:{spid}:gid:{gid}:date:{yyyyMMdd}#NEW_ADD_CREATE_ACCOUNT {account_Id}

    该游戏所有账号 BitMap
    UserInfo:spid:{spid}:gid:{gid}#GAME_ACCOUNT_ALL_NUMS {account_Id}

    创建过角色的账号    BitMap
    UserInfo:spid:{spid}:gid:{gid}#GAME_ACCOUNT_HAS_ROLE {account_Id}

    活跃玩家    Sorted Set
    UserInfo:spid:{spid}:gid:{gid}:sid:{sid}#ACTIVE_PLAYERS score {yyyyMMdd}

    在线玩家    Sorted Set
    UserInfo:spid:{spid}:gid:{gid}:sid:{sid}#ONLINE_PLAYERS score {yyyyMMdd}

    新增创角    Sorted Set
    UserInfo:spid:{spid}:gid:{gid}:sid:{sid}#NEW_ADD_CREATE_ROLE    score   {yyyyMMdd}

    新增创角去除滚服
    UserInfo:spid:{spid}:gid:{gid}:sid:{sid}#NEW_ADD_CREATE_ROLE_RM_OLD    score   {yyyyMMdd}

    累计创角    Sorted Set
    UserInfo:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#ACCOUNT_INFO    score   {yyyyMMdd}

    当日付费角色  Set
    ACTIVE_PLAYERS_INFO:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#RECHARGE_ROLES" {role_id}

    历史付费角色  Set
    ACTIVE_PLAYERS_INFO:spid:{spid}:gid:{gid}:sid:{sid}#RECHARGE_ACCOUNT" {role_id}

    充值次数    Sorted Set
    ACTIVE_PLAYERS_INFO:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#RECHARGE_INFO {score} RECHARGE_TIMES
    充值人数    Sorted Set
    ACTIVE_PLAYERS_INFO:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#RECHARGE_INFO {score} RECHARGE_PLAYERS
    充值金额    Sorted Set
    ACTIVE_PLAYERS_INFO:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#RECHARGE_INFO {score} RECHARGE_AMOUNTS

    累计充值金额    Sorted Set
    ACTIVE_PLAYERS_INFO:spid:{spid}:gid:{gid}:sid:{sid}#RECHARGE_INFO {score} GAME_ACCUMULATION_RECHARGE_AMOUNTS
    累计充值人数    Sorted Set
    ACTIVE_PLAYERS_INFO:spid:{spid}:gid:{gid}:sid:{sid}#RECHARGE_INFO {score} GAME_ACCUMULATION_RECHARGE_ACCOUNTS

    注册付费人数    Sorted Set
    ACTIVE_PLAYERS_INFO:spid:{spid}:gid:{gid}:sid:{sid}RECHARGE_ROLES_NA_CR {score} {yyyyMMdd}
    注册付费金额
    ACTIVE_PLAYERS_INFO:spid:{spid}:gid:{gid}:sid:{sid}RECHARGE_AMOUNTS_NA_CR {score} {yyyyMMdd}

    当日首次付费人数    Sorted Set
     ACTIVE_PLAYERS_INFO:spid:{spid}:gid:{gid}:sid:{sid}RECHARGE_FIRST_PAY_ROLES {score} {yyyyMMdd}
    当日首次付费金额    Sorted Set
     ACTIVE_PLAYERS_INFO:spid:{spid}:gid:{gid}:sid:{sid}RECHARGE_FIRST_PAY_AMOUNTS {score} {yyyyMMdd}
 */
}
