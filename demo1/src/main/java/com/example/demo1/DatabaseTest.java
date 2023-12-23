package com.example.demo1;

import com.example.demo1.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseTest {
    public static void main(String[] args) {
        DatabaseConnector databaseConnector = new DatabaseConnector();

        // Attempt to establish a database connection.
        try (Connection connection = databaseConnector.getConnection()) {
            if (connection != null) {
                // Delete all rows from the 'publications' table.
                String deleteQuery = "delete from  FriendRequests; ";

                try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                    // Execute the delete statement
                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Rows deleted successfully.");
                    } else {
                        System.out.println("No rows deleted.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                System.out.println("Database connection closed.");
            } else {
                System.out.println("Failed to connect to the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
