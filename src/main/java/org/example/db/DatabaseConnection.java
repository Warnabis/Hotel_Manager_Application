package org.example.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static final Properties props = new Properties();

    static {
        try (InputStream input = DatabaseConnection.class.getClassLoader()
          .getResourceAsStream("dbconfig.properties")) {
            if (input == null) {
                throw new IllegalStateException("Файл dbconfig.properties не найден в classpath");
            }
            props.load(input);

            Class.forName(props.getProperty("db.driver"));
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка загрузки конфигурации БД", e);
        }
    }

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
          props.getProperty("db.url"),
          props.getProperty("db.username"),
          props.getProperty("db.password")
        );
    }
}
