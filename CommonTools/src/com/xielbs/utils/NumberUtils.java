/**
 * 
 */
package com.xielbs.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * @since 1.0 数字处理工具
 * @author tanker 创建于2014-1-7
 */
public class NumberUtils {
	/**
	 * 数字相加
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public static Number add(Number first, Number second) {
		if (first == null) {
			return second;
		} else if (second == null) {
			return first;
		}
		return formatDouble(first.doubleValue() + second.doubleValue());
	}
	
	/**
	 * 整数相加
	 * @param first
	 * @param second
	 * @return
	 */
	public static Integer add(Integer first, Integer second) {
		if (first == null) {
			return second;
		} else if (second == null) {
			return first;
		}
		return first+second;
	}


	/**
	 * 对象数字相加
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public static Object add(Object first, Object second) {
		if (first == null || "".equals(first)) {
			return second;
		} else if (second == null || "".equals(second)) {
			return first;
		}
		return formatDouble(Double.valueOf(first.toString())
				+ Double.valueOf(second.toString()));
	}
	
	/**
	 * 被减数为空当成0的数字减法
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public static Number newreduce(Number first, Number second) {
		if (first == null) {
			return null;
		}
		if (second == null) {
			second=0.0;
		}
		return formatDouble(first.doubleValue() - second.doubleValue());
	}

	/**
	 * 数字减法
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public static Number reduce(Number first, Number second) {
		if (first == null || second == null) {
			return null;
		}
		return formatDouble(first.doubleValue() - second.doubleValue());
	}
	
	/**
	 * 赋初值0的数字减法
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public static Number nvlreduce(Number first, Number second) {
		if (second == null) {
			return first;
		}
		if (first == null) {
			first= 0.0;
		}
		return formatDouble(first.doubleValue() - second.doubleValue());
	}


	/**
	 * 对象数字减法
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public static Double reduce(Object first, Object second) {
		if (first == null || second == null || "".equals(first)
				|| "".equals(second)) {
			return null;
		}
		return formatDouble(Double.valueOf(first.toString())
				- Double.valueOf(second.toString()));
	}

	/**
	 * 被减数为空，减数取负值
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public static Double minusReduce(Double first, Double second) {
		if (first == null && second == null) {
			return null;
		} else if (first != null && second == null) {
			return first;
		} else if (first == null && second != null) {
			return -second;
		}
		return formatDouble(first - second);
	}

	/**
	 * 数字乘法
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public static Number multiply(Number first, Number second) {
		if (first == null || second == null) {
			return null;
		}

		return formatDouble(first.doubleValue() * second.doubleValue());
	}

	/**
	 * 对象数值除法
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public static Number divide(Object first, Object second) {
		if (first == null || second == null || "0".equals(second.toString())
				|| "".equals(first) || "".equals(second)) {
			return null;
		}
		try{
		return formatDouble(Double.valueOf(first.toString())
				/ Double.valueOf(second.toString()));
		}catch (Exception e) {
			return null;
		}
	}

	/**
	 * 数字除法
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public static Number divide(Number first, Number second) {
		if (first == null || second == null || second.doubleValue() == 0) {
			return null;
		}
		try{
			return formatDouble(first.doubleValue() / second.doubleValue());
			}catch (Exception e) {
				return null;
			}
	}

	/**
	 * 固定格式格式化大数字
	 * 
	 * @param source
	 * @return
	 */
	public static BigDecimal formatBigDecimal(BigDecimal source) {
		if (source == null)
			return null;
		DecimalFormat df = new DecimalFormat("0.0000");
		return new BigDecimal(df.format(source));
	}

	/**
	 * 固定格式格式化双精度数字
	 * 
	 * @param source
	 * @return
	 */
	public static Double formatDouble(Double source) {
		if (source == null)
			return null;
		DecimalFormat df = new DecimalFormat("0.0000");
		return new Double(df.format(source));
	}

	/**
	 * 按指定格式格式化大数字
	 * 
	 * @param source
	 * @param pattern
	 * @return
	 */
	public static Number formatNumber(Number source, String pattern) {
		if (source == null)
			return null;
		if (pattern != null) {
			NumberFormat df = new DecimalFormat(pattern);
			String value = df.format(source);
			return new BigDecimal(value);
		}
		return source;
	}

