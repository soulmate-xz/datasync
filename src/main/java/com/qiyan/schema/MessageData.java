package com.qiyan.schema;

import com.alibaba.otter.canal.protocol.Message;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageData {

    private Message message;
    private String sendMonitorId;
}
