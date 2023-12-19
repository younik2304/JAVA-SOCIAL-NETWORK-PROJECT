package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
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
    private HBox footer;

    @FXML
    private VBox center;

    @FXML
    private ImageView homeIcon;
    @FXML
    private ImageView userIcon;
    @FXML
    private Button addPub;

    @FXML
    private Button userInfos;

    @FXML
    private ImageView friendRequests;


    public Button getAddPub() {
        return addPub;
    }

    public void initialize() throws SQLException {
        // For testing purposes, let's create a default user image
        Image userImage = Test.returnUserProfileImage(UserSession.getLog_user().getProfilePicture());
        String userName = UserSession.getLog_user().getFirstName() + " " + UserSession.getLog_user().getLastName();

        // Create UserProfile component
        AnchorPane profile = Test.createUserProfile(userName, userImage, 50);
        //Test.createUserProfile(profile,userName,userImage,50);
        // Add UserProfile to the top AnchorPane
        Test.addEventHandlers(profile,UserSession.getLog_user());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ajouterpub.fxml"));
        VBox manualPublicationAnchorPane;
        try {
            manualPublicationAnchorPane = loader.load();
            // Get the controller and set the home_Controller instance

            // Set the stage for addPubController

        } catch (IOException e) {
            throw new RuntimeException("Error loading AnchorPane: " + e.getMessage());
        }
        FXMLLoader infos = new FXMLLoader(getClass().getResource("UserInfos.fxml"));
        VBox userinfos;
        try {
            userinfos = infos.load();
            // Get the controller and set the home_Controller instance

            // Set the stage for addPubController

        } catch (IOException e) {
            throw new RuntimeException("Error loading AnchorPane: " + e.getMessage());
        }

        // Set up the event handler for the addPub button
        addPub.setOnMouseClicked(event -> {
            // Create an Alert
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle("Add Publication");

            // Set the content of the Alert to manualPublicationAnchorPane
            alert.getDialogPane().setContent(manualPublicationAnchorPane);

            // Create a button for the OK action
            ButtonType okButton = new ButtonType("send publication", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(okButton);

            // Get the addPubController
            addPubController controller = loader.getController();

            // Set the action for the OK button
            Node okButtonNode = alert.getDialogPane().lookupButton(okButton);
            okButtonNode.addEventFilter(ActionEvent.ACTION, e -> {
                // Call the sendPublication method in addPubController
                try {
                    controller.sendPublication();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                // Close the alert window
                alert.close();
                populateFeedWithPublications();
            });

            // Show the Alert
            alert.showAndWait();

            // Now that the button is clicked, set the stage for addPubController
            controller.setHomeController(this);
            controller.setStage((Stage) addPub.getScene().getWindow());
        });

        navbar.getChildren().add(profile);
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
        friendRequests.setOnMouseClicked(event->{
            displayFriendRequests();
        });
        userinfos.setOnMouseClicked(e->{
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle("User infos");

            // Set the content of the Alert to manualPublicationAnchorPane
            alert.getDialogPane().setContent(userinfos);
            UserInfos controller = infos.getController();
        });
        Button logout=new Button("log out ");
        logout.setOnAction(e->{
            UserSession.logout(this);
        });
    }

    public void displayFriendRequests() {
        feed.getChildren().clear();
        feed.setPrefHeight(652.0);
        AnchorPane search  = new AnchorPane();

        // Create TextField dynamically
        TextField searchField = new TextField();
        searchField.setPromptText("Search users...");
        searchField.setLayoutX(10.0);
        searchField.setLayoutY(10.0);

        // Create Button dynamically
        Button searchButton = new Button("Search");
        searchButton.setLayoutX(200.0);
        searchButton.setLayoutY(10.0);
        search.getChildren().addAll(searchField,searchButton);
        feed.getChildren().add(search);
        List<Friendship> fr_req=DatabaseConnector.getFriendRequests();
        searchButton.setOnAction(event -> displaysearchedUsers(searchField.getText()));

        for (Friendship friendship : fr_req)
        {
            HBox friendReq=new HBox();
            Button accept=new Button("accept");
            Button refuse=new Button("refuse");
            User user =DatabaseConnector.getUserById(friendship.getUser1Id());
            Image userImage = Test.returnUserProfileImage(user.getProfilePicture());
            AnchorPane userProfile = Test.createUserProfile(user.getFirstName() + " " + user.getLastName(), userImage, 50);
            friendReq.getChildren().addAll(userProfile,accept,refuse);
            accept.setOnAction(event -> FriendRequestService.acceptFriendRequest(friendship.getFriendshipId(),this));
            refuse.setOnAction(event -> FriendRequestService.rejectFriendRequest(friendship.getFriendshipId(),this));

            feed.getChildren().add(friendReq);
        }
    }

    private void displaysearchedUsers(String searchField) {
        List <User> users=DatabaseConnector.getUserByName(searchField);
        DatabaseConnector.get
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

                // Set the reference to home_Controller
                publicationController.setHomeController(this);

                feed.getChildren().add(publicationContainer);
                System.out.println("Publication added to feed: " + publication.getId());
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error loading publication: " + e.getMessage());
            }

        }

    }
    public void refreshFeed() {
        populateFeedWithPublications();
    }


    public  void  populateCenterWithUserPublications(User user) {
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

                // Set the reference to home_Controller
                publicationController.setHomeController(this);

                feed.getChildren().add(publicationContainer);
                System.out.println("Publication added to feed: " + publication.getId());
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error loading publication: " + e.getMessage());
            }
        }

    }
}




