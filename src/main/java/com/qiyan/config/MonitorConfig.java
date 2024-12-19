package com.qiyan.config;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonitorConfig {
    private CanalConfig canalConfig;
    private DBConfig dbConfig;
}
