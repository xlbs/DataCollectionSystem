package com.xielbs.service;

import java.io.Serializable;

/**
 * 终端采集分配管理 
 */
public class TrmAssign implements Serializable {

	public enum OprtType {
		FIRSTASSIGN,UPDATE, DEL,DEASSIGN,ADD_SUCCESS,ONLINE,OFFLINE;
	}

	private static final long serialVersionUID = 1L;

	public TrmAssign() {
	}

	public TrmAssign(String trmAddr, OprtType opType) {
		this.trmAddr = trmAddr;
		this.opType = opType;
	}
	
	public TrmAssign(int trmId, OprtType opType, String ipPort) {
		this.trmId = trmId;
		this.opType = opType;
		this.ipPort = ipPort;
	}
	
	private String trmAddr;
	
	//储能协议使用，其它协议该值为空
	private String trmType;
	
	private String ipPort;
	
	private int trmId;

	public String getTrmType() {
		return trmType;
	}

	public void setTrmType(String trmType) {
		this.trmType = trmType;
	}

	public String getTrmAddr() {
		return trmAddr;
	}

	public void setTrmAddr(String trmAddr) {
		this.trmAddr = trmAddr;
	}

	public String getIpPort() {
		return ipPort;
	}

	public void setIpPort(String ipPort) {
		this.ipPort = ipPort;
	}

	private OprtType opType;

	public OprtType getOpType() {
		return opType;
	}

	public void setOpType(OprtType opType) {
		this.opType = opType;
	}

	public int getTrmId() {
		return trmId;
	}

	public void setTrmId(int trmId) {
		this.trmId = trmId;
	}

}
