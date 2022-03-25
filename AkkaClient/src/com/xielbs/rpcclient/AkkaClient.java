package com.xielbs.rpcclient;

import com.xielbs.config.ClusterConfig;
import com.xielbs.config.ClusterParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.AddressFromURIString;
import akka.actor.Props;
import akka.cluster.Cluster;

import com.xielbs.jedis.util.JedisGlobal;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;

public class AkkaClient {

	private static Logger log = LoggerFactory.getLogger(AkkaClient.class);

	private static AkkaClient instance = new AkkaClient();

	private ClusterConfig clusterConfig = ClusterConfig.getConfig();

	private ActorSystem system;


	public AkkaClient() {

	}

	public ActorSystem getSystem() {
		return system;
	}

	public String getServiceUrl(String className) {
		String url = null;
		try {
			url = JedisGlobal.DOC.queryJedisObj(ClusterParameter.RPC_SERVICE, className);
		} catch (Exception e) {
			log.error("获取通讯服务url失败:" + e);
			e.printStackTrace();
		}
		if (url == null) {
			log.error("未能获取通讯服务url，请确认通讯服务是否启动");
		}
		return url;
	}

	public String getNodeIp(){
		return clusterConfig.getClusterNode();
	}

	public void initActorSystem(ActorSystem system){
		if(system ==null){
			try {
				Config cfg = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + clusterConfig.getPort())
						.withValue("akka.remote.netty.tcp.hostname", ConfigValueFactory.fromAnyRef(clusterConfig.getIp()))
						.withFallback(ConfigFactory.load("rcpclient"));

				this.system = ActorSystem.create(ClusterParameter.CLIENT_SYSTEM, cfg);

				String leaderNode = JedisGlobal.DOC.queryRootKeyValue(ClusterParameter.CLIENT_SYSTEM_LEADER);

				if (leaderNode != null) {
					Cluster.get(this.system).join(AddressFromURIString.parse(leaderNode));
				} else {
					Cluster.get(this.system).join(AddressFromURIString.parse(clusterConfig.getClusterNode()));
				}

				this.system.actorOf(Props.create(ClusterListenerActor.class),"Listener");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			this.system = system;
		}
	}

	public static AkkaClient getInstance() {
		return instance;
	}

	public void stopActor(ActorRef clientServer) {
		system.stop(clientServer);
	}

	/**
	 * 获取默认服务，请求超时建默认为30秒
	 *
	 * @param clz
	 * @return
	 * @throws Exception
	 */
	public <T> T getBean(Class<T> clz) throws Exception {
		return getBean(clz, null, 60);
	}

	/**
	 * 获取默认服务，请求超时建默认为30秒
	 *
	 * @param clz
	 * @return
	 * @throws Exception
	 */
	public <T> T getBean(Class<T> clz, String serviceName) throws Exception {
		return getBean(clz, serviceName, 30);
	}

	/**
	 * 获取超时请求设置的bean服务
	 *
	 * @param clz
	 * @param timeOutSeconds
	 * @return
	 * @throws Exception
	 */
	public <T> T getBean(Class<T> clz, String serviceName, int timeOutSeconds) throws Exception {
		String rpcUrl = getServiceUrl(clz.getName() + (serviceName != null ? serviceName : ""));
		ActorSelection rpc;
		if (rpcUrl != null) {
			rpc = system.actorSelection(rpcUrl);
		} else {
			throw new Exception("服务未启动!");
		}
		return new BeanProxy().proxy(rpc, clz, timeOutSeconds);
	}

	/**
	 * 获取超时请求设置的bean服务
	 *
	 * @param clz
	 * @return
	 * @throws Exception
	 */
	public <T> T getBean(Class<T> clz,String url, String serviceName) throws Exception {
		String rpcUrl =null;
		if(url!=null){
			rpcUrl = url;
		}else{
			rpcUrl = getServiceUrl(clz.getName() + (serviceName != null ? serviceName : ""));// 每次都重新查找内存库的服务url
		}
		ActorSelection rpc;
		if (rpcUrl != null) {
			rpc = system.actorSelection(rpcUrl);
		} else {
			throw new Exception("服务未启动!");
		}
		return new BeanProxy().proxy(rpc, clz, 30);
	}

}
