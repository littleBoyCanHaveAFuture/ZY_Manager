package com.zyh5games.jedis;

import org.apache.log4j.Logger;

import static com.zyh5games.jedis.RedisKey_Body.*;

/**
 * @author song minghua
 * @date 2019/11/28
 */
public class RedisKey_Gen {
    private static final Logger log = Logger.getLogger(RedisKey_Gen.class);

    private static final boolean isLog = false;

    public static String get_ChannelLoginToken(String gameId, String channelId, String channelUid) {
        String key = "Login" +
                ":" + SP_ID + ":" + channelId +
                "#" + channelUid;
        if (isLog) {
            log.info("get_ChannelLoginToken:\t" + key);
        }
        return key;
    }

    /**
     * 全服汇总 每日
     * <p>
     * Hash
     * <p>
     * member - currDay（时间戳 yyyyMMdd） | score - RechargeSummary.json
     */
    public static String get_RechargeInfo_Game(String gameId) {
        String key = RedisKey_Header.RECHARGE_INFO +
                ":" + GAME_ID + ":" + gameId +
                "#" + RedisKey_Tail.RS_DAY_INFO;
        if (isLog) {
            log.info("getKeyDay_RechargeInfo:\t" + key);
        }
        return key;
    }

    /**
     * 区服汇总-同一游戏  每日
     * <p>
     * Hash
     * <p>
     * member - currDay（时间戳 yyyyMMdd） | score - RechargeSummary.json
     */
    public static String get_RechargeInfo_Server(String gameId, String serverId) {
        String key = RedisKey_Header.RECHARGE_INFO +
                ":" + GAME_ID + ":" + gameId +
                ":" + SERVER_ID + ":" + serverId +
                "#" + RedisKey_Tail.RS_SERVER_INFO;
        if (isLog) {
            log.info("getKeyServer_RechargeInfo:\t" + key);
        }
        return key;
    }

    /**
     * 渠道汇总-同一游戏
     * <p>
     * Hash
     * <p>
     * member - currDay（时间戳 yyyyMMdd） | score - RechargeSummary.json
     */
    public static String get_RechargeInfo_Channel(String gameId, String serverId) {
        String key = RedisKey_Header.RECHARGE_INFO +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + serverId +
                "#" + RedisKey_Tail.RS_SERVER_INFO;
        if (isLog) {
            log.info("get_RechargeInfo_Channel:\t" + key);
        }
        return key;
    }

    /**
     * 获取游戏信息
     * 清空redis需要
     * 1.手动加入
     * 2.或者 根据数据库自动导入
     */
    public static String get_GameInfo() {
        String key = RedisKey_Header.GAME_INFO +
                ":" + "id" + "#" + RedisKey_Tail.GAMEINFO;
        if (isLog) {
            log.info("getKey_GameInfo:\t" + key);
        }
        return key;
    }

    /**
     * 获取游戏的渠道信息
     */
    public static String get_ChannelInfo(String gameId) {
        String key = RedisKey_Header.GAME_INFO +
                ":" + GAME_ID + ":" + gameId +
                "#" + RedisKey_Tail.SPIDINFO;
        if (isLog) {
            log.info("getKey_SpInfo:\t" + key);
        }
        return key;
    }

    /**
     * 获取游戏-渠道的区服信息
     */
    public static String get_ServerInfo(String gameId, String channelId) {
        String key = RedisKey_Header.GAME_INFO +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + channelId +
                "#" + RedisKey_Tail.SERVERINFO;
        if (isLog) {
            log.info("getKey_ServerInfo:\t" + key);
        }
        return key;
    }

    /**
     * 2020年6月4日16:15:46
     * 获取游戏-渠道的区服信息
     */
    public static String get_GameServerInfo(String gameId) {
        String key = RedisKey_Header.GAME_INFO +
                ":" + GAME_ID + ":" + gameId +
                "#" + RedisKey_Tail.SERVERINFO;
        if (isLog) {
            log.info("get_GameServerInfo:\t" + key);
        }
        return key;
    }

    /**
     * 新增创号
     * <p>
     * Bit map
     * <p>
     * offset 账号id
     */
    public static String get_AccountCreate_Day(String channelId, String gameId, String currDay) {
        String key = RedisKey_Header.USER_INFO +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + channelId +
                ":" + DATE + ":" + currDay +
                "#" + RedisKey_Tail.NEW_ADD_CREATE_ACCOUNT;
        if (isLog) {
            log.info("getKeyAccountCreateDay:\t" + key);
        }
        return key;
    }

