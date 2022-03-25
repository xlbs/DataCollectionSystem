package com.xielbs.actor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.xielbs.service.RouterService;
import com.xielbs.service.TrmAssign;
import com.xielbs.utils.TimeTag;
import com.xielbs.config.ClusterParameter;
import com.xielbs.jedis.util.JedisGlobal;
import com.xielbs.service.ClusterMsg.FrontMachineCluster;
import com.xielbs.service.ClusterMsg.TimeTaskCluster;
import com.xielbs.service.ClusterMsg.TrmDeassignCluster;
import com.xielbs.service.TrmAssign.OprtType;

import akka.actor.ReceiveTimeout;
import akka.actor.UntypedActor;
import akka.routing.ActorSelectionRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import com.xielbs.utils.JSONUtils;
import scala.concurrent.duration.Duration;

/**
 * 终端登录分配定时任务路由器
 * 
 * @author Administrator
 */
public class RouterActor extends UntypedActor {
	/**
	 * 每台前置机对应一个路由。保证单台前置机的终端平均分配到定时任务
	 */
	Map<String, Router> routerMap = new HashMap<String, Router>();
	/**
	 * 所有定时任务服务地址
	 */
	List<Routee> routees = new ArrayList<Routee>();
	/**
	 * 新加入的定时任务节点，需要分配终端
	 */
	List<Routee> newRoutees = new ArrayList<Routee>();
	/**
	 * 终端分配期间，新的定时任务加入，暂存
	 */
	List<Routee> noRunRoutees = new ArrayList<Routee>();

	Map<String, Routee> noRunRouteeMap = new HashMap<String, Routee>();
	/**
	 * 终端分配是否进行中
	 */
	private AtomicBoolean isDeassign = new AtomicBoolean(false);
	/**
	 * 定时任务集群通知路由
	 */
	Router router;

	Router newRouter;
	/**
	 * 分配数量
	 */
	private AtomicInteger allDeassignNum = new AtomicInteger(0);

	private static ConcurrentHashMap<Integer, Integer> trmLoginIdMap = new ConcurrentHashMap<Integer, Integer>();

