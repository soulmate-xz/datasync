package com.qiyan.utils;

import com.qiyan.config.RedisConfig;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;


@Slf4j
public final class RedisCacheUtils {
    private final static Jedis jedis;

    // 构造函数，初始化 Redis 连接并指定 dbName
     static  {
         RedisConfig config = ParseConfigUtils.getRedisConfig();
        jedis = new Jedis(config.getHostname(), config.getPort());
        log.info("初始化缓存成功!!!");

    }
    // 设置键值并指定过期时间（单位：秒）
    public static void set(String key, String value, int expirationSeconds) {
        jedis.set(key, value);
        if (expirationSeconds > 0) {
            jedis.expire(key, expirationSeconds);
        }
    }

    // 设置键值并指定过期时间（单位：秒）
    public static void set(String key, String value) {
        log.debug("set: " +  key + "=" + value);
        jedis.set(key, value);
        int expirationSeconds = 60 * 5;
        jedis.expire(key, expirationSeconds);
    }

    // 获取键值
    public static String get(String key) {
        return jedis.get(key);
    }

    // 关闭 Redis 连接
    public static void close() {
        if (jedis != null) {
            jedis.close();
        }
    }
}
