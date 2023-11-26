package com.example.demo1;

public class UserSession {
    private static int userId = -1;

    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int newUserId) {
        userId = newUserId;
    }
}