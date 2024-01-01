package com.example.demo1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        time.setStyle("-fx-text-fill: white;");
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
            commentsBox.setSpacing(10);

            // Display comments in the VBox
            for (Comment comment : comments) {
                // Create HBox for each comment using the structure from createMessageContainer
                HBox commentBox = createCommentContainer(comment.getAuthor(),comment.getText(), comment.getTimestamp(), comment.getAuthor().getId()==UserSession.getLog_user().getId());
                commentsBox.getChildren().add(commentBox);
            }

            // Create TextField and Button for adding new comments
            TextField newCommentField = new TextField();
            Button addCommentButton = new Button("Add Comment");
            addCommentButton.setOnAction(event -> {
                String commentText = newCommentField.getText();
                try {
                    DatabaseConnector.addComment(associatedPublication.getId(), UserSession.getLog_user().getId(), commentText);

                    // Update the comments box with the new comment
                    HBox newCommentBox = createCommentContainer(UserSession.getLog_user(),commentText, Timestamp.valueOf(LocalDateTime.now()), true);
                    commentsBox.getChildren().add(newCommentBox);

                    // Clear the input field
                    newCommentField.clear();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            commentsBox.setStyle("-fx-background-color: #a3abb1 ;");
            commentsBox.setSpacing(20);
            addCommentButton.setStyle("-fx-background-color: #555555;\n" +
                    "    -fx-text-fill: white;");
            commentsBox.getChildren().addAll(newCommentField, addCommentButton);

            // Wrap the commentsBox in a ScrollPane
            ScrollPane scrollPane = new ScrollPane(commentsBox);
            scrollPane.setPrefViewportHeight(300);
            scrollPane.setPrefViewportWidth(300);
            scrollPane.setFitToWidth(true);

            // Create and show an Alert with the ScrollPane as content
            Alert alert = new Alert(Alert.AlertType.NONE);
            VBox scroll = new VBox(scrollPane, newCommentField, addCommentButton);
            alert.setTitle("Comments");
            alert.getDialogPane().setContent(scroll);

            // Add a close button to the Alert
            ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(closeButton);

            // Show the Alert
            alert.showAndWait();
        }
    }

    // Helper method to create an HBox for a comment
    private HBox createCommentContainer(User author, String content, Timestamp timestamp, boolean sentByCurrentUser) {
        HBox commentBox = new HBox();
        Label commentLabel = new Label(content);
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM");
        String formattedDateTime = localDateTime.format(formatter);
        Label timestampLabel = new Label(formattedDateTime);

        commentLabel.setWrapText(true);
        commentLabel.setPadding(new Insets(10));

        // Customize the appearance of the comment box and labels based on the sender
        if (sentByCurrentUser) {
            commentBox.setAlignment(Pos.CENTER_RIGHT);
            commentLabel.setTranslateX(-5);
            commentLabel.setStyle("-fx-background-color: #757575; -fx-background-radius: 15; -fx-padding: 15px; -fx-text-fill: white;");
        } else {
            commentBox.setAlignment(Pos.CENTER_LEFT);
            commentLabel.setTranslateX(5);
            commentLabel.setStyle("-fx-background-color: #424242; -fx-background-radius: 15; -fx-padding: 15px; -fx-text-fill: white;");
        }

        Label user = new Label(author.getFirstName() + " " + author.getLastName() + " / \n");

        // Add comment content and timestamp labels to the commentBox
        VBox commentContent = new VBox(user, commentLabel, timestampLabel);
        commentContent.setSpacing(0);
        commentBox.getChildren().add(commentContent);

        return commentBox;
    }


}
