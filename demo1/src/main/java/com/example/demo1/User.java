package com.example.demo1;

import java.util.List;

public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String address;
    private String gander ;
    private String phone_number;


    private String profilePicture;
    private List<User> friends;
    private List<User> friendRequests;
    private List<Publication> publications;
    private List<Message> messages;

    public User(int id, String firstName, String lastName, String email, String password, String address, String gander, String phone_number, String profilePicture) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.address = address;
        this.gander = gander;
        this.phone_number = phone_number;
        this.profilePicture = profilePicture;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGander() {
        return gander;
    }

    public void setGander(String gander) {
        this.gander = gander;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
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
