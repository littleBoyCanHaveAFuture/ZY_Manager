package com.ssm.promotion.core.jedis;

import org.apache.log4j.Logger;

import static com.ssm.promotion.core.jedis.RedisKeyBody.*;

/**
 * @author song minghua
 * @date 2019/11/28
 */
public class RedisKeyNew {
    //注册账号
    private static final Logger log = Logger.getLogger(RedisKeyNew.class);

    /**
     * 新增创号
     * <p>
     * Bit map
     * <p>
     * offset 账号id
     */
    public static String getKeyAccountCreateDay(String channelId, String gameId, String currDay) {
        String key = RedisKeyHeader.USER_INFO +
                ":" + SP_ID + ":" + channelId +
                ":" + GAME_ID + ":" + gameId +
                ":" + DATE + ":" + currDay +
                "#" + RedisKeyTail.NEW_ADD_CREATE_ACCOUNT;
        log.info("getKeyAccountCreateDay:\t" + key);
        return key;
    }

    /**
     * 该游戏所有账号
     * <p>
     * Bit map
     * <p>
     * offset 账号id
     */
    public static String getKeyAccountAll(String channelId, String gameId) {
        String key = RedisKeyHeader.USER_INFO +
                ":" + SP_ID + ":" + channelId +
                ":" + GAME_ID + ":" + gameId +
                "#" + RedisKeyTail.GAME_ACCOUNT_ALL_NUMS;
        log.info("getKeyAccountAll:\t" + key);
        return key;
    }

//进入游戏

    /**
     * 每天：活跃玩家
     * <p>
     * Sorted Set
     * <p>
     * member - currDay（时间戳 yyyyMMdd） | score - 活跃角色数目
     */
    public static String getKeyRolesActiveDay(String channelId, String gameId, String serverId) {
        String key = RedisKeyHeader.USER_INFO +
                ":" + SP_ID + ":" + channelId +
                ":" + GAME_ID + ":" + gameId +
                ":" + SERVER_ID + ":" + serverId +
                "#" + RedisKeyTail.ACTIVE_PLAYERS;
        log.info("getKeyRolesActiveDay:\t" + key);
        return key;
    }

    /**
     * 每天：在线玩家
     * <p>
     * Sorted Set
     * <p>
     * member - currDay（时间戳 yyyyMMdd） | score - 在线角色数目
     */
    public static String getKeyRolesOnlineDay(String channelId, String gameId, String serverId) {
        String key = RedisKeyHeader.USER_INFO +
                ":" + SP_ID + ":" + channelId +
                ":" + GAME_ID + ":" + gameId +
                ":" + SERVER_ID + ":" + serverId +
                "#" + RedisKeyTail.ONLINE_PLAYERS;
        log.info("getKeyRolesOnlineDay:\t" + key);
        return key;
    }

    /**
     * 实时数据：在线玩家
     * <p>
     * Sorted Set
     * <p>
     * member - currDayMin（时间戳 yyyyMMddHHmm） | score - 在线角色数目
     */
    public static String getKeyRolesOnlineMin(String channelId, String gameId, String serverId, String currDay) {
        String key = RedisKeyHeader.REALTIMEDATA +
                ":" + SP_ID + ":" + channelId +
                ":" + GAME_ID + ":" + gameId +
                ":" + SERVER_ID + ":" + serverId +
                ":" + DATE + ":" + currDay +
                "#" + RedisKeyTail.REALTIME_ONLINE_ACCOUNTS;
        log.info("getKeyRolesOnlineMin:\t" + key);
        return key;
    }

