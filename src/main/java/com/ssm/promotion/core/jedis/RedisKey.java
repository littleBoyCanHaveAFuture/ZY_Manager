package com.ssm.promotion.core.jedis;

import static com.ssm.promotion.core.jedis.RedisKeyBody.*;

/**
 * @author song minghua
 * @date 2019/11/28
 */
public class RedisKey {
    //    public static final String FORMAT_SG = "%s:spid:%s:gid:%d";
    //    public static final String FORMAT_SGS = "%s:spid:%s:gid:%d:sid:%d";
    public static final String FORMAT_SG = "%s:" + SP_ID + ":%s:" + GAME_ID + ":%d";
    public static final String FORMAT_SGS = "%s:" + SP_ID + ":%s:" + GAME_ID + ":%d:" + SERVER_ID + ":%d";

    public static final String FORMAT_SG_SS = "%s:" + SP_ID + ":%s:" + GAME_ID + ":%s";
    public static final String FORMAT_SGS_SSS = "%s:" + SP_ID + ":%s:" + GAME_ID + ":%s:" + SERVER_ID + ":%s";

    public static final String FORMAT_SG_SDD = "%s:" + SP_ID + ":%d:" + GAME_ID + ":%d";
    public static final String FORMAT_SGS_SDDD = "%s:" + SP_ID + ":%d:" + GAME_ID + ":%d:" + SERVER_ID + ":%d";
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
     * 当日首次付费金额
     */
    public static final String RECHARGE_FIRST_AMOUNTS = "RE_FAM";

    /**
     * 注册付费金额
     */
    public static final String RECHARGE_AMOUNTS_NA_CA = "RE_AM_NA_CA";

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



}
