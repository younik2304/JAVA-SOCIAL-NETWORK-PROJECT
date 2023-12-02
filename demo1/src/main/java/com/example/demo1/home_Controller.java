package com.example.demo1;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.io.File;
import java.net.MalformedURLException;

public class home_Controller {

    @FXML
    private AnchorPane footer;

    @FXML
    private AnchorPane homefeed;

    @FXML
    private HBox navbar;

    @FXML
    private ImageView profile_picture;

    @FXML
    private Label user_id;

    public void initialize() {
        // Initialize user ID label
        user_id.setText("User ID: " + UserSession.getLog_user().getId()); // Replace with your logic to get the user ID

        // Initialize profile picture
        String profilePicturePath = "C:\\Users\\LENOVO\\Documents\\java_project\\demo1\\src\\main\\resources\\IMAGES";
        CloudinaryImageUtility.downloadImage(UserSession.getLog_user().getProfilePicture(), profilePicturePath);

// Construct the file URL
        try {
            File file = new File(profilePicturePath, UserSession.getLog_user().getProfilePicture()+".png");
            String imageUrl = file.toURI().toURL().toExternalForm();
            System.out.println("Image URL: " + imageUrl);

// Create the Image object
            Image profilePicture = new Image(imageUrl);
            profile_picture.setImage(profilePicture);
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        }
    }

}