    /**
     * 实时数据：在线玩家
     * <p>
     * Sorted Set
     * <p>
     * member - currDayMin（时间戳 yyyyMMddHHmm） | score - 在线角色数目
     */
    public static String getKeyRolesOnlineMinSp(String channelId, String gameId, String currDay) {
        String key = RedisKeyHeader.REALTIMEDATA +
                ":" + SP_ID + ":" + channelId +
                ":" + GAME_ID + ":" + gameId +
                ":" + DATE + ":" + currDay +
                "#" + RedisKeyTail.REALTIME_ONLINE_ACCOUNTS;
        log.info("getKeyRolesOnlineMinSp:\t" + key);
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
    public static String getKeyAccountCreateRoles(String channelId, String gameId) {
        String key = RedisKeyHeader.USER_INFO +
                ":" + SP_ID + ":" + channelId +
                ":" + GAME_ID + ":" + gameId +
                "#" + RedisKeyTail.GAME_ACCOUNT_HAS_ROLE;
        log.info("getKeyAccountCreateRoles:\t" + key);
        return key;
    }

    /**
     * 每天：新增创角
     * <p>
     * Sorted Set
     * <p>
     * member - currDay（时间戳 yyyyMMdd） | score - 创建的角色数目
     */
    public static String getKeyRolesCreateDay(String channelId, String gameId, String serverId) {
        String key = RedisKeyHeader.USER_INFO +
                ":" + SP_ID + ":" + channelId +
                ":" + GAME_ID + ":" + gameId +
                ":" + SERVER_ID + ":" + serverId +
                "#" + RedisKeyTail.NEW_ADD_CREATE_ROLE;
        log.info("getKeyRolesCreateDay:\t" + key);
        return key;
    }

    /**
     * 累计创角：该区服所有角色数目
     * <p>
     * Sorted Set
     * <p>
     * member - RedisKey.GAME_ACCUMULATION_CREATE_ROLE | score - 创建的角色数目
     */
    public static String getKeyRolesCreateServer(String channelId, String gameId, String serverId) {
        String key = RedisKeyHeader.USER_INFO +
                ":" + SP_ID + ":" + channelId +
                ":" + GAME_ID + ":" + gameId +
                ":" + SERVER_ID + ":" + serverId +
                "#" + RedisKeyTail.ACCOUNT_INFO;
        log.info("getKeyRolesCreateServer:\t" + key);
        return key;
    }

    /**
     * 实时创角：该区服所有角色数目
     * <p>
     * Sorted Set
     * <p>
     * member - currDayMin（时间戳 yyyyMMddHHmm）| score - 创建的角色数目
     */
    public static String getKeyRolesCreateMin(String channelId, String gameId, String serverId, String currDay) {
        String key = RedisKeyHeader.USER_INFO +
                ":" + SP_ID + ":" + channelId +
                ":" + GAME_ID + ":" + gameId +
                ":" + SERVER_ID + ":" + serverId +
                ":" + DATE + ":" + currDay +
                "#" + RedisKeyTail.REALTIME_ADD_ROLES;
        log.info("getKeyRolesCreateMin:\t" + key);
        return key;
    }

    /**
     * 实时创角：该区服所有角色数目
     * <p>
     * Sorted Set
     * <p>
     * member - currDayMin（时间戳 yyyyMMddHHmm）| score - 创建的角色数目
     */
    public static String getKeyRolesCreateMinSp(String channelId, String gameId, String currDay) {
        String key = RedisKeyHeader.USER_INFO +
                ":" + SP_ID + ":" + channelId +
                ":" + GAME_ID + ":" + gameId +
                ":" + DATE + ":" + currDay +
                "#" + RedisKeyTail.REALTIME_ADD_ROLES;
        log.info("getKeyRolesCreateMinSp:\t" + key);
        return key;
    }

    /**
     * 每天：新增创角去除滚服
     * <p>
     * Sorted Set
     * <p>
     * member - currDay（时间戳 yyyyMMdd）| score - 创建的角色数目
     */
    public static String getKeyRolesCreateFirst(String channelId, String gameId, String serverId) {
        String key = RedisKeyHeader.USER_INFO +
                ":" + SP_ID + ":" + channelId +
                ":" + GAME_ID + ":" + gameId +
                ":" + SERVER_ID + ":" + serverId +
                "#" + RedisKeyTail.NEW_ADD_CREATE_ROLE_RM_OLD;
        log.info("getKeyRolesCreateFirst:\t" + key);
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
    public static String getKeyRolesPaidMin(String channelId, String gameId, String serverId, String currDay) {
        String key = RedisKeyHeader.REALTIMEDATA +
                ":" + SP_ID + ":" + channelId +
                ":" + GAME_ID + ":" + gameId +
                ":" + SERVER_ID + ":" + serverId +
                ":" + DATE + ":" + currDay +
                "#" + RedisKeyTail.REALTIME_RECHARGE_AMOUNTS;
        log.info("getKeyRolesPaidMin:\t" + key);
        return key;
    }

    /**
     * 实时充值:充值金额 同游戏同渠道不同区服汇总
     * <p>
     * Sorted Set
     * <p>
     * member - currDayMin（时间戳 yyyyMMddHHmm）| score - 充值金额
     */
    public static String getKeyRolesPaidMinSp(String channelId, String gameId, String currDay) {
        String key = RedisKeyHeader.REALTIMEDATA +
                ":" + SP_ID + ":" + channelId +
                ":" + GAME_ID + ":" + gameId +
                ":" + DATE + ":" + currDay +
                "#" + RedisKeyTail.REALTIME_RECHARGE_AMOUNTS;
        log.info("getKeyRolesPaidMinSp:\t" + key);
        return key;
    }

    /**
     * 当日:付费角色
     * <p>
     * Set
     * <p>
     * member - roleId
     */
    public static String getKeyRolesPaidDay(String channelId, String gameId, String serverId, String currDay) {
        String key = RedisKeyHeader.ACTIVE_PLAYERS_INFO +
                ":" + SP_ID + ":" + channelId +
                ":" + GAME_ID + ":" + gameId +
                ":" + SERVER_ID + ":" + serverId +
                ":" + DATE + ":" + currDay +
                "#" + RedisKeyTail.RECHARGE_ROLES;
        log.info("getKeyRolesPaidDay:\t" + key);
        return key;
    }

    /**
     * 开服至今：历史付费角色
     * <p>
     * Set
     * <p>
     * member - roleId
     */
    public static String getKeyRolesPaidServer(String channelId, String gameId, String serverId) {
        String key = RedisKeyHeader.ACTIVE_PLAYERS_INFO +
                ":" + SP_ID + ":" + channelId +
                ":" + GAME_ID + ":" + gameId +
                ":" + SERVER_ID + ":" + serverId +
                "#" + RedisKeyTail.RECHARGE_ACCOUNT;
        log.info("getKeyRolesPaidServer:\t" + key);
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
    public static String getKeyRolesPayInfoDay(String channelId, String gameId, String serverId, String currDay) {
        String key = RedisKeyHeader.ACTIVE_PLAYERS_INFO +
                ":" + SP_ID + ":" + channelId +
                ":" + GAME_ID + ":" + gameId +
                ":" + SERVER_ID + ":" + serverId +
                ":" + DATE + ":" + currDay +
                "#" + RedisKeyTail.RECHARGE_INFO;
        log.info("getKeyRolesPayInfoDay:\t" + key);
        return key;
    }

    /**
     * 累计充值
     * 开服至今
     * <p>
     * Sorted Set
     * <p>
     * member - RedisKey.GAME_ACCUMULATION_RECHARGE_AMOUNTS | score - 充值金额
     * <p>
     * member - RedisKey.GAME_ACCUMULATION_RECHARGE_ACCOUNTS | score - 充值人数
     */
    public static String getKeyRolesPayInfoServer(String channelId, String gameId, String serverId) {
        String key = RedisKeyHeader.ACTIVE_PLAYERS_INFO +
                ":" + SP_ID + ":" + channelId +
                ":" + GAME_ID + ":" + gameId +
                ":" + SERVER_ID + ":" + serverId +
                "#" + RedisKeyTail.RECHARGE_TOTAL_INFO;
        log.info("getKeyRolesPayInfoServer:\t" + key);
        return key;
    }

    /**
     * 注册付费人数
     * 开服至今
     * <p>
     * Sorted Set
     * <p>
     * member - currDay（yyyyMMdd） | score - 注册付费人数
     */
    public static String getKeyRegisterPaidRoles(String channelId, String gameId, String serverId) {
        String key = RedisKeyHeader.ACTIVE_PLAYERS_INFO +
                ":" + SP_ID + ":" + channelId +
                ":" + GAME_ID + ":" + gameId +
                ":" + SERVER_ID + ":" + serverId +
                "#" + RedisKeyTail.RECHARGE_ROLES_NA_CR;
        log.info("getKeyRegisterPaidRoles:\t" + key);
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
    public static String getKeyRegisterPaidAmounts(String channelId, String gameId, String serverId) {
        String key = RedisKeyHeader.ACTIVE_PLAYERS_INFO +
                ":" + SP_ID + ":" + channelId +
                ":" + GAME_ID + ":" + gameId +
                ":" + SERVER_ID + ":" + serverId +
                "#" + RedisKeyTail.RECHARGE_AMOUNTS_NA_CR;
        log.info("getKeyRegisterPaidAmounts:\t" + key);
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
    public static String getKeyFirstPaidRoles(String channelId, String gameId, String serverId) {
        String key = RedisKeyHeader.ACTIVE_PLAYERS_INFO +
                ":" + SP_ID + ":" + channelId +
                ":" + GAME_ID + ":" + gameId +
                ":" + SERVER_ID + ":" + serverId +
                "#" + RedisKeyTail.RECHARGE_FIRST_PAY_ROLES;
        log.info("getKeyFirstPaidRoles:\t" + key);
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
    public static String getKeyFirstPaidRolesAmounts(String channelId, String gameId, String serverId) {
        String key = RedisKeyHeader.ACTIVE_PLAYERS_INFO +
                ":" + SP_ID + ":" + channelId +
                ":" + GAME_ID + ":" + gameId +
                ":" + SERVER_ID + ":" + serverId +
                "#" + RedisKeyTail.RECHARGE_FIRST_PAY_AMOUNTS;
        log.info("getKeyFirstPaidRolesAmounts:\t" + key);
        return key;
    }
}
