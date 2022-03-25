package com.xielbs.jedis.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.exceptions.JedisException;


/**
 * 
 * 功能说明 redis直接操作类 * 
 *
 */
public class JedisUtil {
	
	protected JedisPool jp;	
	
	protected JedisCluster jc;	
	
	public JedisUtil(String jedisName){
		jc = JedisGlobal.getJedisCluster(jedisName);
		if(jc==null){
			jp = JedisGlobal.getJedisPool(jedisName);
		}
	}
	/**
	 * 非集群方式访问redis
	 * @param jp
	 */
	
	public JedisUtil(JedisPool jp){
		this.jp = jp;
	}
	/**
	 * 集群方式访问redis
	 * @param jc
	 */
	public JedisUtil(JedisCluster jc){
		this.jc = jc;
	}
	
	/**
	 * 获取单个Redis数据
	 * @param objType 对象类型
	 * @param objid 对象ID
	 * @return
	 * @throws Exception
	 */
	public String queryJedisObj(String objType,String objid) throws Exception {
		boolean broken = false;
		if(jc == null && jp == null){
			throw new JedisException("redis内存库连接失败");
		}
		Jedis jedis = null;
		try{
			if(jc !=null){
				return jc.hget(objType, objid);
			}else{
				jedis = jp.getResource();
				return jedis.hget(objType, objid);
			}
		}catch(JedisException e){
			broken = handleJedisException(e);e.printStackTrace();
			throw new JedisException("获取数据失败,"+e.getMessage());
		}finally{
			if(jedis!=null){
				closeResource(jedis,broken);
			}
		}
	}
	/**
	 * 获取单个Redis数据
	 * @param objid 对象ID
	 * @return
	 * @throws Exception
	 */
	public  String queryJedisObj(String objid) throws Exception {
		boolean broken = false;
		if(jc == null && jp == null){
			throw new JedisException("redis内存库连接失败");
		}
		Jedis jedis = null;
		try{
			if(jc !=null){
				return jc.get(objid);
			}else{
				jedis = jp.getResource();
				return jedis.get(objid);
			}
		}catch(JedisException e){
			broken = handleJedisException(e);e.printStackTrace();
			throw new JedisException("获取数据失败,"+e.getMessage());
		}finally{
			if(jedis!=null){
				closeResource(jedis,broken);
			}
		}
	}
	/**
	 * 获取List<>列表数据
	 * @param objType 对象类型
	 * @param sta 开始数
	 * @param end 结束数
	 * @return 
	 * @throws Exception
	 */
	public List<String> queryJedisObjList(String objType,Long sta,Long end)throws Exception{
		boolean broken = false;
		if(jc == null && jp == null){
			throw new JedisException("redis内存库连接失败");
		}
		Jedis jedis = null;
		try{
			if(jc !=null){
				if(end == null){
					end = jc.llen(objType);
				}
				return  jc.lrange(objType, sta, end);
			}else{
				jedis = jp.getResource();
				if(end == null){
					end = jedis.llen(objType);
				}
				return jedis.lrange(objType, sta, end);
			}
		}catch(JedisException e){
			broken = handleJedisException(e);e.printStackTrace();
			throw new JedisException("获取数据失败,"+e.getMessage());
		}finally{
			if(jedis!=null){
				closeResource(jedis,broken);
			}
		}
	}
	
