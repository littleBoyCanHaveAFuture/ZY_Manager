package com.ssm.promotion.core.jedis;

import static com.ssm.promotion.core.jedis.RedisKey_Body.*;

/**
 * @author song minghua
 * @date 2019/11/28
 */
public class RedisKey_Member {
    public static final String FORMAT_SHARP = "#";
    /**
     * 充值次数
     */
    public static final String RECHARGE_TIMES = "RE_TS";
    /**
     * 充值人数(账号数目)
     */
    public static final String RECHARGE_PLAYERS = "RE_PL";
    /**
     * 充值金额
     */
    public static final String RECHARGE_AMOUNTS = "RE_AM";
    /**
     * 累计创角
     */
    public static final String GAME_ACCUMULATION_CREATE_ROLE = "GACC_CR";

    /**
     * 累计充值金额
     */
    public static final String GAME_ACCUMULATION_RECHARGE_AMOUNTS = "GACC_RE_AM";
    /**
     * 累计充值人数
     */
    public static final String GAME_ACCUMULATION_RECHARGE_ACCOUNTS = "GACC_RE_AC";

    /**
     * 累计充值次数
     */
    public static final String GAME_ACCUMULATION_RECHARGE_TIMES = "GACC_RE_TIMES";
}
