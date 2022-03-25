package com.xielbs.serviceinterface;
/**
 * rpc服务定义
 * @author Administrator
 *
 */
public interface RpcService {
	
	public void startService();
	
	public void registeService(String serviceName, String url);
	
}
