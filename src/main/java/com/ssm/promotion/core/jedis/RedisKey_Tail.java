package com.ssm.promotion.core.jedis;

/**
 * @author song minghua
 * @date 2019/11/28
 */
public class RedisKey_Tail {
    /*
     * bitmap
     * namespace
     */
    /**
     * 当日新建账号
     */
    public static final String NEW_ADD_CREATE_ACCOUNT = "NA_CA";
    /**
     * 首次创建的账号进入游戏区服
     */
    public static final String NEW_ADD_FIRST_ACCOUNT = "NA_FIRST_CA";
    /**
     * 当日新建角色
     */
    public static final String NEW_ADD_CREATE_ROLE = "NA_CR";
    /**
     * 当日新建角色去除滚服
     */
    public static final String NEW_ADD_CREATE_ROLE_RM_OLD = "NA_CR_RM_OLD";

    /**
     * 同一渠道同一游戏
     * 创建过角色的账号
     */
    public static final String GAME_ACCOUNT_HAS_ROLE = "G_AC_SRole";

    /**
     * 该游戏所有的账号(渠道、官方)
     */
    public static final String GAME_ACCOUNT_ALL_NUMS = "G_AC_ANUMS";
    /**
     * 在线账号
     */
    public static final String ONLINE_PLAYERS = "ON_PL";
    /**
     * 付费账号
     */
    public static final String RECHARGE_ACCOUNTS = "RE_AC";

    /**
     * 有序集合
     * 账号信息 1.累计创角 GAME_ACCUMULATION_CREATE_ROLE
     */
    public static final String ACCOUNT_INFO = "AC_INFO";
    /**
     * 当日充值信息
     */
    public static final String RECHARGE_INFO = "RE_INFO";
    /**
     * 开服纸巾累积充值信息
     */
    public static final String RECHARGE_TOTAL_INFO = "RE_TO_INFO";
    /**
     * 每日累积充值信息
     */
    public static final String RECHARGE_TOTAL_DAY_INFO_AMOUNTS = "RE_DAY_INFO_MONEY";
    /**
     * 每日累积新增创角
     */
    public static final String RECHARGE_TOTAL_DAY_INFO_CREATE_ROLE = "RE_DAY_INFO_CREATE_ROLE";
    /**
     * 每日累积新增创角
     */
    public static final String RECHARGE_TOTAL_DAY_INFO_TIMES = "RE_DAY_INFO_TIMES";

    /**
     * 实时数据 过期时间 一个月
     * 充值金额
     */
    public static final String REALTIME_RECHARGE_AMOUNTS = "REAL_RE_AM";
    /**
     * 实时数据 过期时间 一个月
     * 在线玩家
     * 1.玩家进入游戏 总在线玩家
     */
    public static final String REALTIME_ONLINE_ACCOUNTS = "REAL_ONLINE_AC";
    /**
     * 实时数据 过期时间 一个月
     * 新增玩家
     */
    public static final String REALTIME_ADD_ROLES = "REAL_ADD_ROLES";

    /**
     * Set
     * 每日充值过的角色id
     */
    public static final String RECHARGE_ROLES = "RE_RO";
    /**
     * Sorted Set
     * 当日首次付费信息
     */
    public static final String RECHARGE_FIRST_PAY_ROLES = "RE_FI_P_R";
    /**
     * Sorted Set
     * 当日首次付费金额
     */
    public static final String RECHARGE_FIRST_PAY_AMOUNTS = "RE_FI_P_AM";
    /**
     * 注册付费玩家
     */
    public static final String RECHARGE_ACCOUNT_NA_CA = "RE_AC_NA_CA";
    /**
     * 注册付费金额
     */
    public static final String RECHARGE_ACCOUNTS_AMOUNTS = "RE_AC_AM";
    /**
     * 游戏
     */
    public static final String GAMEINFO = "GAMEINFO";
    /**
     * 游戏渠道
     */
    public static final String SPIDINFO = "SPIDINFO";

    /**
     * 游戏渠道区服
     */
    public static final String SERVERINFO = "SERVERINFO";
    /**
     * 全服概况 每日
     */
    public static final String RS_DAY_INFO = "RS_DAY_INFO";
    /**
     * 区服概况 每日
     */
    public static final String RS_SERVER_INFO = "RS_SERVER_INFO";
    /**
     * 渠道概况 每日
     */
    public static final String RS_CHANNEL_INFO = "RS_CHANNEL_INFO";

}
