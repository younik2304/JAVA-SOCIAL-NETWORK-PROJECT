package com.example.demo1;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnector {
    private final String JDBC_URL = "jdbc:mysql://viaduct.proxy.rlwy.net:33591/railway";
    private final String JDBC_USER = "root";
    private final String JDBC_PASSWORD = "66-5ceh214d5-aA6D6EabAAg5ggHHAhh";

    private static Connection connection;

    public DatabaseConnector() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize the database connection.");
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

    public static List<Publication> getPublications() throws SQLException {
        List<Publication> publications = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM publications")) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int authorId = resultSet.getInt("author_id");
                String description = resultSet.getString("description");
                String content = resultSet.getString("content");
                String timestamp = resultSet.getString("timestamp");

                // Retrieve the User by ID
                User author = getUserById(authorId);

                Publication publication = new Publication(id, author, description, content, timestamp);
                publications.add(publication);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions appropriately based on your application's requirements
        }

        return publications;
    }
    public static List<Publication> getUserPublications(int userId) throws SQLException {
        List<Publication> userPublications = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM publications WHERE author_id = ?")) {
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    int authorId = resultSet.getInt("author_id");
                    String description = resultSet.getString("description");
                    String content = resultSet.getString("content");
                    String timestamp = resultSet.getString("timestamp");

                    // Retrieve the User by ID
                    User author = getUserById(authorId);

                    Publication publication = new Publication(id, author, description, content, timestamp);
                    userPublications.add(publication);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions appropriately based on your application's requirements
        }

        return userPublications;
    }


    public static List<User> getUsers() throws SQLException {
        List<User> userList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users");


             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                String phonenumber = resultSet.getString("phonenumber");
                String address = resultSet.getString("address");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                String gender = resultSet.getString("gender");
                String profilepicture = resultSet.getString("profilepicture");

                userList.add(new User(id, firstName, lastName, email, password, address, gender, phonenumber, profilepicture));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch users from the database.");
        }

        return userList;
    }

    public static User getUserById(int userId) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String firstName = resultSet.getString("firstname");
                    String lastName = resultSet.getString("lastname");
                    String phonenumber = resultSet.getString("phonenumber");
                    String address = resultSet.getString("address");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");
                    String gender = resultSet.getString("gender");
                    String profilepicture = resultSet.getString("profilepicture");

                    return new User(id, firstName, lastName, email, password, address, gender, phonenumber, profilepicture);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions appropriately based on your application's requirements
        }

        return null;
    }

    public static int addPublication(int authorId, String description) {
        // Insert values into the 'publications' table.
        String insertQuery = "INSERT INTO publications (author_id, description, content, timestamp) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            // Set values for the parameters
            preparedStatement.setInt(1, authorId);
            preparedStatement.setString(2, description);
            preparedStatement.setString(3, "default.png");
            preparedStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));

            // Execute the insert statement
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Retrieve the generated keys (in this case, the ID of the inserted publication)
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    updatePublicationContent( generatedKeys.getInt(1), String.valueOf(generatedKeys.getInt(1)));
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // Return -1 to indicate failure
    }
    public static boolean updatePublicationContent(int publicationId, String newContent) {
        // Update the 'publications' table to set a new content for the specified publication ID.
        String updateQuery = "UPDATE publications SET content = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            // Set values for the parameters
            preparedStatement.setString(1, newContent);
            preparedStatement.setInt(2, publicationId);

            // Execute the update statement
            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // Return false to indicate failure
    }



}
