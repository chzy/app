package com.chd.yunpan.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    public static final SimpleDateFormat DATE_DAY_F=new SimpleDateFormat("yyyyMMdd");
    public static final long EIGHT_COMPLEMENT_MILLISECONDS = 8 * 60 * 60 * 1000L;
    public static final long MILLISECONDS_OF_DAY = 24 * 60 * 60 * 1000L;

    /**
     * long time to string
     * 
     * @param timeInMillis
     * @param dateFormat
     * @return
     */
    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        //long ttt;
        if (timeInMillis<1499999999)
           timeInMillis=timeInMillis*1000;

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
        //String ddd=getTime(timeInMillis, DATE_DAY);
    	return Integer.valueOf(getTime(timeInMillis, DATE_DAY));
    }

    public static int getDayWithTimeMillis0(long timeInMillis)
    {
        //String ddd=getTime(timeInMillis, DATE_DAY);
       // if (timeInMillis<1499999999)
        //    calendar.setTimeInMillis(timeInMillis*1000);

        return Integer.valueOf(getTime(timeInMillis, DATE_DAY_F));
    }

    public static int getDayWithTimeMillis00(long timeInMillis)
    {
        //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//定义格式，不显示毫秒
        //Timestamp now = new Timestamp(timeInMillis);//获取系统当前时间
        //String str = df.format(now);

        //SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd");
        //Date d1=new Date(timeInMillis);
        //String str=format.format(d1);

         Calendar calendar = Calendar.getInstance();
        if (timeInMillis<1499999999)
            calendar.setTimeInMillis(timeInMillis*1000);
        else
            calendar.setTimeInMillis(timeInMillis);
        String date=""+calendar.get(Calendar.YEAR)+calendar.get(Calendar.MONTH)+calendar.get(Calendar.DAY_OF_MONTH);
        //return Integer.valueOf(getTime(timeInMillis,DATE_DAY_F));
        return Integer.valueOf(date);
    }


    public static boolean isSameDay(long timestamp1,long timestamp2){
        /*return ((timestamp1+EIGHT_COMPLEMENT_MILLISECONDS) / MILLISECONDS_OF_DAY)
                == ((timestamp2+EIGHT_COMPLEMENT_MILLISECONDS) / MILLISECONDS_OF_DAY);*/
        return getDayWithTimeMillis0(timestamp1)==getDayWithTimeMillis0(timestamp2);
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

    /**
     * 获取当前日期0点时间
     * @param timeInMillis
     * @return
     */
    public static int getZeroTime(int timeInMillis){
        Calendar date=Calendar.getInstance();
        date.setTimeInMillis(timeInMillis*1000L);
        date.set(Calendar.HOUR,0);
        date.set(Calendar.MINUTE,0);
        date.set(Calendar.MILLISECOND,0);
        return (int)(date.getTimeInMillis()/1000);
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

    public static  int ConverYear(Long time)
    {
     return  0;
    }
}