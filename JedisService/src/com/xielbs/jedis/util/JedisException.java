package com.xielbs.jedis.util;

public class JedisException extends Exception{

	public JedisException(){
		super();
	}
	
	public JedisException(String msg){
		super(msg);
	}
	
	public JedisException(String msg,Throwable cause){
		super(msg, cause);
	}
	
	public JedisException(Throwable cause){
		super(cause);
	}
}