	public  long   getListLength(String objType)throws Exception{
		boolean broken = false;
		if(jc == null && jp == null){
			throw new JedisException("redis内存库连接失败");
		}
		Jedis jedis = null;
		try{
			if(jc !=null){
				return  jc.llen(objType);
			}else{
				jedis = jp.getResource();
				return jedis.llen(objType);
			}
		}catch(JedisException e){
			broken = handleJedisException(e);e.printStackTrace();
			throw new JedisException("获取数据失败,"+e.getMessage());
		}finally{
			if(jedis!=null){
				closeResource(jedis,broken);
			}
		}
	}
	
	
	public  void   deleteLastDataList(String objType)throws Exception{
		boolean broken = false;
		if(jc == null && jp == null){
			throw new JedisException("redis内存库连接失败");
		}
		Jedis jedis = null;
		try{
			if(jc !=null){
			    jc.rpop(objType);
			}else{
				jedis = jp.getResource();
				 jedis.rpop(objType);
			}
		}catch(JedisException e){
			broken = handleJedisException(e);e.printStackTrace();
			throw new JedisException("删除数据失败,"+e.getMessage());
		}finally{
			if(jedis!=null){
				closeResource(jedis,broken);
			}
		}
	}
	
	
	/**
	 * 批量查询Jedis的map数据
	 * @param objType
	 * @param keyList
	 * @return
	 * @throws Exception
	 */
	public List<String> queryJedisMapObj(String objType,String... keyList)throws Exception{
		boolean broken = false;
		if(jc == null && jp == null){
			throw new JedisException("redis内存库连接失败");
		}
		Jedis jedis = null;
		try{
			if(jc !=null){
				return jc.hmget(objType, keyList);
			}else{
				jedis = jp.getResource();
				return jedis.hmget(objType, keyList);
			}
		}catch(JedisException e){
			broken = handleJedisException(e);e.printStackTrace();
			throw new JedisException("获取数据失败,"+e.getMessage());
		}finally{
			if(jedis!=null){
				closeResource(jedis,broken);
			}
		}
	}
	
	/**
	 * 批量查询Jedis的map数据
	 * @param objType
	 * @param keyList
	 * @return
	 * @throws Exception
	 */
	public Map<String,String> queryJedisMapObjMap(String objType,String... keyList)throws Exception{
		boolean broken = false;
		if(jc == null && jp == null){
			throw new JedisException("redis内存库连接失败");
		}
		Jedis jedis = null;
		try{
			List<String> list = null;
			Map<String,String> valueMap = new HashMap<String, String>();
			if(jc !=null){
				list =  jc.hmget(objType, keyList);
			}else{
				jedis = jp.getResource();
				list =  jedis.hmget(objType, keyList);
			}
			for(int i =0 ; i < keyList.length;i++){
				valueMap.put(keyList[i], list.get(i));
			}
			return valueMap;
		}catch(JedisException e){
			broken = handleJedisException(e);e.printStackTrace();
			throw new JedisException("获取数据失败,"+e.getMessage());
		}finally{
			if(jedis!=null){
				closeResource(jedis,broken);
			}
		}
	}
	
