package com.xielbs.jedis.util;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * 内存库连接配置参数
 * @author xiongyue
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "JedisConfig")
public class JedisConfig {
	/**
	 * 最大连接数
	 */
	private Integer maxTotal = 50;
	/**
	 * 最大等待连接数
	 */
	private Integer maxIdle = 15;

	private boolean testOnBorrow = false;
	
	/**
	 * 是否需要分表
	 */
	private boolean needJedis = true;
	private String passWord;
	
	/**内存库IP和端口*/
	private List<RedisCluster> redisClusters = new ArrayList<RedisCluster>();
	
	public List<RedisCluster> getRedisClusters() {
		return redisClusters;
	}

	public void setRedisClusters(List<RedisCluster> redisClusters) {
		this.redisClusters = redisClusters;
	}

	/**
	 * 内存库一次取数
	 */
	private int fetchSize = 50000;// 默认10万条
	
	public static JedisConfig getConfig() {
		try {
			return (JedisConfig) XmlUtils.convertXmlFileToObject(JedisConfig.class, "config/JedisConfig.xml");
		} catch (Exception e) {
			JedisConfig config = new JedisConfig();
			XmlUtils.convertToXml(config, "config/JedisConfig.xml");
			return config;
		}
	}
	
	public static JedisConfig getConfig(String path){
		try {
			return (JedisConfig) XmlUtils.convertXmlFileToObject(JedisConfig.class, path);
		} catch (Exception e) {
			return null;
		}
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public boolean isNeedJedis() {
		return needJedis;
	}

	public void setNeedJedis(boolean needJedis) {
		this.needJedis = needJedis;
	}

	public int getFetchSize() {
		return fetchSize;
	}

	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	public Integer getMaxTotal() {
		return maxTotal;
	}

	public void setMaxTotal(Integer maxTotal) {
		this.maxTotal = maxTotal;
	}

	public Integer getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(Integer maxIdle) {
		this.maxIdle = maxIdle;
	}

	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

}
