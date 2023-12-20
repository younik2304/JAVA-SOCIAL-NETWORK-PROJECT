package com.example.demo1;

import javafx.scene.chart.PieChart;

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

    public static boolean deletePublication(Publication publication) {
        // Delete the specified publication from the 'publications' table.
        String deleteQuery = "DELETE FROM publications WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            // Set values for the parameters
            preparedStatement.setInt(1, publication.getId());

            // Execute the delete statement
            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // Return false to indicate failure
    }

    public static List<User> getUserByName(String text) {
        String[] name=text.split(" ");
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE firstname = ? and lastname = ?;")) {
            preparedStatement.setString(1, name[0]);
            preparedStatement.setString(2, name[1]);
            List<User>users=new ArrayList<>();

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
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

                    users.add( new User(id, firstName, lastName, email, password, address, gender, phonenumber, profilepicture));
                }
                return users;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions appropriately based on your application's requirements
        }

        return null;
    }
    public static void addComment(int publicationId, int commenterId, String commentText) throws SQLException {
        // Ensure you have a valid connection (you can adjust this based on your actual database connection logic)... obtain connection

                // SQL query to insert a new comment
                String query = "INSERT INTO comments (publication_id, commenter_id, text, timestamp) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            // Set parameters for the query
            preparedStatement.setInt(1, publicationId);
            preparedStatement.setInt(2, commenterId);
            preparedStatement.setString(3, commentText);

            // Set the timestamp to the current time
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            preparedStatement.setTimestamp(4, timestamp);

            // Execute the update
            preparedStatement.executeUpdate();
        }
    }

    public static List<Comment> getCommentsForPublication(int publicationId) throws SQLException {
        List<Comment> comments = new ArrayList<>();

        // Assuming you have a 'comments' table with columns 'id', 'publication_id', 'commenter_id', 'text', 'timestamp'
        String query = "SELECT * FROM comments WHERE publication_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, publicationId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int commentId = resultSet.getInt("id");
                    int commenterId = resultSet.getInt("commenter_id");
                    String text = resultSet.getString("text");
                    Timestamp timestamp = resultSet.getTimestamp("timestamp");

                    // Assuming you have a constructor in your Comment class
                    Comment comment = new Comment(commentId,publicationId, DatabaseConnector.getUserById(commenterId), text, timestamp);
                    comments.add(comment);
                }
            }
        }

        return comments;
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
                    updatePublicationContent(generatedKeys.getInt(1), String.valueOf(generatedKeys.getInt(1)));
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

    public static void sharePublication(Publication p ) {
        // Insert values into the 'publications' table.
        String insertQuery = "INSERT INTO publications (author_id, description, content, timestamp) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            // Set values for the parameters
            preparedStatement.setInt(1, UserSession.getLog_user().getId());
            preparedStatement.setString(2, p.getDescription());
            preparedStatement.setString(3, p.getContent());
            preparedStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));

            // Execute the insert statement
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                Test.showAlert("Publication","you have share the publication of : "+p.getAuthor().getFirstName()+" "+p.getAuthor().getLastName());
            }
            else
                Test.showAlert("error","couldnt share the publication");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void addFriendRequest(int senderId,int receiverId){
        try(PreparedStatement statement = connection.prepareStatement(FriendRequestService.ADD_FRIEND_REQUEST_SQL)) {
            statement.setInt(1, senderId);
            statement.setInt(2, receiverId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<Friendship> getFriendRequests() {
        List<Friendship> friendRequests = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(FriendRequestService.GET_FRIEND_REQUESTS_SQL)) {
            statement.setInt(1, UserSession.getLog_user().getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Friendship request = new Friendship();
                    request.setFriendshipId(resultSet.getInt("friendship_id"));
                    request.setUser1Id(resultSet.getInt("user1_id"));
                    request.setUser2Id(resultSet.getInt("user2_id"));
                    request.setStatus(resultSet.getString("status"));
                    friendRequests.add(request);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception
        }

        return friendRequests;
    }
    public static void updateFriendRequestStatus(int friendshipId, String updateSql) {
        try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
            statement.setInt(1, friendshipId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception
        }
    }
}



