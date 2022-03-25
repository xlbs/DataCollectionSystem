package com.xielbs.frontmachine;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.xielbs.config.ClusterConfig;
import org.apache.log4j.Logger;

/**
 * @ClassName Initialize
 * @Description TODO
 * @Author xielbs
 * @Date 2021/7/19$ 18:08$
 * @Version 1.0
 */
public class Initialize {

    private static final Logger log = Logger.getLogger(Initialize.class);

    public ClusterConfig clusterConfig = ClusterConfig.getConfig();

    public static Initialize instance = new Initialize();

    public ActorSystem actorSystem;

    public ActorRef recFromTT;

    public ActorRef receiveCall;

    public ActorRef dispatch;

    public ActorRef logActor;

    private Initialize() {
        log.info("====>进入：Initialize");
    }

    public void init(){

    }



}
