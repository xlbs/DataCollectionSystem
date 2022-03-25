package com.xielbs.frontmachine;

import akka.actor.ActorSystem;
import akka.actor.AddressFromURIString;
import akka.actor.Props;
import akka.cluster.Cluster;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import com.xielbs.actor.*;
import com.xielbs.config.ClusterParameter;
import com.xielbs.jedis.util.JedisGlobal;
import org.apache.log4j.Logger;

/**
 * @ClassName FrontMachineService
 * @Description TODO
 * @Author xielbs
 * @Date 2021/7/19$ 17:58$
 * @Version 1.0
 */
public class FrontMachineService {

    private static final Logger log = Logger.getLogger(FrontMachineService.class);

    public static final FrontMachineService service = new FrontMachineService();

    private Initialize init = Initialize.instance;

    private Config cfg = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + init.clusterConfig.getPort())
            .withValue("akka.remote.netty.tcp.hostname", ConfigValueFactory.fromAnyRef(init.clusterConfig.getIp()))
            .withFallback(ConfigFactory.load("frontmachine"));

    public static void main(String[] args) throws Exception {
        FrontMachineService.service.initActorService();
    }

    /**
     * 初始化服务
     */
    public void initActorService(){
        try {

            init.actorSystem = ActorSystem.create(init.clusterConfig.getClusterName(), cfg);

            String leaderNode = JedisGlobal.DOC.queryRootKeyValue(ClusterParameter.SYSTEM_CLUSTER_LEADER);
            if (leaderNode != null) {
                Cluster.get(init.actorSystem).join(AddressFromURIString.parse(leaderNode));
            } else {
                Cluster.get(init.actorSystem).join(AddressFromURIString.parse("akka.tcp://clousystem@"+getRouteIpPort()));
            }

            init.actorSystem.actorOf(
                    Props.create(TransPondActor.class).withDispatcher("receive-dispatcher"),
                    ClusterParameter.FRONTMACHINE_RECEIVE_TRANSPOND_FROM_CLIENT);
            init.actorSystem.actorOf(
                    Props.create(ReceiveFormTransPondActor.class).withDispatcher("receive-dispatcher"),
                    ClusterParameter.FRONTMACHINE_RECEIVE_TRANSPOND_FROM_TRM);
            init.recFromTT = init.actorSystem.actorOf(
                    Props.create(ReceiveFromTimeTaskActor.class).withDispatcher("receive-dispatcher"),
                    ClusterParameter.FRONTMACHINE_RECEIVE_ACTOR_PATH);
            init.receiveCall = init.actorSystem.actorOf(
                    Props.create(ReceiveFromCallServiceActor.class).withDispatcher("receiveCall-dispatcher"),
                    ClusterParameter.FRONTMACHINE_RECEIVE_CALL_ACTOR_PATH);
            init.dispatch = init.actorSystem.actorOf(
                    Props.create(DispatchTrmMsgActor.class).withDispatcher("send-dispatcher"),
                    ClusterParameter.FRONT_MACHINE_RECEIVE_DISPATCH_TRM_MSG);
            init.logActor = init.actorSystem.actorOf(
                    Props.create(TrmLogReportActor.class).withDispatcher("send-dispatcher"),
                    ClusterParameter.FRONT_MACHINE_RECEIVE_TRM_LOG_REPORT);

            init.actorSystem.actorOf(Props.create(ClusterListenerActor.class), ClusterParameter.FRONT_MACHINE_CLUSTER_LISTENER);

            //直接在这里初始化
            Initialize.instance.init();

        }catch (Exception e){
            e.printStackTrace();
            log.error("初始化前置机actor服务 异常:" + e);
        }

    }

    /**
     * 获取定时任务路由器IpPort
     * @return
     */
    public String getRouteIpPort() {
        try {
            String roterIpPort = JedisGlobal.DOC.queryJedisObj(ClusterParameter.ROUTER_IP_PORT);
            if (roterIpPort == null) {
                log.error("未能获取定时任务路由器IpPort，请确认路由器启动后再重启前置机！");
            }
            return roterIpPort;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("获取定时任务路由器IpPort失败:"+e);
        }
        return null;

    }



}
