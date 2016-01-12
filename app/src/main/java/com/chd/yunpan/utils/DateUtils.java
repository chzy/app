package com.chd.yunpan.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	/**
	 * @param str
	 * @return
	 */
	public static String parse2Date(String str){
		
		long time = Long.parseLong(str);
		Date datetime = new Date(time*1000);
		SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
		
		String date = formate.format(datetime);
		return date;
	}

}
