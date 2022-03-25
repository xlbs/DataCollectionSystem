package com.xielbs.init;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.xielbs.config.ClusterParameter;
import com.xielbs.serviceinterface.I_SQL_DataOperat;
import com.xielbs.jedis.util.JedisGlobal;
import com.xielbs.utils.JSONException;
import com.xielbs.utils.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InitDtiGroup {

	private static final Logger log = LoggerFactory.getLogger(InitDtiGroup.class);

	/**
	 * 初始化编码组到内存库
	 * @param dao
	 * @param groupId
	 */
	public static void initDtiGroup(I_SQL_DataOperat dao, String... groupId){
		try{
			if(groupId != null){
				JedisGlobal.DOC.delRedisValue(ClusterParameter.DOC_DTI_GROUP, groupId);
			}else{
				JedisGlobal.DOC.delRootKey(ClusterParameter.DOC_DTI_GROUP);
			}
		}catch(Exception e){
			log.error("删除内存库的【编码组】出现异常：", e);
		}
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT dtigroup.*,dtiitem.`dti_code`,dtiitem.`sort_no` FROM  ");
		sb.append(" c_dtigroup dtigroup LEFT JOIN c_dtigroupitem dtiitem ");
		sb.append(" ON dtigroup.`dti_grpid` = dtiitem.`dti_grpid`");
		if(groupId != null){
			sb.append(" where dtigroup.dti_grpid in ("+groupId);
			for(String str : groupId){
				sb.append(str+",");
			}
			sb.append("-1)");
		}
		List<Map> list;
		try {
			list = dao.querySql(sb.toString());
		} catch (Exception e) {
			log.error("查询【编码组】信息出现异常：", e);
			log.error("错误SQL："+sb.toString());
			return;
		}
		if(list == null || list.isEmpty()){
			log.warn("未查询到【编码组】信息！");
			return;
		}
		Map<Integer,List<Map<String,String>>> valueMap = new LinkedHashMap<Integer, List<Map<String,String>>>();
		for(Map map : list){
			List<Map<String,String>> listmap = valueMap.get(Integer.parseInt(map.get("dti_grpid")+""));
			if(listmap == null){
				listmap = new ArrayList<Map<String,String>>();
			}
			Map<String,String> map2 = new LinkedHashMap<String, String>();
			for(Object obj : map.keySet()){
				map2.put(obj.toString(), map.get(obj)+"");
			}
			listmap.add(map2);
			valueMap.put(Integer.parseInt(map.get("dti_grpid")+""), listmap);
		}
		for(Integer key : valueMap.keySet()){
			try {
				JedisGlobal.DOC.updateJedisObj(ClusterParameter.DOC_DTI_GROUP, key+"", JSONUtils.serialize(valueMap.get(key)));
			} catch (JSONException e) {
				log.error("【编码组】["+key+"]序列化出行异常：", e);
				log.error("【编码组】["+key+"]数据："+valueMap.get(key));
				return;
			} catch (Exception e) {
				log.error("保存【编码组】["+key+"]信息到内存库出现异常：", e);
				return;
			}
		}
		log.info("初始化【编码组】["+ClusterParameter.DOC_DTI_GROUP+"]信息到内存库成功！");
	}
	
}
