package com.xielbs.init;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xielbs.config.ClusterParameter;
import com.xielbs.config.JacksonUtil;
import com.xielbs.serviceinterface.I_SQL_DataOperat;
import com.xielbs.jedis.util.JedisGlobal;
import com.xielbs.utils.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitTele {

	private static final Logger log = LoggerFactory.getLogger(InitTele.class);

	/**
	 * 初始化遥信点信息
	 * @param dao
	 */
	public static void initAllTele(I_SQL_DataOperat dao) {
		try {
			JedisGlobal.DOC.delRootKey(ClusterParameter.MP_TELE_INFO);
			initTeleWithPOb(dao, null, null);
			log.info("初始化【遥信点信息】["+ClusterParameter.MP_TELE_INFO+"]信息到内存库成功！");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("初始化【遥信点信息】信息到内存库出现异常：", e);
		}
	}


	/**
	 * 初始化遥信状态项字典表
	 * @param dao
	 */
	public static void initTeleDIctItem(I_SQL_DataOperat dao) {
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT * from s_dict_item di  where di.`type_no`  in ('182','183','184','185','186','199','200','201') ");
		List<Map> list;
		try {
			list = dao.querySql(sb.toString());
		}catch (Exception e){
			log.error("查询【遥信状态项字典表】信息出现异常：", e);
			log.error("错误SQL："+sb.toString());
			return;
		}
		if(list == null || list.isEmpty()){
			log.warn("未查询到【遥信状态项字典表】信息！");
			return;
		}
		try {
			Map map = new HashMap<String, String>();
			for (Map teleInfoMap : list) {
				String key = teleInfoMap.get("type_no") + "@" + teleInfoMap.get("item_no");
				map.put(key, teleInfoMap.get("item_name"));
			}
			JedisGlobal.DOC.updateMapToRedis(ClusterParameter.YX_DICT_ITEM, map);
			log.info("初始化【遥信状态项字典表】["+ClusterParameter.YX_DICT_ITEM+"]信息到内存库成功！");
		} catch (Exception e) {
			log.error("初始化【遥信状态项字典表】信息到内存库出现异常：", e);
		}
	}


	private static void initTeleWithPOb(I_SQL_DataOperat dao, String pType, String pObId) {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT mp.cp_id, tele.mp_tele_id,tele.mp_tele_type ,tele.property_id,tele.p_object_type, tele.p_object_id ,tele.inner_id,td.`data_item`  FROM c_mp_tele  tele , c_mp_meter  mp ,tele_data_no  td ");
			sb.append(" where tele.`p_object_type` ='02' and mp.`mp_meter_id` = tele.`p_object_id` and  mp.`protocol_type` =td.`protocol_type` and tele.`mp_tele_type` = td.`object_type` and td.`inner_id`  = tele.`inner_id`");
			if (pType != null) {
				sb.append(" and  tele. `p_object_type` = " + pType + " and tele. `p_object_id` = " + pObId);
			}
			List<Map> list = dao.querySql(sb.toString());
			Map<String, Map> vPObKeyMap = new HashMap<String, Map>();
			for (Map teleInfoMap : list) {
				String key = teleInfoMap.get("p_object_type") + "@" + teleInfoMap.get("p_object_id");
				Map lMap = vPObKeyMap.get(key);
				if (lMap == null) {
					lMap = new HashMap<>();
					vPObKeyMap.put(key, lMap);
				}
				lMap.put(teleInfoMap.get("inner_id"), teleInfoMap.get("mp_tele_id"));

				JedisGlobal.DOC.updateJedisObj(ClusterParameter.MP_TELE_INFO, teleInfoMap.get("mp_tele_id").toString(), JSONUtils.serialize(teleInfoMap));
			}
			for (String key : vPObKeyMap.keySet()) {
				JedisGlobal.DOC.updateJedisObj(ClusterParameter.MP_TELE_INFO, key, JSONUtils.serialize(vPObKeyMap.get(key)));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void deleteTele(I_SQL_DataOperat dao, Integer teleId) {
		try {
			// 查出原来的遥信信息
			String teleInfoStr = JedisGlobal.DOC.queryJedisObj(ClusterParameter.MP_TELE_INFO, String.valueOf(teleId));
			// 删除tele;
			JedisGlobal.DOC.delRedisValue(ClusterParameter.MP_TELE_INFO, String.valueOf(teleId));
			if (teleInfoStr != null) {
				// 删除原来的MP;
				Map teleInfoMap = JacksonUtil.queryJsonMap(teleInfoStr);
				String pType = teleInfoMap.get("p_object_type").toString();
				String pId = teleInfoMap.get("p_object_id").toString();

				JedisGlobal.DOC.delRedisValue(ClusterParameter.MP_TELE_INFO, pType + "@" + pId);
				// 重新读原来的MP
				initTeleWithPOb(dao, pType, pId);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}




	public static void initTele(I_SQL_DataOperat dao, int teleId) {
		try {
			// 先删除
			deleteTele(dao, teleId);
			// 再重新读
			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT  p_object_type, p_object_id   FROM c_mp_tele  where  mp_tele_id =  " + teleId);
			List<Map> list = dao.querySql(sb.toString());
			for (Map map : list) {
				Object pObType = map.get("p_object_type");
				Object pObId = map.get("p_object_id");
				if (pObType != null)
					initTeleWithPOb(dao, pObType.toString(), pObId.toString());

				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
