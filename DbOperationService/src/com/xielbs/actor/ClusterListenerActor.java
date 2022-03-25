package com.xielbs.actor;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.xielbs.config.ClusterParameter;
import com.xielbs.jedis.util.JedisGlobal;

import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.LeaderChanged;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.UnreachableMember;
import com.xielbs.service.DbOperationService;

/**
 * 集群监听
 * 
 * @author Administrator
 *
 */
public class ClusterListenerActor extends UntypedActor {

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	Cluster cluster = Cluster.get(getContext().system());

	@Override
	public void preStart() {
		cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(),
				MemberEvent.class, UnreachableMember.class, LeaderChanged.class);
	}

	@Override
	public void postStop() {
		cluster.unsubscribe(getSelf());
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof MemberUp) {
			MemberUp mUp = (MemberUp) message;
			if ( mUp.member().address().hostPort().contains("localhost")
					|| mUp.member().address().hostPort().contains("127.0.0.1")
			) {
				log.error("节点ip异常: {}", mUp.member());
				cluster.down(mUp.member().address());
				return;
			}
			log.info("节点加入: {}", mUp.member());
		} else if (message instanceof UnreachableMember) {
			UnreachableMember mUnreachable = (UnreachableMember) message;
			cluster.down(mUnreachable.member().address());
			log.info("节点不可用: {}", mUnreachable.member());
		} else if (message instanceof LeaderChanged) {
			LeaderChanged leaderChanged = (LeaderChanged) message;
			String serviceAddress = leaderChanged.getLeader().protocol() + "://"+ leaderChanged.getLeader().hostPort();
			if (serviceAddress.equals(DbOperationService.getNodeIp())) {// 只有是本机ip变为主节点才修改内存库
				JedisGlobal.DOC.setRootKeyValue(ClusterParameter.DATABASE_SYSTEM_LEADER, serviceAddress);
				log.info("主节点变更: {}", leaderChanged.leader());
			}
		} else if (message instanceof MemberRemoved) {
			MemberRemoved mRemoved = (MemberRemoved) message;
			cluster.join(mRemoved.member().address());
			log.info("节点删除: {}", mRemoved.member());
		} else if (message instanceof MemberEvent) {

		} else {
			unhandled(message);
		}
	}
}