	@Override
	public void onReceive(Object msg) throws Exception {
		try {
			if (msg instanceof TimeTaskCluster) {
				TimeTaskCluster tc = (TimeTaskCluster) msg;
				if (tc.getOpType().equals("UP")) {
					if (!tc.isStartFirst()) {// 如果定时任务节点在router节点启动之前，此消息为路由器重启之后收到，不做分配处理
						routees.add(ActorSelectionRoutee.apply(this.getContext().actorSelection(tc.getIpPort() + "/user/TrmManager")));
						reInitRouterMap();
						return;
					}
					if (routees.size() > 0) {
						deassignTrmToTimeTask(tc);
						this.getContext().setReceiveTimeout(Duration.create(5, TimeUnit.MINUTES));// 设置分配超时时间5分钟
						return;
					} else {
						desaginAllTrmInfoToTimeTask(tc);
						return;
					}
				} else {
					routees.remove(ActorSelectionRoutee.apply(this.getContext().actorSelection(tc.getIpPort() + "/user/TrmManager")));
					reInitRouterMap();
					deassignDeleteTimeTaskTrm(tc);
				}
			} else if (msg instanceof ReceiveTimeout) {// 分配超时
				getContext().setReceiveTimeout(Duration.Undefined());
				deassignTrmTimeOut();
			} else if (msg instanceof FrontMachineCluster) {
				FrontMachineCluster fmc = (FrontMachineCluster) msg;
				String frontMachineIp = fmc.getIpPort().split("@")[1];
				if (fmc.getOpType().equals("UP")) {
					routerMap.put(frontMachineIp, new Router(new RoundRobinRoutingLogic(), routees));
				} else {
					routerMap.remove(frontMachineIp);
					broadcastFrontMachineOffToTimetask(fmc);
					delRedisTrmOffline(fmc);
					updateWorkInfo(fmc);
				}
			} else if (routees.size() > 0) {
				if (msg instanceof TrmAssign) {
					TrmAssign tdo = (TrmAssign) msg;
					if (tdo.getOpType().equals(OprtType.FIRSTASSIGN)) {
						trmLoginIdMap.put(tdo.getTrmId(), tdo.getTrmId());
						Router router = routerMap.get(tdo.getIpPort());
						if (router != null) {
							router.route(msg, getSender());
						}
					} else if (tdo.getOpType().equals(OprtType.DEASSIGN)) {
						newRouter.route(msg, getSender());
						allDeassignNum.decrementAndGet();
						if (allDeassignNum.get() == 0) {
							isDeassign.set(false);// 完成分配
							System.out.println("分配完成");
							checkNextDeassignTrm();
						} else if (allDeassignNum.get() < 0) {
							allDeassignNum.getAndSet(0);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 重新初始化所有router
	 */
	private void reInitRouterMap() {
		router = new Router(new RoundRobinRoutingLogic(), routees);
		for (String key : routerMap.keySet()) {
			routerMap.put(key, new Router(new RoundRobinRoutingLogic(), routees));
		}
	}

	/**
	 * 通知定时任务前置机关闭
	 */
	private void broadcastFrontMachineOffToTimetask(FrontMachineCluster fmc) {
		for (int i = 0; i < routees.size(); i++) {
			router.route(fmc, getSelf());
		}
	}

	/**
	 * 分配删除的定时任务上的终端
	 * 
	 * @param tc
	 * @throws Exception
	 */
	private void deassignDeleteTimeTaskTrm(TimeTaskCluster tc) throws Exception {
		String ip = tc.getIpPort().split("@")[1];
		if (routees.size() == 0) {// 没有定时任务
			System.out.println("没有定时任务服务可以分配，清空终端对应定时任务集合");
			Map<String, String> mpMap = JedisGlobal.DOC.queryJedisMapAllObj(ClusterParameter.TIMETASK_TRM_TABLE + ip);
			for (String key : mpMap.keySet()) {
				trmLoginIdMap.remove(Integer.parseInt(key));
			}
			JedisGlobal.DOC.delRootKey(ClusterParameter.TRM_TIMETASK_TABLE);
			JedisGlobal.DOC.delRootKey(ClusterParameter.TIMETASK_TRM_TABLE + ip);
			JedisGlobal.DOC.delRootKey(ClusterParameter.TIME_TASK_FM_NUM + ip);
			return;
		}
		Map<String, String> trms = JedisGlobal.DOC.queryJedisMapAllObj(ClusterParameter.TIMETASK_TRM_TABLE + ip);
		if (trms.size() > 0) {
			System.out.println("分配删除的定时任务上的终端：" + trms.size());
			Map<String, List<String>> type_list = new HashMap<String, List<String>>();
			for (String trmAddr : trms.keySet()) {
				String trmType = trms.get(trmAddr);
				List<String> trms_ = type_list.get(trmType);
				if (trms_ == null) {
					trms_ = new ArrayList<String>();
					type_list.put(trmType, trms_);
				}
				trms_.add(trmAddr);
			}
			for (String key : type_list.keySet()) {
				List<String> trms_ = type_list.get(key);
				for (String trmId : trms_) {
					TrmAssign tdo = new TrmAssign(Integer.valueOf(trmId), OprtType.DEASSIGN, key);
					routerMap.get(key).route(tdo, getSender());
				}
			}
			type_list.clear();
			trms.clear();
			type_list = null;
			trms = null;
		}
		JedisGlobal.DOC.delRootKey(ClusterParameter.TIMETASK_TRM_TABLE + ip);
		JedisGlobal.DOC.delRootKey(ClusterParameter.TIME_TASK_FM_NUM + ip);
	}

	/**
	 * 判断是否需要重新分配
	 * 
	 * @param tc
	 * @throws Exception
	 */
	private boolean deassignTrmToTimeTask(TimeTaskCluster tc) throws Exception {
		if (!isDeassign.get()) {
			// 说明分配完成或者未进行分配，允许下一个周期开始分配
			isDeassign.set(true);
			newRoutees.add(ActorSelectionRoutee.apply(this.getContext().actorSelection(tc.getIpPort() + "/user/TrmManager")));
			newRouter = new Router(new RoundRobinRoutingLogic(), newRoutees);
			broadcastInfoToTimeTask();
			return true;
		} else {
			// 说明正在进行分配,等待上次分配工作完成
			ActorSelectionRoutee routee = ActorSelectionRoutee.apply(this.getContext().actorSelection(tc.getIpPort() + "/user/TrmManager"));
			if (noRunRouteeMap.get(tc.getIpPort() + "/user/TrmManager") != null) {
				noRunRoutees.add(routee);
			} else {
				noRunRouteeMap.put(tc.getIpPort() + "/user/TrmManager", routee);
			}
			return false;
		}
	}

	

	/**
	 * 定时任务全部关闭之后，新定时任务启动,分配
	 * 
	 * @param tc
	 * @throws Exception
	 */
	private void desaginAllTrmInfoToTimeTask(TimeTaskCluster tc) throws Exception {
		try {
			routees.add(ActorSelectionRoutee.apply(this.getContext().actorSelection(tc.getIpPort() + "/user/TrmManager")));
			reInitRouterMap();
			Map<String, String> trm_front = JedisGlobal.DOC.queryJedisMapAllObj(ClusterParameter.TRM_FRONTMACHINE_TABLE);
			for (String trmId : trm_front.keySet()) {
				TrmAssign tdo = new TrmAssign(Integer.valueOf(trmId), OprtType.DEASSIGN, trm_front.get(trmId));
				try {
					if (trm_front.get(trmId) != null && routerMap.get(trm_front.get(trmId)) != null) {
						routerMap.get(trm_front.get(trmId)).route(tdo, getSender());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检查上个分配周期内是否有新的定时任务加入集群
	 */
	private void checkNextDeassignTrm() {
		if (noRunRoutees.size() > 0) {// 说明在上一次分配过程中，有新的定时任务启动
			newRoutees.clear();
			newRoutees.addAll(noRunRoutees);
			newRouter = new Router(new RoundRobinRoutingLogic(), newRoutees);
			noRunRoutees.clear();
			noRunRouteeMap.clear();
			broadcastInfoToTimeTask();
		} else {// 清空新的
			newRoutees.clear();
			newRouter = new Router(new RoundRobinRoutingLogic(), newRoutees);
		}
	}

	/**
	 * 重置分配操作
	 */
	private void deassignTrmTimeOut() {
		allDeassignNum.getAndSet(0);
		isDeassign.getAndSet(false);
		newRoutees.clear();
		noRunRoutees.clear();
		noRunRouteeMap.clear();
		for (int i = 0; i < routees.size(); i++) {// 通知定时任务终止分配
			router.route(new TrmDeassignCluster("STOP"), getSelf());
		}
		try {
			Thread.sleep(5000);// 暂停2秒执行下一个分配
			checkNextDeassignTrm();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Map<String, Integer> queryTrmTypeNums() throws Exception {
		String timeTaskIp = RouterService.timeTaskMap.keySet().iterator().next();
		System.out.println("分配参考定时任务:" + timeTaskIp);
		Map<String, String> typeNums = JedisGlobal.DOC.queryJedisMapAllObj(ClusterParameter.TIME_TASK_FM_NUM + timeTaskIp.split("@")[1]);
		Map<String, Integer> typeNums_ = new HashMap<String, Integer>();
		System.out.println("定时任务终端数：" + typeNums);
		int nowSize = routees.size();
		int newSize = newRoutees.size();
		for (String key : typeNums.keySet()) {
			String val = typeNums.get(key);
			Integer v = Integer.valueOf(val);
			v = v * newSize / (nowSize + newSize);
			typeNums_.put(key, v);
			allDeassignNum.addAndGet(v * nowSize);
		}
		System.out.println("从每台定时任务取终端数:" + typeNums_);
		return typeNums_;
	}

	private void delRedisTrmOffline(FrontMachineCluster fmc) {
		try {
			String ip = fmc.getIpPort().substring(fmc.getIpPort().indexOf("@") + 1);
			Map<String, String> allTrmMap = JedisGlobal.DOC.queryJedisMapAllObj(ClusterParameter.TRM_ALL_LOGINED);
			if (allTrmMap == null || allTrmMap.size() == 0)
				return;
			Set<String> thisFpcTrms = new HashSet<String>();
			for (String id : allTrmMap.keySet()) {
				if (ip.equalsIgnoreCase(allTrmMap.get(id)))
					thisFpcTrms.add(id);
			}

			if (thisFpcTrms.size() > 0) {
				JedisGlobal.DOC.delRedisValue(ClusterParameter.TRM_LOG_IN, thisFpcTrms.toArray(new String[thisFpcTrms.size()]));
				JedisGlobal.DOC.delRedisValue(ClusterParameter.TRM_FRONTMACHINE_TABLE, thisFpcTrms.toArray(new String[thisFpcTrms.size()]));
			}
		} catch (Exception c) {
			System.out.println("前置机掉线,更新终端在线情况出错!");
		}
	}

	private void updateWorkInfo(FrontMachineCluster fmc) {
		try {
			String ip = fmc.getIpPort().substring(fmc.getIpPort().indexOf("@") + 1);
			Map<String, String> allTrmMap = JedisGlobal.DOC.queryJedisMapAllObj(ClusterParameter.TRM_ALL_LOGINED);
			if (allTrmMap == null || allTrmMap.size() == 0)
				return;
			Set<String> thisFpcTrms = new HashSet<String>();
			for (String id : allTrmMap.keySet()) {
				if (ip.equalsIgnoreCase(allTrmMap.get(id)))
					thisFpcTrms.add(id);
			}
			if (thisFpcTrms.size() == 0)
				return;
			List<String> onLine = JedisGlobal.DOC.queryJedisMapObj(ClusterParameter.TRM_ONLINE,thisFpcTrms.toArray(new String[thisFpcTrms.size()]));
			Map<String, String> map = new HashMap<>();
			Map<String, String> offMap = JedisGlobal.DOC.queryJedisMapAllObj(ClusterParameter.TRM_OFF_LS);
			Map<String, String> offMapUp = new HashMap<>();
			Map<String, String> workInfo = null;
			Long time;
			String offStr;
			List<Long> offLs;
			for (String str : onLine) {
				try {
					if (str == null)
						continue;
				workInfo = JSONUtils.deserialize(str);
				if ((offStr = offMap.get(workInfo.get("CP_ID"))) != null) {
					offLs = JSONUtils.deserializeList(offStr, Long.class);
					offLs.add(System.currentTimeMillis());
					offMapUp.put(workInfo.get("CP_ID"), JSONUtils.serialize(offLs));
				}
			/*	if (valmap.get("DXDate") == null) {
					valmap.put("DXDate", Calendar.getInstance().getTimeInMillis() + "");// 掉线时间
					valmap.put("DX_NUM", (Integer.parseInt(valmap.get("DX_NUM")) + 1) + "");// 掉线次数
				}*/
				if (workInfo.get("ZXDate") != null) {
				   long	 dt =Long.parseLong(workInfo.get("ZXDate").toString()); 	 	
					workInfo.put("ZXDate", null);
					workInfo.put("loginTime",null);
					workInfo.put("ZX_TIME", (Double.parseDouble(workInfo.get("ZX_TIME"))
							+ (System.currentTimeMillis() -dt) * 1.0 / (1000 * 60)) + "");
					workInfo.put("DXDate", System.currentTimeMillis() + "");
					workInfo.put("DX_NUM", (Integer.parseInt(workInfo.get("DX_NUM")) + 1) + "");// 掉线次数
					workInfo.put("logoutTime", new TimeTag().toString());
				
			

				}
				map.put(workInfo.get("CP_ID"), JSONUtils.serialize(workInfo));
				}catch (Exception c) {
					  c.printStackTrace();
				}

			}
			JedisGlobal.DOC.delRedisValue(ClusterParameter.TRM_FRONTMACHINE_TABLE,thisFpcTrms.toArray(new String[thisFpcTrms.size()]));
			JedisGlobal.DOC.updateMapToRedis(ClusterParameter.TRM_ONLINE, map);
			if (offMapUp.size() > 0) {
				JedisGlobal.DOC.updateMapToRedis(ClusterParameter.TRM_OFF_LS, offMapUp);
			}
		} catch (Exception c) {
			System.out.println("前置机掉线,更新工况信息出错!");
		}
	}

	private void broadcastInfoToTimeTask() {
		try {
			Map<String, Integer> typeNums_ = queryTrmTypeNums();
			for (int i = 0; i < routees.size(); i++) {
				router.route(new TrmDeassignCluster(JSONUtils.serialize(typeNums_)), getSelf());
			}
			routees.addAll(newRoutees);
			reInitRouterMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
