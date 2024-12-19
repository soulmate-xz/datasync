package com.qiyan.config;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RedisConfig {

    private String hostname = "localhost";
    private Integer port = 6379;
    private String username;
    private String password;

    @Override
    public String toString() {
        return "host: " + hostname + ";"
                + "port: " + port;
    }
}
