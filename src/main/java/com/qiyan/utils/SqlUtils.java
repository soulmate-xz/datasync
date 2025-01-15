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
            values.append(formatValue(column)).append(", ");
        }

        sql.setLength(sql.length() - 2);
        values.setLength(values.length() - 2);
        sql.append(") ").append(values).append(");");

        return sql.toString();
    }

    public static String buildUpdateSql(String table, List<CanalEntry.Column> beforeColumns, List<CanalEntry.Column> afterColumns) {
        StringBuilder sql = new StringBuilder("UPDATE " + table + " SET ");
        StringBuilder whereClause = new StringBuilder();

        for (CanalEntry.Column column : afterColumns) {
            sql.append(column.getName()).append(" = ").append(formatValue(column)).append(", ");
        }

        sql.setLength(sql.length() - 2); // Remove last comma

        for (CanalEntry.Column column : beforeColumns) {
            if (column.getIsKey()) { // Assuming the key is marked
                whereClause.append(column.getName()).append(" = ").append(formatValue(column)).append(" AND ");
            }
        }

        if (whereClause.length() > 0) {
            whereClause = new StringBuilder(whereClause.substring(0, whereClause.length() - 5)); // Remove last ' AND '
            sql.append(" WHERE ").append(whereClause).append(";");
        }

        return sql.toString();
    }

    public static String buildDeleteSql(String table, List<CanalEntry.Column> columns) {
        StringBuilder sql = new StringBuilder("DELETE FROM " + table + " WHERE ");

        for (CanalEntry.Column column : columns) {
            if (column.getIsKey()) {
                sql.append(column.getName()).append(" = ").append(formatValue(column)).append(" AND ");
            }
        }

        sql.setLength(sql.length() - 5);
        sql.append(";");

        return sql.toString();
    }

    public static String formatValue(CanalEntry.Column column) {
        if (column.getValue().equals("")) {
            return "NULL";
        } else {
            return switch (column.getMysqlType().split("\\(")[0]) {
                case "tinyint", "smallint", "mediumint", "int", "bigint", "bit", "float", "double", "decimal" -> column.getValue();
                default -> "'" + column.getValue() + "'";
            };
        }
    }
}