	/**
	 * 按指定格式格式化大数字(国际化)
	 * 
	 * @param source
	 * @param pattern
	 * @return
	 */
	public static String formaterLocalNumber(Number source, String pattern,
			Locale locale) {
		if (source != null) {
			NumberFormat df = null;
			if (locale != null) {
				DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
				df = new DecimalFormat(pattern, symbols);
			} else {
				df = new DecimalFormat(pattern);
			}
			df.setGroupingUsed(false);
			String value = df.format(source);
			return value;
		}
		return "";
	}
	
	/**
	 * 按指定格式格式
	 * @param source 数值
	 * @param pattern 格式
	 * @return
	 */
	public static String formaterNumber(Object source, String pattern) {
		if(source == null || "".equals(source)){
			return "";
		}
		if(!(source instanceof Number)){
			return source.toString();
		}
		if(pattern != null && !"".equals(pattern)) {
			NumberFormat df = new DecimalFormat(pattern);
			df.setGroupingUsed(false);
			return df.format((Number) source);
		}
		return source.toString();
	}

	/**
	 * 获取数组最大最小平均值
	 * 
	 * @param values
	 * @return
	 */
	public static Double[] getMaxMinAvg(Object[] values) {
		Double[] bRt = new Double[3];
		int count = 0;
		for (int i = 0; i < values.length; i++) {
			if (values[i] == null)
				continue;
			bRt[0] = getMaxOrMin(true, bRt[0], Double.valueOf(values[i]
					.toString()));
			bRt[1] = getMaxOrMin(false, bRt[1], Double.valueOf(values[i]
					.toString()));
			if (values[i] != null) {
				count++;
				bRt[2] = (Double) add(bRt[2], Double.valueOf(values[i]
						.toString()));
			}
		}
		bRt[2] = (Double) divide(bRt[2], new Double(count));
		return bRt;
	}

	/**
	 * 获取list中最大最小值
	 * 
	 * @param values
	 * @return
	 */
	public static ArrayList<Double> getMaxMin(ArrayList<Double> al) {
		ArrayList<Double> arr = new ArrayList<Double>();
		Double max = null;
		Double min = null;
		for (int i = 0; i < al.size(); i++) {
			Double obj = (Double) al.get(i);
			max = getMaxOrMin(true, max, obj);
			min = getMaxOrMin(false, min, obj);
		}
		arr.add(max);
		arr.add(min);
		return arr;
	}

	/**
	 * 获取list中最大最小平均值
	 * 
	 * @param values
	 * @return
	 */
	public static ArrayList<Double> getMaxMinAge(ArrayList<Double> al) {
		ArrayList<Double> arr = new ArrayList<Double>();
		Double max = null;
		Double min = null;
		Double avg = null;
		for (int i = 0; i < al.size(); i++) {
			Double obj = (Double) al.get(i);
			max = getMaxOrMin(true, max, obj);
			min = getMaxOrMin(false, min, obj);
			avg = (Double) add(avg, obj);
		}
		arr.add(max);
		arr.add(min);
		arr.add((Double) divide(avg, al.size()));
		return arr;
	}

	/**
	 * 获取最大或者最小值
	 * 
	 * @param isMax
	 * @param value1
	 * @param value2
	 * @return
	 */
	public static Double getMaxOrMin(boolean isMax, Double value1, Double value2) {
		if (value1 == null) {
			return formatDouble(value2);
		} else if (value2 == null) {
			return formatDouble(value1);
		} else {
			if (isMax) {
				return formatDouble(Double.valueOf(Math.max(value1
						.doubleValue(), value2.doubleValue())));
			} else {
				return formatDouble(Double.valueOf(Math.min(value1
						.doubleValue(), value2.doubleValue())));
			}
		}
	}
	
	public static boolean newcompareTo(boolean isMax, Object value1, Object value2) {
		if (value1 == null && value2 != null) {
			return false;
		} else if (value1 != null && value2 == null) {
			return false;
		} else if (value1 == null && value2 == null) {
			return false;
		} else {
			if (isMax) {
				if (Double.valueOf(value1.toString()).compareTo(
						Double.valueOf(value2.toString())) < 0)
					return true;
				else
					return false;
			} else {
				if (Double.valueOf(value1.toString()).compareTo(
						Double.valueOf(value2.toString()))>0)
					return true;
				else
					return false;
			}
		}
	}

