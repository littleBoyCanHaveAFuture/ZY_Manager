package com.ssm.promotion.core.jedis;

/**
 * @author song minghua
 * @date 2019/11/28
 */
public class RedisKeyTail {
    /**
     * bitmap
     * namespace
     */
    /**
     * 当日新建账号
     */
    public static final String NEW_ADD_CREATE_ACCOUNT = "NA_CA";
    /**
     * 当日新建角色
     */
    public static final String NEW_ADD_CREATE_ROLE = "NA_CR";
    /**
     * 当日新建角色去除滚服
     */
    public static final String NEW_ADD_CREATE_ROLE_RM_OLD = "NA_CR_RM_OLD";
    /**
     * 创建了账号的账号
     */
    public static final String GAME_ACCOUNT_HAS_ROLE = "GA_SRole";
    /**
     * 创建了多个角色的账号
     * 数据会一直更新
     * 新增创角去除滚服：需要当天之前的数据
     */
    public static final String GAME_ACCOUNT_MULTIPLE_ROLE = "GA_MRole";
    /**
     * 该游戏所有的账号(渠道、官方)
     */
    public static final String GAME_ACCOUNT_ALL_NUMS = "totalAccount";
    /**
     * 活跃玩家
     */
    public static final String ACTIVE_PLAYERS = "activePlayers";
    /**
     * 在线账号
     */
    public static final String ONLINE_PLAYERS = "onlinePlayers";
    /**
     * 付费玩家
     */
    public static final String RECHARGE_ACCOUNT = "RechargeAccount";
    /**
     * 付费玩家
     */
    public static final String RECHARGE_ACCOUNT_M = "RechargeAccountMutiple";
    /**
     * 注册付费玩家
     */
    public static final String RECHARGE_ACCOUNT_NA_CA = "RechargeAccount_NA_CA";
    /**
     * 有序集合
     * 账号信息 1.累计创角 GAME_ACCUMULATION_CREATE_ROLE
     */
    public static final String ACCOUNT_INFO = "AccountInfo";
    /**
     * 当日充值信息
     */
    public static final String RECHARGE_INFO = "RechargeInfo";
    /**
     * 当日累积充值信息
     */
    public static final String RECHARGE_TOTAL_INFO = "RechargeTotalInfo";
    /**
     * 存储充值汇总查询结果
     */
    public static final String RECHARGE_SUMMARY = "RechargeSummary";

    /**
     * 实时数据 过期时间 一个月
     * 充值金额
     */
    public static final String REALTIME_RECHARGE_AMOUNTS = "RRA";
    /**
     * 实时数据 过期时间 一个月
     * 在线玩家
     * 1.玩家进入游戏 总在线玩家
     */
    public static final String REALTIME_ONLINE_ACCOUNTS = "ROA";
    /**
     * 实时数据 过期时间 一个月
     * 新增玩家
     */
    public static final String REALTIME_ADD_Roles = "RAR";
}
