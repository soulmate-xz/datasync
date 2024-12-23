package com.qiyan.manager;

import com.qiyan.config.DBConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBManager {

    // HikariCP 数据源
    private final HikariDataSource dataSource;

    public DBManager(DBConfig dbConfig) {
        HikariConfig config = new HikariConfig();
        String URL = "jdbc:mysql://" + dbConfig.getHostname() + ":" + dbConfig.getPort() + "/" + dbConfig.getDatabase();
        config.setJdbcUrl(URL);
        config.setUsername(dbConfig.getUsername());
        config.setPassword(dbConfig.getPassword());
        config.setMaximumPoolSize(dbConfig.getMaximumPoolSize()); // 最大连接数
        config.setMinimumIdle(dbConfig.getMinimumIdle());     // 最小空闲连接数
        config.setIdleTimeout(dbConfig.getIdleTimeout()); // 空闲超时时间
        config.setConnectionTimeout(dbConfig.getConnectionTimeout()); // 连接超时时间
        config.setLeakDetectionThreshold(dbConfig.getLeakDetectionThreshold()); // 泄漏检测阈值

        System.setProperty("com.zaxxer.hikari.level", "INFO");
        this.dataSource = new HikariDataSource(config);
    }

    // 获取数据库连接
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // 执行更新语句（INSERT, UPDATE, DELETE）
    public void executeUpdate(Connection connection, String sql) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.executeUpdate();
    }

    // 开启事务
    public void beginTransaction(Connection connection) throws SQLException {
        if (connection != null) {
            connection.setAutoCommit(false);
        }
    }

    // 提交事务
    public void commitTransaction(Connection connection) throws SQLException {
        if (connection != null) {
            connection.commit();
            connection.setAutoCommit(true);
        }
    }

    // 回滚事务
    public void rollbackTransaction(Connection connection) throws SQLException {
        if (connection != null) {
            connection.rollback();
            connection.setAutoCommit(true);
        }
    }

    // 关闭资源
    public void close() {
        dataSource.close();
    }
}
