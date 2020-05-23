package com.zyh5games.config;

import com.zyh5games.jedis.JedisManager;
import com.zyh5games.jedis.JedisRechargeCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author song minghua
 * @date 2020/5/23
 */
@ComponentScan
@Configuration
public class JedisPools {
//
//    @Bean
//    public JedisPoolConfig jedisPoolConfig() {
//        JedisPoolConfig config = new JedisPoolConfig();
//        //  资源池中最大连接数
//        config.setMaxTotal(200);
//        //  最大闲置
//        config.setMaxIdle(200);
//        //  最小闲置
//        config.setMinIdle(10);
//        //  当资源池连接用尽后，调用者的最大等待时间(单位为毫秒)
//        config.setMaxWaitMillis(2000);
//        //  向资源池借用连接时是否做连接有效性检测(ping)，无效连接会被移除
//        config.setTestOnBorrow(true);
//        //  向资源池归还连接时是否做连接有效性检测(ping)，无效连接会被移除
//        config.setTestOnReturn(false);
//        //
//        config.setTestWhileIdle(true);
//        //  是否开启jmx监控，可用于监控
//        config.setJmxEnabled(true);
//        //  空闲资源的检测周期(单位为毫秒)
//        config.setTimeBetweenEvictionRunsMillis(30000);
//        //  资源池中资源最小空闲时间(单位为毫秒)，达到此值后空闲资源将被移除
//        config.setMinEvictableIdleTimeMillis(60000);
//
//        return config;
//    }
//
//    @Bean(name = "ali")
//    public JedisPool jedisPoolAli(JedisPoolConfig jedisPoolConfig) {
//        String host = "r-bp1a0bf9408e94c4pd.redis.rds.aliyuncs.com";
//        int port = 6379;
//        int timeout = 5000;
//        String password = "ppB7xnDF6pQdZRZp";
//        return new JedisPool(jedisPoolConfig, host, port, timeout, password);
//    }
//
//    @Bean(name = "tencent")
//    public JedisPool jedisPoolTencent(JedisPoolConfig jedisPoolConfig) {
//        String host = "172.16.0.11";
//        int port = 6379;
//        int timeout = 5000;
//        String password = "oURLPXI5qb5uNDPz";
//        return new JedisPool(jedisPoolConfig, host, port, timeout, password);
//    }
//
//    @Bean
//    public JedisManager jedisManager() {
//        return new JedisManager();
//    }
//
//    @Bean
//    public JedisRechargeCache jedisRechargeCache() {
//        return new JedisRechargeCache();
//    }
}
