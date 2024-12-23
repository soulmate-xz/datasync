package com.qiyan.thread;


import com.qiyan.ErrorRecord;
import com.qiyan.config.AlarmPushConfig;
import com.qiyan.utils.HttpClientUtil;
import com.qiyan.utils.ParseConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AlarmPushThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(AlarmPushThread.class);

    private final ErrorRecord errorRecord;

    public AlarmPushThread(ErrorRecord errorRecord) {
        this.errorRecord = errorRecord;
    }

    private static final String template = """
            # 数据同步程序异常,请暂停使用并检查!!!!!
                        
            - 异常节点:  monitorId
            - 异常信息
                        
              ```
              	sqlInfo
            	```
                        
            - 错误信息
                        
              ```
              	exception
              ```
                        
            """;

    @Override
    public void run() {
        send(this.errorRecord);
    }

    public void send(ErrorRecord errorRecord) {
        AlarmPushConfig alarmPushConfig = ParseConfigUtils.getAlarmPushConfig();
        if (Objects.isNull(alarmPushConfig)) {
            logger.info("无报警推送配置!!!");
            return;
        }
        String content = template.replace("monitorId", errorRecord.getMonitorId())
                .replace("sqlInfo", errorRecord.getSqlInfo())
                .replace("exception", errorRecord.getException());
        Map<String, Object> body = new HashMap<>();
        Map<String, String> commonVO = new HashMap<>();
        body.put("content", content);
        commonVO.put("msgType", "markdown");
        for (String userId : alarmPushConfig.getUserIds()) {
            commonVO.put("toUser", userId);
            body.put("commonVO", commonVO);
            HttpClientUtil.post(alarmPushConfig.getUrl(), body);
        }
    }
}
