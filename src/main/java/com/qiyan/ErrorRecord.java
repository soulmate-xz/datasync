package com.qiyan;


import com.qiyan.config.DataSourceConfig;
import com.qiyan.utils.ParseConfigUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ErrorRecord {
    private String monitorId;
    private String exception;
    private String sqlInfo = "";

    private static final Logger logger = LoggerFactory.getLogger(ErrorRecord.class);

    public ErrorRecord save() {
//        DataSourceConfig dataSourceConfig = ParseConfigUtils.getDataSourceConfig();
//        String url = "jdbc:mysql://"
//                + dataSourceConfig.getHostname() + ":"
//                + dataSourceConfig.getPort() + "/"
//                + dataSourceConfig.getDatabase();
//        try {
//            Connection connection = DriverManager.getConnection(url, dataSourceConfig.getUsername(), dataSourceConfig.getPassword());
//            String sql = "INSERT INTO material_data_sync_error_record(monitor_id, exception, sql_info) VALUES(?, ?, ?)";
//            PreparedStatement preparedStatement = connection.prepareStatement(sql);
//            setParameters(preparedStatement, this.monitorId, this.exception, this.sqlInfo);
//            logger.error("保存错误信息: " + preparedStatement);
//            preparedStatement.executeUpdate();  // todo: 此处保存错误的sql会失败
//        } catch (SQLException sqlException) {
//            sqlException.printStackTrace();
//            logger.error("保存异常信息错误: " + sqlException);
//        }
        return this;
    }

    // 设置 SQL 参数
    private static void setParameters(PreparedStatement preparedStatement, Object... params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
        }
    }

    private String b64EncodeSql(String sql) {
        if (sql == null) {
            return null;
        }

        // 使用 Base64 编码
        return Base64.getEncoder().encodeToString(sql.getBytes());
    }

}
