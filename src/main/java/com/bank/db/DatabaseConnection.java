package com.bank.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/three_tier_banking_system";
    private static final String USER = "root";
    private static final String PASSWORD = System.getenv("PASSWORD");

    private static  Connection connection = null;

    private DatabaseConnection() {
        // private constructor to prevent instantiation
    }

    public static Connection getConnection() {
        try {
            // if connection is null or idel to stop then create new connection
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database Connected Successfully!");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Connection Failed: " + e.getMessage());
            throw new RuntimeException("Database connection error");
        }
        return connection;
    }
}
