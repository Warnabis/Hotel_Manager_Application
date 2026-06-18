package org.example.db;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    public static String url = "jdbc:postgresql://localhost:5432/Hotel";
    public static String user = "postgres";
    public static String password = System.getenv("POSTGRES_PASSWORD");

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver is not found.",e);
        }
        return java.sql.DriverManager.getConnection(url, user, password);
    }
}