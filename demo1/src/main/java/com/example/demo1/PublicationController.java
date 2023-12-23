package com.example.demo1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.swing.*;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class PublicationController {

    @FXML
    private VBox publicationContainer;

    @FXML
    private HBox publicationUser;

    @FXML
    private ImageView publicationImage;

    @FXML
    private Button comments;

    @FXML
    private Button sharePublication;

    @FXML
    private Button deletePublication; // Add the delete button

    @FXML
    private ScrollPane descriptionContainer;

    private Publication publication;

    private home_Controller homeController;

    public void setHomeController(home_Controller homeController) {
        this.homeController = homeController;
    }

    public void initialize() {
        // Add initialization logic if needed
    }

    public Publication getPublication() {
        return publication;
    }

    @FXML
    public void setPublication(Publication publication) {
        this.publication = publication;

        User user = publication.getAuthor();
        Image userImage = Test.returnUserProfileImage(user.getProfilePicture());
        AnchorPane profile = Test.createUserProfile(user.getFirstName() + " " + user.getLastName(), userImage, 50);
        Test.addEventHandlers(profile, user);
        Label time = new Label(publication.getTimestamp());
        time.centerShapeProperty();
        publicationUser.getChildren().addAll(profile, time);

        Label descriptionLabel = new Label(publication.getDescription());
        descriptionLabel.setWrapText(true);
        descriptionContainer.setContent(descriptionLabel);

        Image content = Test.returnPublicationImage(publication.getContent());
        publicationImage.setImage(content);

        // Check if the publication belongs to the logged-in user
        if (publication.getAuthor().getId() == UserSession.getLog_user().getId()) {
            // If yes, show the delete button
            deletePublication.setVisible(true);
        } else {
            // If not, hide the delete button
            deletePublication.setVisible(false);
        }
    }

    @FXML
    private void sharePublicationClicked() {
        // Access the associated Publication and perform actions
        Publication associatedPublication = getPublication();

        if (associatedPublication != null) {
            if (associatedPublication.getAuthor().getId() == UserSession.getLog_user().getId()) {
                Test.showAlert("Can't share this publication", "The publication you wish to share belongs to you");
            } else {
                TextInputDialog dialog = new TextInputDialog(associatedPublication.getDescription());
                dialog.setTitle("Edit Description");
                dialog.setHeaderText("Edit the description before sharing:");
                dialog.setContentText("New Description:");

                dialog.showAndWait().ifPresent(newDescription -> {
                    associatedPublication.setDescription(newDescription);
                    DatabaseConnector.sharePublication(associatedPublication);
                    if (homeController != null) {
                        homeController.refreshFeed();
                    }
                });
            }
        }
    }

    @FXML
    private void deletePublicationClicked() {
        // Access the associated Publication
        Publication associatedPublication = getPublication();

        if (associatedPublication != null) {
            // Show a confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Publication");
            alert.setHeaderText("Confirm Deletion");
            alert.setContentText("Are you sure you want to delete this publication?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // User clicked OK, delete the publication
                boolean del = DatabaseConnector.deletePublication(associatedPublication);
                if (del) {
                    Test.showAlert("Deletion of publication is successful", "you will be prompted to the home feed ");
                    if (homeController != null) {
                        homeController.refreshFeed();
                    }
                } else {
                    Test.showAlert("error", "error deleting the publication");
                }


            }
        }
    }
    @FXML
    private void showComments() {
        // Access the associated Publication
        Publication associatedPublication = getPublication();

        if (associatedPublication != null) {
            // Retrieve comments for the publication from the database
            List<Comment> comments = null;
            try {
                comments = DatabaseConnector.getCommentsForPublication(associatedPublication.getId());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            // Create VBox to hold comments
            VBox commentsBox = new VBox();
            commentsBox.setSpacing(40);
            // Display comments in the VBox
            for (Comment comment : comments) {
                HBox commentBox = new HBox();
                VBox details=new VBox();
                details.setSpacing(10);
                // Create user profile for the commenter
                User commenter = comment.getAuthor();
                Image commenterImage = Test.returnUserProfileImage(commenter.getProfilePicture());
                AnchorPane commenterProfile = Test.createUserProfile(commenter.getFirstName() + " " + commenter.getLastName(), commenterImage, 50);
                //Test.addEventHandlers(commenterProfile, commenter);

                // Create label for the comment text
                Label commentLabel = new Label(comment.getText() + " - " + comment.getTimestamp());
                commentLabel.setWrapText(true);
                commentLabel.setStyle("-fx-text-fill: white;");
                details.getChildren().addAll(commenterProfile, commentLabel);
                commentBox.getChildren().add(details);
                commentsBox.getChildren().add(commentBox);
            }
            commentsBox.setStyle("-fx-background-color: black;");
            // Create TextField and Button for adding new comments
            TextField newCommentField = new TextField();
            Button addCommentButton = new Button("Add Comment");
            Alert alert = new Alert(Alert.AlertType.NONE);
            addCommentButton.setOnAction(event -> {
                String comment = newCommentField.getText();
                try {
                    DatabaseConnector.addComment(associatedPublication.getId(), UserSession.getLog_user().getId(), comment);

                    // Schedule the closing of the alert on the JavaFX Application Thread
                   alert.close();
                   Test.showAlert("comment added ","the comments you added has been succesfully sent ");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            commentsBox.getChildren().addAll(newCommentField, addCommentButton);

            // Wrap the commentsBox in a ScrollPane
            ScrollPane scrollPane = new ScrollPane(commentsBox);
            scrollPane.setPrefViewportHeight(300);
            scrollPane.setPrefViewportWidth(300);
            scrollPane.setFitToWidth(true);

            // Create and show an Alert with the ScrollPane as content
            VBox scroll=new VBox(scrollPane,newCommentField,addCommentButton);
            alert.setTitle("Comments");
            alert.getDialogPane().setContent(scroll);

            // Add a close button to the Alert
            ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(closeButton);

            // Show the Alert
            alert.showAndWait();
        }
    }



}
