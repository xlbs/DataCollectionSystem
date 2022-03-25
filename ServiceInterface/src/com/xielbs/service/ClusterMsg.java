package com.xielbs.service;

import java.io.Serializable;

/**
 * 集群变更信息
 * 
 * @author Administrator
 */
public class ClusterMsg {

	public interface  ClusterMsgFrame extends Serializable {

	}

	/**
	 * 前置机断开
	 * @author Administrator
	 *
	 */
	public static class FrontMachineCluster implements ClusterMsgFrame {

		private static final long serialVersionUID = 1L;

		private String ipPort;

		private String opType;

		public String getOpType() {
			return opType;
		}

		public void setOpType(String opType) {
			this.opType = opType;
		}

		public String getIpPort() {
			return ipPort;
		}

		public void setIpPort(String ipPort) {
			this.ipPort = ipPort;
		}
		
		public FrontMachineCluster() {
		}

		public FrontMachineCluster(String ipPort, String opType) {
			this.ipPort = ipPort;
			this.opType = opType;
		}
	}

	/**
	 * 定时任务连接
	 * @author Administrator
	 *
	 */
	public static class TimeTaskCluster implements ClusterMsgFrame {

		private static final long serialVersionUID = 1L;

		private String ipPort;

		private String opType;

		private boolean startFirst;

		public boolean isStartFirst() {
			return startFirst;
		}

		public void setStartFirst(boolean startFirst) {
			this.startFirst = startFirst;
		}

		public String getOpType() {
			return opType;
		}

		public void setOpType(String opType) {
			this.opType = opType;
		}

		public String getIpPort() {
			return ipPort;
		}

		public void setIpPort(String ipPort) {
			this.ipPort = ipPort;
		}
		
		public TimeTaskCluster() {
		}

		public TimeTaskCluster(String ipPort, String opType) {
			this.ipPort = ipPort;
			this.opType = opType;
		}
		
		public TimeTaskCluster(String ipPort, String opType, boolean startFirst) {
			this.ipPort = ipPort;
			this.opType = opType;
			this.startFirst = startFirst;
		}
	}
	
	
	/**
	 * 新的定时任务连接，重新分配终端
	 * @author Administrator
	 *
	 */
	public static class TrmDeassignCluster implements ClusterMsgFrame {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private String trmNumsTypeJson;

		public String getTrmNumsTypeJson() {
			return trmNumsTypeJson;
		}

		public void setTrmNumsTypeJson(String trmNumsTypeJson) {
			this.trmNumsTypeJson = trmNumsTypeJson;
		}
		
		public TrmDeassignCluster(String trmNumsTypeJson) {
			this.trmNumsTypeJson = trmNumsTypeJson;
		}
	}
	
	/**
	 * 计算服务连接
	 * @author Administrator
	 *
	 */
	public static class CalculateServerCluster implements ClusterMsgFrame {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String ipOprt;
		private String opType;

		public String getOpType() {
			return opType;
		}

		public void setOpType(String opType) {
			this.opType = opType;
		}

		public String getIpOprt() {
			return ipOprt;
		}

		public void setIpOprt(String ipOprt) {
			this.ipOprt = ipOprt;
		}
		
		public CalculateServerCluster() {
		}

		public CalculateServerCluster(String ipOprt, String opType) {
			this.ipOprt = ipOprt;
			this.opType = opType;
		}
	}
}
