package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class home_Controller {

    @FXML
    private HBox navbar;

    @FXML
    private VBox sidebar;

    @FXML
    private VBox feed;


    @FXML
    private VBox center;

    @FXML
    private ImageView homeIcon;
    @FXML
    private ImageView userIcon;
    private AnchorPane manualPublicationAnchorPane;



    public void initialize() throws SQLException {
        // For testing purposes, let's create a default user image
        Image userImage = Test.returnUserProfileImage(UserSession.getLog_user().getProfilePicture());
        String userName = UserSession.getLog_user().getFirstName() + " " + UserSession.getLog_user().getLastName();

        // Create UserProfile component
        AnchorPane profile = Test.createUserProfile(userName, userImage, 50);
        //Test.createUserProfile(profile,userName,userImage,50);
        // Add UserProfile to the top AnchorPane
        Test.addEventHandlers(profile,UserSession.getLog_user());
        FXMLLoader anchorPaneLoader = new FXMLLoader(getClass().getResource("ajouterpub.fxml"));
        try {
            manualPublicationAnchorPane = anchorPaneLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error loading AnchorPane: " + e.getMessage());
        }
        navbar.getChildren().add(profile);
        center.getChildren().add(manualPublicationAnchorPane);
        populateSidebarWithUsers();
        populateFeedWithPublications();
        homeIcon.setOnMouseClicked(event -> {
            // Handle the home icon click event
            populateFeedWithPublications();
        });

        // Set the click event for the user icon
        userIcon.setOnMouseClicked(event -> {
            // Handle the user icon click event
            populateCenterWithUserPublications(UserSession.getLog_user());
        });

        // Add icons to the navbar
        // ...

        // You can add more initialization logic here
    }

    private void populateSidebarWithUsers() throws SQLException {
        // Fetch the list of users from the database
        List<User> userList = DatabaseConnector.getUsers();

        // Create a VBox to hold user profiles in the sidebar
        //VBox userProfilesVBox = new VBox(User.getNumberOfUsers()); // Adjust the spacing between user profiles

        // Populate the VBox with user profiles
        for (User user : userList) {
            Image userImage = Test.returnUserProfileImage(user.getProfilePicture());
            AnchorPane userProfile = Test.createUserProfile(user.getFirstName() + " " + user.getLastName(), userImage, 50);

            // Pass the user to addEventHandlers
            Test.addEventHandlers(userProfile, user);

            sidebar.getChildren().add(userProfile);
        }

        // Add the VBox to the sidebar
        //sidebar.getChildren().add(userProfilesVBox);
    }



    public  void populateFeedWithPublications() {
        if ( !center.getChildren().contains(manualPublicationAnchorPane)) {
            center.getChildren().add(manualPublicationAnchorPane);
        }
        // Fetch the list of publications from the database
        List<Publication> publicationList = null;
        try {
            publicationList = DatabaseConnector.getPublications();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Clear existing content in the feed
        feed.getChildren().clear();
        feed.setPrefHeight(652.0);

        // Populate the VBox with publication containers
        for (Publication publication : publicationList) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("publication.fxml"));
            try {
                VBox publicationContainer = loader.load();
                PublicationController publicationController = loader.getController();

                // Set the associated Publication
                publicationController.setPublication(publication);

                feed.getChildren().add(publicationContainer);
                System.out.println("Publication added to feed: " + publication.getId());
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error loading publication: " + e.getMessage());
            }
        }

    }


    public  void  populateCenterWithUserPublications(User user) {
        if(user.getId()==UserSession.getLog_user().getId()) {
            if (!center.getChildren().contains(manualPublicationAnchorPane)) {
                center.getChildren().add(manualPublicationAnchorPane);
            }
        }else
        if (center.getChildren().contains(manualPublicationAnchorPane)) {
            center.getChildren().remove(manualPublicationAnchorPane);
        }
        // Fetch the list of publications for the selected user from the database
        List<Publication> userPublications = null;
        try {
            userPublications = DatabaseConnector.getUserPublications(user.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Clear existing content in the feed
            feed.getChildren().clear();
            feed.setPrefHeight(652.0);

        // Populate the VBox with user's publications
        for (Publication publication : userPublications) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("publication.fxml"));
            try {
                VBox publicationContainer = loader.load();
                PublicationController publicationController = loader.getController();

                // Set the associated Publication
                publicationController.setPublication(publication);

                feed.getChildren().add(publicationContainer);
                System.out.println("Publication added to feed: " + publication.getId());
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error loading publication: " + e.getMessage());
            }
        }

    }
}




