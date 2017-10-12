package com.github.boot.framework.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {
	protected static transient final Log log = LogFactory.getLog(DateUtils.class);

	private static SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
	

	public static String dateToString(Date date) {
		if (date == null)
			return "";
		Calendar cl = Calendar.getInstance();
		cl.setTime(date);
		return cl.get(Calendar.YEAR) + "-" + (cl.get(Calendar.MONTH) + 1) + "-"
				+ cl.get(Calendar.DAY_OF_MONTH);
	}

	public static String dateTo0String(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(date).trim();

	}

	public static String dateToString(Date dDate, String format) {
		try {
			SimpleDateFormat simDateFormat = new SimpleDateFormat(format);
			return simDateFormat.format(dDate);
		} catch (Exception e) {
			return null;
		}
	}

	public static Date parseTimeStamp(String date) throws Exception {
		try {
			return timeStampFormat.parse(date);
		} catch (ParseException e) {
			throw new Exception("日期格式不正确");
		}

	}

	/**
	 * 根据指定日期格式格式化日期
	 * @param date
	 * @param formater
	 * @return
	 */
	public static String format(Date date, String formater) {
		if (date == null) {
			return null;
		}
		
		if(null == timeStampFormat || timeStampFormat.equals("")){
			return timeStampFormat.format(date);
		}
		SimpleDateFormat sdf = new SimpleDateFormat(formater);
		return sdf.format(date);
	}

	/**
	 * 格式化日期
	 * @param date
	 * @param formater
	 * @return
	 */
	public static Date parse(String date, String formater) {
		SimpleDateFormat sdf = new SimpleDateFormat(formater);
		Date result = null;
		try {
			result = sdf.parse(date.trim());
		} catch (ParseException e) {
			log.error("", e);
		}
		return result;
	}
	
	 /**
     * @Description: long类型转换成日期
     * 
     * @param lo 毫秒数
     * @return String yyyy-MM-dd
     */
    public static String longToDate(long lo){
        Date date = new Date(lo);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        return sd.format(date);
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
