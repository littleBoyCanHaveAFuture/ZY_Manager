package com.ssm.promotion.core.jedis;

/**
 * @author song minghua
 * @date 2019/11/28
 */
public class RedisKeyHeader {
    public static final String USER_INFO = "UserInfo";
    public static final String ACTIVE_PLAYERS_INFO = "API";
    public static final String RS_INFO = "RS";
    public static final String Token = "Token";
    /**
     * 实时数据 过期时间 一个月
     */
    public static final String REALTIMEDATA = "RTS";
}
