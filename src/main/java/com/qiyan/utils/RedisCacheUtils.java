package com.qiyan.utils;

import com.qiyan.config.RedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;


public final class RedisCacheUtils {
    private final static Jedis jedis;

    private static final Logger logger = LoggerFactory.getLogger(RedisCacheUtils.class);

    // 构造函数，初始化 Redis 连接并指定 dbName
    static {
        RedisConfig config = ParseConfigUtils.getRedisConfig();
        jedis = new Jedis(config.getHostname(), config.getPort());
        logger.info("初始化缓存成功!!!");

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
        logger.debug("set: " + key + "=" + value);
        jedis.set(key, value);
        long expirationSeconds = 60L * 5L;
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
