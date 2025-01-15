package com.qiyan.manager;

import com.qiyan.config.MonitorConfig;
import com.qiyan.schema.MessageData;
import com.qiyan.thread.MessageHandleThread;
import com.qiyan.thread.ReceiveMessageThread;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class DBMonitorManager {

    private static final Logger logger = LoggerFactory.getLogger(DBMonitorManager.class);

    @Getter
    @Setter
    private String id;

    @Setter
    @Getter
    private MonitorConfig monitorConfig;

    @Getter
    private final BlockingQueue<MessageData> messageQueue = new LinkedBlockingQueue<>();

    private final List<BlockingQueue<MessageData>> otherMonitorMessageQueue = new ArrayList<>();

    private MessageHandleThread messageHandleThread;
    private ReceiveMessageThread receiveMessageThread;

    public DBMonitorManager(MonitorConfig monitorConfig) {
        this.monitorConfig = monitorConfig;
        this.id = monitorConfig.getCanalConfig().getDestination();
        this.messageHandleThread = new MessageHandleThread(id, messageQueue, this.monitorConfig.getDbConfig());
        this.receiveMessageThread = new ReceiveMessageThread(id, otherMonitorMessageQueue, this.monitorConfig.getCanalConfig());
    }

    public void run() {

        logger.info("START MONITOR <" + id + "> .....");
        this.messageHandleThread.start();
        this.receiveMessageThread.start();
        logger.info("START MONITOR <" + id + "> SUCCESS");
    }

    public void addOtherMonitorMessageQueue(BlockingQueue<MessageData> otherMonitorMessageQueue) {
        this.otherMonitorMessageQueue.add(otherMonitorMessageQueue);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DBMonitorManager) {
            return this.id.equals(((DBMonitorManager) obj).getId());
        }
        return super.equals(obj);
    }

    public void checkAlive() {
        if (!this.messageHandleThread.isAlive()) {
            logger.error("Monitor <" + this.id + "> === MessageHandleThread 异常停止,正在重启");
            this.messageHandleThread = new MessageHandleThread(id, messageQueue, this.monitorConfig.getDbConfig());
            this.messageHandleThread.start();
            logger.error("Monitor <" + this.id + "> === MessageHandleThread 重启成功");
        }
        if (!this.receiveMessageThread.isAlive()) {
            logger.error("Monitor <" + this.id + "> === ReceiveMessageThread 异常停止,正在重启");
            this.receiveMessageThread = new ReceiveMessageThread(id, otherMonitorMessageQueue, this.monitorConfig.getCanalConfig());
            this.receiveMessageThread.start();
            logger.error("Monitor <" + this.id + "> === ReceiveMessageThread 重启成功");
        }
    }
}
