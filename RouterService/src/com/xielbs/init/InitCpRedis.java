package com.xielbs.init;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.xielbs.exception.DataOperationException;
import com.xielbs.serviceinterface.I_SQL_DataOperat;
import com.xielbs.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xielbs.config.ClusterParameter;
import com.xielbs.config.JacksonUtil;
import com.xielbs.jedis.util.JedisGlobal;


public class InitCpRedis {

	private final static Logger log = LoggerFactory.getLogger(InitCpRedis.class);

	/**
	 * 把终端信息以及任务存储到内存库
	 * 
	 * @param dao
	 * @param noArr
	 */
	public static void InitRcpRedis(I_SQL_DataOperat dao, Integer[] noArr) {
		Map<String, String> redisTempMap = null;
		try {
			redisTempMap = JedisGlobal.DOC.queryJedisMapAllObj("r_task_tmp");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		if (dao == null) {
			log.error("初始化终端ID为" + noArr[0] + "信息失败,dao未赋值,请查看");
			return;
		}
		// 重新查询
		StringBuffer sb = new StringBuffer();
		sb.append("select cp.`cp_id`,cp.`cp_name`,cp.`protocol_type`,cp.`cp_addr`,cp.trm_id from c_cp cp");
		if (noArr.length == 1) {
			sb.append(" WHERE cp.`cp_id` = " + noArr[0]);
		} else {
			sb.append(" WHERE cp.`cp_id` > " + noArr[0] + " and cp.`cp_id` <= " + noArr[1]);
		}
		// sb.append(" WHERE cp.`cp_id` >=24 and cp.cp_id < 29");
		List<Map> allCp = null;
		try {
			allCp = dao.querySql(sb.toString());
		} catch (DataOperationException e1) {
			log.error("查询终端ID大于" + noArr[0] + "，小于" + noArr[1] + "的终端信息失败,原因:" + e1.getMessage());
		}

		Map<Integer, List<Map>> rcpTaskList = new HashMap<Integer, List<Map>>();// 终端ID
																				// 任务列表
		Map<Integer, Map> rcpInfoMap = new HashMap<Integer, Map>();// 采集点信息

		for (Map cp : allCp) {
			try {
				InitCpRedis.delCpRedis(dao, NumberUtils.toInteger(cp.get("cp_id")));
				JedisGlobal.DOC.updateJedisObj(ClusterParameter.CP_PROTOCOL_ADDR,cp.get("protocol_type") + "@" + cp.get("cp_addr"), cp.get("cp_id").toString());
			} catch (Exception e) {
				log.error("终端" + cp.get("cp_id") + "保存内存库地址信息失败,,原因:" + e.getMessage());
			}
			rcpInfoMap.put((Integer) cp.get("cp_id"), cp);
		}

		for (Integer cpId : rcpInfoMap.keySet()) {
			try {
				Map mapCp = rcpInfoMap.get(cpId);
				JedisGlobal.DOC.updateJedisObj(ClusterParameter.cp_info + cpId.toString(), "info", JSONUtils.serialize(mapCp));
			} catch (JSONException e) {
				log.error("终端" + cpId + "序列化终端信息集合失败");
			} catch (Exception e) {
				log.error("终端" + cpId + "序列化终端信息集合失败");
			}
		}
		sb = new StringBuffer();
		sb.append(" SELECT e.`asset_state` run_status,e.equi_id, mp.*,cp.cp_addr");
		sb.append(
				" FROM c_mp_meter mp left join c_cp cp on cp.cp_id=mp.cp_id left join e_equipment e ON e.`equi_no` = mp.`equi_no` ");
		if (noArr.length == 1) {
			sb.append(" WHERE mp.`cp_id` = " + noArr[0]);
		} else {
			sb.append(" WHERE mp.`cp_id` > " + noArr[0] + " and mp.`cp_id` <= " + noArr[1]);
		}
		List<Map> allMp = null;
		try {
			allMp = dao.querySql(sb.toString());
		} catch (DataOperationException e1) {
			log.error("查询终端ID大于" + noArr[0] + "，小于" + noArr[1] + "的遥测点信息失败,原因:" + e1.getMessage());
		}
		// 初始化变比数据到内存库
		List<Map> listpp = null;
		Map<Integer, List<Map>> allMpCpMap = new HashMap<Integer, List<Map>>(); // 终端对应遥测点信息包含ID和协议类型
		Map<Integer, Map<String, String>> allMpParamsMap = new HashMap<Integer, Map<String, String>>();
		sb = new StringBuffer();
		sb.append(
				"SELECT DISTINCT(mp.`mp_meter_id`),ep.`equi_pp_no`,ep.`equi_pp_val` FROM c_mp_meter mp  LEFT JOIN e_equipment e  ON e.equi_no=mp.`equi_no`  LEFT JOIN e_equipment_proper ep ON e.`equi_id` = ep.`equi_id` ");
		if (noArr.length == 1) {
			sb.append(" where  mp.`cp_id` = " + noArr[0]);
		} else {
			sb.append(" where mp.`cp_id` > " + noArr[0] + " and mp.`cp_id` <= " + noArr[1]);
		}
		try {
			listpp = dao.querySql(sb.toString());
			for (Map map : listpp) {
				Map<String, String> mpMap = allMpParamsMap.get(NumberUtils.toInteger(map.get("mp_meter_id")));
				if (mpMap == null) {
					mpMap = new HashMap<String, String>();
					allMpParamsMap.put(NumberUtils.toInteger(map.get("mp_meter_id")), mpMap);
				}
				String ppno = StringUtils.toString(map.get("equi_pp_no")).trim();
				String ppVal = StringUtils.toString(map.get("equi_pp_val")).trim();
				if (ppno.equalsIgnoreCase("jlyb_db_pt"))
					mpMap.put("pt_ratio", ppVal);
				else if (ppno.equalsIgnoreCase("jlyb_db_ct"))
					mpMap.put("ct_ratio", ppVal);
				else if (ppno.equalsIgnoreCase("jlyb_db_tf"))
					mpMap.put("measure_ratio", ppVal);
				else if (ppno.equalsIgnoreCase("jlyb_db_eddy"))
					mpMap.put("ratedP2", ppVal);
				else if (ppno.equalsIgnoreCase("jlyb_db_eddl"))
					mpMap.put("ratedC2", ppVal);
				else if (ppno.equalsIgnoreCase("jlyb_db_dl"))
					mpMap.put("ratedC1", ppVal);
				else if (ppno.equalsIgnoreCase("jlyb_db_dy"))
					mpMap.put("ratedP1", ppVal);
				else if (ppno.equalsIgnoreCase("open_min"))
					mpMap.put("open_min", ppVal);
				else if (ppno.equalsIgnoreCase("open_max"))
					mpMap.put("open_max", ppVal);
				else if (ppno.equalsIgnoreCase("ins_height_sw"))
					mpMap.put("ins_height_sw", ppVal);
				

			}
		} catch (DataOperationException e1) {
			e1.printStackTrace();
		}

		Map<Integer, Map> rmpValueMap = new HashMap<Integer, Map>();// <遥测点ID，遥测点信息>
		Map<String, String> mpDetail = null;
		List<Map> mpsOneCp = null;
		for (Map mp : allMp) {
			rmpValueMap.put((Integer) mp.get("mp_meter_id"), mp);
			//
			mpsOneCp = allMpCpMap.get(NumberUtils.toInteger(mp.get("cp_id")));
			if (mpsOneCp == null) {
				mpsOneCp = new ArrayList<Map>();
				allMpCpMap.put(NumberUtils.toInteger(mp.get("cp_id")), mpsOneCp);
			}
			Map<String, String> m = new HashMap<String, String>();
			m.put("mp_meter_id", mp.get("mp_meter_id") + "");
			m.put("protocol_type", mp.get("protocol_type") + "");
			m.put("inner_id", mp.get("inner_id") + "");
			m.put("equi_id", mp.get("equi_id") + "");
			mpsOneCp.add(m);
			mpDetail = new HashMap<String, String>();
			for (Object obj : mp.keySet()) {
				if (mp.get(obj) == null) {
					continue;
				}
				mpDetail.put(obj.toString(), mp.get(obj) == null ? null : mp.get(obj) + "");
			}
			Map<String, String> mpParamMap = allMpParamsMap.get(NumberUtils.toInteger(mp.get("mp_meter_id")));
			if (mpParamMap != null) {
				String pt = mpParamMap.get("pt_ratio");
				if (pt == null || pt.isEmpty())
					pt = "1";
				String ct = mpParamMap.get("ct_ratio");
				if (ct == null || ct.isEmpty())
					ct = "1";

				String tf = String.valueOf(Double.valueOf(pt) * Double.valueOf(ct));
				mpDetail.put("pt_ratio", pt);
				mpDetail.put("ct_ratio", ct);
				mpDetail.put("measure_ratio", tf);

				String c2 = mpParamMap.get("ratedC2");
				if (c2 == null || c2.isEmpty()) {
					String c1 = mpParamMap.get("ratedC1");
					if (c1 == null || c1.isEmpty())
						c2 = "";
					else
						c2 = String.valueOf(Double.valueOf(c1) / Double.valueOf(ct));
				}
				mpDetail.put("ratedC2", c2);

				String c1 = mpParamMap.get("ratedC1");
				if (c1 == null || c1.isEmpty()) {
					 c2 = mpParamMap.get("ratedC2");
					if (c2 == null || c2.isEmpty())
						c1 = "";
					else
						c1 = String.valueOf(Double.valueOf(c2) * Double.valueOf(ct));
				}
				mpDetail.put("ratedC1", c1);

				
				String p2 = mpParamMap.get("ratedP2");
				if (p2 == null || p2.isEmpty()) {
					String p1 = mpParamMap.get("ratedP1");
					if (p1 == null || p1.isEmpty())
						p2 = "-1";
					else
						p2 = String.valueOf(Double.valueOf(p1) / Double.valueOf(pt));
				}
				mpDetail.put("ratedP2", p2);
				
				String p1 = mpParamMap.get("ratedP1");
				if (p1 == null || p1.isEmpty()) {
					 p2 = mpParamMap.get("ratedP2");
					if (p2 == null || p2.isEmpty())
						p1 = "";
					else
						p1 = String.valueOf(Double.valueOf(p2) * Double.valueOf(pt));
				}
				mpDetail.put("ratedP1", p1);
				//
				
				String  openMin = mpParamMap.get("open_min");
			   if(openMin!=null)
				mpDetail.put("open_min",openMin);
			
			   String  openMax = mpParamMap.get("open_max");
			   if(openMax!=null)
				mpDetail.put("open_max",openMax);
			   
			   String  insHeightSw = mpParamMap.get("ins_height_sw");
			   if(insHeightSw!=null)
				mpDetail.put("ins_height_sw",insHeightSw);
			   
			
			}

			try {
				JedisGlobal.DOC.updateJedisObj(ClusterParameter.MP_METER_INFO, mp.get("mp_meter_id").toString(), JSONUtils.serialize(mpDetail));
			} catch (Exception e) {
				log.error("遥测点" + mp.get("mp_meter_id") + "保存内存库信息失败,列表信息：,原因:" + e.getMessage());
			}
		}

		for (Integer cpId : rcpInfoMap.keySet()) {
			mpsOneCp = allMpCpMap.get(cpId);
			if (mpsOneCp != null) {
				try {
					JedisGlobal.DOC.updateJedisObj(ClusterParameter.cp_info+cpId , "mpInfo", JSONUtils.serialize(mpsOneCp));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		sb = new StringBuffer();
		sb.append(
				"SELECT task.cp_task_id,task.mp_meter_id,task.tmp_id,task.cp_id,task.exe_flag,tasktime.exe_datetime ");
		sb.append(" FROM c_trm_task task ");
		sb.append(" LEFT JOIN c_trm_task_last_time tasktime on task.cp_task_id=tasktime.cp_task_id ");
		if (noArr.length == 1) {
			sb.append(" WHERE task.`cp_id` = " + noArr[0] + " ORDER BY tmp_id");
		} else {
			sb.append(" WHERE task.`cp_id` > " + noArr[0] + " and task.`cp_id` <= " + noArr[1] + " ORDER BY tmp_id");
		}
		// sb.append(" WHERE task.`cp_id` >=24 and task.cp_id < 29");
		List<Map> allTask = null;

		try {
			allTask = dao.querySql(sb.toString());
		} catch (DataOperationException e1) {
			log.error("查询终端ID大于" + noArr[0] + "，小于" + noArr[1] + "的任务信息失败,原因:" + e1.getMessage());
		}
		Calendar exeDate = Calendar.getInstance();
		String saveLastTimeSql = "";
		List<Map> cpTask;
		for (Map task : allTask) {
			cpTask = rcpTaskList.get(Integer.parseInt((task.get("cp_id").toString())));
			if (cpTask == null) {
				cpTask = new ArrayList<Map>();
			}
			if (task.get("exe_datetime") != null) {
				exeDate.setTime((Date) task.get("exe_datetime"));
				task.put("exe_datetime", CalendarUtils.formatCalendar(exeDate, CalendarUtils.yyyyMMddHHmmss));
			} else {
				Calendar cal = Calendar.getInstance();
				saveLastTimeSql = "INSERT INTO c_trm_task_last_time(cp_task_id,exe_datetime,write_date) values("
						+ task.get("cp_task_id") + ",'" + CalendarUtils.formatCalendar(cal) + "','"
						+ CalendarUtils.formatCalendar(cal) + "')";
				try {
					dao.savesql(saveLastTimeSql);
				} catch (DataOperationException e) {
					log.info("新增任务" + task.get("cp_task_id") + "最后任务执行时间失败");
					e.printStackTrace();
				}
			}
			cpTask.add(task);
			rcpTaskList.put(Integer.parseInt(task.get("cp_id").toString()), cpTask);
		}

		List<Map> taskList = null;
		Map<String, Object> taskMap = null;
		String tmpstr = null;
		Map<String, String> tmpMap = null;// 模板任务信息
		Map<String, String> mapTask = null;// 单个遥测点任务
		Map rmpMap = null;// 单个遥测点信息
		String ptrl = null;
		for (Integer cpId : rcpTaskList.keySet()) {
			taskList = rcpTaskList.get(cpId);
			if (rcpInfoMap.get(cpId) == null) {
				continue;
			}
			ptrl = (String) rcpInfoMap.get(cpId).get("protocol_type");
			if (taskList != null && taskList.size() > 0) {
				taskMap = new LinkedHashMap<String, Object>();
				for (Map map : taskList) {
					rmpMap = rmpValueMap.get(Integer.parseInt(map.get("mp_meter_id").toString()));
					if (rmpMap == null) {
						log.error("查询遥测点" + map.get("mp_meter_id") + "信息为空");
						continue;
					}
					List<Map<String, String>> listRmpTask = (List<Map<String, String>>) (taskMap.get(ptrl) == null
							? new ArrayList<Map<String, String>>() : taskMap.get(ptrl));
					tmpstr = redisTempMap.get(map.get("tmp_id") + "");
					if (tmpstr == null) {
						log.error("找不到终端模板id为" + map.get("tmp_id") + "" + "的信息");
						continue;
					}
					try {
						tmpMap = JSONUtils.deserialize(tmpstr);
					} catch (JSONException e) {
						log.error("模板ID" + map.get("tmp_id") + "序列化失败,序列字符串" + tmpstr);
						continue;
					}
					mapTask = new LinkedHashMap<String, String>();
					mapTask.putAll(tmpMap);
					try {
						if (map.get("exe_datetime") == null) {
							map.put("exe_datetime", CalendarUtils.formatCalendar(Calendar.getInstance()));
						}
						JedisGlobal.DOC.updateJedisObj(ClusterParameter.cp_info+cpId.toString(), "task_" + map.get("cp_task_id"),
								(String) map.get("exe_datetime"));
					} catch (Exception e) {
						log.error("初始化终端" + cpId + "任务" + map.get("cp_task_id") + "最后执行时间失败,原因:" + e.getMessage());
					}
					map.remove("exe_datetime");
					map.put("run_status", rmpMap.get("run_status"));
					mapTask.putAll(map);
					listRmpTask.add(mapTask);
					taskMap.put(ptrl, listRmpTask);
				}
				if (taskMap.size() == 0) {
					log.info("终端ID为" + cpId + "配置任务出错");
				} else {
					if (rcpInfoMap.get(cpId) == null) {
						continue;
					}
					for (String strPrtl : taskMap.keySet()) {
						try {
							JedisGlobal.DOC.updateJedisObj(ClusterParameter.cp_info+cpId.toString(), ptrl,
									JSONUtils.serialize(taskMap.get(strPrtl)));
						} catch (JSONException e) {
							log.error("终端" + cpId + "序列化遥测点任务模板集合失败,列表信息：" + taskMap.get(strPrtl));
						} catch (Exception e) {
							log.error("终端" + cpId + "保存内存库信息失败,列表信息：,原因:" + e.getMessage());
						}
					}
				}
			}
		}
	}

	/**
	 * 删除终端相关信息
	 * 
	 * @param dao
	 * @param rcpId
	 */
	public static void delCpRedis(I_SQL_DataOperat dao, Integer rcpId) {
		// 删除终端下原本的信息
		try {
			// 查询终端更新前信息
			String val = JedisGlobal.DOC.queryJedisObj(ClusterParameter.cp_info+rcpId.toString(), "info");
			if (val != null && !val.isEmpty()) {
				try {
					Map rcpmap = JSONUtils.deserialize(val);
					if (rcpmap != null) {
						JedisGlobal.DOC.delRedisValue(ClusterParameter.CP_PROTOCOL_ADDR,
								rcpmap.get("protocol_type") + "@" + rcpmap.get("cp_addr"));
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		} catch (Exception e2) {
		}

		try {
			// 查询终端更新前信息
			String val = JedisGlobal.DOC.queryJedisObj(ClusterParameter.cp_info+rcpId.toString(), "mpInfo");
			if (val != null && !val.isEmpty()) {
				try {
					List<Map> lsMap = JacksonUtil.queryJsonList(val);
					if (lsMap != null) {
						for (Map map : lsMap) {
							JedisGlobal.DOC.delRedisValue(ClusterParameter.MP_METER_INFO,
									map.get("mp_meter_id").toString());
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		} catch (Exception e2) {
		}

		try {
			JedisGlobal.DOC.delRootKey(ClusterParameter.cp_info+rcpId.toString());
		} catch (Exception e) {
		}

	}

}
