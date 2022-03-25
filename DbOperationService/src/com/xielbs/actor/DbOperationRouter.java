package com.xielbs.actor;

import java.util.*;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import com.xielbs.config.ClusterParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbOperationRouter extends UntypedActor {

	Logger log = LoggerFactory.getLogger(DbOperationRouter.class);

	private Map<String, Object> proxyBeans;

	private Router router;

	public DbOperationRouter(Map<Class<?>, Object> beans) {
		proxyBeans = new HashMap<>();
		for (Iterator<Class<?>> iterator = beans.keySet().iterator(); iterator.hasNext();) {
			Class<?> serviceInterface = iterator.next();
			proxyBeans.put(serviceInterface.getName(), beans.get(serviceInterface));
		}
		List<Routee> routeList = new ArrayList<Routee>();
		for (int i = 0; i < 100; i++) {
			routeList.add(new ActorRefRoutee(
					this.getContext().system()
							.actorOf(Props.create(DbOperationActor.class, proxyBeans)
									.withDispatcher("other-dispatcher"), ClusterParameter.DATABASE_OPERATION_ACTOR + i
							)
					)
			);
		}
		router = new Router(new RoundRobinRoutingLogic(), routeList);
	}

	@Override
	public void onReceive(Object message) throws Exception {
		log.debug("数据库操作路由接收到来着【"+getSender().path().toString()+"】的消息，即将通过路由发送到数据库操作DbOperationActor");
		router.route(message, getSender());
	}

}