	public Map<String,String> queryJedisMapAllObj(String objType)throws Exception{
		boolean broken = false;
		if(jc == null && jp == null){
			throw new JedisException("redis内存库连接失败");
		}
		Jedis jedis = null;
		try{
			if(jc !=null){
				return jc.hgetAll(objType);
			}else{
				jedis = jp.getResource();
				return jedis.hgetAll(objType);
			}
		}catch(JedisException e){
			broken = handleJedisException(e);e.printStackTrace();
			throw new JedisException("获取数据失败,"+e.getMessage());
		}finally{
			if(jedis!=null){
				closeResource(jedis,broken);
			}
		}
	}
	/**
	 * 更新单个对象
	 * @param objType 对象类型
	 * @param objId 对象ID
	 * @param jsonObj 对象json字符串
	 * @return
	 * @throws Exception
	 */
	public Boolean updateJedisObj(String objType,String objId,String jsonObj)throws Exception{
		Jedis jedis = null;
		boolean broken = false;
		try {
        	if(jc!=null){
        		jc.hset(objType, objId,jsonObj);
        	}else{
        		jedis = jp.getResource();
        		jedis.hset(objType, objId,jsonObj);
        	}
        	return true;
        } catch (Throwable t) {
            // 分布式锁异常
        	t.printStackTrace();
        	throw new JedisException("更新失败,"+t.getMessage());
        } finally {
            if(jedis!=null){
				closeResource(jedis,broken);
			}
        }
	}
	
	
	public Boolean delAndUpdateJedisObj(String objType,String objId,String jsonObj)throws Exception{
		Jedis jedis = null;
		boolean broken = false;
		try {
        	if(jc!=null){
        		jc.hdel(objType, objId);
        		jc.hset(objType, objId,jsonObj);
        	}else{
        		jedis = jp.getResource();
        		jedis.hdel(objType, objId);
        		jedis.hset(objType, objId,jsonObj);
        	}
        	return true;
        } catch (Throwable t) {
            // 分布式锁异常
        	t.printStackTrace();
        	throw new JedisException("更新失败,"+t.getMessage());
        } finally {
            if(jedis!=null){
				closeResource(jedis,broken);
			}
        }
	}
	/**
	 * 插入单个数据到list
	 * @param objType
	 * @param list
	 * @throws Exception
	 */
	public void insertObjectToRedisListHead(String objType,String obj)throws Exception{
		boolean broken = false;
		if(jc == null && jp == null){
			throw new JedisException("redis内存库连接失败");
		}
		Jedis jedis = null;
		try{
			if(jc !=null){
				jc.rpush(objType, obj);
			}else{
				jedis = jp.getResource();
				jedis.lpush(objType, obj);
			}
		}catch(JedisException e){
			broken = handleJedisException(e);e.printStackTrace();
			throw new JedisException("插入数据失败,"+e.getMessage());
		}finally{
			if(jedis!=null){
				closeResource(jedis,broken);
			}
		}
	}
	
	/**
	 * 插入list数组
	 * @param objType
	 * @param list
	 * @throws Exception
	 */
	public void insertListToRedis(String objType,List<String> list)throws Exception{
		boolean broken = false;
		if(jc == null && jp == null){
			throw new JedisException("redis内存库连接失败");
		}
		Jedis jedis = null;
		try{
			if(jc !=null){
				jc.rpush(objType,
						list.toArray(new String[list.size()]));
			}else{
				jedis = jp.getResource();
				for(String str : list){
					jedis.rpush(objType,str);
				}
			}
		}catch(JedisException e){
			broken = handleJedisException(e);e.printStackTrace();
			throw new JedisException("插入数据失败,"+e.getMessage());
		}finally{
			if(jedis!=null){
				closeResource(jedis,broken);
			}
		}
	}
	
	/**
	 * 插入或者更新map数组
	 * @param objType
	 * @param valueMap
	 * @throws Exception
	 */
	public void updateMapToRedis(String objType,Map<String,String> valueMap)throws Exception{
		boolean broken = false;
		if(jc == null && jp == null){
			throw new JedisException("redis内存库连接失败");
		}
		Jedis jedis = null;
		try{
			if(jc !=null){
				jc.hmset(objType, valueMap);
			}else{
				jedis = jp.getResource();
				jedis.hmset(objType, valueMap);
			}
		}catch(JedisException e){
			broken = handleJedisException(e);e.printStackTrace();
			throw new JedisException("更新数据失败,"+e.getMessage());
		}finally{
			if(jedis!=null){
				closeResource(jedis,broken);
			}
		}
	}
	/**
	 * 删除指定目录下的ID节点数据
	 * @param objType
	 * @param delIdList
	 * @throws Exception
	 */
	public void delRedisValue(String objType,String...delIdList)throws Exception{
		boolean broken = false;
		if(jc == null && jp == null){
			throw new JedisException("redis内存库连接失败");
		}
		Jedis jedis = null;
		try{
			if(jc !=null){
				jc.hdel(objType, delIdList);
			}else{
				jedis = jp.getResource();
				jedis.hdel(objType, delIdList);
			}
		}catch(JedisException e){
			broken = handleJedisException(e);e.printStackTrace();
			throw new JedisException("删除数据失败,"+e.getMessage());
		}finally{
			if(jedis!=null){
				closeResource(jedis,broken);
			}
		}
	}
	
