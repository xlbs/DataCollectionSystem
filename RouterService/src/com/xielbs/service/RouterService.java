package com.xielbs.service;

import java.util.*;

import com.xielbs.actor.ClusterListenerActor;
import com.xielbs.actor.RouterActor;
import com.xielbs.config.ClusterConfig;
import com.xielbs.config.ClusterParameter;
import com.xielbs.config.JacksonUtil;
import com.xielbs.init.DevUpdate;
import com.xielbs.init.InitAlarmParam;
import com.xielbs.init.InitCpRedis;
import com.xielbs.init.InitTele;
import com.xielbs.serviceinterface.I_SQL_DataOperat;
import com.xielbs.jedis.util.JedisGlobal;
import com.xielbs.rpcclient.AkkaClient;
import com.xielbs.init.InitParam;
import com.xielbs.utils.JSONUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;

import akka.actor.ActorSystem;
import akka.actor.AddressFromURIString;
import akka.actor.Props;
import akka.cluster.Cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Administrator 定时任务路由器服务
 */
public class RouterService {

	private static final Logger log = LoggerFactory.getLogger(RouterService.class);

	public static Map<String, String> timeTaskMap = new LinkedHashMap<String, String>();

	public static ClusterConfig clusterConfig = ClusterConfig.getConfig();

    public static I_SQL_DataOperat dao = null;

	public static void main(String... args) {
		RouterService routerService = new RouterService();
		routerService.startService();
	}

