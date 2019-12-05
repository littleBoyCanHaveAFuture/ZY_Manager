package com.ssm.promotion.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 1034683568@qq.com
 * @project_name perfect-ssm
 * @date 2017-3-1
 */
public class DateUtil {
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

    private static final String FORMAT_YYMMDD = "yyyyMMdd";
    private static final String JS_FORMAT_YYMMDD = "yyyy-MM-dd HH:mm";

    /**
     * 转化时间
     * 并获取这段时间的所有天数
     */
    public static List<String> transTimes(String startTimes, String endTimes) throws Exception {
        startTimes = formatJsTime(startTimes);
        endTimes = formatJsTime(endTimes);

        List<String> timeList = new ArrayList<>();
        if (startTimes.equals(endTimes)) {
            timeList.add(startTimes);
        } else {
            timeList = DateUtil.getDateStr(startTimes, endTimes);
        }
        if (timeList != null) {
            timeList.forEach(day -> {
                if (day != null && !day.isEmpty()) {
                    System.out.println("day:" + day);
                }
            });
        }
        return timeList;
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

    public static String getCurrentDateStr() throws Exception {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static String getCurrentDayStr() throws Exception {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYMMDD);
        return sdf.format(date);
    }

    public static List<String> getDateStr(String startTime, String endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYMMDD);
        List<String> dateList = new ArrayList<>();
        if (!"".equals(startTime) && !"".equals(endTime)) {
            try {
                long start = sdf.parse(startTime).getTime();
                long end = sdf.parse(endTime).getTime();
                int n = (int) ((end - start) / DAY_MILLIS);
                for (int i = 0; i <= n; i++) {
                    dateList.add(sdf.format(start + DAY_MILLIS * i));
                }
                return dateList;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String formatJsTime(String startTime) throws Exception {
        System.out.println(startTime);
        Date d = formatString(startTime, JS_FORMAT_YYMMDD);
        return formatDate(d, FORMAT_YYMMDD);
    }

    public static void main(String[] args) throws Exception {
//        Date end = new Date();
//        Date start = DateUtils.addDays(new Date(), -10);//合计 10+1 天
//        getDateStr(formatDate(start, "yyyyMMdd"), formatDate(end, "yyyyMMdd"));


        String startTime = "2019-12-01 10:47";
        Date d = formatString(startTime, JS_FORMAT_YYMMDD);
        String ss = formatDate(d, FORMAT_YYMMDD);
        System.out.println(ss);
    }
}
