package com.qiyan.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiyan.config.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
public final class ParseConfigUtils {

    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private static final SystemConfig systemConfig;

    static {
        File configFile = new File("config.json");
        log.info("读取配置文件: " + configFile.getAbsolutePath());
        try {
            systemConfig = jsonMapper.readValue(configFile, SystemConfig.class);
            log.info("读取配置文件成功!!!");
            log.info("REDIS CONFIG: "
                    + systemConfig.getRedisConfig().getHostname() + ":"
                    + systemConfig.getRedisConfig().getPort()
            );
            for (MonitorConfig monitorConfig : systemConfig.getMonitorConfigs()) {
                log.info("MONITOR<" + monitorConfig.getCanalConfig().getDestination() + ">: " + monitorConfig);
            }
        } catch (IOException exception) {
            log.error("读取配置文件失败!!!");
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
