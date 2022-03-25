package com.xielbs.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 0.0 日期处理工具
 * @author tanker 创建于2014-1-7
 */
public class CalendarUtils {
	/**
	 * 标准日期格式
	 */
	public static final String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 年月日日期格式
	 */
	public static final String yyyyMMdd = "yyyy-MM-dd";

	/**
	 * 年月日期格式
	 */
	public static final String yyyyMM = "yyyy-MM";

	public static final String MMddyyyy = "MM/dd/yyyy";

	/**
	 * 月份列表
	 */
	public static final Map<String, String> monthMap = new LinkedHashMap<String, String>();

	/**
	 * 字符串按指定格式转换成日期格式Calendar yyyy-MM-dd HH:mm:ss/yyyy-MM-dd
	 * 
	 * @param sTime
	 * @return
	 */
	public static Calendar convertStrToCalendar(String sTime, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date date = new Date();
		try {
			date = sdf.parse(sTime);
		} catch (java.text.ParseException ex) {
			return null;
		}
		Calendar cRt = Calendar.getInstance();
		cRt.setTime(date);
		return cRt;
	}

	/**
	 * 字符串格式yyyy-MM-dd HH:mm:ss转换成日期格式Calendar
	 *
	 * @param sTime
	 * @return
	 */
	public static Calendar getCalendar(String sTime) {
		return convertStrToCalendar(sTime, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * date转换成calednar格式
	 *
	 * @param sTime
	 * @return
	 */
	public static Calendar getCalendar(Date date) {
		Calendar cRt = Calendar.getInstance();
		cRt.setTime(date);
		return cRt;
	}

	/**
	 * 字符串格式yyyy-MM-dd HH:mm:ss转换成日期格式Date
	 *
	 * @param sTime
	 * @return
	 */
	public static Date getDate(String sTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		try {
			date = sdf.parse(sTime);
		} catch (java.text.ParseException ex) {
			return null;
		}
		return date;
	}

	/**
	 * 转数据库日期类型
	 *
	 * @param strDate
	 *            字符串
	 * @return
	 * @throws Exception
	 */
	public static java.sql.Date toSqlDate(String strDate) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat(yyyyMMddHHmmss);
		Date Date = null;
		try {
			Date = df.parse(strDate);
		} catch (Exception e) {
			throw new Exception("日期转换出错!");
		}
		return new java.sql.Date(Date.getTime());
	}

	/**
	 * 字符串格式yyyy-MM-dd HH:mm:ss转换成日期格式Timestamp
	 * 
	 * @param sTime
	 * @return
	 */
	public static Timestamp getTimestamp(String sTime) {
		Calendar cTemp = getCalendar(sTime);
		cTemp.set(Calendar.SECOND, 0);
		cTemp.set(Calendar.MILLISECOND, 0);
		return new Timestamp(cTemp.getTimeInMillis());
	}

	/**
	 * 将Timestamp转换成Calendar格式
	 * 
	 * @param time
	 * @return
	 */
	public static Calendar getCalendar(Timestamp time) {
		if (time == null)
			return null;
		Calendar cRt = Calendar.getInstance();
		cRt.setTimeInMillis(time.getTime());
		// cRt.set(Calendar.SECOND, 0);
		// cRt.set(Calendar.MILLISECOND, 0);
		return cRt;
	}

	/**
	 * 清除日期时分秒毫秒
	 * 
	 * @param time
	 * @return
	 */
	public static void clearHMSCalendar(Calendar time) {
		if (time == null)
			return;
		cleanCalendar(time, Calendar.DAY_OF_MONTH);
	}

	/***
	 * 清除日期中时分秒， 根据参数设置年/月的开始日： 例如参数为Calendar.DAY_OF_MONTH
	 * 将设置日期为此天零点；参数为Calendar.MONTH 将设置日期为此月第一天零点；参数为Calendar.YEAR
	 * 将设置日期为此年份1月1日零点
	 * 
	 * @param timetag
	 * @param cycleType
	 */
	public static void cleanCalendar(Calendar timetag, int cycleType) {
		switch (cycleType) {
		case Calendar.HOUR_OF_DAY: {
			break;
		}
		case Calendar.DAY_OF_MONTH: {
			timetag.set(Calendar.HOUR_OF_DAY, 0);
			break;
		}
		case Calendar.MONTH: {
			timetag.set(Calendar.DAY_OF_MONTH, 1);
			timetag.set(Calendar.HOUR_OF_DAY, 0);
			break;
		}
		case Calendar.YEAR:
			timetag.set(Calendar.MONTH, 0);
			timetag.set(Calendar.DAY_OF_MONTH, 1);
			timetag.set(Calendar.HOUR_OF_DAY, 0);
			break;
		}
		timetag.set(Calendar.MINUTE, 0);
		timetag.set(Calendar.SECOND, 0);
		timetag.set(Calendar.MILLISECOND, 0);
	}

	/**
	 * 比较两个日期
	 * 
	 * @param time1
	 * @param time2
	 * @param field
	 * @return ret
	 */
	public static int compareTime(Calendar time1, Calendar time2, int field) {
		int ret = 0;
		switch (field) {
		case Calendar.YEAR: {
			ret = time1.get(Calendar.YEAR) - time2.get(Calendar.YEAR);
			break;
		}
		case Calendar.MONTH: {
			while (time1.get(Calendar.YEAR) > time2.get(Calendar.YEAR)) {
				time2.add(Calendar.YEAR, 1);
				ret += 12;
			}
			ret -= time2.get(Calendar.MONTH);
			ret += time1.get(Calendar.MONTH);
			break;
		}
		case Calendar.DAY_OF_MONTH: {
			ret = (int) ((time1.getTimeInMillis() - time2.getTimeInMillis()) / (24 * 60 * 60 * 1000));
			break;
		}
		case Calendar.HOUR_OF_DAY: {
			ret = (int) ((time1.getTimeInMillis() - time2.getTimeInMillis()) / (60 * 60 * 1000));
			break;
		}
		case Calendar.MINUTE: {
			ret = (int) ((time1.getTimeInMillis() - time2.getTimeInMillis()) / (60 * 1000));
			break;
		}
		case Calendar.SECOND: {
			ret = (int) ((time1.getTimeInMillis() - time2.getTimeInMillis()) / (1000));
			break;
		}
		}
		return ret;
	}

	/**
	 * 按指定格式格式化日期，并以String显示
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String formatCalendar(Calendar date, String pattern) {
		if (date == null)
			return "";
		DateFormat df = new SimpleDateFormat(pattern);
		return df.format(date.getTime());
	}

	/**
	 * 按指定格式格式化日期，并以String显示
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String formatDate(Object date, String pattern) {
		if (date == null)
			return "";
		DateFormat df = new SimpleDateFormat(pattern);
		return df.format(((java.sql.Date) date).getTime());
	}

	/**
	 * 按标准日期格式格式化日期，并以String显示
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String formatCalendar(Calendar date) {
		return formatCalendar(date, yyyyMMddHHmmss);
	}

	/**
	 * 按标准日期格式格式化日期，并以String显示
	 * 
	 * @param cal
	 *            日期
	 * @return
	 */
	public static String getToday(Calendar cal) {
		if (cal == null) {
			return formatCalendar(Calendar.getInstance(), yyyyMMdd);
		} else {
			return formatCalendar(cal, yyyyMMdd);
		}
	}

	/**
	 * 专为table 过滤日期用
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String formatCalendar(Object date, String pattern) {
		if (date == null)
			return "";
		if (date instanceof Calendar) {
			return formatCalendar((Calendar) date, pattern);
		} else {
			return "";
		}
	}

	/**
	 * 按标准日期格式格式化日期成string
	 * 
	 * @param date
	 * @return
	 */
	public static String formatCalendar(Object date) {
		return formatCalendar(date, yyyyMMddHHmmss);
	}

	/**
	 * 获取日期，当天传入0，前一天传入-1，后一天传1依次类推
	 * 
	 * @param days
	 * @return
	 */
	public static String getDateString(int days) {
		Calendar today = Calendar.getInstance();
		today.add(Calendar.DAY_OF_MONTH, days);
		return CalendarUtils.formatCalendar(today, CalendarUtils.yyyyMMdd);
	}

	/**
	 * 获取当月1号
	 * 
	 * @return
	 */
	public static String getCurrMonSta() {
		Calendar today = Calendar.getInstance();
		today.set(Calendar.DAY_OF_MONTH, 1);
		return CalendarUtils.formatCalendar(today, CalendarUtils.yyyyMMdd);
	}

	/**
	 * 获取上月1号
	 * 
	 * @return
	 */
	public static String getLastMonSta() {
		Calendar today = Calendar.getInstance();
		today.set(Calendar.DAY_OF_MONTH, 1);
		today.add(Calendar.MONTH, -1);
		return CalendarUtils.formatCalendar(today, CalendarUtils.yyyyMMdd);
	}

	/**
	 * 获取下月1号-Calendar类型
	 * 
	 * @return
	 */
	public static String getNextMonSta() {
		Calendar today = Calendar.getInstance();
		today.set(Calendar.DAY_OF_MONTH, 1);
		today.add(Calendar.MONTH, 1);
		return CalendarUtils.formatCalendar(today, CalendarUtils.yyyyMMdd);
	}

	/**
	 * 获取当月1号-Calendar类型
	 * 
	 * @return
	 */
	public static Calendar getCalMonSta(int months) {
		Calendar today = Calendar.getInstance();
		today.set(Calendar.DAY_OF_MONTH, 1);
		today.add(Calendar.MONTH, months);
		clearHMSCalendar(today);
		return today;
	}

	/**
	 * 获取月份列表
	 * 
	 * @return
	 */
	public static Map<String, String> getMonthMap() {
		if (monthMap.isEmpty()) {
			for (int i = 1; i <= 12; i++) {
				monthMap.put(i + "", i + "");
			}
		}
		return monthMap;
	}

	/**
	 * 获取年份列表
	 * 
	 * @return
	 */
	public static LinkedHashMap<String, String> getYearMap(int years) {
		Calendar today = Calendar.getInstance();
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		for (int i = today.get(Calendar.YEAR) - years; i <= today
				.get(Calendar.YEAR); i++) {
			map.put(i + "", i + "");
		}
		return map;
	}

	/**
	 * 获取年份
	 * 
	 * @param cal
	 * @return
	 */
	public static String getYear(Calendar cal) {
		if (cal == null) {
			cal = Calendar.getInstance();
		}
		return cal.get(Calendar.YEAR) + "";
	}

	/**
	 * 获取月份
	 * 
	 * @param cal
	 * @return
	 */
	public static String getMonth(Calendar cal) {
		if (cal == null) {
			cal = Calendar.getInstance();
		}
		return (cal.get(Calendar.MONTH) + 1) + "";
	}

	/**
	 * 拼接sql查询日期
	 * 
	 * @param cal
	 *            Calendar格式参数
	 * @return
	 */
	public static String getToDate(Calendar cal) {
		return "to_date('" + formatCalendar(cal, "yyyy-MM-dd HH:mm:ss")
				+ "','YYYY-MM-DD HH24:MI:SS')";
	}

	/**
	 * 判断时间是否合法
	 * 
	 * @param date
	 * @return
	 */
	public static boolean isValidTime(String time) {
		int spaceIndex = time.indexOf(" ");
		if (spaceIndex == -1) {
			return false;
		}
		String date = time.substring(0, spaceIndex);
		String hms = time.substring(spaceIndex + 1, time.length());
		if (!isValidDate(date)) {
			return false;
		}
		int first = hms.indexOf(':');
		int second = hms.lastIndexOf(':');
		if (first == second) {
			// System.out.println("您输入的日期缺少\":\"符号");
			return false;
		}
		String hh = hms.substring(0, first);
		String mm = hms.substring(first + 1, second);
		String ss = hms.substring(second + 1, hms.length());
		if (isNumber(hh) == false || isNumber(mm) == false
				|| isNumber(ss) == false) {
			// System.out.println("您输入的日期包含不可用字符");
			return false;
		}
		int h = Integer.parseInt(hh);
		int m = Integer.parseInt(mm);
		int s = Integer.parseInt(ss);
		if (h < 0 || h > 23) {
			return false;
		}
		if (m < 0 || m >= 60) {
			return false;
		}
		if (s < 0 || s >= 60) {
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		System.out.println(CalendarUtils.isValidDate("2004-2-29"));
		System.out.println(CalendarUtils.isValidTime("2015-9-30 00:09:60"));
	}

	/**
	 * 判断日期是否合法
	 * 
	 * @param date
	 * @return
	 */
	public static boolean isValidDate(String date) {
		int first = date.indexOf('-');
		int second = date.lastIndexOf('-');

		if (first == second) {
			// System.out.println("您输入的日期缺少\"-\"符号");
			return false;
		} else {
			String year = date.substring(0, first);
			String month = date.substring(first + 1, second);
			String day = date.substring(second + 1, date.length());
			int maxDays = 31;
			if (isNumber(year) == false || isNumber(month) == false
					|| isNumber(day) == false) {
				// System.out.println("您输入的日期包含不可用字符");
				return false;
			} else if (year.length() < 4) {
				// System.out.println("您输入的年份少于四位");
				return false;
			}
			int y = Integer.parseInt(year);
			int m = Integer.parseInt(month);
			int d = Integer.parseInt(day);
			if (m > 12 || m < 1) {
				// System.out.println("您输入的月份不在规定范围内");
				return false;
			} else if (m == 4 || m == 6 || m == 9 || m == 11) {
				maxDays = 30;
			} else if (m == 2) {
				if (y % 4 > 0)
					maxDays = 28;
				else if (y % 100 == 0 && y % 400 > 0)
					maxDays = 28;
				else
					maxDays = 29;
			}
			if (d < 1 || d > maxDays) {
				// System.out.println("您输入的日期不在规定范围内");
				return false;
			}
		}
		return true;
	}

	private static boolean isNumber(String num) {
		// TODO Auto-generated method stub
		return num.matches("[0-9]+");
	}
}
