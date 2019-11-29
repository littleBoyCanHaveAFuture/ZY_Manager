package com.ssm.promotion.core.redis;


import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;


/**
 *
 * @author 13
 * @date 2017/12/4
 */
@Component
@EnableCaching
@Configuration
public class RedisCacheConfig extends CachingConfigurerSupport {

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory();
        //ip地址
        redisConnectionFactory.setHostName("47.101.44.31");
        //端口号
        redisConnectionFactory.setPort(6379);
        //redis登录密码
        redisConnectionFactory.setPassword("50558b3dc9aa68a");
        //database 默认是16个，不设置的话默认为0
        redisConnectionFactory.setDatabase(2);
        return redisConnectionFactory;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory cf) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
        redisTemplate.setConnectionFactory(cf);
        return redisTemplate;
    }

    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        //默认过期时间
        cacheManager.setDefaultExpiration(3000);
        return cacheManager;
    }

}