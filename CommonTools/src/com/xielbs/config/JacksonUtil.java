package com.xielbs.config;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonUtil {

	static ObjectMapper  objectMapper;
	public static ObjectMapper getObjMapper(){
		if(objectMapper == null){
			objectMapper = new ObjectMapper();
		}
		return objectMapper;
	}
	
	public static <T> List<T> queryJsonList(String json) throws Exception{
		return getObjMapper().readValue(json, List.class);
	}
	
	public static Map queryJsonMap(String json) throws Exception {
		return  getObjMapper().readValue(json, Map.class);
	}
	
		
	
	
}
