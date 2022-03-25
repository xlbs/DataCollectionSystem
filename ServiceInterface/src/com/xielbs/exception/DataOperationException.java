package com.xielbs.exception;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class DataOperationException extends Exception {

	private static final long serialVersionUID = 1L;

	public static byte TYPE_CONNECT_FAILURE = 1;

	public static byte TYPE_BATCH_EXCEPTION = 2;

	public static byte TYPE_EXESQL = 3;

	public static byte TYPE_NOTEXIST = 4;

	public static byte TYPE_NETWORK_OVERTIME = 5;

	public static byte TYPE_OTHER = 99;

	private byte type = TYPE_OTHER;
	
	public DataOperationException(byte type, String message, Throwable cause) {
		super(message,cause);
		this.type = type;
	}
	
	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}
	
	public int getSQLErrorCode(){
		if(getCause() instanceof SQLException){
			SQLException se = (SQLException)getCause();
			return se.getErrorCode();
		}
		return type;
	}
	
	public String isDBDisconnect(){
		if(getCause() instanceof SQLException){
			SQLException se = (SQLException)getCause();
			int c = se.getErrorCode();
			//17002  IO 错误: Software caused connection abort: recv failed 连接上了数据库查询过程中网络断了，导致查询超时
			//0  从连接池获取连接失败,数据库配置错误(IP端口用户名或者密码等错误,连接不上数据库)
			//java.sql.SQLWarning: ORA-28002: the password will expire within 0 days
			List<Integer> dcs = Arrays.asList(0,17002,28002);
			if(dcs.contains(c))
				return "ErrorCode="+c+",Msg:"+se.getMessage();
			if(se.getMessage().contains("表空间不足"))
				return se.getMessage();
			if(se.getMessage().contains("不存在"))
				return se.getMessage();
			if(se.getMessage().contains("password will expire"))
				return se.getMessage();
		}
		return null;
	}
	
	
	
	@Override
	public String toString() {
		return "DataOperationException [type=" + type + "=" + getSQLErrorCode() + "]"+getMessage();
	}

	public static void main(String[] args) {
		System.out.println((byte)130);
	}


	
}