    /**
     * 账号是否新增账号：第一个游戏
     * <p>
     * Bit map
     * <p>
     * offset 账号id
     */
    public static String get_AccountCreate_Server_Day(String gameId, String serverId, String currDay) {
        String key = RedisKey_Header.USER_INFO +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + serverId +
                ":" + DATE + ":" + currDay +
                "#" + RedisKey_Tail.NEW_ADD_FIRST_ACCOUNT;
        if (isLog) {
            log.info("get_AccountCreate_Server_Day:\t" + key);
        }
        return key;
    }

    /**
     * 该游戏所有账号
     * <p>
     * Bit map
     * <p>
     * offset 账号id
     */
    public static String get_AccountAll(String channelId, String gameId) {
        String key = RedisKey_Header.USER_INFO +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + channelId +
                "#" + RedisKey_Tail.GAME_ACCOUNT_ALL_NUMS;
        if (isLog) {
            log.info("getKeyAccountAll:\t" + key);
        }
        return key;
    }

//进入游戏

    /**
     * 登陆token
     */
    public static String get_LoginToken(String gameId, String channelId) {
        String key = RedisKey_Header.Token +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + channelId;
        if (isLog) {
            log.info("get_LoginToken:\t" + key);
        }
        return key;
    }

    /**
     * 某游戏
     * 每天：活跃玩家
     * <p>
     * Sorted Set
     * <p>
     * member - currDay（时间戳 yyyyMMdd） | score - 活跃角色数目
     */
    public static String get_RolesActive_Day_Game(String gameId, String currDay) {
        String key = RedisKey_Header.ACTIVE_PLAYERS_INFO_NEW +
                ":" + GAME_ID + ":" + gameId +
                ":" + DATE + "#" + currDay;
        if (isLog) {
            log.info("getKeyRolesActiveDay_Game:\t" + key);
        }
        return key;
    }

    /**
     * 某游戏渠道
     * 每天：活跃玩家
     * <p>
     * Sorted Set
     * <p>
     * member - currDay（时间戳 yyyyMMdd） | score - 活跃角色数目
     */
    public static String get_RolesActive_Day_Channel(String gameId, String channelId, String currDay) {
        String key = RedisKey_Header.ACTIVE_PLAYERS_INFO_NEW +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + channelId +
                ":" + DATE + "#" + currDay;
        if (isLog) {
            log.info("getKeyRolesActiveDay_Game:\t" + key);
        }
        return key;
    }

    /**
     * 某游戏渠道区服
     * 每天：活跃玩家
     * <p>
     * Sorted Set
     * <p>
     * member - currDay（时间戳 yyyyMMdd） | score - 活跃角色数目
     */
    public static String get_RolesActive_Day_Server(String gameId, String serverId, String currDay) {
        String key = RedisKey_Header.ACTIVE_PLAYERS_INFO_NEW +
                ":" + GAME_ID + ":" + gameId +
                ":" + SERVER_ID + ":" + serverId +
                ":" + DATE + "#" + currDay;
        if (isLog) {
            log.info("getKeyRolesActiveDay:\t" + key);
        }
        return key;
    }

    /**
     * 每天：在线玩家
     * <p>
     * Sorted Set
     * <p>
     * member - currDay（时间戳 yyyyMMdd） | score - 在线角色数目
     */
    public static String get_RolesOnline_Day(String channelId, String gameId, String serverId) {
        String key = RedisKey_Header.USER_INFO +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + channelId +
                ":" + SERVER_ID + ":" + serverId +
                "#" + RedisKey_Tail.ONLINE_PLAYERS;
        if (isLog) {
            log.info("getKeyRolesOnlineDay:\t" + key);
        }
        return key;
    }

    /**
     * 实时数据：在线玩家
     * <p>
     * Sorted Set
     * <p>
     * member - currDayMin（时间戳 yyyyMMddHHmm） | score - 在线角色数目
     */
    public static String get_RolesOnline_Min(String channelId, String gameId, String serverId, String currDay) {
        String key = RedisKey_Header.REALTIMEDATA +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + channelId +
                ":" + SERVER_ID + ":" + serverId +
                ":" + DATE + ":" + currDay +
                "#" + RedisKey_Tail.REALTIME_ONLINE_ACCOUNTS;
        if (isLog) {
            log.info("getKeyRolesOnlineMin:\t" + key);
        }
        return key;
    }