	private void startService() {
		try {
			JedisGlobal.DOC.setRootKeyValue(ClusterParameter.ROUTER_IP_PORT, clusterConfig.getIp() + ":" + clusterConfig.getPort());

			Config cfg = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + clusterConfig.getPort())
					.withValue("akka.remote.netty.tcp.hostname", ConfigValueFactory.fromAnyRef(clusterConfig.getIp()))
					.withFallback(ConfigFactory.load("routerserver"));

			ActorSystem actorSystem = ActorSystem.create(ClusterParameter.COLLECTION_SYSTEM, cfg);

			String leaderNode = JedisGlobal.DOC.queryRootKeyValue(ClusterParameter.COLLECTION_SYSTEM_LEADER);
			if (leaderNode != null) {
				Cluster.get(actorSystem).join(AddressFromURIString.parse(leaderNode));
			} else {
				Cluster.get(actorSystem).join(AddressFromURIString.parse(clusterConfig.getClusterNode()));
			}

			actorSystem.actorOf(Props.create(RouterActor.class).withDispatcher("other-dispatcher"), ClusterParameter.ROUTER_ACTOR);
			actorSystem.actorOf(Props.create(ClusterListenerActor.class), ClusterParameter.ROUTER_CLUSTER_LISTENER);

			AkkaClient.getInstance().initActorSystem(actorSystem);

			dao = AkkaClient.getInstance().getBean(I_SQL_DataOperat.class);
			InitParam.initAllParam(dao);

//			ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
//			scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
//				@Override
//				public void run() {
//					dealWebCpChangeNotice(dao);
//				}
//			}, 1, 20, TimeUnit.SECONDS);
//
//			final Calendar now = Calendar.getInstance();
//			Calendar execDate = Calendar.getInstance();
//			execDate.set(Calendar.HOUR_OF_DAY, 3);
//			execDate.set(Calendar.MINUTE, 15);
//			execDate.set(Calendar.SECOND, 0);
//			while (execDate.before(now)) {
//				execDate.add(Calendar.DAY_OF_MONTH, +1);
//			}
//			int delayMin = CalendarUtils.compareTime(execDate, now, Calendar.MINUTE);
//			scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
//				@Override
//				public void run() {
//					deleteCpDocChangeNotice();
//				}
//			}, delayMin, 24 * 60, TimeUnit.MINUTES);

			log.info("路由服务启动成功");

		} catch (Exception e) {
			e.printStackTrace();
			log.error("路由服务启动异常：", e);
		}




	}



	public static void dealWebCpChangeNotice(I_SQL_DataOperat dao) {
	try {
		Map<String, String> notices = JedisGlobal.DATA.queryJedisMapAllObj("cp_change");
		if (notices == null || notices.size() == 0) {
            return;
        }
		String keys[] = new String[notices.keySet().size()];
		notices.keySet().toArray(keys);
		for (int i = 0; i < keys.length; i++) {
			for (int j = i + 1; j < keys.length; j++) {
				if (Long.valueOf(keys[i]).longValue() > Long.valueOf(keys[j]).longValue()) {
					String tmp = keys[i];
					keys[i] = keys[j];
					keys[j] = tmp;
				}
			}
		}

      Map<String,String> mapRecord = new  HashMap<String,String>();
		
		for (int i = 0; i < keys.length; i++) {
			try {
				String key = keys[i];
				String value = notices.get(key);
				JedisGlobal.DATA.delRedisValue("cp_change", key);
					Map map = JacksonUtil.queryJsonMap(value);
					String objType = map.get("objType")==null?null:map.get("objType").toString();
			    	String opType = map.get("operateType")==null?null:map.get("operateType").toString();
					String objId=map.get("objId")==null?null:map.get("objId").toString();
					if(objType==null||objType.isEmpty()||objId==null||objId.isEmpty()||opType==null||opType.isEmpty())
					  continue;
	               if(mapRecord.get(objType+objId+opType)!=null)
                        continue;
	               mapRecord.put(objType+objId+opType,key);
                       						
					
					if(objType.equalsIgnoreCase("alarm_object_config"))
				{
					InitAlarmParam.initAllAlarmRelaObject(RouterService.dao);
					continue;
				}
				int iObjId = Integer.valueOf(objId);
				// 找出原來的cp;
				if (objType.equalsIgnoreCase("tele")) {
					handleTeleCp(iObjId, key, map);
				}
				if (opType.equalsIgnoreCase("add") || opType.equalsIgnoreCase("update")) {
					if (objType.equalsIgnoreCase("cp")) {
						InitCpRedis.InitRcpRedis(RouterService.dao, new Integer[] { iObjId });
					} else if (objType.equalsIgnoreCase("tele")) {
						InitTele.initTele(RouterService.dao, iObjId);
					} else if (objType.equalsIgnoreCase("event_threshold")) {
						InitAlarmParam.initThreshold(RouterService.dao, iObjId);
					} else if (objType.equalsIgnoreCase("equipment")) {
						DevUpdate.updateMpPropertyRedis(RouterService.dao, iObjId);
						DevUpdate.updateGeneratorMpRedis(RouterService.dao, iObjId);
					}
				} else if (opType.equalsIgnoreCase("delete")) {
					if (objType.equalsIgnoreCase("cp")) {
						InitCpRedis.delCpRedis(RouterService.dao, iObjId);
					} else if (objType.equalsIgnoreCase("tele")) {
						InitTele.deleteTele(RouterService.dao, iObjId);
					} else if (objType.equalsIgnoreCase("event_threshold")) {
						InitAlarmParam.deleteThreshold(RouterService.dao, iObjId);
					}
				}
				///
				JedisGlobal.DATA.updateJedisObj(ClusterParameter.CP_DOC_CHANGE, key, value);
			

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	} catch (Exception e) {
		e.printStackTrace();
	}
}

	public static void handleTeleCp(int objId, String key, Map map) {
		// 查出原来的遥信信息
		try {
			String teleInfoStr = JedisGlobal.DOC.queryJedisObj(ClusterParameter.MP_TELE_INFO, String.valueOf(objId));
			if (teleInfoStr != null) {
				Map teleInfoMap = JacksonUtil.queryJsonMap(teleInfoStr);
				Object cpId = teleInfoMap.get("cp_id");
				if (cpId != null) {
					map.put("old_cp", cpId);
					String value = JSONUtils.serialize(map);
					JedisGlobal.DATA.updateJedisObj(ClusterParameter.CP_DOC_CHANGE, key, value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void deleteCpDocChangeNotice() {
		try {
			Set<String> keys = JedisGlobal.DATA.getAllKeys(ClusterParameter.CP_DOC_CHANGE);
			if (keys != null && keys.size() > 0) {
			//	Thread.sleep(600 * 1000l); // 休息10分钟再删除
				JedisGlobal.DATA.delRootKey(ClusterParameter.CP_DOC_CHANGE);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
