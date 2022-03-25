/**
 * 
 */
package com.xielbs.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

/**
 * @since 1.0 JDK中JAXB相关的重要Annotation�?
 * <p>
 * @XmlType，将Java类或枚举类型映射到XML模式类型
 * @XmlAccessorType(XmlAccessType.FIELD)，控制字段或属�?的序列化。FIELD表示JAXB将自动绑定Java类中的每个非静�?�?
 * @XmlAccessorOrder，控制JAXB 绑定类中属�?和字段的排序�?
 * @XmlJavaTypeAdapter，使用定制的适配器（即扩展抽象类XmlAdapter并覆盖marshal()和unmarshal()方法），以序列化Java类为XML�?
 * @XmlElementWrapper ，对于数组或集合（即包含多个元素的成员变量），生成一个包装该数组或集合的XML元素（称为包装器）�?
 * @XmlRootElement，将Java类或枚举类型映射到XML元素�?
 * @XmlElement，将Java类的�?��属�?映射到与属�?同名的一个XML元素�?
 * @XmlAttribute，将Java类的�?��属�?映射到与属�?同名的一个XML属�?�?/p>
 * @author tanker 创建�?014-1-15
 */
public class
XmlUtils {
	/**
	 * 系统默认配置文件放置位置
	 */
	public static String configPath = "config";

	/**
	 * 将对象直接转换成String类型�?XML输出
	 * 
	 * @param obj
	 * @return
	 */
	public static String convertToXml(Object obj) {
		// 创建输出�?
		StringWriter sw = new StringWriter();
		try {
			// 利用jdk中自带的转换类实�?
			JAXBContext context = JAXBContext.newInstance(obj.getClass());

			Marshaller marshaller = context.createMarshaller();
			// 格式化xml输出的格�?
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
			// 将对象转换成输出流形式的xml
			marshaller.marshal(obj, sw);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return sw.toString();
	}

	/**
	 * 将对象根据路径转换成xml文件
	 * 
	 * @param obj
	 * @param path
	 * @return
	 */
	public static void convertToXml(Object obj, String path) {
		try {
			// 利用jdk中自带的转换类实�?
			JAXBContext context = JAXBContext.newInstance(obj.getClass());

			Marshaller marshaller = context.createMarshaller();
			// 格式化xml输出的格�?
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
			// 将对象转换成输出流形式的xml
			// 创建输出�?
			FileWriter fw = null;
			try {
				fw = new FileWriter(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
			marshaller.marshal(obj, fw);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将String类型的xml转换成对�?
	 */
	public static Object convertXmlStrToObject(Class<?> clazz, String xmlStr) {
		Object xmlObject = null;
		try {
			JAXBContext context = JAXBContext.newInstance(clazz);
			// 进行将Xml转成对象的核心接�?
			Unmarshaller unmarshaller = context.createUnmarshaller();
			StringReader sr = new StringReader(xmlStr);
			xmlObject = unmarshaller.unmarshal(sr);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return xmlObject;
	}

	/**
	 * 将file类型的xml转换成对�?
	 */
	public static Object convertXmlFileToObject(Class<?> clazz, String xmlPath) {
		Object xmlObject = null;
		try {
			JAXBContext context = JAXBContext.newInstance(clazz);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			FileReader fr = null;
			try {
				fr = new FileReader(xmlPath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			xmlObject = unmarshaller.unmarshal(fr);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return xmlObject;
	}

	/**
	 * 根据java类生成Schema.xsd文件
	 * 
	 * @param classesToBeBound
	 */
	public static void generateSchema(Class<?>... classesToBeBound) {
		try {
			JAXBContext context = JAXBContext.newInstance(classesToBeBound);
			context.generateSchema(new DefaultSchemaOutputResolver());
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @since 1.0 默认实现
	 * @author tanker 创建�?014-1-15
	 */
	static class DefaultSchemaOutputResolver extends SchemaOutputResolver {
		/**
		 * @see SchemaOutputResolver
		 */
		public Result createOutput(String namespaceUri, String suggestedFileName)
				throws IOException {
			return new StreamResult(new File(configPath, suggestedFileName));
		}
	}
}
