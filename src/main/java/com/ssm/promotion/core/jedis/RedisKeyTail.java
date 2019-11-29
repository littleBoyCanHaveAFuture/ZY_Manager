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
    public static final String NEW_ADD_CREATE_ACCOUNT = "NA_CA";
    public static final String NEW_ADD_CREATE_ROLE = "NA_CR";
    public static final String GAME_ACCOUNT_HAS_ROLE = "GA_SRole";
    public static final String GAME_ACCOUNT_MULTIPLE_ROLE = "GA_SRole";
    public static final String GAME_ACCOUNT_ALL_NUMS = "totalAccount";
    public static final String ACTIVE_PLAYERS = "activePlayers";
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
