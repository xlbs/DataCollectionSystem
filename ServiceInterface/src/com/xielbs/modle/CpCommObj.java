package com.xielbs.modle;

import java.io.Serializable;

/**
 * 通讯接口对外终端参数对象
 * 
 * @author wuwl
 * 
 */
public class CpCommObj implements Serializable {

	private static final long serialVersionUID = 1L;

	private int trmId = -1;

	private String trmAddr = ""; // 十六进制字符串,包括行政编码和终端地址

	private byte trmPrtl = -1; // 终端协议具体类型

	private String trmName; // 终端名称

	private byte prtlType = -1; // 终端协议序列

	private int funCode = -1;// 功能码
	
	private String cpType;//终端类型;

	private String algKey = "";// 算法密钥

	private String algNo = "";// 算法编号

	private byte safeChn = 0;// 0表示默认无隔离通道,1表示电力专网隔离通道,2表示GPRS采集网隔离通道

	private String adminPwd = "";// 管理员密码

	// 如果为TCPCLIENT通道类型，则COM_PARA1字段填写前置机所在服务器IP （也可不填），表示前置机通过网络连接电能量终端时占用的本地IP
	private String chnMain = "";// 主通讯方式 字典表130

	private String chnSel = null;// 备通讯方式 目前不用

	private String protocolVersion = "";// 终端软件版本,东软协议用

	private Object attatchment2;

	private Object attachment;

	private byte  transfer=0; //透传协议  子协议类型

	private Object prtlObject; //当前处理终端报文协议对象

	public Object getPrtlObject() {
		return prtlObject;
	}

	public void setPrtlObject(Object prtlObject) {
		this.prtlObject = prtlObject;
	}

	public byte getTransfer() {
		return transfer;
	}

	public void setTransfer(byte transfer) {
		this.transfer = transfer;
	}

	public CpCommObj() {
	}

	public CpCommObj(int trmId) {
		this.trmId = trmId;
	}

	public CpCommObj(int trmId, String trmAddr, byte trmPrtl, byte prtlType) {
		super();
		this.trmId = trmId;
		this.trmAddr = trmAddr;
		this.trmPrtl = trmPrtl;
		this.prtlType = prtlType;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CpCommObj other = (CpCommObj) obj;
		if (trmAddr == null) {
			if (other.trmAddr != null)
				return false;
		} else if (!trmAddr.equals(other.trmAddr))
			return false;
		if (trmId != other.trmId)
			return false;
		return true;
	}

	public String getAdminPwd() {
		return adminPwd;
	}

	public String getAlgKey() {
		return algKey;
	}

	public String getAlgNo() {
		return algNo;
	}

	public Object getAttachment() {
		return attachment;
	}

	public Object getAttatchment2() {
		return attatchment2;
	}

	public String getChnMain() {
		return chnMain;
	}

	public String getChnSel() {
		return chnSel;
	}

	public String getCpType() {
		return cpType;
	}

	public int getFunCode() {
		return funCode;
	}

	public String getProtocolVersion() {
		return protocolVersion;
	}

	public byte getPrtlType() {
		return prtlType;
	}

	public byte getSafeChn() {
		return safeChn;
	}

	public String getTrmAddr() {
		return trmAddr;
	}

	public int getTrmId() {
		return trmId;
	}

	public String getTrmName() {
		return trmName;
	}

	public byte getTrmPrtl() {
		return trmPrtl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((trmAddr == null) ? 0 : trmAddr.hashCode());
		result = prime * result + trmId;
		return result;
	}

	public void setAdminPwd(String adminPwd) {
		this.adminPwd = adminPwd;
	}

	public void setAlgKey(String algKey) {
		this.algKey = algKey;
	}

	public void setAlgNo(String algNo) {
		this.algNo = algNo;
	}

	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}

	public void setAttatchment2(Object attatchment2) {
		this.attatchment2 = attatchment2;
	}

	public void setChnMain(String chnMain) {
		this.chnMain = chnMain;
	}

	public void setChnSel(String chnSel) {
		this.chnSel = chnSel;
	}

	public void setCpType(String cpType) {
		this.cpType = cpType;
	}

	public void setFunCode(int funCode) {
		this.funCode = funCode;
	}

	public void setProtocolVersion(String protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	public void setPrtlType(byte prtlType) {
		this.prtlType = prtlType;
	}

	public void setSafeChn(byte safeChn) {
		this.safeChn = safeChn;
	}

	public void setTrmAddr(String trmAddr) {
		this.trmAddr = trmAddr;
	}

	public void setTrmId(int trmId) {
		this.trmId = trmId;
	}

	public void setTrmName(String trmName) {
		this.trmName = trmName;
	}

	public void setTrmPrtl(byte trmPrtl) {
		this.trmPrtl = trmPrtl;
	}

}
