package org.example.db;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    public static final String url = "jdbc:postgresql://localhost:5432/Hotel";
    public static final String user = "postgres";
    public static final String password = System.getenv("POSTGRES_PASSWORD");

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver is not found.",e);
        }
        return java.sql.DriverManager.getConnection(url, user, password);
    }
}