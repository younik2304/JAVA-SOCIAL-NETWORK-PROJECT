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

    public static User getUserByName(String text) {
        String[] name = text.split(" ");

        // Check if there are at least two elements in the array
        if (name.length >= 2) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE firstname = ? and lastname = ?;")) {
                preparedStatement.setString(1, name[0]);
                preparedStatement.setString(2, name[1]);


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

                        User user = new User(id, firstName, lastName, email, password, address, gender, phonenumber, profilepicture);
                        return user;
                    }

                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle exceptions appropriately based on your application's requirements
            }
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

    public static void removeFriend(int id) {
        String sql = "delete from  Friends where  (user_id = ? and friend_id = ?) or (user_id = ? and friend_id = ?);";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, UserSession.getLog_user().getId());
            preparedStatement.setInt(2, id);
            preparedStatement.setInt(3, id);
            preparedStatement.setInt(4, UserSession.getLog_user().getId());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("deleting  friend failed, no rows affected.");
            }
        }catch (SQLException  s ){
            System.out.println(s.getErrorCode());
        }
    }

    public static List<Message> getMessagesBetweenUsers(int yourUserId, int otherUserId) {
        List<Message> messages = new ArrayList<>();

        String query = "SELECT * FROM messages WHERE (sender_id = ? AND recipient_id = ?) OR (sender_id = ? AND recipient_id = ?) ORDER BY timestamp";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, yourUserId);
            preparedStatement.setInt(2, otherUserId);
            preparedStatement.setInt(3, otherUserId);
            preparedStatement.setInt(4, yourUserId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int messageId = resultSet.getInt("id");
                    int senderId = resultSet.getInt("sender_id");
                    int recipientId = resultSet.getInt("recipient_id");
                    String messageText = resultSet.getString("message_text");
                    Timestamp timestamp = resultSet.getTimestamp("timestamp");

                    User sender = getUserById(senderId);
                    User recipient = getUserById(recipientId);

                    Message message = new Message(messageId, sender, recipient, messageText, timestamp);
                    messages.add(message);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions appropriately based on your application's requirements
        }

        return messages;
    }

    public static boolean saveMessageToDatabase(int yourUserId, int otherUserId, String messageText) {
        String query = "INSERT INTO messages (sender_id, recipient_id, message_text, timestamp) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, yourUserId);
            preparedStatement.setInt(2, otherUserId);
            preparedStatement.setString(3, messageText);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions appropriately based on your application's requirements
            return false;
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
        int loggedInUserId = UserSession.getLog_user().getId();

        // Use a JOIN clause to get publications from friends of the logged-in user
        String query ="SELECT p.*, u.id AS author_id, u.firstname, u.lastname, u.email, u.password, u.address, u.gender, u.phonenumber, u.profilepicture " +
                "FROM publications p " +
                "JOIN Friends f ON (p.author_id = f.friend_id OR p.author_id = f.user_id) " +
                "JOIN users u ON p.author_id = u.id " +
                "WHERE (f.user_id = ? OR f.friend_id = ?) " +

                "UNION " +

                "SELECT p.*, u.id AS author_id, u.firstname, u.lastname, u.email, u.password, u.address, u.gender, u.phonenumber, u.profilepicture " +
                "FROM publications p " +
                "JOIN users u ON p.author_id = u.id " +
                "WHERE p.author_id = ?";;

        try ( PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, UserSession.getLog_user().getId());
            preparedStatement.setInt(2, UserSession.getLog_user().getId());
            preparedStatement.setInt(3, UserSession.getLog_user().getId());


            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    int authorId = resultSet.getInt("author_id");
                    String description = resultSet.getString("description");
                    String content = resultSet.getString("content");
                    String timestamp = resultSet.getString("timestamp");

                    // Retrieve the User by ID
                    User author = new User(authorId,
                            resultSet.getString("firstname"),
                            resultSet.getString("lastname"),
                            resultSet.getString("email"),
                            resultSet.getString("password"),
                            resultSet.getString("address"),
                            resultSet.getString("gender"),
                            resultSet.getString("phonenumber"),
                            resultSet.getString("profilepicture"));

                    Publication publication = new Publication(id, author, description, content, timestamp);
                    publications.add(publication);
                }
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
        int loggedInUserId = UserSession.getLog_user().getId();

        // Use a JOIN clause to get friends of the logged-in user
        String query = "SELECT u.* FROM users u " +
                "JOIN Friends f ON (u.id = f.friend_id OR u.id = f.user_id) " +
                "WHERE (f.user_id = ? OR f.friend_id = ?) AND u.id <> ?";

        try ( PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, loggedInUserId);
            preparedStatement.setInt(2, loggedInUserId);
            preparedStatement.setInt(3, loggedInUserId);

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

                    userList.add(new User(id, firstName, lastName, email, password, address, gender, phonenumber, profilepicture));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch friends from the database.");
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
    public static List<FriendRequests> getFriendRequests() {
        // Your database query to retrieve friend requests
        String query = "SELECT * FROM FriendRequests WHERE receiver_id = ?";
        List<FriendRequests> friendRequests = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, UserSession.getLog_user().getId());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    FriendRequests friendRequest = new FriendRequests(
                            resultSet.getInt("request_id"),
                            resultSet.getInt("sender_id"),
                            resultSet.getInt("receiver_id")
                    );
                    friendRequests.add(friendRequest);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return friendRequests;
    }
    public static List<User> getFriends() {
        String query = "SELECT * from Friends where (user_id = ? or friend_id = ?) ;";

        List<User> friends = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, UserSession.getLog_user().getId());
            preparedStatement.setInt(2, UserSession.getLog_user().getId());


            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    User user =new User();
                    if(UserSession.getLog_user().getId()==resultSet.getInt(1)){
                        user=DatabaseConnector.getUserById(resultSet.getInt(2));
                    }
                    else user=DatabaseConnector.getUserById(resultSet.getInt(1));

                    friends.add(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return friends;
    }


    public static int createFriendRequest(int sender_id ,int receiver_id ) throws SQLException {
        String sql = "INSERT INTO FriendRequests (sender_id, receiver_id) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, sender_id);
            preparedStatement.setInt(2, receiver_id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Creating friend request failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return the auto-generated ID
                } else {
                    throw new SQLException("Creating friend request failed, no ID obtained.");
                }
            }
        }
    }
    public static void createFriend(int user_id) throws SQLException {
        String sql = "INSERT INTO Friends (user_id, friend_id) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, UserSession.getLog_user().getId());
            preparedStatement.setInt(2, user_id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Creating friend failed, no rows affected.");
            }
        }catch (SQLException  s ){
            System.out.println(s.getErrorCode());
        }
    }
    public static void deleteFriendRequest(int friendRequestId) throws SQLException {
        String sql = "DELETE FROM FriendRequests WHERE request_id= ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, friendRequestId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Deleting friend request failed, no rows affected.");
            }
        }
    }

}



