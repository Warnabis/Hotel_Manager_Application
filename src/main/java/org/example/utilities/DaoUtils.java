package org.example.utilities;

import java.sql.*;

public final class DaoUtils {

    private DaoUtils() {}

    public static int getGeneratedId(PreparedStatement pstmt, Connection connection, String tableName) throws SQLException {
        try (ResultSet rs = pstmt.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        String maxSql = "SELECT COALESCE(MAX(id), 0) + 1 FROM " + tableName;
        try (Statement stmt = connection.createStatement();
             ResultSet maxRs = stmt.executeQuery(maxSql)) {
            if (maxRs.next()) {
                return maxRs.getInt(1);
            }
        }
        throw new SQLException("Не удалось получить ID для таблицы " + tableName);
    }
}