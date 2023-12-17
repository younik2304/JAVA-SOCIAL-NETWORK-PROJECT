package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class addPubController{
    private static String imagepath="";

    @FXML
    private TextField description;

    @FXML
    private ImageView pub_image;

    public void loadImage(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(Test.class.getResource("home.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        home_Controller controller = loader.getController();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a Profile Picture");

        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);
          // Debug print


        File selectedFile = fileChooser.showOpenDialog(Test.getStage(pub_image));
        if (selectedFile != null) {
            // Get the file path without the file: prefix
            String imagePath = selectedFile.toURI().getPath();

            // Upload the image to Cloudinary using the file path
            //
            // Display the image in your UI if needed
            Image image = new Image("file:" + imagePath);
            pub_image.setImage(image);
            imagepath=imagePath;
        }
    }

    public void sendPublication(ActionEvent actionEvent) throws SQLException {
        FXMLLoader loader = new FXMLLoader(Test.class.getResource("home.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        home_Controller controller = loader.getController();
        // Get the user ID of the currently logged-in user
        int authorId = UserSession.getLog_user().getId();

        // Get the description from the TextField
        String descriptionText = description.getText();

        // Check if the user has selected an image
        if (imagepath.isEmpty()) {
            // Handle the case where no image is selected (optional)
            System.out.println("No image selected for the publication.");
            imagepath="default.png";

        }
        int publicationAdded = DatabaseConnector.addPublication(authorId, descriptionText);
        // Upload the image to Cloudinary and get the public ID


        // Add the publication to the database


        if (publicationAdded != -1) {
            CloudinaryImageUtility.uploadPublicationImage(imagepath, String.valueOf(publicationAdded));
            System.out.println("Publication sent successfully.");
            // Optionally, clear the description TextField and reset the image
            description.clear();
            pub_image.setImage(null);
            imagepath = "";
            controller.initialize();

        } else {
            Test.showAlert("Failed to send publication.", "no return id ");
            // Handle the case where the publication couldn't be added to the database
        }
    }
}
