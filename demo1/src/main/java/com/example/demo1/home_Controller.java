package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;

import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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
    @FXML
    private Button addPub;

    @FXML
    private Button userInfos;
    @FXML
    private HBox footer ;

    @FXML
    private ImageView friendRequests;
    @FXML
    private ImageView messages;

    // Create a ChatClient for the logged-in user
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
        FXMLLoader anchorPaneLoader = new FXMLLoader(getClass().getResource("ajouterpub.fxml"));
        addPub.setOnMouseClicked(event -> {
            // Create an Alert
            VBox userinfos=new VBox();
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle("Add Publication");
            try {
                userinfos = anchorPaneLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // Set the content of the Alert to manualPublicationAnchorPane
            alert.getDialogPane().setContent(userinfos);

            // Create a button for the OK action
            ButtonType okButton = new ButtonType("send publication", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(okButton);

            // Get the addPubController
            addPubController controller = anchorPaneLoader.getController();

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
        FXMLLoader infosLoader = new FXMLLoader(getClass().getResource("UserInfos.fxml"));
        HBox userinfos;
        try {
            userinfos = infosLoader.load();
            // Get the controller without setting the stage
            UserInfos controller = infosLoader.getController();
            controller.initialize();

            userInfos.setOnMouseClicked(e -> {
                Alert alert = new Alert(Alert.AlertType.NONE);
                alert.setTitle("User infos");

                // Set the content of the Alert to userinfos
                alert.getDialogPane().setContent(userinfos);

                // Add a custom button to the alert to handle closing
                ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(closeButton);

                // Show the Alert
                alert.showAndWait().ifPresent(response -> {
                    if (response == closeButton) {
                        // Handle the close button action if needed
                    }
                });
            });

        } catch (IOException e) {
            System.out.println("Error loading UserInfos.fxml: " + e.getMessage());
        }


        // Set up the event handler for the addPub button

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
            searchUsers(DatabaseConnector.getFriendRequests());
        });
        Button logout=new Button("log out ");
        footer.getChildren().add(logout);
        logout.setOnAction(e->{
            UserSession.logout(this);
        });

    }

    public void searchUsers(List<FriendRequests> fr_reqs) {
        feed.getChildren().clear();
        feed.setPrefHeight(652.0);
        AnchorPane search = new AnchorPane();

        // Create TextField dynamically
        TextField searchField = new TextField();
        searchField.setPromptText("Search users...");
        searchField.setLayoutX(10.0);
        searchField.setLayoutY(10.0);

        // Create Button dynamically
        Button searchButton = new Button("Search");
        searchButton.setLayoutX(200.0);
        searchButton.setLayoutY(10.0);
        search.getChildren().addAll(searchField, searchButton);
        feed.getChildren().addAll(search);
        for (FriendRequests request : fr_reqs) {
            HBox userBox = new HBox();
            Button acceptButton = new Button("Accept");
            Button rejectButton = new Button("Reject");
            User sender = DatabaseConnector.getUserById(request.getSenderId());
            // Set actions for accept and reject buttons
            acceptButton.setOnAction(event -> {
                try {
                    DatabaseConnector.createFriend(request.getSenderId());
                    DatabaseConnector.deleteFriendRequest(request.getRequestId());
                    searchUsers(DatabaseConnector.getFriendRequests());
                    sidebar.getChildren().clear();
                    try {
                        populateSidebarWithUsers();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            rejectButton.setOnAction(event -> {
                try {
                    DatabaseConnector.deleteFriendRequest(request.getRequestId());
                    searchUsers(DatabaseConnector.getFriendRequests());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            userBox.getChildren().addAll(Test.createUserProfile(sender.getFirstName() + " " + sender.getLastName(), Test.returnUserProfileImage(sender.getProfilePicture()), 40), acceptButton, rejectButton);
            feed.getChildren().add(userBox);
        }

        searchButton.setOnAction(event -> {
            String[]username =searchField.getText().split(" ");
            if (username[0]==UserSession.getLog_user().getFirstName() && username[1]==UserSession.getLog_user().getLastName()) {
                Test.showAlert("wrong input ","you just searched your name ");
            }else {
                User user = DatabaseConnector.getUserByName(searchField.getText());
                if (user == null) {
                    Test.showAlert("no user", "there are no users with the name :" + searchField.getText());
                } else {
                    List<User> friends = DatabaseConnector.getFriends();
                    for (User friend : friends){
                        System.out.println("friend"+ friend.getLastName());
                    }
                    displayFriendRequests(user, fr_reqs, friends);
                }
            }
        });


    }
    public void displayFriendRequests(User user,List<FriendRequests>reqs,List<User>friends ){
        feed.getChildren().clear();
        feed.setPrefHeight(652.0);
        HBox userBox=new HBox();
        if (reqs.stream().anyMatch(request -> request.getSenderId() == user.getId() || request.getReceiverId() == user.getId())){

            Button acceptButton = new Button("Accept");
            Button rejectButton = new Button("Reject");
            // Set actions for accept and reject buttons
            acceptButton.setOnAction(event -> {
                try {
                    DatabaseConnector.createFriend(user.getId());
                    DatabaseConnector.deleteFriendRequest(reqs.get(user.getId()).getRequestId());
                    searchUsers(DatabaseConnector.getFriendRequests());
                    sidebar.getChildren().clear();
                    try {
                        populateSidebarWithUsers();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            rejectButton.setOnAction(event -> {
                try {

                    DatabaseConnector.deleteFriendRequest(reqs.get(user.getId()).getRequestId());
                    searchUsers(DatabaseConnector.getFriendRequests());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            userBox.getChildren().addAll(Test.createUserProfile(user.getFirstName() + " " + user.getLastName(), Test.returnUserProfileImage(user.getProfilePicture()), 40), acceptButton, rejectButton);
        }else if (friends.stream().anyMatch(friend -> friend.getId() == user.getId())){
            System.out.println("friend" +user.getFirstName());
            Button unfriendButton = new Button("unfriend");
            unfriendButton.setOnAction(event -> {
                DatabaseConnector.removeFriend(user.getId());
                searchUsers(DatabaseConnector.getFriendRequests());
                sidebar.getChildren().clear();
                try {
                    populateSidebarWithUsers();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            userBox.getChildren().addAll(Test.createUserProfile(user.getFirstName() + " " + user.getLastName(), Test.returnUserProfileImage(user.getProfilePicture()), 40), unfriendButton);
        }else {
            System.out.println("not a friend "+user.getLastName());
            Button sendrequest = new Button("send a request");
            sendrequest.setOnAction(event -> {
                try {
                    DatabaseConnector.createFriendRequest(UserSession.getLog_user().getId(),user.getId());
                    searchUsers(DatabaseConnector.getFriendRequests());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            userBox.getChildren().addAll(Test.createUserProfile(user.getFirstName() + " " + user.getLastName(), Test.returnUserProfileImage(user.getProfilePicture()), 40), sendrequest);
        }
        feed.getChildren().add(userBox);
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

                feed.getChildren().add(publicationContainer);
                System.out.println("Publication added to feed: " + publication.getId());
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error loading publication: " + e.getMessage());
            }
        }

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

                feed.getChildren().add(publicationContainer);
                System.out.println("Publication added to feed: " + publication.getId());
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error loading publication: " + e.getMessage());
            }
        }

    }

    public VBox getFeed() {
        return feed;
    }

    public void refreshFeed() {
        populateFeedWithPublications();
    }
}




