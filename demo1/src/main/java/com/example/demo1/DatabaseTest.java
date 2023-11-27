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
                String selectQuery = "CREATE TABLE users (id INT PRIMARY KEY AUTO_INCREMENT, firstname VARCHAR(255) NOT NULL, lastname VARCHAR(255) NOT NULL, phonenumber VARCHAR(20) NOT NULL, address VARCHAR(255) NOT NULL, email VARCHAR(255) NOT NULL, password VARCHAR(255) NOT NULL, gender VARCHAR(10) NOT NULL, profilepicture VARCHAR(255));";


                // Execute the INSERT query.
                statement.executeUpdate(selectQuery.strip());
               //while(r.next()) {
                 //  System.out.println(r.getString("email"));
               //}
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
