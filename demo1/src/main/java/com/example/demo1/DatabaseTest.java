package com.example.demo1;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseTest {
    public static void main(String[] args) {
        DatabaseConnector databaseConnector = new DatabaseConnector();

        // Attempt to establish a database connection.
        Connection connection = databaseConnector.getConnection();

        if (connection != null) {
            try {
                // Create a statement to execute SQL queries.
                Statement statement = connection.createStatement();

                // Insert values into the 'users' table.
                String selectQuery = "select * from users ";

                // Execute the INSERT query.
                ResultSet r =statement.executeQuery(selectQuery);
               while(r.next()) {
                   System.out.println(r.getString("email"));
               }
                // Close the statement and connection.
                statement.close();

                System.out.println("Database connection closed.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Failed to connect to the Oracle database.");
        }
    }
}
