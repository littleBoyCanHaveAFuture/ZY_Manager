package com.zyh5games.jedis;


import com.zyh5games.util.StringUtils;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * 开发公司：SOJSON在线工具 <p>
 * 版权所有：© www.sojson.com<p>
 * 博客地址：http://www.sojson.com/blog/  <p>
 * <p>
 * <p>
 * Redis Manager Utils
 * <p>
 * <p>
 * 区分　责任人　日期　　　　说明<br/>
 * 创建　周柏成　2016年6月2日 　<br/>
 *
 * @author zhou-baicheng
 * @version 1.0, 2016年6月2日 <br/>
 * @email so@sojson.com
 */
public class JedisManager {
    private static final Logger log = Logger.getLogger(JedisManager.class);
    private JedisPool jedisPool;

    public Jedis getJedis() {
        Jedis jedis = null;
        try {
            jedis = getJedisPool().getResource();
        } catch (JedisConnectionException e) {
            String message = StringUtils.trim(e.getMessage());
            if ("Could not get a resource from the pool".equalsIgnoreCase(message)) {
                log.info("++++++++++请检查你的redis服务++++++++");
                log.info("|①.请检查是否安装redis服务");
                log.info("|②.请检查redis 服务是否启动。");
                log.info("|③.请检查redis启动是否带配置文件启动，也就是是否有密码，是否端口有变化（默认6379）。解决方案，参考第二点。如果需要配置密码和改变端口，请修改spring-cache.xml配置。|");
                log.info("|③.阿里云白名单 公司的网络经常会更换ip，需要更新白名单");
                log.info("项目退出中....生产环境中，请删除这些东西。我来自。JedisManage.java line:53");
                //停止项目
                System.exit(0);
            }
            throw new JedisConnectionException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return jedis;
    }

    public void returnResource(Jedis jedis, boolean isBroken) {
        if (jedis == null) {
            return;
        }
        /**
         * @deprecated starting from Jedis 3.0 this method will not be exposed.
         * Resource cleanup should be done using @see {@link redis.clients.jedis.Jedis#close()}
         */
//        if (isBroken){
//            getJedisPool().returnBrokenResource(jedis);
//        }else{
//            getJedisPool().returnResource(jedis);
//        }
        jedis.close();
    }


    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
}
