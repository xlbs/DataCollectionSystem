package com.xielbs.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @since 1.0
 * 对象反射工具类
 * @author tanker
 * 创建于2014-1-8
 */
public abstract class ObjectUtils {
	
	/**
	 * 设置对象属性值
	 * @param object 对象
	 * @param methodName 设置方法名称
	 * @param cls 设置参数类型
	 * @param value 设置参数值
	 * @throws Exception
	 */
	public static void setValue(Object object,String methodName,Class cls,Object value) throws Exception {
		object.getClass().getMethod(methodName, new Class[]{cls})
		.invoke(object, new Object[]{value});
	}
	/**
	 * 默认参数类型设置对象属性
	 * @param object 对象
	 * @param methodName 设置方法名称
	 * @param value 设置参数值
	 * @throws Exception
	 */
	public static void setValue(Object object,String methodName,Object value) throws Exception {
		setValue(object, methodName, value.getClass(), value);
	}
	/**
	 * 获取对象属性值
	 * @param object 对象
	 * @param methodName 获取方法名称
	 * @return 返回值
	 * @throws Exception
	 */
	public static Object getValue(Object object,String methodName) throws Exception {
		return object.getClass().getMethod(methodName, (Class[])null)
		.invoke(object, (Object[])null);
	}
	/**
	 * 对象数组非空判断
	 * @param array
	 * @return
	 */
	public static boolean isEmpty(Object[] array) {
		return (array == null || array.length == 0);
	}
	/**
	 * 将对象集合中每个对象按照指定的属性，与格式取值，然后转换成Map键值对
	 * @param objs 对象
	 * @param properties 单个对象的属性名称集合，需满足javabean条件
	 * @param patterns 可能需要的格式化
	 * @return
	 */
	public static List<Map> parseObjectsToMaps(List<?> objs,List<String> properties,String[] patterns){
		List<Map> list = new ArrayList<Map>();
		if(objs==null||objs.size()==0)return list;
		for(int i=0;i<objs.size();i++){
			list.add(parserObjectToMap(objs.get(i), properties,patterns));
		}
		return list;
	}
	
	/**
	 * 解析嵌套对象属性为map,以备gt-grid显示数据用
	 * 比如属性名称为a.b将依次取a属性值，然后再取a属性值中的b属性值
	 * @param obj
	 * @param properties
	 * @return
	 */
	public static Map parserObjectToMap(Object obj,List<String> properties,String[] patterns){
		Map result = new HashMap();
		for(int i = 0;i<properties.size();i++){
			String[] prop_s = properties.get(i).split("\\.");
			Object value = obj;
			for(int j=0;j<prop_s.length;j++){
				value = getPropertyofObject(prop_s[j], value, patterns!=null?patterns[j]:null);
			}
			result.put(prop_s[prop_s.length-1], value);
		}
		return result;
	}
	
	/**
	 * 对象直接属性取值
	 * @param propertyName
	 * @param obj
	 * @param pattern
	 * @return
	 */
	public static Object getPropertyofObject(String propertyName, Object obj,
			String pattern) {
		if(obj==null)return "";
		try {
			Method method = obj.getClass().getMethod(
					"get" + StringUtils.capitalize(propertyName), (Class[]) null);
			Object objs = method.invoke(obj, (Object[]) null);
			if (pattern != null && objs != null) {
				if (objs instanceof Number) {
					return NumberUtils.formatNumber(((Number) objs),
							pattern);
				} else if (objs instanceof Calendar) {
					return CalendarUtils.formatCalendar((Calendar) objs, pattern);
				}
			}
			return objs;
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	

	
	/**
	 * 判断字符是否为空(return true)
	 * @param str
	 * @return
	 */
	public static boolean isNull(Object str){
		return (str == null || "".equals(str) || "null".equalsIgnoreCase(str.toString()) || "undefined".equalsIgnoreCase(str.toString()));
	}
	
	/**
	 * 反射执行对象方法
	 * @param obj 目标对象
	 * @param methodName 方法名称
	 * @param argsType 参数类型
	 * @param args 方法参数
	 * @return
	 */
	public static Object invokeMethodByClass(Object obj, String methodName, Class[] argsType, Object[] args) {
		if(obj == null){
			return null;
		}
		try {
			Method mh = obj.getClass().getDeclaredMethod(methodName, argsType);
			return mh.invoke(obj, args);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 转Array
	 * @param strArray
	 * @return
	 */
	public static Integer[] toArray(String str, String regex){
		if(isNull(str)){
			return new Integer[0];
		}
		String[] strArray = str.split(regex);
		Integer[] intArry = new Integer[strArray.length];
		for(int i=0;i<strArray.length;i++){
			intArry[i]=Integer.valueOf(strArray[i]) ;
		}
		return intArry;
	}
	
	/**
	 * 转Array
	 * @param strArray
	 * @return
	 */
	public static String[] toStrArray(String str, String regex){
		if(isNull(str)){
			return new String[0];
		}
		return str.split(regex);
	}
	
	/**
	 * 转Array
	 * @param strArray
	 * @return
	 */
	public static Short[] toShtArray(String str, String regex){
		if(isNull(str)){
			return new Short[0];
		}
		String[] strArray = str.split(regex);
		Short[] shtArry = new Short[strArray.length];
		for(int i=0;i<strArray.length;i++){
			shtArry[i]=Short.valueOf(strArray[i]) ;
		}
		return shtArry;
	}
	
}
