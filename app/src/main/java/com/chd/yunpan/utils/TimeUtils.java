package com.chd.yunpan.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TimeUtils
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-8-24
 */
public class TimeUtils {

    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm"/*:ss*/);
    public static final SimpleDateFormat DATE_FORMAT_DATE    = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat DATE_YEAR=new SimpleDateFormat("yyyy");
    public static final SimpleDateFormat DATE_MONTH=new SimpleDateFormat("MM");
    public static final SimpleDateFormat DATE_DAY=new SimpleDateFormat("dd");

    /**
     * long time to string
     * 
     * @param timeInMillis
     * @param dateFormat
     * @return
     */
    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(timeInMillis));
    }
    
    public static int getYearWithTimeMillis(long timeInMillis)
    {
    	return Integer.valueOf(getTime(timeInMillis, DATE_YEAR));
    }
    
    public static int getMonthWithTimeMillis(long timeInMillis)
    {
    	return Integer.valueOf(getTime(timeInMillis, DATE_MONTH));
    }
    
    public static int getDayWithTimeMillis(long timeInMillis)
    {
    	return Integer.valueOf(getTime(timeInMillis, DATE_DAY));
    }

    /**
     * long time to string, format is {@link #DEFAULT_DATE_FORMAT}
     * 
     * @param timeInMillis
     * @return
     */
    public static String getTime(long timeInMillis) {
        return getTime(timeInMillis, DEFAULT_DATE_FORMAT);
    }

    public static String getDay(long timeInMillis){
    	return getTime(timeInMillis, DATE_FORMAT_DATE);
    }

    public static String getDate(long timeInMillis){
        return getTime(timeInMillis, DATE_FORMAT_DATE);
    }
    /**
     * get current time in milliseconds
     * 
     * @return
     */
    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }
    /*
     * 获取文件的时间
     */
    public static String getFileDate(File file) {
        return DATE_FORMAT_DATE.format(new Date(file.lastModified()));
    }

    /**
     * get current time in milliseconds, format is {@link #DEFAULT_DATE_FORMAT}
     * 
     * @return
     */
    public static String getCurrentTimeInString() {
        return getTime(getCurrentTimeInLong());
    }

    /**
     * get current time in milliseconds
     * 
     * @return
     */
    public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
        return getTime(getCurrentTimeInLong(), dateFormat);
    }

    public static String getTime(Long time, String format) {
        return getTime(time,new SimpleDateFormat(format));
    }
}