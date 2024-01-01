package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.SQLException;

public class addPubController {

    private static String imagepath;
    @FXML
    private TextField description;

    @FXML
    private ImageView pub_image;

    private home_Controller homeController;

    public void setHomeController(home_Controller homeController) {
        this.homeController = homeController;
    }
    private static Stage stage;

    public static void setStage(Stage stage) {
        addPubController.stage = stage;
    }

    public void loadImage(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a Profile Picture");

        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);

        File selectedFile = fileChooser.showOpenDialog(Test.getStage(pub_image));
        if (selectedFile != null) {
            // Get the file path without the file: prefix
            String imagePath = selectedFile.toURI().getPath();

            // Upload the image to Cloudinary using the file path
            //
            // Display the image in your UI if needed
            Image image = new Image("file:" + imagePath);
            pub_image.setImage(image);
            addPubController.imagepath = imagePath;
        }
        else imagepath= "IMAGES/letter-a-xxl.png";
    }

    public static String getImagepath() {
        return imagepath;
    }

    public TextField getDescription() {
        return description;
    }

    public ImageView getPub_image() {
        return pub_image;
    }

    public void sendPublication() throws SQLException {
        // Get the user ID of the currently logged-in user
        int authorId = UserSession.getLog_user().getId();

        // Get the description from the TextField
        String descriptionText = description.getText();

        // Check if the user has selected an image


        int publicationAdded = DatabaseConnector.addPublication(authorId, descriptionText);

        // Upload the image to Cloudinary and get the public ID

        // Add the publication to the database

        if (publicationAdded != -1) {
            CloudinaryImageUtility.uploadPublicationImage(addPubController.imagepath, String.valueOf(publicationAdded));

            Test.showAlert("Publication sent successfully.","publication " +publicationAdded +" is added ");
                // Optionally, clear the description TextField and reset the image
                description.clear();
                pub_image.setImage(null);
                addPubController.imagepath = "";
                if (homeController != null) {
                    homeController.refreshFeed();
                }

            } else {
                Test.showAlert("Failed to send publication.", "no return id ");
                // Handle the case where the publication couldn't be added to the database
            }
        }
    }
