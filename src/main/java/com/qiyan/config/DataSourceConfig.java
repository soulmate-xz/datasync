package com.qiyan.config;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataSourceConfig {
    private String hostname = "localhost";
    private Integer port = 3306;
    private String database;
    private String username;
    private String password;
}
