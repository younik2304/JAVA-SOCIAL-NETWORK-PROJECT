package com.example.demo1;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class home_Controller {
    @FXML
    public BorderPane border;

    @FXML
    public HBox navbar;

    @FXML
    public VBox sidebar;

    @FXML
    public VBox feed;
    @FXML
    public ScrollPane scroll;


    @FXML
    public VBox center;

    @FXML
    public ImageView homeIcon;
    @FXML
    public ImageView userIcon;
    @FXML
    public Button addPub;

    @FXML
    public Button userInfos;
    @FXML
    public HBox footer ;

    @FXML
    public ImageView friendRequests;
    @FXML
    public ImageView messages;
    @FXML
    private Button logoutButton;
    public User currentChatUser;
    public Client activeClient;
    public  ChatController chatController; // Add this field

    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }

    public VBox getSidebar() {
        return sidebar;
    }
    private static final Logger logger = LoggerFactory.getLogger(home_Controller.class);

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
        addPub.setOnMouseClicked(event -> {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Add Publication");

            // Create a new FXMLLoader instance for each dialog
            FXMLLoader anchorPaneLoader = new FXMLLoader(getClass().getResource("ajouterpub.fxml"));

            // Load the content from ajouterpub.fxml
            try {
                Parent content = anchorPaneLoader.load();
                dialog.getDialogPane().setContent(content);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Add buttons to the dialog
            ButtonType okButtonType = new ButtonType("Send Publication", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

            // Get the controller
            addPubController controller = anchorPaneLoader.getController();

            // Set the action for the OK button
            Node okButtonNode = dialog.getDialogPane().lookupButton(okButtonType);
            okButtonNode.addEventFilter(ActionEvent.ACTION, e -> {
                try {
                    // Call the sendPublication method in addPubController
                    controller.sendPublication();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                // Close the dialog window
                dialog.setResult(okButtonType);
                populateFeedWithPublications();
            });

            // Set the stage for addPubController
            controller.setHomeController(this);

            controller.setStage((Stage) addPub.getScene().getWindow());

            // Show the dialog and wait for user input
            Optional<ButtonType> result = dialog.showAndWait();
        });

        userInfos.setOnMouseClicked(e -> {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("User Infos");

            // Create a new FXMLLoader instance for each dialog
            FXMLLoader infosLoader = new FXMLLoader(getClass().getResource("UserInfos.fxml"));

            try {
                HBox userinfos = infosLoader.load();
                // Get the controller without setting the stage
                UserInfos controller = infosLoader.getController();
                controller.initialize();

                // Set the content of the dialog to userinfos
                dialog.getDialogPane().setContent(userinfos);

                // Add a custom button to the dialog to handle closing

                dialog.getDialogPane().getButtonTypes().setAll(ButtonType.CANCEL);

                // Show the dialog and wait for user input
                Optional<ButtonType> result = dialog.showAndWait();


            } catch (IOException ex) {
                System.out.println("Error loading UserInfos.fxml: " + ex.getMessage());
            }
        });


        // Set up the event handler for the addPub button

        navbar.getChildren().add(profile);
        populateSidebarWithUsers();
        populateFeedWithPublications();
        homeIcon.setOnMouseClicked(event -> {
            scroll.setContent(feed);
            sidebar.getChildren().clear();
            populateFeedWithPublications();
            try {
                populateSidebarWithUsers();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });

        // Set the click event for the user icon
        userIcon.setOnMouseClicked(event -> {
            // Handle the user icon click event
            scroll.setContent(feed);
            populateCenterWithUserPublications(UserSession.getLog_user());
        });
        friendRequests.setOnMouseClicked(event->{
            searchUsers(DatabaseConnector.getFriendRequests());
        });

        logoutButton.setOnAction(e->{
            UserSession.logout(this);
        });

        messages.setOnMouseClicked(e-> {
            int serverPort = UserSession.getLog_user().getId()+1000; // Choose a suitable port number
            Server server = new Server(serverPort,this);
            Thread serverThread = new Thread(server);
            serverThread.start();
            feed.getChildren().clear();
            populateSidebarWithChatUsers();
        });

    }

    public void searchUsers(List<FriendRequests> fr_reqs) {
        feed.getChildren().clear();
        feed.setPrefHeight(652.0);
        HBox search = new HBox();

        // Create TextField dynamically
        TextField searchField = new TextField();
        searchField.setPromptText("Search users...");
        searchField.setLayoutX(10.0);
        searchField.setLayoutY(10.0);

        // Create Button dynamically
        Button searchButton = new Button("Search");
        searchButton.setLayoutX(200.0);
        searchButton.setLayoutY(10.0);
        search.setSpacing(20);
        search.getChildren().addAll(searchField, searchButton);
        feed.getChildren().addAll(search);
        for (FriendRequests request : fr_reqs) {
            HBox userBox = new HBox();
            userBox.setSpacing(20);
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
            acceptButton.setLayoutY(10);
            rejectButton.setLayoutY(10);

            userBox.getChildren().addAll(Test.createUserProfile(sender.getFirstName() + " " + sender.getLastName(), Test.returnUserProfileImage(sender.getProfilePicture()), 40), acceptButton, rejectButton);
            feed.getChildren().add(userBox);
        }

        searchButton.setOnAction(event -> {
            String[]username =searchField.getText().split(" ");
            if (username[0].equalsIgnoreCase(UserSession.getLog_user().getFirstName()) && username[1].equalsIgnoreCase(UserSession.getLog_user().getLastName())) {
                Test.showAlert("wrong input ","you just searched your name ");
            }else {
                User user = DatabaseConnector.getUserByName(searchField.getText());
                if (user == null) {
                    Test.showAlert("no user", "there are no users with the name :" + searchField.getText());
                } else {
                    List<User> friends = DatabaseConnector.getFriends();
                    displayFriendRequests(user, fr_reqs, friends);
                }
            }
        });


    }
    public void displayFriendRequests(User user,List<FriendRequests>reqs,List<User>friends ){
        feed.getChildren().clear();
        feed.setPrefHeight(652.0);
        HBox userBox=new HBox();
        userBox.setSpacing(20);
        if (reqs.stream().anyMatch(request ->  request.getReceiverId() == user.getId())){

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
        userBox.setSpacing(20);

        feed.getChildren().add(userBox);
    }
    private void populateSidebarWithUsers() throws SQLException {
        sidebar.getChildren().clear();
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
    private void populateSidebarWithChatUsers() {
        // Fetch the list of users from the database
        List<User> userList = null;
        try {
            userList = DatabaseConnector.getUsers();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        sidebar.getChildren().clear();

        // Create a VBox to hold user profiles in the sidebar
        //VBox userProfilesVBox = new VBox(User.getNumberOfUsers()); // Adjust the spacing between user profiles

        // Populate the VBox with user profiles
        for (User user : userList) {
            Image userImage = Test.returnUserProfileImage(user.getProfilePicture());
            AnchorPane userProfile = Test.createUserProfile(user.getFirstName() + " " + user.getLastName(), userImage, 50);

            // Pass the user to addEventHandlers
            Test.addEventHandlerstochat(userProfile,user);
            userProfile.setOnMouseClicked(e->{
                connectToServer(user.getId());
                loadChatContent(user,this);

                //String originalStyle = userBox.getStyle();
                feed.setStyle("-fx-background-color: #d3d3d3;");
            });

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

     private void connectToServer(int UserId) {
        if (activeClient != null) {
            activeClient.closeClient();
        }
        try {
            Client client = new Client("localhost", UserId + 1000, this);
            Thread clientThread = new Thread(client);
            clientThread.start();
            activeClient = client;
            System.out.println("active client is "+UserId);
            logger.debug("connected to server successfully ");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error connecting to server: " + e.getMessage());
            logger.error("eroor");
        }
    }

    private void loadChatContent(User user, home_Controller home) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("chat.fxml"));
            VBox chatContent = loader.load();  // Load FXML file first
            chatController = loader.getController();  // Access the controller after loading

            chatContent=chatController.setChat(user, this);
            feed.getChildren().clear();
            chatContent.setAlignment(Pos.CENTER);
            scroll.setContent(chatContent);


        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading ChatController content: " + e.getMessage());
        }
    }

}




