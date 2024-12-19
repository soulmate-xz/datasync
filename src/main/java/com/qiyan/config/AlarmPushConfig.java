package com.qiyan.config;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmPushConfig {
    private List<String> userIds;
    private String url;
}
