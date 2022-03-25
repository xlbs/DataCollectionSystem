package com.xielbs.init;

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

/**
 * 
 * 功能说明 初始化任务模板
 * @author hgl
 * 时间:2016年12月21日
 *
 */
public class InitTaskTmp {

	private static final Logger log = LoggerFactory.getLogger(InitTaskTmp.class);

	/**
	 * 初始化任务模板到内存库
	 * @param dao
	 * @param tmpId
	 */
	public static void initTaskTmp(I_SQL_DataOperat dao, String... tmpId){
		try{
			if(tmpId != null){
				JedisGlobal.DOC.delRedisValue(ClusterParameter.DOC_TASK_TMP, tmpId);
			}else{
				JedisGlobal.DOC.delRootKey(ClusterParameter.DOC_TASK_TMP);
			}
		}catch(Exception e){
			log.error("删除内存库的【任务模板】出现异常：", e);
		}
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT  tmp.* FROM  c_task_tmp tmp ");
		if(tmpId != null){
			sb.append(" where tmp.tmp_id in (");
			for(String str : tmpId){
				sb.append(str+",");
			}
			sb.append("-1)");
		}
		List<Map> list;
		try {
			list = dao.querySql(sb.toString());
		} catch (Exception e) {
			log.error("查询【任务模板】信息出现异常：", e);
			log.error("错误SQL："+sb.toString());
			return;
		}
		if(list == null || list.isEmpty()){
			log.warn("未查询到【任务模板】信息！");
			return;
		}
		for(Map map : list){
			Map<String,String> map1 = new LinkedHashMap<String, String>();
			for(Object obj : map.keySet()){
				map1.put(obj.toString(), map.get(obj) == null ? "":map.get(obj).toString());
			}
			try {
				JedisGlobal.DOC.updateJedisObj(ClusterParameter.DOC_TASK_TMP, map.get("tmp_id")+"", JSONUtils.serialize(map1));
			} catch (JSONException e) {
				log.error("【任务模板】序列化出行异常：", e);
				log.error("【任务模板】数据："+map1);
			} catch (Exception e) {
				log.error("保存【任务模板】信息到内存库出现异常：", e);
			}
		}
		log.info("初始化【任务模板】["+ClusterParameter.DOC_TASK_TMP+"]信息到内存库成功！");
	}
	
}
