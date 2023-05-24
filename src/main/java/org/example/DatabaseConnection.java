package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://localhost/dogsdb";
    private static String username;
    private static String password;

    public DatabaseConnection(String username, String password) {
        DatabaseConnection.username = username;
        DatabaseConnection.password = password;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, username, password);
    }
}