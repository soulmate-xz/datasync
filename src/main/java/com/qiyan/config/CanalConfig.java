package com.qiyan.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CanalConfig {
    private String hostname = "localhost";
    private Integer port = 11111;
    private String destination = "";
    private String username = "";
    private String password = "";
    private String subscribe = "";

    public CanalConfig(String destination, String subscribe) {
        this.destination = destination;
        this.subscribe = subscribe;
    }

    @Override
    public String toString() {
        return hostname + ":" + port
                + "?destination=" + destination + ";"
                + "username=" + username + ";"
                + "subscribe=" + subscribe + ";";
    }
}
