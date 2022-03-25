package com.xielbs.rpcclient;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.xielbs.datamodle.RpcEvent;


public class ClientActor extends UntypedActor {

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private ActorSelection rpc;

	public ClientActor(ActorSelection rpc) {
		this.rpc = rpc;
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof RpcEvent.CallMethod) {
			rpc.tell(message, getSender());

			RpcEvent.CallMethod obj = (RpcEvent.CallMethod)message;
			String fromPath = getSender().path().toString();
			String toPath = rpc.anchorPath().toString();
			toPath = toPath.substring(0, toPath.length()-1);
			toPath = toPath + rpc.pathString();

			log.debug("消息流【"+fromPath+" ==> "+toPath+"】，执行方法："+obj.getMethodName());
		}
	}
}
