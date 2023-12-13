package com.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.swing.*;

public class PublicationController {

    @FXML
    private VBox publicationContainer;

    @FXML
    private HBox publicationUser;

    @FXML
    private ImageView publicationImage;

    @FXML
    private TextField comment;
    @FXML
    private ScrollPane descriptioncontainer;
    @FXML
    private AnchorPane commentAnchorPane;

    public void initialize() {
        // Add initialization logic if needed
    }

    @FXML
    public void setPublication(Publication publication) {
        User user = publication.getAuthor();
        Image userImage = Test.returnUserProfileImage(user.getProfilePicture());
        AnchorPane profile = Test.createUserProfile(user.getFirstName() + " " + user.getLastName(), userImage, 50);
        Test.addEventHandlers(profile,user);
        Label time = new Label(publication.getTimestamp());
        publicationUser.getChildren().addAll(profile, time);

        // Replace TextArea with Label for displaying text
        Label descriptionLabel = new Label(publication.getDescription());
        descriptionLabel.setWrapText(true); // Enable text wrapping if needed
        descriptioncontainer.setContent(descriptionLabel);

        Image content = Test.returnPublicationImage(publication.getContent());
        publicationImage.setImage(content);
    }

    @FXML
    private void addComment() {
        // Implement logic to add a comment
    }
}
