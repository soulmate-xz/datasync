package com.qiyan.config;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemConfig {
    private RedisConfig redisConfig;
    private List<MonitorConfig> monitorConfigs;
    private DataSourceConfig dataSourceConfig;
    private AlarmPushConfig alarmPushConfig;
}
