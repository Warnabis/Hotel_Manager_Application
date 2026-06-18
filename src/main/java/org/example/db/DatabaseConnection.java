package org.example.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private DatabaseConnection() {}
    public static final String URL = "jdbc:postgresql://localhost:5432/Hotel";
    public static final String USER = "postgres";
    public static final String PASSWORD = System.getenv("POSTGRES_PASSWORD");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}