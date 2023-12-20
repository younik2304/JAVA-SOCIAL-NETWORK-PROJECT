package com.example.demo1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendRequestService {
    public  static final String ADD_FRIEND_REQUEST_SQL = "INSERT INTO friendships (user1_id, user2_id, status) VALUES (?, ?, 'PENDING')";
    public static final String GET_FRIEND_REQUESTS_SQL = "SELECT * FROM friendships WHERE user2_id = ? AND status = 'PENDING'";
    public static final String ACCEPT_FRIEND_REQUEST_SQL = "UPDATE friendships SET status = 'ACCEPTED' WHERE friendship_id = ?";
    public static final String REJECT_FRIEND_REQUEST_SQL = "UPDATE friendships SET status = 'REJECTED' WHERE friendship_id = ?";


    // In the class where acceptFriendRequest and rejectFriendRequest are defined

    public static void acceptFriendRequest(int friendshipId, home_Controller homeController) {
        DatabaseConnector.updateFriendRequestStatus(friendshipId, ACCEPT_FRIEND_REQUEST_SQL);
        //homeController.displayFriendRequests();
    }

    public static void rejectFriendRequest(int friendshipId, home_Controller homeController) {
        DatabaseConnector.updateFriendRequestStatus(friendshipId, REJECT_FRIEND_REQUEST_SQL);
        //homeController.displayFriendRequests();
    }



}
