package com.example.demo1;


import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static boolean emailValidation(String email) {
        // Regular expression for a simple email format
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        // Compile the regular expression
        Pattern pattern = Pattern.compile(emailRegex);

        // Create matcher object
        Matcher matcher = pattern.matcher(email);

        // Return true if the email matches the pattern, otherwise false
        return matcher.matches();
    }

    public static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static Stage getStage(Node node) {
        return (Stage) node.getScene().getWindow();
    }

    public static Image returnUserProfileImage(String profilePictureName) {
        String profilePicturePath = "C:\\Users\\LENOVO\\Documents\\java_project\\demo1\\src\\main\\resources\\UserProfiles";
        File profilePictureFile = new File(profilePicturePath, profilePictureName + ".png");

        if (!profilePictureFile.exists()) {
            // If the file doesn't exist locally, download it from Cloudinary
            CloudinaryImageUtility.downloadProfileImage(profilePictureName, profilePicturePath);
        }

        String imageUrl = null;
        try {
            imageUrl = profilePictureFile.toURI().toURL().toExternalForm();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return new Image(imageUrl);
    }

    public static Image returnPublicationImage(String contentImageName) {
        String contentImagePath = "C:\\Users\\LENOVO\\Documents\\java_project\\demo1\\src\\main\\resources\\PUBLICATION_IMAGES";
        File contentImageFile = new File(contentImagePath, contentImageName + ".png");

        if (!contentImageFile.exists()) {
            // If the file doesn't exist locally, download it from Cloudinary
            CloudinaryImageUtility.downloadPublicationImage(contentImageName, contentImagePath);
        }

        String contentImageUrl = null;
        try {
            contentImageUrl = contentImageFile.toURI().toURL().toExternalForm();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return new Image(contentImageUrl);
    }

    public static AnchorPane createUserProfile(String userName, Image userImage, double imageSize) {
        // Create AnchorPane to hold the profile components
        AnchorPane profilePane = new AnchorPane();

        // Create ImageView
        ImageView imageView = new ImageView(userImage);
        imageView.setFitWidth(imageSize);
        imageView.setFitHeight(imageSize);

        // Clip the image to a circle
        imageView.setClip(new Circle(imageSize / 2.0, imageSize / 2.0, imageSize / 2.0));

        // Create Label for the username
        Label nameLabel = new Label(userName);
        nameLabel.setTextFill(Color.WHITE); // Set text color to white
        nameLabel.setStyle("-fx-font-size: 20px;"); // Set font size to 20px

        // Set the position of ImageView and Label
        AnchorPane.setTopAnchor(imageView, 0.0);
        AnchorPane.setLeftAnchor(imageView, 0.0);

        AnchorPane.setTopAnchor(nameLabel, 0.0);
        AnchorPane.setLeftAnchor(nameLabel, imageSize + 10.0); // Adjust the distance between image and label

        // Calculate the vertical center position in the navbar
        double verticalCenter = (70 - imageSize) / 2.0;

        // Set the vertical center position
        AnchorPane.setTopAnchor(profilePane, verticalCenter);

        // Add ImageView and Label to the AnchorPane
        profilePane.getChildren().addAll(imageView, nameLabel);


        return profilePane;
    }

    public static void addEventHandlers(AnchorPane profile, User user) {
        // Mouse click event handler
        profile.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    // Handle left mouse click (primary button)
                    // Redirect to another scene here
                    Test.redirectToUserProfile(user, (Stage) profile.getScene().getWindow());

                }
            }
        });

        // Hover event handlers
        profile.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // Handle mouse enter (hover)
                // Add hover effects or actions here
                profile.setStyle("-fx-background-color: lightgray;"); // Example: Change background color on hover
            }
        });

        profile.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // Handle mouse exit (hover)
                // Remove hover effects or actions here
                profile.setStyle("-fx-background-color: transparent;"); // Example: Reset background color on exit
            }
        });
    }

    private static void redirectToUserProfile(User user, Stage stage) {
        // Implement the logic to redirect to the user profile scene
        // You can use FXMLLoader to load the user profile FXML and set the user data
        // For now, let's print a message to simulate the redirection
        System.out.println("Redirecting to user profile: " + user.getFirstName() + " " + user.getLastName());

        // Assuming you have a method to populate the center with the selected user's publications
        FXMLLoader loader = new FXMLLoader(Test.class.getResource("home.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        home_Controller controller = loader.getController();
        controller.populateCenterWithUserPublications(user);

        // Replace 'yourAnchorPane' with the actual anchor pane holding your content
        // This assumes that the 'home_Controller' class has a reference to the 'feed' VBox
        //controller.populateCenterWithUserPublications(user);

        // Set the new scene
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    public static void addEventHandlerstochat(AnchorPane profile, User user) {
        // Mouse click event handler

        // Hover event handlers
        profile.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // Handle mouse enter (hover)
                // Add hover effects or actions here
                profile.setStyle("-fx-background-color: lightgray;"); // Example: Change background color on hover
            }
        });

        profile.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // Handle mouse exit (hover)
                // Remove hover effects or actions here
                profile.setStyle("-fx-background-color: transparent;"); // Example: Reset background color on exit
            }
        });
    }
}
