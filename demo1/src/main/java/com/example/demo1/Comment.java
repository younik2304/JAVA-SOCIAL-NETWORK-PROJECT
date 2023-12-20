package com.example.demo1;

import java.sql.Timestamp;

public class Comment {
    private int id;
    private int publication_id;
    private User author;
    private String text;
    private Timestamp timestamp;

    public Comment(int id, int publication_id, User author, String text, Timestamp timestamp) {
        this.id = id;
        this.publication_id = publication_id;
        this.author = author;
        this.text = text;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPublication_id() {
        return publication_id;
    }

    public void setPublication_id(int publication_id) {
        this.publication_id = publication_id;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    // Getters and setters for the fields.
}
