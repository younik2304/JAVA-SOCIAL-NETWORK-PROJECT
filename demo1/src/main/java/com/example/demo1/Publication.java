package com.example.demo1;

import java.util.List;

public class Publication {
    private int id;
    private User author;
    private String description ;

    private String content ;
    private String timestamp;
    private List<Comment> comments;

    public Publication(int id, User author, String description, String content, String timestamp) {
        this.id = id;
        this.author = author;
        this.description = description;
        this.content = content;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    // Getters and setters for the fields.

    public void addComment(Comment comment) {
        // Add logic to add comments to this publication.
    }
}
