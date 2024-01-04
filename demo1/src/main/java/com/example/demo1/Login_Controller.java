package com.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class Login_Controller {
    @FXML
    private Label loginLabel;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button signupButton;

    @FXML
    private Label createAccountLabel;

    @FXML
    void handleLoginButtonAction() {
        String email = emailField.getText();
        String password = passwordField.getText();
        if(email.isEmpty() || password.isEmpty() ){
            Test.showAlert("fields empty","you have to fill all the fields ");
        }else {
            //check if the account exists with the correct credentiels
            if (login(email, password)) {
                try {

                    SceneSwitcher.switchScene("home.fxml",Test.getStage(loginButton),"home feed");

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                Test.showAlert("Login Successful", "You are now logged in.");
            } else {
                Test.showAlert("Login Failed", "User not found or invalid credentials.");
            }
        }
    }

    @FXML
    void handleSignupButtonAction() {
        try {
            SceneSwitcher.switchScene("Signup.fxml",Test.getStage(loginButton),"signup");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean login(String email, String password) {
        try {
            DatabaseConnector databaseConnector = new DatabaseConnector();
            Connection connection = databaseConnector.getConnection();

            String sql = "SELECT id,firstname,lastname,gender,phonenumber,profilepicture,address FROM users WHERE email = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int id =resultSet.getInt(1);
                String firstname=resultSet.getString(2);
                String lastname=resultSet.getString(3);
                String gender =resultSet.getString(4);
                String phonenumber=resultSet.getString(5);
                String profilepicture=resultSet.getString(6);
                String address=resultSet.getString(7);
                User tempUser=new User(id,firstname,lastname,email,password,address,gender,phonenumber,profilepicture);
                UserSession.setLog_user(tempUser);
                return true ;
            }

            resultSet.close();
            preparedStatement.close();
            databaseConnector.closeConnection();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return false;
    }


}
