package com.xielbs.jedis.util;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ipport")
public class RedisCluster {
	
	private String clusterName; 
	
	private List<IP> ipports;

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public List<IP> getIpports() {
		return ipports;
	}

	public void setIpports(List<IP> ipports) {
		this.ipports = ipports;
	}
	
}
