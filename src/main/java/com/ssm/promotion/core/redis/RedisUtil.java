package com.ssm.promotion.core.redis;

import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author 13
 * @date 2017/12/4
 */
public class RedisUtil {

    private static final String CACHE_NAME = "perfect-ssm-cache:";
    /**
     * 过期时间
     */
    private static final int EXPIRE_TIME = 3000;

    private RedisTemplate template;

    private RedisCache cache;

    public RedisUtil() {
        init();
    }

    public void init() {
        //RedisCacheConfig中定义了
        template = SpringUtil.getBean("redisTemplate");
        cache = new RedisCache(CACHE_NAME, CACHE_NAME.getBytes(), template, EXPIRE_TIME);
    }

    public void put(String key, Object obj) {
        cache.put(key, obj);
    }

    public Object get(String key, Class clazz) {
        return cache.get(key) == null ? null : cache.get(key, clazz);
    }

    public void del(String key) {
        cache.evict(key);
    }
}