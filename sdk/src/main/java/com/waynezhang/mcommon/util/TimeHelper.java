/*
 * Copyright (c) 2013 Shanda Corporation. All rights reserved.
 *
 * Created on 2013-9-12.
 */

package com.waynezhang.mcommon.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类.
 *
 * @author James Shen
 */
public final class TimeHelper {

    public static final String FORMAT_TYPE_TIMESTAMP = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_TYPE_SHORT_TIMESTAMP = "yyyyMMddHHmmss";
    public static final String FORMAT_TYPE_LONG_TIMESTAMP = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String FORMAT_TYPE_DATE = "yyyy-MM-dd";

    public static final String FORMAT_TYPE_MONTH = "yyyy年MM月";

    private TimeHelper() {
    }

    /**
     * 时间转换, 字符串(yyyy-MM-dd HH:mm:ss | yyyy-MM-dd) 转换成 Date.
     *
     * @param str
     * @return
     */
    public static Date convertTimestamp(String str) {
        if (str == null || str.trim().length() == 0) {
            return null;
        }
        if (str.length() == 10) {
            str += " 00:00:00";
        }
        return new SimpleDateFormat(FORMAT_TYPE_TIMESTAMP).parse(str, new ParsePosition(0));
    }

    /**
     * 时间转换, Date 转换成 字符串(yyyy-MM-dd HH:mm:ss).
     *
     * @param date
     * @return
     */
    public static String convertTimestamp(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(FORMAT_TYPE_TIMESTAMP).format(date);
    }

    /**
     * 时间转换, 字符串(yyyyMMddHHmmss | yyyyMMdd) 转换成 Date.
     *
     * @param str
     * @return
     */
    public static Date convertShortTimestamp(String str) {
        if (str == null || str.trim().length() == 0) {
            return null;
        }
        if (str.length() == 8) {
            str += "000000";
        }
        return new SimpleDateFormat(FORMAT_TYPE_SHORT_TIMESTAMP).parse(str, new ParsePosition(0));
    }

    /**
     * 时间转换, Date 转换成 字符串(yyyyMMddHHmmss).
     *
     * @param date
     * @return
     */
    public static String convertShortTimestamp(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(FORMAT_TYPE_SHORT_TIMESTAMP).format(date);
    }

    /**
     * 时间转换, 字符串(yyyy-MM-dd HH:mm:ss.SSS | yyyy-MM-dd) 转换成 Date.
     *
     * @param str
     * @return
     */
    public static Date convertLongTimestamp(String str) {
        if (str == null || str.trim().length() == 0) {
            return null;
        }
        if (str.length() == 10) {
            str += " 00:00:00.000";
        }
        return new SimpleDateFormat(FORMAT_TYPE_LONG_TIMESTAMP).parse(str, new ParsePosition(0));
    }

    /**
     * 时间转换, Date 转换成 字符串(yyyy-MM-dd HH:mm:ss.SSS).
     *
     * @param date
     * @return
     */
    public static String convertLongTimestamp(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(FORMAT_TYPE_LONG_TIMESTAMP).format(date);
    }

    /**
     * 时间转换, 字符串(yyyy-MM-dd) 转换成 Date.
     *
     * @param str
     * @return
     */
    public static Date convertDate(String str) {
        if (str == null || str.trim().length() == 0) {
            return null;
        }
        return new SimpleDateFormat(FORMAT_TYPE_DATE).parse(str, new ParsePosition(0));
    }

    /**
     * 时间转换, Date 转换成 字符串(yyyy-MM-dd).
     *
     * @param date
     * @return
     */
    public static String convertDate(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(FORMAT_TYPE_DATE).format(date);
    }

    /**
     * 时间转换, Date 转换成 字符串(yyyy年MM月).
     *
     * @param date
     * @return
     */
    public static String convertMonth(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(FORMAT_TYPE_MONTH).format(date);
    }

    /**
     * 获取当前时间.
     *
     * @return
     */
    public static Date getNow() {
        return new Date();
    }

    /**
     * 修改时间.
     *
     * @param date
     * @param days
     * @param hours
     * @param minutes
     * @return
     */
    public static Date modify(Date date, int days, int hours, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (days != 0) {
            calendar.add(Calendar.DATE, days);
        }
        if (hours != 0) {
            calendar.add(Calendar.HOUR_OF_DAY, hours);
        }
        if (minutes != 0) {
            calendar.add(Calendar.MINUTE, minutes);
        }
        return calendar.getTime();
    }

