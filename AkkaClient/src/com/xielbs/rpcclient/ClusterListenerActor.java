package com.xielbs.rpcclient;

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
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * 客户端集群监听
 * @author Administrator
 *
 */
public class ClusterListenerActor extends UntypedActor {


	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	Cluster cluster = Cluster.get(getContext().system());

	@Override
	public void preStart() {
		cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(),
				MemberEvent.class, UnreachableMember.class,LeaderChanged.class);
	}

	@Override
	public void postStop() {
		cluster.unsubscribe(getSelf());
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof MemberUp) {
			MemberUp mUp = (MemberUp) message;
			log.info("Member is Up: {}", mUp.member());
		} else if (message instanceof UnreachableMember) {
			UnreachableMember mUnreachable = (UnreachableMember) message;
			log.info("Member detected as unreachable: {}",
					mUnreachable.member());
			cluster.down(mUnreachable.member().address());
		} else if (message instanceof LeaderChanged) {
			LeaderChanged lChanged = (LeaderChanged) message;
			log.info("Member is LeaderChanged: {}", lChanged.leader());
			String serviceAddress = lChanged.getLeader().protocol() + "://"+lChanged.getLeader().hostPort();
			if(serviceAddress.equals(AkkaClient.getInstance().getNodeIp())){//只有是本机ip变为主节点才修改内存库
				JedisGlobal.DOC.setRootKeyValue(ClusterParameter.CLIENT_SYSTEM_LEADER, serviceAddress);
			}
		}else if (message instanceof MemberRemoved) {
			MemberRemoved mRemoved = (MemberRemoved) message;
			cluster.join(mRemoved.member().address());
			log.info("Member is Removed: {}", mRemoved.member());
		} else if (message instanceof MemberEvent) {

		} else {
			unhandled(message);
		}
	}


}