	public static boolean compareTo(boolean isMax, Object value1, Object value2) {
		if (value1 == null && value2 != null) {
			return true;
		} else if (value1 != null && value2 == null) {
			return false;
		} else if (value1 == null && value2 == null) {
			return false;
		} else {
			if (isMax) {
				if (Double.valueOf(value1.toString()).compareTo(
						Double.valueOf(value2.toString())) > 0)
					return false;
				else
					return true;
			} else {
				if (Double.valueOf(value1.toString()).compareTo(
						Double.valueOf(value2.toString())) > 0)
					return true;
				else
					return false;
			}
		}
	}

	/**
	 * 格式化字符串值为BigDecimal或者空格字符
	 * 
	 * @param source
	 * @return
	 */
	public static Object formatBigDecimal(String source, String pattern) {
		if (source == null || "".equalsIgnoreCase(source)
				|| "null".equalsIgnoreCase(source)
				|| "NaN".equalsIgnoreCase(source)
				|| source.equalsIgnoreCase(source))
			return " ";
		DecimalFormat df = new DecimalFormat(pattern);
		return new BigDecimal(df.format(Double.valueOf(source)));
	}

	/**
	 * 将字节转换成整型
	 * 
	 * @param value
	 *            字节类型值
	 * @return
	 */
	public static int byteToInt(byte value) {
		if (value < 0) {
			return value + 256;
		}
		return value;
	}

	/**
	 * 将char型数据转成字节数组
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] toBytes(char value) {
		byte[] bt = new byte[2];
		for (int i = 0; i < bt.length; i++) {
			bt[i] = (byte) (value >>> (i * 8));
		}
		return bt;
	}

	/**
	 * 将short型数据转成字节数组
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] toBytes(short value) {
		byte[] bt = new byte[2];
		for (int i = 0; i < bt.length; i++) {
			bt[i] = (byte) (value >>> (i * 8));
		}
		return bt;
	}

	/**
	 * 将int型数据转成字节数组
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] toBytes(int value) {
		byte[] bt = new byte[4];
		for (int i = 0; i < bt.length; i++) {
			bt[i] = (byte) (value >>> (i * 8));
		}
		return bt;
	}

	/**
	 * 将long型数据转成字节数组
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] toBytes(long value) {
		byte[] bt = new byte[8];
		for (int i = 0; i < bt.length; i++) {
			bt[i] = (byte) (value >>> (i * 8));
		}
		return bt;
	}
	
	/**
	 * 计算百分比
	 * @param first 被除数
	 * @param second 除数
	 * @param defaultVal 默认值
	 * @return
	 */
	public static Double getPercentage(Double first, Double second, Double defaultVal){
		if(first == null || second == null || second == 0d){
			return defaultVal;
		}
		return Double.parseDouble(formaterNumber((first*100+0d)/second, "0.00"));
	}
	
	/**
	 * 转short
	 * @param obj
	 * @return
	 */
	public static Short toShort(Object obj){
		if(obj == null || "".equals(obj)){
			return null;
		}
		return Short.valueOf(obj.toString());
	}
	
	/**
	 * 转Float
	 * @param obj
	 * @return
	 */
	public static Float toFloat(Object obj){
		if(obj == null || "".equals(obj)){
			return null;
		}
		return Float.valueOf(obj.toString());
	}
	
	/**
	 * 转Integer
	 * @param obj
	 * @return
	 */
	public static Integer toInteger(Object obj){
		if(ObjectUtils.isNull(obj)){
			return null;
		}
		return Integer.valueOf(obj.toString());
	}
	
	/**
	 * 转Long
	 * @param obj
	 * @return
	 */
	public static Long toLong(Object obj){
		if(ObjectUtils.isNull(obj)){
			return null;
		}
		return Long.valueOf(obj.toString());
	}
	
	/**
	 * 转Double
	 * @param obj
	 * @return
	 */
	public static Double toDouble(Object obj){
		if(obj == null || "".equals(obj)){
			return null;
		}
		return Double.valueOf(obj.toString());
	}
	
	/**
	 * 转Byte
	 * @param obj
	 * @return
	 */
	public static Byte toByte(Object obj){
		if(obj == null || "".equals(obj)){
			return null;
		}
		return Byte.valueOf(obj.toString());
	}

}
