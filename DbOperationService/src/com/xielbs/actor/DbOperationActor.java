package com.xielbs.actor;

import java.lang.reflect.Method;
import java.util.Map;

import akka.actor.UntypedActor;

import com.xielbs.datamodle.RpcEvent;
import com.xielbs.utils.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbOperationActor extends UntypedActor {

	Logger log = LoggerFactory.getLogger(DbOperationActor.class);

	private Map<String, Object> proxyBeans;

	public DbOperationActor(Map<String, Object> proxyBeans) {
		this.proxyBeans = proxyBeans;
	}

	@Override
	public void onReceive(Object message) throws Exception {

		if (message instanceof RpcEvent.CallMethod) {
			RpcEvent.CallMethod obj = (RpcEvent.CallMethod) message;
			Object bean = proxyBeans.get(obj.getBeanName());
			Object[] params = obj.getParams();
			Class<?>[] paramsTypes = obj.getParamTypes();
			try {
				Method method = bean.getClass().getMethod(obj.getMethodName(), paramsTypes);
				Object o = method.invoke(bean, params);
				if(o instanceof java.lang.Long && obj.getMethodName().equals("get")){
					o = NumberUtils.toInteger(o);
				}
				if (o != null) {
					getSender().tell(o, getSelf());
				} else {
					getSender().tell(new NullPointerException("noResult"),getSelf());
				}
			} catch (Exception e) {
				e.printStackTrace();
				getSender().tell(e, getSelf());
			}

			String fromPath = getSelf().path().toString();
			String toPath = getSender().path().toString();

			log.debug("消息流【"+fromPath+" ==> "+toPath+"】，执行方法："+obj.getMethodName());
		}
	}
}
