package com.xielbs.init;

import java.util.List;
import java.util.Map;

import com.xielbs.config.ClusterParameter;
import com.xielbs.serviceinterface.I_SQL_DataOperat;
import com.xielbs.jedis.util.JedisGlobal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitGeneratorMp {

	private static final Logger log = LoggerFactory.getLogger(InitGeneratorMp.class);

	public static void deleteGenMp(I_SQL_DataOperat dao, String devId) {
		try {
			// 查出原来的信息
			String mpId = JedisGlobal.DOC.queryJedisObj(ClusterParameter.MP_GENMP_INFO, "@" + devId);
			if (mpId != null) {
				JedisGlobal.DOC.delRedisValue(ClusterParameter.MP_GENMP_INFO, "@" + devId);
				JedisGlobal.DOC.delRedisValue(ClusterParameter.MP_GENMP_INFO, mpId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化发电机对应表计
	 * @param dao
	 * @param devId
	 */
	public  static void initGenMpWithGen(I_SQL_DataOperat dao, String devId) {
		try {
			if(devId==null) {
				JedisGlobal.DOC.delRootKey(ClusterParameter.MP_GENMP_INFO);
			} else {
				deleteGenMp(dao, devId);
			}

			StringBuffer sb = new StringBuffer();
			sb.append(" select  mp.`mp_meter_id`,e.`equi_id` from `e_equipment_proper` e, c_mp_meter mp where mp.`mp_meter_id` = e.`equi_pp_val` and e.`equi_pp_no`='fdj_zndb_ycd' ");
			if (devId != null) {
				sb.append(" and  e. `equi_id` = " + devId);
			}
			List<Map> list = dao.querySql(sb.toString());
			for (Map map : list) {
				String strDev = map.get("equi_id").toString();
				String mpId = map.get("mp_meter_id").toString();
				JedisGlobal.DOC.updateJedisObj(ClusterParameter.MP_GENMP_INFO,	"@" +strDev, mpId);
				JedisGlobal.DOC.updateJedisObj(ClusterParameter.MP_GENMP_INFO, mpId,strDev);
			}
			log.info("初始化【发电机对应表计】["+ClusterParameter.MP_GENMP_INFO+"]信息到内存库成功！");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("初始化【发电机对应表计】信息到内存库出现异常：", e);
		}

	}

  
}
