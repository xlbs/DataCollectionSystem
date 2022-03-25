package com.xielbs.service;

import java.util.HashMap;
import java.util.Map;

import com.xielbs.actor.DbOperationRouter;
import com.xielbs.actor.ClusterListenerActor;
import com.xielbs.serviceinterface.I_SQL_DataOperat;

import akka.actor.ActorSystem;
import akka.actor.AddressFromURIString;
import akka.actor.Props;
import akka.cluster.Cluster;

import com.xielbs.imp.Imp_SQL_DataOperat;
import com.xielbs.config.ClusterConfig;
import com.xielbs.config.ClusterParameter;
import com.xielbs.jedis.util.JedisGlobal;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbOperationService {

	private static final Logger log = LoggerFactory.getLogger(DbOperationService.class);

	private static ClusterConfig clusterConfig = ClusterConfig.getConfig();
	
	public static String getNodeIp(){
		return clusterConfig.getClusterNode();
	}
	
	public static void main(String[] args) {
		DbOperationService dbService = new DbOperationService();
		dbService.startService();
	}

	public void startService() {
		try {
            final Config cfg = ConfigFactory.parseString("akka.remote.netty.tcp.port="+ clusterConfig.getPort())
                    .withValue("akka.remote.netty.tcp.hostname",ConfigValueFactory.fromAnyRef(clusterConfig.getIp()))
                    // 默认读取application.conf配置文件
                    .withFallback(ConfigFactory.load());

            ActorSystem system = ActorSystem.create(ClusterParameter.DATABASE_SYSTEM, cfg);

            String leaderNode = JedisGlobal.DOC.queryRootKeyValue(ClusterParameter.DATABASE_SYSTEM_LEADER);
			if (leaderNode != null) {
				Cluster.get(system).join(AddressFromURIString.parse(leaderNode));
			} else {
				Cluster.get(system).join(AddressFromURIString.parse(clusterConfig.getClusterNode()));
			}

			system.actorOf(Props.create(ClusterListenerActor.class), ClusterParameter.DATABASE_OPERATION_CLUSTER_LISTENER);

//          SessionFactoryManager.startService();
            Map<Class<?>, Object> beans = new HashMap<Class<?>, Object>();

            // Server 加入发布的服务
//		    beans.put(IOperate.class, new ImpOperator());
            beans.put(I_SQL_DataOperat.class, new Imp_SQL_DataOperat());

            system.actorOf(Props.create(DbOperationRouter.class, beans).withDispatcher("main-dispatcher"), ClusterParameter.DATABASE_OPERATION_ROUTER);

			String serviceIp = "akka.tcp://" + clusterConfig.getClusterName() + "@" + clusterConfig.getIp() + ":" + clusterConfig.getPort();
//		    registerService(IOperate.class.getName(), serviceIp+"/user/EQService");
			registerService(I_SQL_DataOperat.class.getName(), serviceIp+"/user/"+ClusterParameter.DATABASE_OPERATION_ROUTER);

            log.info("数据库操作服务启动成功");

		} catch (Exception e) {
			e.printStackTrace();
			log.error("数据库操作服务启动出现异常：", e);

		}

	}

	public void registerService(String serviceName,String url) {
		try {
			JedisGlobal.DOC.updateJedisObj(ClusterParameter.RPC_SERVICE, serviceName, url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