    /**
     * 实时数据：在线玩家
     * <p>
     * Sorted Set
     * <p>
     * member - currDayMin（时间戳 yyyyMMddHHmm） | score - 在线角色数目
     */
    public static String get_RolesOnline_Min_Channel(String channelId, String gameId, String currDay) {
        String key = RedisKey_Header.REALTIMEDATA +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + channelId +
                ":" + DATE + ":" + currDay +
                "#" + RedisKey_Tail.REALTIME_ONLINE_ACCOUNTS;
        if (isLog) {
            log.info("getKeyRolesOnlineMinSp:\t" + key);
        }
        return key;
    }
//创角

    /**
     * 创建过角色的账号
     * <p>
     * Bit map
     * <p>
     * offset 账号id
     */
    public static String get_Account_CreateRoles(String channelId, String gameId) {
        String key = RedisKey_Header.USER_INFO +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + channelId +
                "#" + RedisKey_Tail.GAME_ACCOUNT_HAS_ROLE;
        if (isLog) {
            log.info("getKeyAccountCreateRoles:\t" + key);
        }
        return key;
    }

    /**
     * 每天：新增创角
     * <p>
     * Sorted Set
     * <p>
     * member - currDay（时间戳 yyyyMMdd） | score - 创建的角色数目
     */
    public static String get_RolesCreate_Day(String channelId, String gameId, String serverId) {
        String key = RedisKey_Header.USER_INFO +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + channelId +
                ":" + SERVER_ID + ":" + serverId +
                "#" + RedisKey_Tail.NEW_ADD_CREATE_ROLE;
        if (isLog) {
            log.info("getKeyRolesCreateDay:\t" + key);
        }
        return key;
    }

    /**
     * 累计创角：该区服所有角色数目
     * <p>
     * Sorted Set
     * <p>
     * member - RedisKey.GAME_ACCUMULATION_CREATE_ROLE | score - 创建的角色数目
     */
    public static String get_RolesCreate_Server(String channelId, String gameId, String serverId) {
        String key = RedisKey_Header.USER_INFO +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + channelId +
                ":" + SERVER_ID + ":" + serverId +
                "#" + RedisKey_Tail.ACCOUNT_INFO;
        if (isLog) {
            log.info("getKeyRolesCreateServer:\t" + key);
        }
        return key;
    }

    /**
     * 实时创角：该区服所有角色数目
     * <p>
     * Sorted Set
     * <p>
     * member - currDayMin（时间戳 yyyyMMddHHmm）| score - 创建的角色数目
     */
    public static String get_RolesCreate_Min(String channelId, String gameId, String serverId, String currDay) {
        String key = RedisKey_Header.USER_INFO +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + channelId +
                ":" + SERVER_ID + ":" + serverId +
                ":" + DATE + ":" + currDay +
                "#" + RedisKey_Tail.REALTIME_ADD_ROLES;
        if (isLog) {
            log.info("getKeyRolesCreateMin:\t" + key);
        }
        return key;
    }

    /**
     * 实时创角：游戏该渠道所有角色数目
     * <p>
     * Sorted Set
     * <p>
     * member - currDayMin（时间戳 yyyyMMddHHmm）| score - 创建的角色数目
     */
    public static String get_RolesCreate_Min_Channel(String channelId, String gameId, String currDay) {
        String key = RedisKey_Header.USER_INFO +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + channelId +
                ":" + DATE + ":" + currDay +
                "#" + RedisKey_Tail.REALTIME_ADD_ROLES;
        if (isLog) {
            log.info("getKeyRolesCreateMinSp:\t" + key);
        }
        return key;
    }

    /**
     * 每天：新增创角去除滚服
     * <p>
     * Sorted Set
     * <p>
     * member - currDay（时间戳 yyyyMMdd）| score - 创建的角色数目
     */
    public static String get_RolesCreate_First(String channelId, String gameId, String serverId) {
        String key = RedisKey_Header.USER_INFO +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + channelId +
                ":" + SERVER_ID + ":" + serverId +
                "#" + RedisKey_Tail.NEW_ADD_CREATE_ROLE_RM_OLD;
        if (isLog) {
            log.info("getKeyRolesCreateFirst:\t" + key);
        }
        return key;
    }
//充值

    /**
     * 实时充值:充值金额
     * <p>
     * Sorted Set
     * <p>
     * member - currDayMin（时间戳 yyyyMMddHHmm）| score - 充值金额
     */
    public static String get_RolesPaid_Min(String channelId, String gameId, String serverId, String currDay) {
        String key = RedisKey_Header.REALTIMEDATA +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + channelId +
                ":" + SERVER_ID + ":" + serverId +
                ":" + DATE + ":" + currDay +
                "#" + RedisKey_Tail.REALTIME_RECHARGE_AMOUNTS;
        if (isLog) {
            log.info("getKeyRolesPaidMin:\t" + key);
        }
        return key;
    }

