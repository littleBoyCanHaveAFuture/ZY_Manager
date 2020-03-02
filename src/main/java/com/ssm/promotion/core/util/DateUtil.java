package com.ssm.promotion.core.util;

import com.ssm.promotion.core.service.impl.RechargeSummaryImpl;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author 1034683568@qq.com
 * @project_name perfect-ssm
 * @date 2017-3-1
 */
public class DateUtil {
    private static final Logger log = Logger.getLogger(DateUtil.class);
    public static final long SECOND_MILLIS = 1000L;
    public static final int MINUTE_SECONDS = 60;
    public static final int HOUR_MINUTES = 60;
    public static final int DAY_HOURS = 24;
    public static final int WEEK_DAYS = 7;
    public static final int MONTH_DAYS = 30;
    public static final int YEAR_DAYS = 365;
    public static final long MINUTE_MILLIS = 60000L;
    public static final long HOUR_MILLIS = 3600000L;
    public static final long DAY_MILLIS = 86400000L;
    public static final long WEEK_MILLIS = 604800000L;
    public static final long MONTH_MILLIS = 2592000000L;
    public static final long YEAR_MILLIS = 31536000000L;
    public static final int HOUR_SECONDS = 3600;
    public static final int DAY_SECONDS = 86400;
    public static final int WEEK_SECONDS = 604800;

    public static final String FORMAT_YYMMDD = "yyyyMMdd";
    public static final String FORMAT_YYYYMMddHHmm = "yyyyMMddHHmm";
    public static final String FORMAT_YYYY_MMDD_HHmm = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_YYYY_MMDD_HHmmSS = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获得某天最小时间 2017-10-15 00:00:00
     */
    public static Date getStartOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static long getEndTimestamp() {
        long now = System.currentTimeMillis() / SECOND_MILLIS;
        long daySecond = 60 * 60 * 24;
        long dayTime = now - (now + 8 * 60 * 60) % daySecond;
        return dayTime + daySecond;
    }