    /**
     * 修改时间.
     *
     * @param date
     * @param days
     * @param hours
     * @param minutes
     * @param seconds
     * @return
     */
    public static Date modify(Date date, int days, int hours, int minutes, int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (days != 0) {
            calendar.add(Calendar.DATE, days);
        }
        if (hours != 0) {
            calendar.add(Calendar.HOUR_OF_DAY, hours);
        }
        if (minutes != 0) {
            calendar.add(Calendar.MINUTE, minutes);
        }
        if (seconds != 0) {
            calendar.add(Calendar.SECOND, seconds);
        }
        return calendar.getTime();
    }

    /**
     * 修改时间.
     *
     * @param date
     * @param year
     * @param month
     * @param days
     * @param hours
     * @param minutes
     * @param seconds
     * @return
     */
    public static Date modify(Date date, int year, int month, int days, int hours, int minutes, int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (year != 0) {
            calendar.add(Calendar.YEAR, year);
        }
        if (month != 0) {
            calendar.add(Calendar.MONTH, month);
        }
        if (days != 0) {
            calendar.add(Calendar.DATE, days);
        }
        if (hours != 0) {
            calendar.add(Calendar.HOUR_OF_DAY, hours);
        }
        if (minutes != 0) {
            calendar.add(Calendar.MINUTE, minutes);
        }
        if (seconds != 0) {
            calendar.add(Calendar.SECOND, seconds);
        }
        return calendar.getTime();
    }

    /**
     * 修改时间.
     *
     * @param calendar
     * @param days
     * @param hours
     * @param minutes
     * @return
     */
    public static Date modify(Calendar calendar, int days, int hours, int minutes) {
        if (days != 0) {
            calendar.add(Calendar.DATE, days);
        }
        if (hours != 0) {
            calendar.add(Calendar.HOUR_OF_DAY, hours);
        }
        if (minutes != 0) {
            calendar.add(Calendar.MINUTE, minutes);
        }
        return calendar.getTime();
    }

    /**
     * 修改时间.
     *
     * @param calendar
     * @param days
     * @param hours
     * @param minutes
     * @param seconds
     * @return
     */
    public static Date modify(Calendar calendar, int days, int hours, int minutes, int seconds) {
        if (days != 0) {
            calendar.add(Calendar.DATE, days);
        }
        if (hours != 0) {
            calendar.add(Calendar.HOUR_OF_DAY, hours);
        }
        if (minutes != 0) {
            calendar.add(Calendar.MINUTE, minutes);
        }
        if (seconds != 0) {
            calendar.add(Calendar.SECOND, seconds);
        }
        return calendar.getTime();
    }

    /**
     * 修改时间.
     *
     * @param calendar
     * @param year
     * @param month
     * @param days
     * @param hours
     * @param minutes
     * @param seconds
     * @return
     */
    public static Date modify(Calendar calendar, int year, int month, int days, int hours, int minutes, int seconds) {
        if (year != 0) {
            calendar.add(Calendar.YEAR, year);
        }
        if (month != 0) {
            calendar.add(Calendar.MONTH, month);
        }
        if (days != 0) {
            calendar.add(Calendar.DATE, days);
        }
        if (hours != 0) {
            calendar.add(Calendar.HOUR_OF_DAY, hours);
        }
        if (minutes != 0) {
            calendar.add(Calendar.MINUTE, minutes);
        }
        if (seconds != 0) {
            calendar.add(Calendar.SECOND, seconds);
        }
        return calendar.getTime();
    }

