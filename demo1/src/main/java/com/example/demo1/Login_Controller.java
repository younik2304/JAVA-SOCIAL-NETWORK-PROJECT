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
            if (login(email, password)) {
                try {
                    SceneSwitcher.switchScene("home.fxml",Test.getStage(loginButton),"home feed");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                // User is authenticated, navigate to the main application or dashboard.
                // Replace the following line with your navigation logic.
                Test.showAlert("Login Successful", "You are now logged in.");
            } else {
                Test.showAlert("Login Failed", "User not found or invalid credentials.");
            }
        }
    }

    @FXML
    void handleSignupButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("signup.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            Stage stage = (Stage) signupButton.getScene().getWindow();

            stage.setScene(scene);
            stage.setTitle("signup");
            stage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean login(String email, String password) {
        try {
            DatabaseConnector databaseConnector = new DatabaseConnector();
            Connection connection = databaseConnector.getConnection();

            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return true;
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
