package com.example.demo1;

import java.util.List;

public class Publication {
    private int id;
    private User author;
    private String description ;
    private String type;
    private String content ;
    private List<Comment> comments;

    public Publication(User author, String content) {
        this.author = author;
        this.content = content;
        // Initialize the comments list.
    }

    // Getters and setters for the fields.

    public void addComment(Comment comment) {
        // Add logic to add comments to this publication.
    }
}
