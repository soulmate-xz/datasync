package com.qiyan.manager;

import com.alibaba.otter.canal.protocol.Message;
import com.qiyan.config.MonitorConfig;
import com.qiyan.schema.MessageData;
import com.qiyan.thread.MessageHandleThread;
import com.qiyan.thread.ReceiveMessageThread;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;


@Slf4j
public class DBMonitorManager {

    @Getter
    @Setter
    private String id;

    @Setter
    @Getter
    private MonitorConfig monitorConfig;

    @Getter
    private final Queue<MessageData> messageQueue = new ConcurrentLinkedQueue<>();

    private final List<Queue<MessageData>> otherMonitorMessageQueue = new ArrayList<>();

    private final MessageHandleThread messageHandleThread;
    private final ReceiveMessageThread receiveMessageThread;

    public DBMonitorManager(MonitorConfig monitorConfig) {
        this.monitorConfig = monitorConfig;
        this.id = monitorConfig.getCanalConfig().getDestination();
        this.messageHandleThread = new MessageHandleThread(id, messageQueue, this.monitorConfig.getDbConfig());
        this.receiveMessageThread = new ReceiveMessageThread(id, otherMonitorMessageQueue, this.monitorConfig.getCanalConfig());
    }

    public void run() {

        log.info("START MONITOR <" + id + "> .....");
        this.messageHandleThread.start();
        this.receiveMessageThread.start();
        log.info("START MONITOR <" + id + "> SUCCESS");
    }

    public void addOtherMonitorMessageQueue(Queue<MessageData> otherMonitorMessageQueue) {
        this.otherMonitorMessageQueue.add(otherMonitorMessageQueue);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DBMonitorManager) {
            return this.id.equals(((DBMonitorManager) obj).getId());
        }
        return super.equals(obj);
    }
}
