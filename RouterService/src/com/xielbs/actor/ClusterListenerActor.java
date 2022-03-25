package com.xielbs.actor;

import com.xielbs.config.ClusterParameter;
import com.xielbs.service.RouterService;
import com.xielbs.serviceinterface.CallServiceInterface;
import com.xielbs.jedis.util.JedisGlobal;
import com.xielbs.service.ClusterMsg.FrontMachineCluster;
import com.xielbs.service.ClusterMsg.TimeTaskCluster;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.LeaderChanged;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.ReachableMember;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.cluster.Member;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * 集群监听 定时任务监听前置机，如果前置机关掉，则对应前置机分配来的终端任务需停止并且删除
 * 
 * @author Administrator
 *
 */
public class ClusterListenerActor extends UntypedActor {

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	Cluster cluster = Cluster.get(getContext().system());

	ActorSelection as = getContext().actorSelection("/user/"+ClusterParameter.ROUTER_ACTOR);

	Member routerMember = null;
	
	@Override
	public void preStart() {
		cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(),
				MemberEvent.class, UnreachableMember.class,ReachableMember.class,
				MemberRemoved.class, LeaderChanged.class);
	}

	@Override
	public void postStop() {
		cluster.unsubscribe(getSelf());
	}

	@Override
	public void onReceive(Object message) throws Exception  {
		if (message instanceof MemberUp) {
			MemberUp mUp = (MemberUp) message;
			String serviceAddress = mUp.member().address().toString();
			//定时任务服务监听
			if (mUp.member().hasRole(ClusterParameter.TIME_TASK_ROLE)) {
				RouterService.timeTaskMap.put(serviceAddress, serviceAddress);
				JedisGlobal.DOC.updateJedisObj(ClusterParameter.TIME_TASK_MAP, serviceAddress, serviceAddress);
				log.info("定时任务服务节点加入: {}", mUp.member());
				boolean startFirst = true;
				if(routerMember == null){
					startFirst = false;
					System.out.println("null节点启动在router之前");
				}else{
					if(mUp.member().isOlderThan(routerMember)){
						startFirst = false;
						System.out.println("节点启动在router之前");
					}else{
						System.out.println("节点启动在router之后");
					}
				}
				as.tell(new TimeTaskCluster(serviceAddress,"UP",startFirst), getSelf());
			}
			//前置机服务监听
			else if (mUp.member().hasRole(ClusterParameter.FRONT_MACHINE_ROLE)) {
				JedisGlobal.DOC.updateJedisObj(ClusterParameter.FRONT_MACHINE_MAP, serviceAddress, serviceAddress);
				log.info("前置机服务节点加入: {}", mUp.member());
				as.tell(new FrontMachineCluster(serviceAddress,"UP"), getSelf());
			}
			//召测服务监听
			else if(mUp.member().hasRole(ClusterParameter.TRM_CALL_ROLE)){
				JedisGlobal.DOC.setRootKeyValue(ClusterParameter.TRM_CALL_ROLE, "true");
				log.info("召测服务节点加入: {}", mUp.member());
			}
			//路由服务监听
			else if(mUp.member().hasRole(ClusterParameter.ROUTER_ROLE)){
				log.info("路由服务节点加入: {}", mUp.member());
				routerMember = mUp.member();
			}
		}else if (message instanceof LeaderChanged) {
			LeaderChanged leaderChanged = (LeaderChanged) message;
			String serviceAddress = leaderChanged.getLeader().protocol() + "://"+ leaderChanged.getLeader().hostPort();
			if (serviceAddress.equals(RouterService.clusterConfig.getClusterNode())) {// 只有是本机ip变为主节点才修改内存库
				JedisGlobal.DOC.setRootKeyValue(ClusterParameter.COLLECTION_SYSTEM_LEADER, serviceAddress);
				log.info("采集系统主节点变更: {}", leaderChanged.leader());
			}
		} else if (message instanceof UnreachableMember) {
			UnreachableMember mUnreachable = (UnreachableMember) message;
			log.info("节点不可用: {}", mUnreachable.member());
		} else if (message instanceof ReachableMember) {
			ReachableMember reachable = (ReachableMember) message;
			log.info("节点可用: {}", reachable.member());
		} else if (message instanceof MemberRemoved) {
			MemberRemoved mRemoved = (MemberRemoved) message;
			String serviceAddress = mRemoved.member().address().toString();
			if (mRemoved.member().hasRole(ClusterParameter.TIME_TASK_ROLE)) {
				RouterService.timeTaskMap.remove(serviceAddress);
				JedisGlobal.DOC.delRedisValue(ClusterParameter.TIME_TASK_MAP, serviceAddress);
				log.info("定时任务服务节点删除: {}", mRemoved.member());
				as.tell(new TimeTaskCluster(serviceAddress,"REMOVE"), getSelf());
			} else if (mRemoved.member().hasRole(ClusterParameter.FRONT_MACHINE_ROLE)) {
				JedisGlobal.DOC.delRedisValue(ClusterParameter.FRONT_MACHINE_MAP, serviceAddress);
				log.info("前置机服务节点删除: {}", mRemoved.member());
				as.tell(new FrontMachineCluster(serviceAddress,"REMOVE"), getSelf());
			}else if(mRemoved.member().hasRole(ClusterParameter.TRM_CALL_ROLE)){
				JedisGlobal.DOC.delRedisValue(ClusterParameter.RPC_SERVICE, CallServiceInterface.class.getName());
				log.info("召测服务节点删除: {}", mRemoved.member());
			}
			log.info("节点删除: {}", mRemoved.member());
		} else {
			unhandled(message);
		}
	}	

}