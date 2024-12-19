package com.qiyan.thread;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.qiyan.Daemon;
import com.qiyan.ErrorRecord;
import com.qiyan.config.CanalConfig;
import com.qiyan.schema.MessageData;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Queue;


@Slf4j
public class ReceiveMessageThread extends Thread {

    private final String monitorId;

    private final List<Queue<MessageData>> otherMonitorMessageQueue;
    private final CanalConfig canalConfig;

    public ReceiveMessageThread(
            String monitorId,
            List<Queue<MessageData>> otherMonitorMessageQueue,
            CanalConfig canalConfig
    ) {
        this.monitorId = monitorId;
        this.otherMonitorMessageQueue = otherMonitorMessageQueue;
        this.canalConfig = canalConfig;
    }

    @Override
    public void run() {
        CanalConnector canalConnector = CanalConnectors.newSingleConnector(
                new InetSocketAddress(canalConfig.getHostname(), canalConfig.getPort()),
                canalConfig.getDestination(),
                canalConfig.getUsername(),
                canalConfig.getPassword()
        );
        try {
            log.info("MONITOR <" + monitorId + "> 开启日志监听...");
            int errorCount = 0;
            while (Daemon.getRunning()) {
                try {
                    canalConnector.connect();
                    canalConnector.subscribe(canalConfig.getSubscribe());
                    Message message = canalConnector.get(100);
                    errorCount = 0;
                    if (!message.getEntries().isEmpty()) {
                        log.info("MONITOR <" + monitorId + "> 收到数据库BinLog日志;");
                        for (Queue<MessageData> queue: otherMonitorMessageQueue) {
                            MessageData messageData = new MessageData(message, monitorId);
                            queue.add(messageData);
                        }
                    }
                } catch (CanalClientException exception) {
                    ErrorRecord record = ErrorRecord.builder()
                            .monitorId(monitorId)
                            .sql("")
                            .exception(exception.toString()).build().save();
                    new AlarmPushThread(record).start();
                    errorCount += 1;
                    if (errorCount >= 5) {  // 5次都连不上canal退出程序
                        Daemon.setRunning(false);
                        log.error("canal 连接失败!!! 退出程序!!!");
                    }
                    Thread.sleep(1000L * 5 * errorCount);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        canalConnector.disconnect();
    }
}
