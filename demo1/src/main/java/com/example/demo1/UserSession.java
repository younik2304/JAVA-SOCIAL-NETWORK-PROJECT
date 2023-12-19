package com.example.demo1;

public class UserSession {
    private static User log_user;

    public static User getLog_user() {
        return log_user;
    }

    public static void setLog_user(User log_user) {
        UserSession.log_user = log_user;
    }

    public static void logout(home_Controller h) {
        log_user=null;
        SceneSwitcher.switchScene("login.fxml",Test.getStage(h.getAddPub()),"login");
    }
}