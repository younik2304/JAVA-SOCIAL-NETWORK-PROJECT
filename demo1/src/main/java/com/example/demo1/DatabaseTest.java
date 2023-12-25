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
                String deleteQuery = "CREATE TABLE messages (\n" +
                        "    id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                        "    sender_id INT,\n" +
                        "    recipient_id INT,\n" +
                        "    message_text VARCHAR(255),\n" +
                        "    timestamp TIMESTAMP,\n" +
                        "    FOREIGN KEY (sender_id) REFERENCES users(id),\n" +
                        "    FOREIGN KEY (recipient_id) REFERENCES users(id)\n" +
                        ");\n ";

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