    /**
     * 实时充值:充值金额 同游戏同渠道不同区服汇总
     * <p>
     * Sorted Set
     * <p>
     * member - currDayMin（时间戳 yyyyMMddHHmm）| score - 充值金额
     */
    public static String get_RolesPaid_Min_Channel(String channelId, String gameId, String currDay) {
        String key = RedisKey_Header.REALTIMEDATA +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + channelId +
                ":" + DATE + ":" + currDay +
                "#" + RedisKey_Tail.REALTIME_RECHARGE_AMOUNTS;
        if (isLog) {
            log.info("getKeyRolesPaidMinSp:\t" + key);
        }
        return key;
    }

    /**
     * 当日:付费角色
     * <p>
     * Set
     * <p>
     * member - roleId
     */
    public static String get_RolesPaid_Day(String channelId, String gameId, String serverId, String currDay) {
        String key = RedisKey_Header.ACTIVE_PLAYERS_INFO +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + channelId +
                ":" + SERVER_ID + ":" + serverId +
                ":" + DATE + ":" + currDay +
                "#" + RedisKey_Tail.RECHARGE_ROLES;
        if (isLog) {
            log.info("getKeyRolesPaidDay:\t" + key);
        }
        return key;
    }

    /**
     * 当日:付费账号
     * <p>
     * Set
     * <p>
     * member - roleId
     */
    public static String get_AccountPaid_Day(String channelId, String gameId, String serverId, String currDay) {
        String key = RedisKey_Header.ACTIVE_PLAYERS_INFO +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + channelId +
                ":" + SERVER_ID + ":" + serverId +
                ":" + DATE + ":" + currDay +
                "#" + RedisKey_Tail.RECHARGE_ACCOUNTS;
        if (isLog) {
            log.info("getKeyRolesPaidDay:\t" + key);
        }
        return key;
    }

    /**
     * 开服至今：历史付费角色
     * <p>
     * Set
     * <p>
     * member - roleId
     */
    public static String get_RolesPaid_Server(String channelId, String gameId, String serverId) {
        String key = RedisKey_Header.ACTIVE_PLAYERS_INFO +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + channelId +
                ":" + SERVER_ID + ":" + serverId +
                "#" + RedisKey_Tail.RECHARGE_ROLES;
        if (isLog) {
            log.info("getKeyRolesPaidServer:\t" + key);
        }
        return key;
    }

    /**
     * 当日：充值次数、充值金额、充值人数
     * <p>
     * Sorted Set
     * <p>
     * member - RedisKey.RECHARGE_TIMES | score - 充值次数
     * <p>
     * member - RedisKey.RECHARGE_AMOUNTS | score - 充值金额
     * <p>
     * member - RedisKey.RECHARGE_PLAYERS |  score - 充值人数
     */
    public static String get_RolesPayInfo_Day(String channelId, String gameId, String serverId, String currDay) {
        String key = RedisKey_Header.ACTIVE_PLAYERS_INFO +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + channelId +
                ":" + SERVER_ID + ":" + serverId +
                ":" + DATE + ":" + currDay +
                "#" + RedisKey_Tail.RECHARGE_INFO;
        if (isLog) {
            log.info("getKeyRolesPayInfoDay:\t" + key);
        }
        return key;
    }

    /**
     * 累计充值
     * ---开服至今----
     * <p>
     * Sorted Set
     * <p>
     * member - RedisKey.GAME_ACCUMULATION_RECHARGE_AMOUNTS | score - 充值金额
     * <p>
     * member - RedisKey.GAME_ACCUMULATION_RECHARGE_ACCOUNTS | score - 充值人数
     * <p>
     * member - RedisKey.GAME_ACCUMULATION_RECHARGE_TIMES | score - 充值次数
     */
    public static String get_RolesPayInfo_Server(String channelId, String gameId, String serverId) {
        String key = RedisKey_Header.ACTIVE_PLAYERS_INFO +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + channelId +
                ":" + SERVER_ID + ":" + serverId +
                "#" + RedisKey_Tail.RECHARGE_TOTAL_INFO;
        if (isLog) {
            log.info("getKeyRolesPayInfoServer:\t" + key);
        }
        return key;
    }

