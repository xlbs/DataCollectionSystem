package com.xielbs.database;

public class ConfigException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public ConfigException(String msg) {
		super(msg);
	}
	public ConfigException(Throwable cause) {
		super(cause.getMessage(), cause);
	}
	public ConfigException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public ConfigException(String configKey, String itemKey, String value, String msg, Throwable cause) {
		super(compose(configKey, itemKey, value, msg), cause);
	}
	
	public ConfigException(String configKey, String itemKey, String value, String msg) {
		super(compose(configKey, itemKey, value, msg));
	}
	
	public ConfigException(String configItemKey, String value, String msg) {
		super(compose(configItemKey, value, msg));
	}
	
	public ConfigException(String configItemKey, String value, String msg, Throwable cause) {
		super(compose(configItemKey, value, msg), cause);
	}
	
	private static String compose(String configKey, String itemKey, String value, String msg) {
		return "config name:" + configKey + " item key:" + itemKey + " value:" + value + "  " + msg;
	}
	
	private static String compose(String configItemKey, String value, String msg) {
		return "config/item:" + configItemKey + " value:" + value + "  " + msg;
	}
	
}
