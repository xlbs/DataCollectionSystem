package com.xielbs.test;

import akka.actor.ActorSystem;
import akka.actor.AddressFromURIString;
import akka.actor.Props;
import akka.cluster.Cluster;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import com.xielbs.config.ClusterConfig;
import com.xielbs.config.ClusterParameter;
import com.xielbs.jedis.util.JedisGlobal;
import com.xielbs.rpcclient.AkkaClient;
import com.xielbs.rpcclient.ClusterListenerActor;

import java.util.List;
import java.util.Map;

/**
 * @ClassName QueryTest
 * @Description TODO
 * @Author xielbs
 * @Date 2021/7/22$ 15:24$
 * @Version 1.0
 */
public class QueryTest {

    private static ClusterConfig clusterConfig = ClusterConfig.getConfig();

    public static void main(String[] args) {
        try {
//            AkkaClient.getInstance().initActorSystem(null);
//            I_SQL_DataOperat dao = AkkaClient.getInstance().getBean(I_SQL_DataOperat.class);
//            String sql = " SELECT ea.*,e.`property_id` FROM event_alarm_threshold_set ea , e_equipment e  WHERE   ea.`object_type` <> '15' AND  ea.`object_id` = e.`equi_id` union SELECT ea.*,tele.`property_id` FROM event_alarm_threshold_set ea ,c_mp_tele  tele  WHERE   ea.`object_type` = '15' AND  ea.`object_id` = tele.mp_tele_id ";
//            List<Map> list = dao.querySql(sql);
//            System.out.println("===="+list.size());
        }catch (Exception e){
            e.printStackTrace();
        }



    }



}
