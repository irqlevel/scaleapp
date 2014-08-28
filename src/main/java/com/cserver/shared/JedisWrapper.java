package com.cserver.shared;

import redis.clients.jedis.Jedis;


public class JedisWrapper {
	private static final String TAG = "JedisWrapper";

	public static Jedis getJedis(String redisHost) {
		Jedis jedisObj = new Jedis(redisHost);
		if (jedisObj != null) {
			try {
				SLog.d(TAG, "redis dbSize=" + jedisObj.dbSize());
			} catch (Exception e) {
				SLog.exception(TAG, e);
				jedisObj = null;
			}
		}
		
		if (jedisObj == null) {
			SLog.e(TAG, "redis not connected");	
			return null;
		} else {
			SLog.d(TAG, "redis connected");
			return jedisObj;
		}
	}
	
	public static boolean keySetNxExpire(Jedis jedis, String key, String value, int seconds) {
		if (0 == jedis.setnx(key, value)) {
			return false;
		}
		jedis.expire(key, seconds);
		return true;
	}
	
	public static String keyGetDelete(Jedis jedis, String key) {
			String result = null;
			
			long tempId = jedis.incr("tempId");
			String tempIds = Long.toString(tempId);
			
			jedis.rename(key, Long.toString(tempId));
			result = jedis.get(tempIds);
			jedis.del(tempIds);
			
			return result;
	}
	
}
