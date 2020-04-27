package com.ssm.promotion.core.timer;

import com.ssm.promotion.core.jedis.jedisRechargeCache;
import com.ssm.promotion.core.sdk.LoginToken;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author songminghua
 * Description:定时任务
 */
@Component
public class ScheduledService {
    private static final Logger log = Logger.getLogger(ScheduledService.class);
    /**
     * 速率 单位s
     */
    private static final int rate = 60;
    /**
     * <渠道,游戏-区服>
     */
    private static Map<String, Object> map = new HashMap<>();

    @Autowired
    jedisRechargeCache cache;

    public static Date stampForDate(long timestamp) {
        return new Date(timestamp);
    }

    public static String dateForString(Date date) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(date);
    }

    public static String getTime() {
        Long time = System.currentTimeMillis();
        String times = dateForString(stampForDate(time));
        return times;
    }

    /**
     * cron：        通过表达式来配置任务执行时间
     * fixedRate：   定义一个按一定频率执行的定时任务
     * fixedDelay：  定义一个按一定频率执行的定时任务，与上面不同的是，改属性可以配合initialDelay， 定义该任务延迟执行时间。
     */
    @Scheduled(fixedRate = rate * 1000)
    public void scheduled() {
        log.info("scheduled 60s 过去了");
        log.info(System.getProperty("file.encoding"));
        LoginToken.cleanInvalid(System.currentTimeMillis());
    }
//服务器启动时 可以检查一遍

    /**
     * 每分钟整点触发一次
     * 1.秒（0~59）
     * 2.分钟（0~59）
     * 3.小时（0~23）
     * 4.天（月）（0~31，但是你需要考虑你月的天数）
     * 5.月（0~11）
     * 6.天（星期）（1~7 1=SUN 或 SUN，MON，TUE，WED，THU，FRI，SAT）
     * 7.年份（1970－2099）
     */
    @Scheduled(cron = "10 * * * * ?")
    public void redis() {
        //reids 统计在线 todo 临时注释20200421
//        cache.setRealtimeData();
    }

    @Scheduled(cron = "50 59 23 * * ?")
    public void redisOffline() {
        //reids 将前一天的在线玩家数据转移todo 临时注释20200421
//        cache.updateNextDayOnlineData();
    }
}

