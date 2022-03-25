package com.xielbs.exception;

public class ProtocolException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public ProtocolException(){
		
	}
	
	public ProtocolException(String msg){
		super(msg);
	}
	
	public ProtocolException(String msg, Throwable throwable){
		super(msg,throwable);
	}
	
	public String getMessage(){
		return "Protocol Exception."+super.getMessage();
	}
	
}
