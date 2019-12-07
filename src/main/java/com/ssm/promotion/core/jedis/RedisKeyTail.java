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
    public static final String ONLINE_PLAYERS = "activePlayers";
    public static final String RECHARGE_ACCOUNT = "RechargeAccount";
    public static final String RECHARGE_ACCOUNT_NA_CA = "RechargeAccount_NA_CA";
    /**
     * sorted set
     * namespace
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

}