    /**
     * 获取周(每周的第几天).
     * SUNDAY = 1
     * MONDAY = 2
     * TUESDAY = 3
     * WEDNESDAY = 4
     * THURSDAY = 5
     * FRIDAY = 6
     * SATURDAY = 7
     *
     * @param date
     * @return
     */
    public static int getDayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return getDayOfWeek(cal);
    }

    /**
     * 获取周(每周的第几天).
     * SUNDAY = 1
     * MONDAY = 2
     * TUESDAY = 3
     * WEDNESDAY = 4
     * THURSDAY = 5
     * FRIDAY = 6
     * SATURDAY = 7
     *
     * @param calendar
     * @return
     */
    public static int getDayOfWeek(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取周(每月的第几周: 1~6).
     *
     * @param date
     * @return
     */
    public static int getWeekOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return getWeekOfMonth(cal);
    }

    /**
     * 获取周(每月的第几周: 1~6).
     *
     * @param calendar
     * @return
     */
    public static int getWeekOfMonth(Calendar calendar) {
        return calendar.get(Calendar.WEEK_OF_MONTH);
    }

    private static int getQuarter(int month) {
        int quarter = -1;
        if (month >= 0 && month <= 2) {
            quarter = 0;
        } else if (month >= 3 && month <= 5) {
            quarter = 1;
        } else if (month >= 6 && month <= 8) {
            quarter = 2;
        } else if (month >= 9 && month <= 11) {
            quarter = 3;
        }
        return quarter;
    }

    /**
     * 获取季.
     * 第一季 = 0
     * 第二季 = 1
     * 第三季 = 2
     * 第四季 = 3
     *
     * @param date
     * @return
     */
    public static int getQuarter(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return getQuarter(cal);
    }

    /**
     * 获取季.
     * 第一季 = 0
     * 第二季 = 1
     * 第三季 = 2
     * 第四季 = 3
     *
     * @param calendar
     * @return
     */
    public static int getQuarter(Calendar calendar) {
        return getQuarter(calendar.get(Calendar.MONTH));
    }

    public static boolean isSameHour(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.HOUR_OF_DAY) == cal2.get(Calendar.HOUR_OF_DAY));
    }

    public static boolean isSameHour(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameHour(cal1, cal2);
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    public static boolean isSameMonth(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH));
    }

    public static boolean isSameMonth(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameMonth(cal1, cal2);
    }

    public static boolean isSameQuarter(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                getQuarter(cal1.get(Calendar.MONTH)) == getQuarter(cal2.get(Calendar.MONTH)));
    }

    public static boolean isSameQuarter(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameQuarter(cal1, cal2);
    }

    public static boolean isSameYear(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR));
    }

    public static boolean isSameYear(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameYear(cal1, cal2);
    }

    /**
     * 是否有效.
     *
     * @param now       当前时间
     * @param beginTime 生效时间, 空值不检查
     * @param endTime   失效时间, 空值不检查
     * @return
     */
    public static boolean checkDateRange(Date now, Date beginTime, Date endTime) {
        if (beginTime != null && beginTime.after(now)) {
            return false;
        }
        if (endTime != null && endTime.before(now)) {
            return false;
        }
        return true;
    }

    /**
     * 转换为微信的消息时间格式例如（1小时前）
     * @param time
     * @return
     */
    public static String toMessageTimeString(Date time){
        if(null == time)
            return "未知时间";
        long year=365*24*60*60;
        long month=30*24*60*60;
        long day=24*60*60;
        long hour=60*60;
        long minute=60;

        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();

        long nowLong=now.getTime()/1000;
        long timeLong=time.getTime()/1000;
        long timeVal=nowLong-timeLong;

        if(timeVal>year){
            long years=timeVal/year;
            return years+"年前";
        }else if(timeVal>month){
            long years=timeVal/month;
            return years+"月前";
        }else if(timeVal>day){
            long years=timeVal/day;
            return years+"天前";
        }else if(timeVal>hour){
            long years=timeVal/hour;
            return years+"小时前";
        }else if(timeVal>minute){
            long years=timeVal/minute;
            return years+"分钟前";
        }else{
            return "0分钟前";
        }
    }

    /**
     * 转换为微信的消息时间格式例如（1小时前）
     * @param time 字符串(yyyy-MM-dd HH:mm:ss | yyyy-MM-dd)
     * @return
     */
    public static String toMessageTimeString(String time) {
        return toMessageTimeString(convertTimestamp(time));
    }

    /**
     * 转换为微信的消息时间格式例如（1小时前）,但是在intervalDays以外的时间则显示具体时间(当前年份内，不显示年，精确到分钟；当前年份以前，显示完整时间，精确到分钟)
     * @param time
     * @param intervalDays
     * @return
     */
    public static String toMessageTimeString(String time, int intervalDays){
        Date date = convertTimestamp(time);
        if(null == date)
            return "未知时间";
        long day=24*60*60;
        long hour=60*60;
        long minute=60;

        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();

        long nowLong=now.getTime()/1000;
        long timeLong=date.getTime()/1000;
        long timeVal=nowLong-timeLong;

        if(timeVal>intervalDays*day){
            String result = time;
            String nowYear = String.valueOf(calendar.get(Calendar.YEAR));
            String year = time.substring(0, 4);
            if(nowYear.equals(year)){
                result = time.substring(5);
            }
            result = result.substring(0, result.lastIndexOf(":"));
            return result;
        }else if(timeVal>day){
            long years=timeVal/day;
            return years+"天前";
        }else if(timeVal>hour){
            long years=timeVal/hour;
            return years+"小时前";
        }else if(timeVal>minute){
            long years=timeVal/minute;
            return years+"分钟前";
        }else{
            return "0分钟前";
        }
    }
}
