package com.example.demo1;

import com.example.demo1.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DatabaseTest {
    public static void main(String[] args) {
        DatabaseConnector databaseConnector = new DatabaseConnector();

        // Attempt to establish a database connection.
        try (Connection connection = databaseConnector.getConnection()) {
            if (connection != null) {
                // Insert values into the 'publications' table.
                String insertQuery = "delete from table publications";

                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    // Set values for the parameters
                   /* preparedStatement.setInt(1, 12); // Replace with the actual author_id
                    preparedStatement.setString(2, "Sample Description");
                    preparedStatement.setString(3, "4");
                    preparedStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis())); // Use the current timestamp
*/
                    // Execute the insert statement
                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Row inserted successfully.");
                    } else {
                        System.out.println("Failed to insert row.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                System.out.println("Database connection closed.");
            } else {
                System.out.println("Failed to connect to the Oracle database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
