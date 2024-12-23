package com.qiyan.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiyan.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class ParseConfigUtils {

    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private static final SystemConfig systemConfig;

    private static final Logger logger = LoggerFactory.getLogger(ParseConfigUtils.class);

    static {
        File configFile = new File("config.json");
        logger.info("读取配置文件: " + configFile.getAbsolutePath());
        try {
            systemConfig = jsonMapper.readValue(configFile, SystemConfig.class);
            logger.info("读取配置文件成功!!!");
            logger.info("REDIS CONFIG: "
                    + systemConfig.getRedisConfig().getHostname() + ":"
                    + systemConfig.getRedisConfig().getPort()
            );
            for (MonitorConfig monitorConfig : systemConfig.getMonitorConfigs()) {
                logger.info("MONITOR<" + monitorConfig.getCanalConfig().getDestination() + ">: " + monitorConfig);
            }
        } catch (IOException exception) {
            logger.error("读取配置文件失败!!!");
            throw new RuntimeException(exception);
        }
    }

    public static RedisConfig getRedisConfig() {
        return systemConfig.getRedisConfig();
    }

    public static List<MonitorConfig> getMonitorConfigs() {
        return systemConfig.getMonitorConfigs();
    }

    public static DataSourceConfig getDataSourceConfig() {
        return systemConfig.getDataSourceConfig();
    }

    public static AlarmPushConfig getAlarmPushConfig() {
        return systemConfig.getAlarmPushConfig();
    }

}
