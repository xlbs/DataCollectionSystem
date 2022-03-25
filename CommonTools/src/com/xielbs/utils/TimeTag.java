package com.xielbs.utils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeTag {
	public static final int TIMEUNIT_SECOND = 0; //秒
	public static final int TIMEUNIT_MINUTE = 1; //分
	public static final int TIMEUNIT_HOUR = 2;  //时
	public static final int TIMEUNIT_DAY = 3;   //日
	public static final int TIMEUNIT_WEEK = 4; //周
	public static final int TIMEUNIT_MONTH = 5; //月
	public static final int TIMEUNIT_YEAR = 6;  //年
	
	
	public static final String YYYYMMDD = "yyyyMMdd";  
	public static final String YYYYMM = "yyyyMM";  
	public static final String _YYYYMMDD = "yyyy-MM-dd";  
	public static final String HHMMSS = "HH:mm:ss";  
	public static final String YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";  
	
	public int year=0, month=0, day=0, hour=0, minute=0, second=0;
	private Calendar cal = Calendar.getInstance();
	
	//构造方法
	public TimeTag(){
		setTime(Calendar.getInstance().getTimeInMillis());
	}
	
	public TimeTag(long millis){
		setTime(millis);
	}
	
	public TimeTag(int year, int month, int day, int hour, int minute, int second){
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		setCalendar();
	}
	
	public TimeTag(Calendar cal){
		setTime(cal.getTimeInMillis());
	}
	
	
	// 工具方法
	 
	/**
	 * 将字符串解析成日期对象，包括日期和时间
	 * @param strTime 日期字符串
	 * @return  日期对象
	 */
	public static Calendar strAllFieldCalendar(String strTime){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		try{
			date=sdf.parse(strTime);
		}catch(ParseException e){
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}
	
	/**
	 * 将字符串解析成日期对象，只包括时间部分
	 * @param strTime 日期字符串（只包括时间部分）
	 * @return 日期对象
	 */
	public static Calendar strTimeFieldCalendar(String strTime){
		Calendar cal = Calendar.getInstance();
		String date = cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH)+"-"+cal.get(Calendar.DATE)+" ";
		date += strTime;
		return strAllFieldCalendar(date);
	}
	
	/**
	 * 返回日期对象当天最早时间点
	 * @param cal 日期对象
	 * @return 最早时间
	 */
	public static Calendar setMinCalendar(Calendar cal){
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}
	
	/**
	 * 返回日期对象当天最晚时间点
	 * @param cal 日期对象
	 * @return 最晚时间
	 */
	public static Calendar setMaxCalendar(Calendar cal){
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND,59);
		return cal;
	}
	
	public void setTime(int year, int month, int day, int hour, int minute) {
		if (year < 2000)
			this.year = year + 2000;
		else
			this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = 0;
		setCalendar();
	}
	
	public void setTime(long millis){
		cal.setTimeInMillis(millis);
		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH)+1;
		day = cal.get(Calendar.DATE);
		hour = cal.get(Calendar.HOUR_OF_DAY);
		minute = cal.get(Calendar.MINUTE);
		second = cal.get(Calendar.SECOND);	
	}
	
	public void setCalendar(){
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month-1);
		cal.set(Calendar.DATE, day);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		cal.set(Calendar.MILLISECOND, 0);
	}
	
	public void add(int field,int amount){
		switch(field){
		case TIMEUNIT_YEAR:{
			cal.add(Calendar.YEAR, amount);
			break;
		}
		case TIMEUNIT_MONTH:{
			cal.add(Calendar.MONTH, amount);
			break;
		}
		case TIMEUNIT_WEEK:{
			cal.add(Calendar.DATE, amount*7);
			break;
		}
		case TIMEUNIT_DAY:{
			cal.add(Calendar.DATE, amount);
			break;
		}
		case TIMEUNIT_HOUR:{
			cal.add(Calendar.HOUR_OF_DAY, amount);
			break;
		}
		case TIMEUNIT_MINUTE:{
			cal.add(Calendar.MINUTE, amount);
			break;
		}
		case TIMEUNIT_SECOND:{
			cal.add(Calendar.SECOND, amount);
			break;
		}
		}
		setTime(cal.getTimeInMillis());
	}
	
	public String toString() {
		int dbYear = year;
		if (dbYear < 1000) {
			dbYear += 2000;
		}
		String yearStr = String.valueOf(year);
		String monthStr = String.valueOf(month);
		String dayStr = String.valueOf(day);
		String hourStr = String.valueOf(hour);
		String minuteStr = String.valueOf(minute);
		String secondStr = String.valueOf(second);
		yearStr = yearStr.length() < 2 ? "0" + yearStr : yearStr;
		monthStr = monthStr.length() < 2 ? "0" + monthStr : monthStr;
		dayStr = dayStr.length() < 2 ? "0" + dayStr : dayStr;
		hourStr = hourStr.length() < 2 ? "0" + hourStr : hourStr;
		minuteStr = minuteStr.length() < 2 ? "0" + minuteStr : minuteStr;
		secondStr = secondStr.length() < 2 ? "0" + secondStr : secondStr;
		StringBuffer time = new StringBuffer().append(dbYear).append("-").append(monthStr).append("-").append(dayStr).append(" ").append(
				hourStr).append(":").append(minuteStr).append(":").append(secondStr);
		return time.toString();
	}
	
	public String toDateString(){
		String year = ""+this.year;
		String month = this.month<10?"0"+this.month:""+this.month;
		String day = this.day<10?"0"+this.day:""+this.day;
		StringBuffer sb = new StringBuffer().append(year);
		sb.append("-").append(month).append("-").append(day);
		return sb.toString();
	}
	
	/**
	 *  Setter和Getter方法
	 */
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
		setCalendar();
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
		setCalendar();
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
		setCalendar();
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
		setCalendar();
	}
	public int getMinute() {
		return minute;
	}
	public void setMinute(int minute) {
		this.minute = minute;
		setCalendar();
	}
	public int getSecond() {
		return second;
	}
	public void setSecond(int second) {
		this.second = second;
		setCalendar();
	}
	
	public Calendar getCalendar(){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(this.cal.getTimeInMillis());
		return cal;
	}
	
	@Override
	public TimeTag clone(){
		return new TimeTag(this.cal.getTimeInMillis());
	}
	
	public String  getFormatStr(String format){
		if(format==null||format.trim().equals(""))
			format = TimeTag.YYYYMMDDHHMMSS;
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.format(cal.getTime());
		return sdf.format(cal.getTime());
	}
	public String toDbString() {
		int dbYear = year;
		if (year < 1000) {
			dbYear += 2000;
		}
		NumberFormat f = NumberFormat.getInstance();
		f.setMinimumIntegerDigits(2);
		String time = dbYear + "-" + f.format(month) + "-" + f.format(day) + " "
				+ f.format(hour) + ":" + f.format(minute) + ":" + f.format(second);
		return time;
	}
	
}
