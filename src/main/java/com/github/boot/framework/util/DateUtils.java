package com.github.boot.framework.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {

	private static String FORMAT_STRING="yyyy-MM-dd HH:mm:ss";

	/**
	 * The minimum supported epoch second.
	 */
	public static final long MIN_SECOND = -31557014167219200L;
	/**
	 * The maximum supported epoch second.
	 */
	public static final long MAX_SECOND = 253400630340000L;

	/**
	 * Date转字符串，格式为yyyy-MM-dd HH:mm:ss.
	 * 
	 * @param date
	 *            日期
	 * @return 字符串
	 */
	public static String date2String(Date date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_STRING);
		return simpleDateFormat.format(date);
	}
	
	public static Date addDay(Date date,int days){
		GregorianCalendar calendar=new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_YEAR, days);
		return calendar.getTime();
	}
	
	public static String dateToString(Date dDate, String format) {
		try {
			SimpleDateFormat simDateFormat = new SimpleDateFormat(format);
			return simDateFormat.format(dDate);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 判断选择的日期是否是本周
	 * @param date
	 * @return
	 */
	public static boolean isCurrWeek(Date date)	{
		Calendar calendar = Calendar.getInstance();
		int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
		calendar.setTime(date);
		int paramWeek = calendar.get(Calendar.WEEK_OF_YEAR);
		if(paramWeek == currentWeek){
			return true;
		}
		return false;
	}

}
