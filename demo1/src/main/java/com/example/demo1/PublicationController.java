package com.example.demo1;

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
import java.sql.Statement;
import java.sql.Timestamp;
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
    private void addComment() {
        // Implement logic to add a comment
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
                boolean del=DatabaseConnector.deletePublication(associatedPublication);
                if(del) {
                    Test.showAlert("Deletion of publication is succesfull", "you will be prompted to the home feed ");
                    FXMLLoader loader = new FXMLLoader(Test.class.getResource("home.fxml"));
                    Parent root;
                    try {
                        root = loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }

                    home_Controller controller = loader.getController();
                    controller.populateFeedWithPublications();

                }else
                    Test.showAlert("error","error deleting the publication");
                // You can perform additional actions if needed
            }
        }
    }
}
