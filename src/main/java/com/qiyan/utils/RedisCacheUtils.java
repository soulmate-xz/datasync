package com.qiyan.utils;

import com.qiyan.config.RedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


public final class RedisCacheUtils {
    private final static JedisPool jedisPool;

    private static final Logger logger = LoggerFactory.getLogger(RedisCacheUtils.class);

    // 构造函数，初始化 Redis 连接并指定 dbName
    static {
        RedisConfig config = ParseConfigUtils.getRedisConfig();

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);  // 最大连接数
        poolConfig.setMaxIdle(5);  // 最大空闲连接数
        poolConfig.setMinIdle(2);  // 最小空闲连接数
        poolConfig.setTestOnBorrow(true);  // 检查连接有效性
        poolConfig.setTestOnReturn(true);  // 归还时检查连接有效性

        logger.info("初始化缓存: " + config);
        jedisPool = new JedisPool(poolConfig, config.getHostname(), config.getPort());
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("k", "v");
            jedis.expire("k", 1L);
            logger.info("初始化缓存成功!!!");
        } catch (Exception e) {
            logger.error("Redis 初始化失败: " + e.getMessage());
        }

    }

    // 设置键值并指定过期时间（单位：秒）
    public static void set(String key, String value, int expirationSeconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, value);
            if (expirationSeconds > 0) {
                jedis.expire(key, expirationSeconds);
            }
        } catch (Exception e) {
            logger.error("设置Redis缓存失败: " + e);
        }
    }

    // 设置键值并指定过期时间（单位：秒）
    public static void set(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            logger.debug("set: " + key + "=" + value);
            jedis.set(key, value);
            long expirationSeconds = 60L * 5L;
            jedis.expire(key, expirationSeconds);
        } catch (Exception e) {
            logger.error("设置Redis缓存失败: " + e);
        }
    }

    // 获取键值
    public static String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        } catch (Exception e) {
            logger.error("获取Redis缓存失败: " + e);
        }
        return null;
    }

    // 关闭 Redis 连接
    public static void close() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }
}