	/**
	 * 删除指定目录
	 * @param objType
	 * @throws Exception
	 */
	public void delRootKey(String objType)throws Exception{
		boolean broken = false;
		if(jc == null && jp == null){
			throw new JedisException("redis内存库连接失败");
		}
		Jedis jedis = null;
		try{
			if(jc !=null){
				jc.del(objType);
			}else{
				jedis = jp.getResource();
				jedis.del(objType);
			}
		}catch(JedisException e){
			broken = handleJedisException(e);e.printStackTrace();
			throw new JedisException("删除数据失败,"+e.getMessage());
		}finally{
			if(jedis!=null){
				closeResource(jedis,broken);
			}
		}
	}
	
	/**
	 * 在最上层设置key-value
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	public void setRootKeyValue(String key,String value)throws Exception{
		boolean broken = false;
		if(jc == null && jp == null){
			throw new JedisException("redis内存库连接失败");
		}
		Jedis jedis = null;
		try{
			if(jc !=null){
				jc.set(key, value);
			}else{
				jedis = jp.getResource();
				jedis.set(key, value);
			}
		}catch(JedisException e){
			broken = handleJedisException(e);e.printStackTrace();
			throw new JedisException("设置数据失败,"+e.getMessage());
		}finally{
			if(jedis!=null){
				closeResource(jedis,broken);
			}
		}
	}
	
	/**
	 * 在最上层设置key-value
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	public void setRootKeyValueTimeOut(String key,String value,Integer seconds)throws Exception{
		boolean broken = false;
		if(jc == null && jp == null){
			throw new JedisException("redis内存库连接失败");
		}
		Jedis jedis = null;
		try{
			if(jc !=null){
				jc.set(key, value);
				jc.expire(key, seconds);
			}else{
				jedis = jp.getResource();
				jedis.set(key, value);
				jedis.expire(key, seconds);
			}
		}catch(JedisException e){
			broken = handleJedisException(e);e.printStackTrace();
			throw new JedisException("设置数据失败,"+e.getMessage());
		}finally{
			if(jedis!=null){
				closeResource(jedis,broken);
			}
		}
	}
	/**
	 * 删除集合中前realSize个元素
	 * 
	 * @param type
	 * @param realSize
	 * @throws Exception
	 */
	public void deleteFechSizeOfList(String type, int realSize)throws Exception {
		boolean broken = false;
		if(jc == null && jp == null){
			throw new JedisException("redis内存库连接失败");
		}
		Jedis jedis = null;
		try{
			if(jc !=null){
				jc.ltrim(type, realSize, -1);
			}else{
				jedis = jp.getResource();
				jedis.ltrim(type, realSize, -1);
			}
		}catch(JedisException e){
			broken = handleJedisException(e);e.printStackTrace();
			throw new JedisException("删除数据失败,"+e.getMessage());
		}finally{
			if(jedis!=null){
				closeResource(jedis,broken);
			}
		}
	}
	
	
	/**
	 * 删除集合中前realSize个元素
	 * 
	 * @param type
	 * @param realSize
	 * @throws Exception
	 */
	public void deleteRedisAll()throws Exception {
		boolean broken = false;
		if(jc == null && jp == null){
			throw new JedisException("redis内存库连接失败");
		}
		Jedis jedis = null;
		try{
			if(jc !=null){
				Map<String,JedisPool> map = jc.getClusterNodes();
				Jedis jd = null;
				for(JedisPool po : map.values()){
					try{
						jd = po.getResource();
						jd.flushAll();
					}catch(JedisException e){}finally{
						po.returnResource(jd);
					}
				}
			}else{
				jedis = jp.getResource();
				jedis.flushAll();
			}
		}catch(JedisException  e){
			broken = handleJedisException(e);e.printStackTrace();
			throw new JedisException("删除数据失败,"+e.getMessage());
		}finally{
			if(jedis!=null){
				closeResource(jedis,broken);
			}
		}
	}
	/**
	 * 获取根节点值
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public String queryRootKeyValue(String key)throws Exception{
		boolean broken = false;
		if(jc == null && jp == null){
			throw new JedisException("redis内存库连接失败");
		}
		Jedis jedis = null;
		try{
			if(jc !=null){
				return jc.get(key);
			}else{
				jedis = jp.getResource();
				return jedis.get(key);
			}
		}catch(Exception e){
			throw new JedisException("获取数据失败,"+e.getMessage());
		}finally{
			if(jedis!=null){
				closeResource(jedis,broken);
			}
		}
	}
	
	/**
	 * 获取根节点是否有值
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public boolean isRootKeyValue(String key)throws Exception{
		boolean broken = false;
		if(jc == null && jp == null){
			throw new JedisException("redis内存库连接失败");
		}
		Jedis jedis = null;
		try{
			if(jc !=null){
				return jc.exists(key);
			}else{
				jedis = jp.getResource();
				return jedis.exists(key);
			}
		}catch(JedisException e){
			broken = handleJedisException(e);e.printStackTrace();
			throw new JedisException("获取数据失败,"+e.getMessage());
		}finally{
			if(jedis!=null){
				closeResource(jedis,broken);
			}
		}
	}
	
	/**
	 * 获取所有keys
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public Set<String> getAllKeys(String tableName)throws Exception{
		boolean broken = false;
		if(jc == null && jp == null){
			throw new JedisException("redis内存库连接失败");
		}
		Jedis jedis = null;
		try{
			if(jc !=null){
				return jc.hkeys(tableName);
			}else{
				jedis = jp.getResource();
				return jedis.hkeys(tableName);
			}
		}catch(JedisException e){
			broken = handleJedisException(e);e.printStackTrace();
			throw new JedisException("获取数据失败,"+e.getMessage());
		}finally{
			if(jedis!=null){
				closeResource(jedis,broken);
			}
		}
	}
	
	/**
	 * 插入set数组
	 * @param objType
	 * @param list
	 * @throws Exception
	 */
	public void insertSetToRedis(String objType,String... str)throws Exception{
		boolean broken = false;
		if(jc == null && jp == null){
			throw new JedisException("redis内存库连接失败");
		}
		Jedis jedis = null;
		try{
			if(jc !=null){
				jc.sadd(objType, str);
			}else{
				jedis = jp.getResource();
				jedis.sadd(objType,
						str);
			}
		}catch(JedisException e){
			broken = handleJedisException(e);e.printStackTrace();
			throw new JedisException("插入数据失败,"+e.getMessage());
		}finally{
			if(jedis!=null){
				closeResource(jedis,broken);
			}
		}
	}
	
