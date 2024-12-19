package com.qiyan.utils;

import com.alibaba.otter.canal.protocol.CanalEntry;

import java.util.List;

public final class SqlUtils {

    // Build SQL methods
    public static String buildInsertSql(String table, List<CanalEntry.Column> columns) {
        StringBuilder sql = new StringBuilder("INSERT INTO " + table + " (");
        StringBuilder values = new StringBuilder("VALUES (");

        for (CanalEntry.Column column : columns) {
            sql.append(column.getName()).append(", ");
            values.append("'").append(column.getValue()).append("', ");
        }

        sql.setLength(sql.length() - 2);
        values.setLength(values.length() - 2);
        sql.append(") ").append(values).append(");");

        return sql.toString();
    }

    public static String buildUpdateSql(String table, List<CanalEntry.Column> beforeColumns, List<CanalEntry.Column> afterColumns) {
        StringBuilder sql = new StringBuilder("UPDATE " + table + " SET ");
        String whereClause = "";

        for (CanalEntry.Column column : afterColumns) {
            // TODO: 2024/12/17 需要判断字段类型, 判断是否需要添加"'"(引号)
            sql.append(column.getName()).append(" = '").append(column.getValue()).append("', ");
        }

        sql.setLength(sql.length() - 2); // Remove last comma

        for (CanalEntry.Column column : beforeColumns) {
            if (column.getIsKey()) { // Assuming the key is marked
                whereClause += column.getName() + " = '" + column.getValue() + "' AND ";
            }
        }

        if (!whereClause.isEmpty()) {
            whereClause = whereClause.substring(0, whereClause.length() - 5); // Remove last ' AND '
            sql.append(" WHERE ").append(whereClause).append(";");
        }

        return sql.toString();
    }

    public static String buildDeleteSql(String table, List<CanalEntry.Column> columns) {
        StringBuilder sql = new StringBuilder("DELETE FROM " + table + " WHERE ");

        for (CanalEntry.Column column : columns) {
            if (column.getIsKey()) {
                sql.append(column.getName()).append(" = '").append(column.getValue()).append("' AND ");
            }
        }

        sql.setLength(sql.length() - 5);
        sql.append(";");

        return sql.toString();
    }

}
