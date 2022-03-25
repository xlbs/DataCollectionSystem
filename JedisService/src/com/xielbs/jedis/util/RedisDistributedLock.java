package com.xielbs.jedis.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

public class RedisDistributedLock {
     
    // 锁的名字  
    protected String lockKey;  
      
    // 锁的有效时长(毫秒)  
    protected long lockExpires;  
      
    protected long waitLockTime = 3000;
    
    protected volatile boolean locked;  
    
    JedisCluster jc;
    
    Jedis jd;
    
    public RedisDistributedLock(JedisCluster jc, String lockKey, long lockExpires,long waitLockTime){
        this.lockKey = lockKey;  
        this.lockExpires = lockExpires; 
        this.jc = jc;
        this.waitLockTime =waitLockTime;
    }
    
    public RedisDistributedLock(Jedis jd, String lockKey, long lockExpires,long waitLockTime){
        this.lockKey = lockKey;  
        this.lockExpires = lockExpires; 
        this.jd = jd;
        this.waitLockTime =waitLockTime;
    }
    
    public synchronized boolean acquire() throws InterruptedException {
        long timeout = waitLockTime;
        while (timeout >= 0) {
            long expires = System.currentTimeMillis() + lockExpires + 1;
            String expiresStr = String.valueOf(expires); //锁到期时间
            
            if (jc!=null&&jc.setnx(lockKey, expiresStr) == 1) {
                // lock acquired
                locked = true;
                return true;
            }
            if (jd!=null&&jd.setnx(lockKey, expiresStr) == 1) {
                // lock acquired
                locked = true;
                return true;
            }

            String currentValueStr = jc!=null?jc.get(lockKey):jd.get(lockKey); //redis里的时间
            if (currentValueStr != null && Long.parseLong(currentValueStr) < System.currentTimeMillis()) {
                //判断是否为空，不为空的情况下，如果被其他线程设置了值，则第二个条件判断是过不去的
                // lock is expired
                String oldValueStr = jc!=null?jc.getSet(lockKey, expiresStr):jd.getSet(lockKey, expiresStr);
                //获取上一个锁到期时间，并设置现在的锁到期时间，
                //只有一个线程才能获取上一个线上的设置时间，因为jedis.getSet是同步的
                if (oldValueStr != null && oldValueStr.equals(currentValueStr)) {
                    //如过这个时候，多个线程恰好都到了这里，但是只有一个线程的设置值和当前值相同，他才有权利获取锁
                    // lock acquired
                    locked = true;
                    return true;
                }
            }
            timeout -= 100;
            Thread.sleep(100);
        }
        return false;
    }
    
    public void release(){
    	if(jc!=null){
    		jc.del(lockKey);
    	}else{
    		jd.del(lockKey);
    	}
    	
    }
}
