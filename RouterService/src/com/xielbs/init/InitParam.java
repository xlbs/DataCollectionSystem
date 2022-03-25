package com.xielbs.init;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.xielbs.exception.DataOperationException;
import com.xielbs.serviceinterface.I_SQL_DataOperat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InitParam {

	private static final Logger log = LoggerFactory.getLogger(InitParam.class);

//	public static Map<String,Map<String,String>> chanelCpMap = new LinkedHashMap<String, Map<String,String>>();//通道对应采集点集合 key为IP:ports value虚拟ID

	public static void initAllParam(I_SQL_DataOperat dao){
		Calendar cal = Calendar.getInstance();
		//初始化编码组
		InitDtiGroup.initDtiGroup(dao, null);
		//初始化任务模板
		InitTaskTmp.initTaskTmp(dao, null);
		//初始化遥信点信息
		InitTele.initAllTele(dao);
		//初始化遥信状态项字典表
		InitTele.initTeleDIctItem(dao);
		//初始化发电机对应表计
		InitGeneratorMp.initGenMpWithGen(dao, null);
		//初始化告警设置
		InitAlarmParam.initAllThreshold(dao);
		InitAlarmParam.initAllAlarmRelaObject(dao);
		
		StringBuffer sb = new StringBuffer();
		int endCjCpNo = 0;
		int maxId = 0;

		sb.append("SELECT MAX(cp_id) as maxid FROM c_cp");
		List<Map> list = null;
		try {
			list = dao.querySql(sb.toString());
		} catch (Exception e1) {
			log.error("查询终端最大ID失败,原因:"+e1.getMessage());
		}
		if(list != null && list.size()>0){
			Object  tmp =list.get(0).get("maxid");
			if(tmp!=null)
		    	maxId = (Integer) list.get(0).get("maxid");
		}
		if(maxId==0)
			return;

		List<Integer[]> listCjCpNo = new ArrayList<Integer[]>();
		while(true){
			int minCjCpNo = endCjCpNo;
			sb = new StringBuffer("SELECT MAX(cp_id) as maxId,MIN(cp_id) as minId FROM (SELECT cp_id FROM c_cp  WHERE cp_id >"+minCjCpNo+"  ORDER BY cp_id   LIMIT 1000  )cp");
			try {
				list = dao.querySql(sb.toString());
			} catch (DataOperationException e1) {
				log.error("查询终端ID大于"+minCjCpNo+"后2000条信息失败,原因:"+e1.getMessage());
			}
			for(Map map : list){
				endCjCpNo = (Integer) map.get("maxId");
			}
			Integer[] noArr = new Integer[]{minCjCpNo,endCjCpNo};
			listCjCpNo.add(noArr);

			InitCpRedis.InitRcpRedis(dao, noArr);

			if(endCjCpNo >= maxId ){
				break;
			}
		}
		log.info("初始化终端任务信息耗时"+((Calendar.getInstance().getTimeInMillis() - cal.getTimeInMillis())/1000)+"秒");
	}
	
}
