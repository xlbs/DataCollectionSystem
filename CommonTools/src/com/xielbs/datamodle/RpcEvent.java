package com.xielbs.datamodle;

import java.io.Serializable;

public interface RpcEvent {

	public static class CallMethod implements Serializable{

		private String methodName;

		private Class[] paramTypes;


		private Object[] params;

		public CallMethod(String methodName, Object[] params) {
			super();
			this.methodName = methodName;
			this.params = params;
		}

		public CallMethod(String methodName,Class[] paramTypes, Object[] params) {
			super();
			this.methodName = methodName;
			this.paramTypes = paramTypes;
			this.params = params;
		}

		private String beanName;

		public CallMethod(String methodName,Class[] paramTypes, Object[] params, String beanName) {
			super();
			this.methodName = methodName;
			this.paramTypes = paramTypes;
			this.params = params;
			this.beanName = beanName;
		}

//		public CallMethod(String methodName, Object[] params, String beanName) {
//			super();
//			this.methodName = methodName;
//			this.params = params;
//			this.beanName = beanName;
//		}

		public String getMethodName() {
			return methodName;
		}

		public void setMethodName(String methodName) {
			this.methodName = methodName;
		}

		public Class[] getParamTypes() {
			return paramTypes;
		}

		public void setParamTypes(Class[] paramTypes) {
			this.paramTypes = paramTypes;
		}

		public Object[] getParams() {
			return params;
		}

		public void setParams(Object[] params) {
			this.params = params;
		}

		public String getBeanName() {
			return beanName;
		}

		public void setBeanName(String beanName) {
			this.beanName = beanName;
		}

	}
}
