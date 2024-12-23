package com.qiyan.thread;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.qiyan.Daemon;
import com.qiyan.ErrorRecord;
import com.qiyan.config.CanalConfig;
import com.qiyan.schema.MessageData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Queue;


public class ReceiveMessageThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(ReceiveMessageThread.class);

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
            logger.info("MONITOR <" + monitorId + "> 开启日志监听...");
            int errorCount = 0;
            while (Daemon.getRunning()) {
                try {
                    canalConnector.connect();
                    canalConnector.subscribe(canalConfig.getSubscribe());
                    Message message = canalConnector.get(100);
                    errorCount = 0;
                    if (!message.getEntries().isEmpty()) {
                        logger.info("MONITOR <" + monitorId + "> 收到数据库BinLog日志;");
                        for (Queue<MessageData> queue : otherMonitorMessageQueue) {
                            MessageData messageData = new MessageData(message, monitorId);
                            queue.add(messageData);
                        }
                    }
                } catch (CanalClientException exception) {
                    ErrorRecord record = ErrorRecord.builder()
                            .monitorId(monitorId)
                            .sqlInfo("canal 连接异常")
                            .exception(exception.toString()).build().save();
                    new AlarmPushThread(record).start();
                    errorCount += 1;
                    if (errorCount >= 5) {  // 5次都连不上canal退出程序
                        Daemon.setRunning(false);
                        logger.error("canal 连接失败!!! 退出程序!!!");
                        record = ErrorRecord.builder()
                                .monitorId(monitorId)
                                .sqlInfo("canal 连接异常")
                                .exception("canal 连接失败, 程序自动退出!!!").build().save();
                        new AlarmPushThread(record).start();
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
