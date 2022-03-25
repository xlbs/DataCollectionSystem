package com.xielbs.rpcclient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.xielbs.datamodle.RpcEvent;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;

public class BeanProxy implements InvocationHandler {

	private ActorSelection rpc;

	private Class<?> clz;

	private int timeOutSeconds = 30;

	public <T> T proxy(ActorSelection rpc, Class<T> clz) {
		this.rpc = rpc;
		this.clz = clz;
		Class<?>[] clzz = new Class<?>[] { clz };
		return (T) Proxy.newProxyInstance(clz.getClassLoader(), clzz, this);
	}

	public <T> T proxy(ActorSelection rpc, Class<T> clz, int timeOutSeconds) {
		this.rpc = rpc;
		this.clz = clz;
		this.timeOutSeconds = timeOutSeconds;
		Class<?>[] clzz = new Class<?>[] { clz };
		return (T) Proxy.newProxyInstance(clz.getClassLoader(), clzz, this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)throws Throwable {
		Object result = null;
		ActorRef rpcClientServer = AkkaClient.getInstance().getSystem()
				.actorOf(
						Props.create(ClientActor.class, rpc).withDispatcher(
								"main-dispatcher"),UUID.randomUUID().toString());
		try{
			RpcEvent.CallMethod callMethod = new RpcEvent.CallMethod(method.getName(),method.getParameterTypes(), args, clz.getName());
			Future<Object> future = Patterns.ask(rpcClientServer,callMethod, new Timeout(Duration.create(timeOutSeconds, TimeUnit.SECONDS)));
			Object o = Await.result(future, Duration.create(timeOutSeconds, TimeUnit.SECONDS));
			result = o;
		}catch(Exception e){
			e.printStackTrace();
			throw  new RuntimeException("查询超时");
		}finally{
			AkkaClient.getInstance().stopActor(rpcClientServer);
		}
		if (result instanceof Throwable) {
			if (result instanceof NullPointerException) {
				NullPointerException ne = (NullPointerException) result;
				if (ne.getMessage().equals("noResult")) {
					result = null;
				} else {
					throw (Throwable) result;
				}
			} else {
				throw (Throwable) result;
			}
		}
		return result;
	}

}
