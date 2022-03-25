package com.xielbs.jedis.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 初始化所有内存库连接
 * 
 * @author Administrator
 *
 */
public class JedisGlobal {

	private static Map<String, JedisCluster> jedisClusters = new HashMap<String, JedisCluster>();

	private static Map<String, JedisPool> jedisPools = new HashMap<String, JedisPool>();
	
	private static Map<String,JedisUtil> jedisUtils = new HashMap<String, JedisUtil>();

	static {
		initService();
	}

	public static JedisUtil DOC = jedisUtils.get("docRedis");

	public static JedisUtil DATA = jedisUtils.get("dataRedis");
	
	public static JedisUtil getJedisUtil(String configName){
		return jedisUtils.get(configName);
	}

	public static JedisCluster getJedisCluster(String clusterName) {
		return jedisClusters.get(clusterName);
	}

	public static JedisPool getJedisPool(String poolName) {
		return jedisPools.get(poolName);
	}

	private static void initService() {
		JedisConfig jconfig = null;
		try {
			jconfig = JedisConfig.getConfig();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		if (jconfig == null) {
			System.out.println("dataoperator服务需要JedisConfig或者SystemConfig配置文件");
		}
		try {
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxTotal(jconfig.getMaxTotal());
			config.setMaxIdle(jconfig.getMaxIdle());
			config.setTestOnBorrow(false);
			List<RedisCluster> redisClusters = jconfig.getRedisClusters();
			for (RedisCluster rc : redisClusters) {
				if (rc.getIpports().size() > 1) {// 集群
					Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
					for (IP ip : rc.getIpports()) {
						jedisClusterNodes.add(new HostAndPort(ip.getIp(),
								Integer.parseInt(ip.getPort())));
					}
					JedisCluster jc = new JedisCluster(jedisClusterNodes,
							config);
					jedisClusters.put(rc.getClusterName(), jc);
					jedisUtils.put(rc.getClusterName(), new JedisUtil(jc));
				} else {// 单台
					IP ip = rc.getIpports().get(0);
					JedisPool jp = null;
					 String pwd =ip.getPwd();
					 if(pwd==null||pwd.equals(""))
						  pwd = null;
					
					 String  db =ip.getDatabase();
					 if(db==null||db.equals(""))
						  db ="0";
					 
					jp = new JedisPool(config, ip.getIp(),	Integer.parseInt(ip.getPort()),10000,pwd,	Integer.parseInt(db));
					jedisPools.put(rc.getClusterName(), jp);
					jedisUtils.put(rc.getClusterName(), new JedisUtil(jp));
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
