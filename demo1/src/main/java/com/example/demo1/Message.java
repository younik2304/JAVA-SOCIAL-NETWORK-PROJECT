package com.example.demo1;

import java.sql.Timestamp;

public class Message {
    private int id;
    private User sender;
    private User recipient;
    private String messageText;
    private Timestamp timestamp;

    public Message(int id, User sender, User recipient, String messageText, Timestamp timestamp) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    public Message(String data,Timestamp t) {
        this.messageText=data;
        this.timestamp=t;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
