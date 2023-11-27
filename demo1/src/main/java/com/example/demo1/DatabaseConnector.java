package com.example.demo1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    //private static final String JDBC_URL = "jdbc:mysql://sql11.freesqldatabase.com:3306/sql11664992";
    private static final String JDBC_URL = "jdbc:mysql://viaduct.proxy.rlwy.net:33591/railway";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "66-5ceh214d5-aA6D6EabAAg5ggHHAhh";

    private Connection connection;

    public DatabaseConnector() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load the Oracle JDBC driver.
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load mysql driver.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to the database.");
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