	/**
	 * 删除Set<>列表数据
	 * @param objType
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public long deleteJedisObjSet(String objType,String...  str )throws Exception{
		boolean broken = false;
		if(jc == null && jp == null){
			throw new JedisException("redis内存库连接失败");
		}
		Jedis jedis = null;
		try{
			if(jc !=null){
				return  jc.srem(objType, str);
			}else{
				jedis = jp.getResource();
				return jedis.srem(objType, str);
			}
		}catch(JedisException e){
			broken = handleJedisException(e);e.printStackTrace();
			throw new JedisException("获取数据失败,"+e.getMessage());
		}finally{
			if(jedis!=null){
				closeResource(jedis,broken);
			}
		}
	}
	
	/**
	 * 获取set<>列表数据
	 * @param objType 对象类型
	 * @param sta 开始数
	 * @param end 结束数
	 * @return 
	 * @throws Exception
	 */
	public Set<String> queryJedisObjSet(String objType)throws Exception{
		boolean broken = false;
		if(jc == null && jp == null){
			throw new JedisException("redis内存库连接失败");
		}
		Jedis jedis = null;
		try{
			if(jc !=null){
				return  jc.smembers(objType);
			}else{
				jedis = jp.getResource();
				return jedis.smembers(objType);
			}
		}catch(JedisException e){
			broken = handleJedisException(e);e.printStackTrace();
			throw new JedisException("获取数据失败,"+e.getMessage());
		}finally{
			if(jedis!=null){
				closeResource(jedis,broken);
			}
		}
	}
	
