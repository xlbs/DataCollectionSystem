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

public class InitAlarmParam {

    private final static Logger log = LoggerFactory.getLogger(InitAlarmParam.class);

    /**
     * 初始化告警设置
     * @param dao
     */
    public static void initAllThreshold(I_SQL_DataOperat dao) {
        try {
            JedisGlobal.DOC.delRootKey(ClusterParameter.ALARM_THRESHOLD);
            initThresholdWithObject(dao, null, null);
            log.info("初始化【告警设置】[" + ClusterParameter.ALARM_THRESHOLD + "]信息到内存库成功！");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("初始化【告警设置】信息到内存库出现异常：", e);
        }
    }

    public static void deleteThreshold(I_SQL_DataOperat dao, Integer eventId) {
        try {
            // 查出原来的
            String eventInfo = JedisGlobal.DOC.queryJedisObj(ClusterParameter.ALARM_THRESHOLD, String.valueOf(eventId));
            // 删除event;
            JedisGlobal.DOC.delRedisValue(ClusterParameter.ALARM_THRESHOLD, String.valueOf(eventId));
            if (eventInfo != null) {
                // 删除原来的mp;
                Map eventInfoMap = JacksonUtil.queryJsonMap(eventInfo);

                String obType = eventInfoMap.get("object_type").toString();
                String obId = eventInfoMap.get("object_id").toString();
                if (obType.equalsIgnoreCase("15")) {
                    JedisGlobal.DOC.delRedisValue(ClusterParameter.ALARM_THRESHOLD, obType + "@" + obId);
                } else {
                    JedisGlobal.DOC.delRedisValue(ClusterParameter.ALARM_THRESHOLD, "@" + obId);
                }
                // 重新读原来的MP
                initThresholdWithObject(dao, obType, obId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initThresholdWithObject(I_SQL_DataOperat dao, String obType, String obId) {
        try {
            StringBuffer sb1 = new StringBuffer();
            sb1.append("SELECT ea.*,e.`property_id` FROM event_alarm_threshold_set ea , e_equipment e  WHERE   ea.`object_type` <> '15' AND  ea.`object_id` = e.`equi_id`");
            if (obType != null) {
                sb1.append(" and  ea. `object_type` = " + obType + " and  ea.`object_id` = " + obId);
            }
            StringBuffer sb2 = new StringBuffer();
            sb2.append("SELECT ea.*,tele.`property_id` FROM event_alarm_threshold_set ea ,c_mp_tele  tele  WHERE   ea.`object_type` = '15' AND  ea.`object_id` = tele.mp_tele_id ");
            if (obType != null) {
                sb2.append(" and    ea. `object_type` = " + obType + " and  ea.`object_id` = " + obId);
            }
            String sql = sb1.toString() + " union " + sb2.toString() ;

            List<Map> list = dao.querySql(sql);

            Map<String, Map> vObKeyMap = new HashMap<String, Map>();
            Map<String, Map> vEventKeyMap = new HashMap<String, Map>();

            for (Map eventInfoMap : list) {
                String key = "";
                if (eventInfoMap.get("object_type").toString().equalsIgnoreCase("15")) {
                    key = eventInfoMap.get("object_type") + "@" + eventInfoMap.get("object_id");
                } else {
                    key = "@" + eventInfoMap.get("object_id");
                }
                Map lMap = vObKeyMap.get(key);
                if (lMap == null) {
                    lMap = new HashMap<>();
                    vObKeyMap.put(key, lMap);
                }
                lMap.put(eventInfoMap.get("event_id"), eventInfoMap.get("event_id"));
                vEventKeyMap.put(eventInfoMap.get("event_id").toString(), eventInfoMap);
            }

            for (String key : vObKeyMap.keySet()) {
                JedisGlobal.DOC.updateJedisObj(ClusterParameter.ALARM_THRESHOLD, key, JSONUtils.serialize(vObKeyMap.get(key)));
            }

            sb1.setLength(0);
            sb1.append(" SELECT * FROM event_user_set ");
            if (obType != null) {
                if (vObKeyMap.size() == 0) {
                    return;  //没有事件;
                }

                String ids = "";
                for (Map idMap : vObKeyMap.values()) {
                    for (Object id : idMap.keySet()) {
                        ids += id + ",";
                    }
                }
                ids = ids.substring(0, ids.length() - 1);
                sb2.append(" where  `event_id`  in  (" + ids + ")");
            }

            list = dao.querySql(sb1.toString());
            Map<String, Map> vEventKeyUserMap = new HashMap<String, Map>();
            for (Map userInfoMap : list) {
                String key = userInfoMap.get("event_id").toString();
                Map lMap = vEventKeyUserMap.get(key);
                if (lMap == null) {
                    lMap = new HashMap<>();
                    vEventKeyUserMap.put(key, lMap);
                }
                lMap.put(userInfoMap.get("user_id"), userInfoMap.get("user_id"));
            }
            for (String eventId : vEventKeyMap.keySet()) {
                Map map = vEventKeyMap.get(eventId);
                if (map != null) {
                    map.put("user", vEventKeyUserMap.get(eventId));
                }
                JedisGlobal.DOC.updateJedisObj(ClusterParameter.ALARM_THRESHOLD, eventId, JSONUtils.serialize(map));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initThreshold(I_SQL_DataOperat dao, int eventId) {
        try {
            //先删除;
            deleteThreshold(dao, eventId);

            StringBuffer sb = new StringBuffer();
            sb.append(" SELECT e.*  FROM   event_alarm_threshold_set e  where  e. `event_id` =" + eventId);
            List<Map> list = dao.querySql(sb.toString());
            for (Map map : list) {
                Object obType = map.get("object_type");
                Object obId = map.get("object_id");
                if (obType != null)
                    initThresholdWithObject(dao, obType.toString(), obId.toString());

                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initAllAlarmRelaObject(I_SQL_DataOperat dao) {
        try {
            JedisGlobal.DOC.delRootKey(ClusterParameter.ALARM_RELA_OBJECT);
            //
            InitAlarmParam.initAlarmRelaObject(dao, null, null, null, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initAlarmRelaObject(I_SQL_DataOperat dao, String obType, String obId, String sonsorType, String sonsorId) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append(" SELECT aoc. object_type,aoc.object_id ,mp.mp_meter_id  FROM  alarm_object_config aoc ,e_equipment  e,c_mp_meter mp where e.equi_no = mp.equi_no and e.`equi_id`=aoc.sonsor_id");
            if (obType != null)
                sb.append(" where  `object_type` = " + obType + " and  `object_id` = " + obId + " and  `sonsor_type` = " + sonsorType + " and  `sonsor_id` = " + sonsorId);

            List<Map> list = dao.querySql(sb.toString());
            for (Map recMap : list) {
                String mpId = recMap.get("mp_meter_id").toString();
                //	String ob =recMap.get("object_type")+"@" +recMap.get("object_id");
                String ob = "@" + recMap.get("object_id");
                JedisGlobal.DOC.updateJedisObj(ClusterParameter.ALARM_RELA_OBJECT, mpId, ob);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
