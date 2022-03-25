package com.xielbs.init;

import java.util.List;
import java.util.Map;

import com.xielbs.serviceinterface.I_SQL_DataOperat;
import com.xielbs.utils.JSONUtils;
import com.xielbs.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xielbs.config.ClusterParameter;
import com.xielbs.config.JacksonUtil;
import com.xielbs.jedis.util.JedisGlobal;

public class DevUpdate {

	static Logger log = LoggerFactory.getLogger(InitCpRedis.class);

	public static void updateMpPropertyRedis(I_SQL_DataOperat dao, int  equiId) {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append(
					"SELECT mp.`mp_meter_id`,ep.`equi_pp_no`,ep.`equi_pp_val` FROM c_mp_meter mp  INNER  JOIN e_equipment e  ON e.equi_no=mp.`equi_no`  INNER JOIN e_equipment_proper ep ON e.`equi_id` = ep.`equi_id`");
			sb.append(" and e.equi_id = " + equiId);

			List<Map> listpp = dao.querySql(sb.toString());
			if(listpp==null ||listpp.size()==0)
				return;
			String mpId = null;
			String pt = null;
			String ct = null;
			String tf = null;
			String ratedP2 = null;
			String ratedP1 = null;
			String ratedC1 = null;
			String ratedC2 = null;
			String openMin =null;
			String openMax=null;
			String insHeightSw =null;
             
			for (Map map : listpp) {
		 		if(map.get("mp_meter_id")==null)
                       return;						
		 		mpId = StringUtils.toString(map.get("mp_meter_id")).trim();
				String ppno = StringUtils.toString(map.get("equi_pp_no")).trim();
				String ppVal = StringUtils.toString(map.get("equi_pp_val")).trim();
				if (ppno.equalsIgnoreCase("jlyb_db_pt"))
					pt = ppVal;
				else if (ppno.equalsIgnoreCase("jlyb_db_ct"))
					ct = ppVal;
				else if (ppno.equalsIgnoreCase("jlyb_db_tf"))
					tf = ppVal;
				else if (ppno.equalsIgnoreCase("jlyb_db_eddy"))
					ratedP2 = ppVal;
				else if (ppno.equalsIgnoreCase("jlyb_db_eddl"))
					ratedC2 = ppVal;
				else if (ppno.equalsIgnoreCase("jlyb_db_dl"))
					ratedC1 = ppVal;
				else if (ppno.equalsIgnoreCase("jlyb_db_dy"))
					ratedP1 = ppVal;
				else if (ppno.equalsIgnoreCase("open_min"))
					openMin= ppVal;
				else if (ppno.equalsIgnoreCase("open_max"))
					openMax =ppVal;
				else if (ppno.equalsIgnoreCase("ins_height_sw"))
					insHeightSw =ppVal;

			}

			if (pt == null || pt.isEmpty())
				pt = "1";
			if (ct == null || ct.isEmpty())
				ct = "1";

			if (ratedC2 == null || ratedC2.isEmpty()) {
				if (ratedC1 == null || ratedC1.isEmpty())
					ratedC2 = "-1";
				else
					ratedC2 = String.valueOf(Double.valueOf(ratedC1) / Double.valueOf(ct));
			}
			if (ratedP2 == null || ratedP2.isEmpty()) {
				if (ratedP1 == null || ratedP1.isEmpty())
					ratedP2 = "-1";
				else
					ratedP2 = String.valueOf(Double.valueOf(ratedP1) / Double.valueOf(pt));
			}

			String mpInfoStr = JedisGlobal.DOC.queryJedisObj(ClusterParameter.MP_METER_INFO, mpId);
			if(mpInfoStr!=null)  //否则意味这此测量点还没刷新到实时库，通过CP；
			{
				Map mpDetail = JacksonUtil.queryJsonMap(mpInfoStr);
				mpDetail.put("pt_ratio", pt);
				mpDetail.put("ct_ratio", ct);
				mpDetail.put("measure_ratio", tf);

				mpDetail.put("ratedP1", ratedP1);
				mpDetail.put("ratedP2", ratedP2);
				mpDetail.put("ratedC1", ratedC1);
				mpDetail.put("ratedC2", ratedC2);
				if (openMin != null)
					mpDetail.put("open_min", openMin);

				if (openMax != null)
					mpDetail.put("open_max", openMax);		

				if (insHeightSw != null)
					mpDetail.put("ins_height_sw", insHeightSw);		

			JedisGlobal.DOC.updateJedisObj(ClusterParameter.MP_METER_INFO, mpId, JSONUtils.serialize(mpDetail));
			System.out.print("update meter "+ mpId+'\n');
			}
		} catch (Exception e) {
			log.error("设备点" +equiId + "保存内存库信息失败,列表信息：,原因:" + e.getMessage());
		}
	}
	public static void updateGeneratorMpRedis(I_SQL_DataOperat dao,  int  equiId) {
		InitGeneratorMp.initGenMpWithGen(dao,equiId+"");
	}

}