    /**
     * 获取上n个小时整点小时时间
     *
     * @param date
     * @return
     */
    public static String getLastHourTime(Date date, int n) {
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYYY_MMDD_HHmmSS);
        ca.set(Calendar.HOUR_OF_DAY, ca.get(Calendar.HOUR_OF_DAY) - n);
        date = ca.getTime();
        return sdf.format(date);
    }

    /**
     * 获取当前时间的整点小时时间
     *
     * @param date
     * @return
     */
    public static String getCurrHourTime(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        date = ca.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYYY_MMDD_HHmmSS);
        return sdf.format(date);
    }

    /**
     * 时间戳转其他格式
     */
    public static String formatDate(long timestamp, String outFormat) throws Exception {
        log.info("formatDate:" + timestamp);
        Date dates = new Date(timestamp);
        formatDate(dates, outFormat);
        return formatDate(dates, outFormat);
    }

    /**
     * inFormat
     * 转
     * outFormat
     */
    public static String formatDate(String times, String inFormat, String outFormat) throws Exception {
        log.info("formatDate:" + times);
        Date date = formatString(times, inFormat);
        return formatDate(date, outFormat);
    }

    public static String formatDate(Date date, String format) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        if (date != null) {
            result = sdf.format(date);
        }
        return result;
    }

    public static Date formatString(String str, String format) throws Exception {
        if (StringUtil.isEmpty(str)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse(str);
    }

    /**
     * 当前时间:yyyyMMddHHmm
     */
    public static String getCurrentMinuteStr() throws Exception {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYYYMMddHHmm);
        return sdf.format(date);
    }

    /**
     * 当前时间:yyyyMMddHHmm
     *
     * @param amount 往前、往后的分钟
     */
    public static String getCurrentMinuteStr(int amount) throws Exception {
        Date date = DateUtils.addMinutes(new Date(), amount);
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYYYMMddHHmm);
        return sdf.format(date);
    }

    /**
     * 当前时间:yyyy-MM-dd HH:mm:ss
     */
    public static String getCurrentDateStr() throws Exception {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYYY_MMDD_HHmmSS);
        return sdf.format(date);
    }

    /**
     * 当天时间:年月日=yyyyMMdd
     */
    public static String getCurrentDayStr() throws Exception {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYMMDD);
        return sdf.format(date);
    }

    /**
     * 求天数
     *
     * @param startTimes 开始时间
     * @param endTimes   结束时间
     * @return 这段时间的天数 yyyyMMdd
     */
    public static List<String> transTimes(String startTimes, String endTimes, String inFormat) throws Exception {
        SimpleDateFormat in = new SimpleDateFormat(FORMAT_YYMMDD);

        startTimes = formatDate(startTimes, inFormat, FORMAT_YYMMDD);
        endTimes = formatDate(endTimes, inFormat, FORMAT_YYMMDD);

        List<String> timeList = new ArrayList<>();
        if (startTimes.equals(endTimes)) {
            timeList.add(startTimes);
        } else {
            List<String> dateList = new ArrayList<>();
            if (!"".equals(startTimes) && !"".equals(endTimes)) {
                try {
                    long start = in.parse(startTimes).getTime();
                    long end = in.parse(endTimes).getTime();

                    int n = (int) ((end - start) / DAY_MILLIS);

                    for (int i = 0; i <= n; i++) {
                        dateList.add(in.format(start + DAY_MILLIS * i));
                    }
                    return dateList;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return timeList;
    }


    /**
     * 求分钟数
     * 24h制
     * yyyyMMDDmm 年-月-日-小时-分钟格式的时间
     *
     * @param startTime 开始时间 yyyy-MM-dd HH:mm:ss
     * @param endTime   结束时间 yyyy-MM-dd HH:mm:ss
     * @return 这段时间的天数 yyyyMMddHHmm
     */
    public static List<String> getDateMinStr(String startTime, String endTime) {
        SimpleDateFormat sdfIn = new SimpleDateFormat(FORMAT_YYYY_MMDD_HHmm);
        SimpleDateFormat sdfOut = new SimpleDateFormat(FORMAT_YYYYMMddHHmm);

        List<String> dateList = new ArrayList<>();
        if (!startTime.isEmpty() && !endTime.isEmpty()) {
            try {
                long start = sdfIn.parse(startTime).getTime();
                long end = sdfIn.parse(endTime).getTime();

                long stage = MINUTE_MILLIS;

                int n = (int) ((end - start) / stage);

                for (int i = 0; i <= n; i++) {
                    dateList.add(sdfOut.format(start + stage * i));
                }

                return dateList;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static void main(String[] args) throws Exception {
//        Date end = new Date();
////        Date start = DateUtils.addDays(new Date(), -10);//合计 10+1 天
//        Date start = DateUtils.addHours(new Date(), -1);
//
//        System.out.println("start:" + start.getTime());
//        System.out.println("end:" + end.getTime());
//        List<String> res = getDateMinStr(formatDate(start, FORMAT_YYYY_MMDD_HHmmSS), formatDate(end, FORMAT_YYYY_MMDD_HHmmSS));
//        res.forEach(System.out::println);
//
//        String s = DateUtil.getCurrHourTime(end);
//        System.out.println(s);
//
//        String startTime = "2019-12-01 10:47";
//        Date d = formatString(startTime, JS_FORMAT_YYMMDD);
//        String ss = formatDate(d, FORMAT_YYMMDD);
//        System.out.println(ss);
//        Integer gameId = 1;
//        Integer serverId = 1;
//        String s = String.format("gid:{%d}:sid:{%d}", gameId, serverId);
//        System.out.println(s);
//        System.out.println(DateUtil.getCurrentMinuteStr());
//        long time = 1576209640600L;
//        String s = formatDate(time, FORMAT_YYYY_MMDD_HHmmSS);
//        System.out.println(System.currentTimeMillis());
//        System.out.println(DateUtil.getEndTimestamp());
//        Date s = formatString("2019-12-19 16:20:05.0", DateUtil.FORMAT_YYYY_MMDD_HHmmSS);
//        System.out.println(s.getTime());
        String currDay = DateUtil.getCurrentDayStr();
        String nextDay = DateUtil.formatDate(DateUtil.getEndTimestamp() * 1000, DateUtil.FORMAT_YYMMDD);
        System.out.println(currDay);
        System.out.println(nextDay);
    }
}
