package com.ssm.promotion.core.jedis;

/**
 * @author song minghua
 * @date 2019/11/28
 */
public class RedisKey {

    /**
     * 充值次数
     */
    public static final String RECHARGE_TIMES = "paytimes";
    /**
     * 充值人数(账号数目)
     */
    public static final String RECHARGE_PLAYERS = "accountnumbers";
    /**
     * 充值金额
     */
    public static final String RECHARGE_AMOUNTS = "payamounts";
    /**
     * 当日首次付费金额
     */
    public static final String RECHARGE_FIRST_AMOUNTS = "firstamounts";
    /**
     * 注册付费金额
     */
    public static final String RECHARGE_AMOUNTS_NA_CA = "amounts_NA_CA";

    /**
     * 累计创角
     */
    public static final String GAME_ACCUMULATION_CREATE_ROLE = "ACC_CR";

    /**
     * 累计充值金额
     */
    public static final String GAME_ACCUMULATION_RECHARGE_AMOUNTS = "ACC_RAM";
    /**
     * 累计充值人数
     */
    public static final String GAME_ACCUMULATION_RECHARGE_ACCOUNTS = "ACC_RACC";

}
