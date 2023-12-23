package com.example.demo1;
// FriendshipRequest.java
public class FriendRequests {
    private int requestId;
    private int senderId;
    private int receiverId;


    public FriendRequests( int requestId,int  senderId, int receiverId) {
        this.requestId=requestId;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    // Getters and setters for fields

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }



}
