package com.xielbs.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "ip", "port" ,"clusterName","clusterNode"})
@XmlRootElement(name = "ClusterConfig")
public class ClusterConfig {
	
	private String ip;
	
	private String port;
	
	private String clusterNode;
	
	private String clusterName;
	
	public static ClusterConfig getConfig() {
		try {
			return (ClusterConfig) XmlUtils.convertXmlFileToObject(ClusterConfig.class, "config/ClusterConfig.xml");
		} catch (Exception e) {
			ClusterConfig config = new ClusterConfig();
			XmlUtils.convertToXml(config, "config/ClusterConfig.xml");
			return config;
		}
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getClusterNode() {
		return clusterNode;
	}

	public void setClusterNode(String clusterNode) {
		this.clusterNode = clusterNode;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
	
}
