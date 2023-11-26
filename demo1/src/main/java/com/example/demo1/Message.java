package com.example.demo1;

public class Message {
    private int id;
    private User sender;
    private User recipient;
    private String messageText;

    public Message(User sender, User recipient, String messageText) {
        this.sender = sender;
        this.recipient = recipient;
        this.messageText = messageText;
    }

    // Getters and setters for the fields.
}
