package com.example.demo1;

public class Comment {
    private int id;
    private User author;
    private String text;

    public Comment(User author, String text) {
        this.author = author;
        this.text = text;
    }

    // Getters and setters for the fields.
}
