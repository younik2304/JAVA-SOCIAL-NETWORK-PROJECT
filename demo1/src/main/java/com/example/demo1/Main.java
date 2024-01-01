package com.example.demo1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();

        // Set the icon

        Image icon = new Image("/Images/logo.png");
        stage.getIcons().add(icon);

        // Create a new scene with the loaded FXML
        Scene scene = new Scene(root);

        // Set the new scene on the stage
        stage.setScene(scene);

        // Set the title for the new scene
        stage.setTitle("login");

        // Show the stage with the new scene
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}