package com.ssm.promotion.core.jedis;

import org.apache.log4j.Logger;

/**
 * sdk的缓存
 * 1.zy_game
 * todo
 *
 * @author tgzwmkkkk
 */
public class jedisSDKCache {
    private static final Logger log = Logger.getLogger(jedisSDKCache.class);
    /**
     * 为了不和其他的缓存混淆，采用追加前缀方式以作区分
     */
    private static final String REDIS_SDK_CACHE = "sdk-cache:";
    /**
     * Redis 分片(分区)，也可以在配置文件中配置
     */
    private static final int DB_INDEX = 4;
    private jedisManager jedisManager;

    public void setJedisManager(jedisManager jedismanager) {
        this.jedisManager = jedismanager;
    }

}