	/**
	 * 插入zSet到redis
	 * @param objType
	 * @param index
	 * @param value
	 * @throws Exception
	 */
	public void insertZSetToRedis(String objType,Long index,String value)throws Exception{
		boolean broken = false;
		if(jc == null && jp == null){
			throw new JedisException("redis内存库连接失败");
		}
		Jedis jedis = null;
		try{
			if(jc !=null){
				jc.zadd(objType, index, value);
			}else{
				jedis = jp.getResource();
				jedis.zadd(objType, index, value);
			}
		}catch(JedisException e){
			broken = handleJedisException(e);
			e.printStackTrace();
			throw new JedisException("获取数据失败,"+e.getMessage());
		}finally{
			if(jedis!=null){
				closeResource(jedis,broken);
			}
		}
	} 
	
	/**
	 *	 * @param objType
	 * @param index
	 * @param value
	 * @throws Exception
	 */
	public void deleteZSetToRedis(String objType,String sta,String end)throws Exception{
		boolean broken = false;
		if(jc == null && jp == null){
			throw new JedisException("redis内存库连接失败");
		}
		Jedis jedis = null;
		try{
			if(jc !=null){
				jc.zremrangeByScore(objType, sta,end);
			}else{
				jedis = jp.getResource();
				jedis.zremrangeByScore(objType, sta,end);
			}
		}catch(JedisException e){
			broken = handleJedisException(e);
			e.printStackTrace();
			throw new JedisException("获取数据失败,"+e.getMessage());
		}finally{
			if(jedis!=null){
				closeResource(jedis,broken);
			}
		}
	} 
	/**
	 * * @param objType
	 * @param index
	 * @param value
	 * @throws Exception
	 */
	public Set<String> queryZSetToRedis(String objType,String indexBegin,String  indexEnd)throws Exception{
		boolean broken = false;
		if(jc == null && jp == null){
			throw new JedisException("redis内存库连接失败");
		}
		Jedis jedis = null;
		try{
			if(jc !=null){
				return jc.zrangeByScore(objType, indexBegin,indexEnd);
			}else{
				jedis = jp.getResource();
				return jedis.zrangeByScore(objType,  indexBegin,indexEnd);
			}
		}catch(JedisException e){
			broken = handleJedisException(e);
			e.printStackTrace();
			throw new JedisException("获取数据失败,"+e.getMessage());
		}finally{
			if(jedis!=null){
				closeResource(jedis,broken);
			}
		}
	} 
	
	public Set<String> queryZSetToRedis(String objType,String index)throws Exception{
		  return queryZSetToRedis(objType, index, index);
	} 
    /**
     * Handle jedisException, write log and return whether the connection is broken.
     */
    protected boolean handleJedisException(JedisException jedisException) {
        if (jedisException instanceof JedisConnectionException) {
        } else if (jedisException instanceof JedisDataException) {
            if ((jedisException.getMessage() != null) && (jedisException.getMessage().indexOf("READONLY") != -1)) {
            } else {
                // dataException, isBroken=false
                return false;
            }
        } else {
        }
        return true;
    }
    /**
     * Return jedis connection to the pool, call different return methods depends on the conectionBroken status.
     */
    protected void closeResource(Jedis jedis, boolean conectionBroken) {
        try {
            if (conectionBroken) {
            	jp.returnBrokenResource(jedis);
            } else {
            	jp.returnResource(jedis);
            }
        } catch (Exception e) {
        }
    }

	public JedisPool getJp() {
		return jp;
	}

	public void setJp(JedisPool jp) {
		this.jp = jp;
	}


}
