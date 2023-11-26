package com.example.demo1;

import java.util.List;

public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String profilePicture;
    private List<User> friends;
    private List<User> friendRequests;
    private List<Publication> publications;
    private List<Message> messages;

    public User(String firstName, String lastName, String email, String password, String profilePicture) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.profilePicture = profilePicture;
        // Initialize friends, friendRequests, publications, and messages lists.
    }

    // Getters and setters for the fields.

    public void sendFriendRequest(User friendRequest) {
        // Add logic to send friend requests.
    }

    public void acceptFriendRequest(User friend) {
        // Add logic to accept friend requests.
    }

    public void sendPublication(Publication publication) {
        // Add logic to create and send publications.
    }

    public void commentOnPublication(Publication publication, Comment comment) {
        // Add logic to add comments to publications.
    }

    public void sendMessage(User recipient, String messageText) {
        // Add logic to send messages.
    }
}
