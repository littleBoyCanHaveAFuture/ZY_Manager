package com.ssm.promotion.core.timer;

import com.ssm.promotion.core.sdk.LoginToken;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

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

}