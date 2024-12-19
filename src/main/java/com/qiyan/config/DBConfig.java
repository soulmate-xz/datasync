package com.qiyan.config;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DBConfig {
    private String hostname = "localhost";
    private Integer port = 3306;
    private String database;
    private String username;
    private String password;

    // 连接池可选值
    private Integer maximumPoolSize = 10;  // 最大连接数
    private Integer minimumIdle = 3;  // 最小空闲连接数
    private Integer idleTimeout = 30000;  // 空闲超时时间
    private Integer connectionTimeout = 30000;  // 连接超时时间
    private Integer leakDetectionThreshold = 30000;  // 泄漏检测阈值

    @Override
    public String toString() {
        return  hostname + ":" + port
                + "?database=" + database + ";"
                + "?username=" + username + ";"
                + "?maximumPoolSize=" + maximumPoolSize + ";"
                + "?minimumIdle=" + minimumIdle + ";"
                + "?idleTimeout=" + idleTimeout + ";"
                + "?connectionTimeout=" + connectionTimeout + ";"
                + "?leakDetectionThreshold=" + leakDetectionThreshold + ";";
    }

}
