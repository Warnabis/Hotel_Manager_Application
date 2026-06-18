package org.example.utilities;

import java.sql.*;

public final class DaoUtils {

    private DaoUtils() {}

    private static String sanitizeTableName(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя таблицы не может быть пустым");
        }

        if (!tableName.matches("^[a-zA-Z0-9_.]+$")) {
            throw new IllegalArgumentException("Недопустимое имя таблицы: " + tableName);
        }
        return tableName;
    }

    public static int getGeneratedId(PreparedStatement pstmt, Connection connection, String tableName) throws SQLException {
        try (ResultSet rs = pstmt.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        String safeTableName = sanitizeTableName(tableName);
        String maxSql = "SELECT COALESCE(MAX(id), 0) + 1 FROM " + safeTableName;
        try (Statement stmt = connection.createStatement();
             ResultSet maxRs = stmt.executeQuery(maxSql)) {
            if (maxRs.next()) {
                return maxRs.getInt(1);
            }
        }
        throw new SQLException("Не удалось получить ID для таблицы " + tableName);
    }
}