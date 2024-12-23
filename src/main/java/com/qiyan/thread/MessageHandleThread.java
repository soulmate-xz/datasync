package com.qiyan.thread;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.qiyan.Daemon;
import com.qiyan.ErrorRecord;
import com.qiyan.config.DBConfig;
import com.qiyan.manager.DBManager;
import com.qiyan.schema.MessageData;
import com.qiyan.utils.RedisCacheUtils;
import com.qiyan.utils.SqlUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Queue;


public class MessageHandleThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(MessageHandleThread.class);

    private final Queue<MessageData> messageQueue;
    private final String monitorId;
    @Getter
    private final DBConfig dbConfig;
    private final DBManager dbManager;

    public MessageHandleThread(String monitorId, Queue<MessageData> messageQueue, DBConfig dbConfig) {
        this.monitorId = monitorId;
        this.messageQueue = messageQueue;
        this.dbConfig = dbConfig;
        this.dbManager = new DBManager(dbConfig);
    }

    @Override
    public void run() {
        logger.info("MONITOR <" + monitorId + "> 开启消息监听...");
        while (Daemon.getRunning()) {
            if (!messageQueue.isEmpty()) {
                MessageData messageData = messageQueue.poll();
                Message message = messageData.getMessage();
                logger.info("MONITOR <" + monitorId + "> 收到消息;");
                List<CanalEntry.Entry> entryList = message.getEntries();
                if (!entryList.isEmpty()) {
                    for (CanalEntry.Entry entry : entryList) {
                        CanalEntry.EntryType entryType = entry.getEntryType();
                        if (entryType.equals(CanalEntry.EntryType.ROWDATA)) {
                            String tableName = entry.getHeader().getTableName();
                            ByteString storeValue = entry.getStoreValue();
                            String sql = "";
                            try {
                                CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(storeValue);
                                CanalEntry.EventType eventType = rowChange.getEventType();
                                List<CanalEntry.RowData> rowDataList = rowChange.getRowDatasList();
                                Connection connection;
                                try {
                                    connection = dbManager.getConnection();
                                    dbManager.beginTransaction(connection);
                                    try {
                                        for (CanalEntry.RowData rowData : rowDataList) {
                                            switch (eventType) {
                                                case INSERT ->
                                                        sql = SqlUtils.buildInsertSql(tableName, rowData.getAfterColumnsList());
                                                case UPDATE ->
                                                        sql = SqlUtils.buildUpdateSql(tableName, rowData.getBeforeColumnsList(), rowData.getAfterColumnsList());
                                                case DELETE ->
                                                        sql = SqlUtils.buildDeleteSql(tableName, rowData.getBeforeColumnsList());
                                                default -> {
                                                }
                                            }
                                            logger.info("MONITOR <" + monitorId + "> 解析到sql: " + sql);
                                            RedisCacheUtils.set(buildSqlKey(sql, messageData.getSendMonitorId()), "1");  // 防止重复执行
                                            if (!sql.equals("")) {
                                                String cacheKey = buildSqlKey(sql);
                                                String cache = RedisCacheUtils.get(cacheKey);
                                                if (Objects.isNull(cache)) {
                                                    dbManager.executeUpdate(connection, sql);
                                                    RedisCacheUtils.set(cacheKey, "1");
                                                    logger.info("MONITOR <" + monitorId + "> 执行sql: " + sql);
                                                } else {
                                                    logger.info("MONITOR <" + monitorId + "> 收到重复sql: " + sql);
                                                }
                                            }
                                        }
                                        dbManager.commitTransaction(connection);
                                        logger.info("MONITOR <" + monitorId + "> 提交事务");
                                        connection.close();
                                    } catch (SQLException sqlException) {
                                        logger.error("MONITOR <" + monitorId + "> message handle sql执行异常");
                                        sqlException.printStackTrace();
                                        dbManager.rollbackTransaction(connection);
                                        connection.close();
                                        ErrorRecord record = ErrorRecord.builder().monitorId(monitorId).sqlInfo(sql).exception(sqlException.toString()).build().save();
                                        new AlarmPushThread(record).start();
                                    }
                                } catch (SQLException sqlException) {
                                    logger.error("MONITOR <" + monitorId + "> message handle 获取数据库连接异常");
                                    sqlException.printStackTrace();
                                    ErrorRecord record = ErrorRecord.builder().monitorId(monitorId).sqlInfo(sql).exception(sqlException.toString()).build().save();
                                    new AlarmPushThread(record).start();
                                }
                            } catch (InvalidProtocolBufferException exception) {
                                logger.error("MONITOR <" + monitorId + "> message handle 解析消息异常");
                                exception.printStackTrace();
                                ErrorRecord record = ErrorRecord.builder().monitorId(monitorId).sqlInfo(sql).exception(exception.toString()).build().save();
                                new AlarmPushThread(record).start();
                            }
                        }
                    }
                }
            }
        }
    }

    private String buildSqlKey(String sql) {
        return buildSqlKey(sql, monitorId);
    }

    private String buildSqlKey(String sql, String sendMonitorId) {
        try {
            // 获取 MD5 摘要实例
            MessageDigest md = MessageDigest.getInstance("MD5");
            String input = sendMonitorId + ":" + sql;
            // 将输入字符串转换为字节数组并计算摘要
            byte[] digest = md.digest(input.getBytes());

            // 转换为 16 进制表示的字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b); // 保证为正数
                if (hex.length() == 1) {
                    hexString.append('0'); // 补零
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

}