    /**
     * 累计充值金额
     * ---每天12点结束的----
     * <p>
     * Sorted Set
     * <p>
     * member - yyyyMMdd
     * score - 金额分
     * <p>
     */
    public static String get_RolesPayInfo_Server_Day(String channelId, String gameId, String serverId) {
        String key = RedisKey_Header.ACTIVE_PLAYERS_INFO +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + channelId +
                ":" + SERVER_ID + ":" + serverId +
                "#" + RedisKey_Tail.RECHARGE_TOTAL_DAY_INFO_AMOUNTS;
        if (isLog) {
            log.info("getKeyRolesPayInfoServer_Day:\t" + key);
        }
        return key;
    }

    /**
     * 累计新增创角
     * ---每天12点结束的----
     * <p>
     * Sorted Set
     * <p>
     * member - currDay（时间戳 yyyyMMdd） | score - 创建的角色数目
     */
    public static String get_RolesCreate_Server_Day(String channelId, String gameId, String serverId) {
        String key = RedisKey_Header.USER_INFO +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + channelId +
                ":" + SERVER_ID + ":" + serverId +
                "#" + RedisKey_Tail.RECHARGE_TOTAL_DAY_INFO_CREATE_ROLE;
        if (isLog) {
            log.info("getKeyRolesCreateServer_Day:\t" + key);
        }
        return key;
    }

    /**
     * 累计充值次数
     * ---每天12点结束的----
     * <p>
     * Sorted Set
     * <p>
     * member - yyyyMMdd
     * score - 金额分
     * <p>
     */
    public static String get_RolesPayTimes_Server_Day(String channelId, String gameId, String serverId) {
        String key = RedisKey_Header.ACTIVE_PLAYERS_INFO +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + channelId +
                ":" + SERVER_ID + ":" + serverId +
                "#" + RedisKey_Tail.RECHARGE_TOTAL_DAY_INFO_TIMES;
        if (isLog) {
            log.info("getKeyRolesPayTimesServer_Day:\t" + key);
        }
        return key;
    }

    /**
     * 注册付费
     * 开服至今
     * <p>
     * Sorted Set
     * <p>
     * member - currDay（yyyyMMdd） | score - 注册付费人数
     */
    public static String get_RegisterPaid_Accounts(String channelId, String gameId, String serverId) {
        String key = RedisKey_Header.ACTIVE_PLAYERS_INFO +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + channelId +
                ":" + SERVER_ID + ":" + serverId +
                "#" + RedisKey_Tail.RECHARGE_ACCOUNT_NA_CA;
        if (isLog) {
            log.info("getKeyRegisterPaidRoles:\t" + key);
        }
        return key;
    }

    /**
     * 每天：注册付费金额
     * 开服至今
     * <p>
     * Sorted Set
     * <p>
     * member - currDay（yyyyMMdd） | score - 注册付费金额
     */
    public static String get_RegisterPaid_Amounts(String channelId, String gameId, String serverId) {
        String key = RedisKey_Header.ACTIVE_PLAYERS_INFO +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + channelId +
                ":" + SERVER_ID + ":" + serverId +
                "#" + RedisKey_Tail.RECHARGE_ACCOUNTS_AMOUNTS;
        if (isLog) {
            log.info("getKeyRegisterPaidAmounts:\t" + key);
        }
        return key;
    }

    /**
     * 每天：当日首次付费人数
     * 开服至今
     * <p>
     * Sorted Set
     * <p>
     * member - currDay（yyyyMMdd） | score - 注册付费人数
     */
    public static String get_FirstPaid_Roles(String channelId, String gameId, String serverId) {
        String key = RedisKey_Header.ACTIVE_PLAYERS_INFO +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + channelId +
                ":" + SERVER_ID + ":" + serverId +
                "#" + RedisKey_Tail.RECHARGE_FIRST_PAY_ROLES;
        if (isLog) {
            log.info("getKeyFirstPaidRoles:\t" + key);
        }
        return key;
    }

    /**
     * 每天：当日首次付费金额
     * 开服至今
     * <p>
     * Sorted Set
     * <p>
     * member - currDay（yyyyMMdd） | score - 当日首次付费金额
     */
    public static String get_FirstPaid_Roles_Amounts(String channelId, String gameId, String serverId) {
        String key = RedisKey_Header.ACTIVE_PLAYERS_INFO +
                ":" + GAME_ID + ":" + gameId +
                ":" + SP_ID + ":" + channelId +
                ":" + SERVER_ID + ":" + serverId +
                "#" + RedisKey_Tail.RECHARGE_FIRST_PAY_AMOUNTS;
        if (isLog) {
            log.info("getKeyFirstPaidRolesAmounts:\t" + key);
        }
        return key;
    }
}
