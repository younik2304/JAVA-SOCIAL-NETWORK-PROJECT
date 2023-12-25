package com.example.demo1;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class home_Controller {

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
    private User currentChatUser;
    private Client activeClient;

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
                connectToServer(UserSession.getLog_user().getId(), user.getId());
                chatBox(user);
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
    private void chatBox(User user) {
        currentChatUser = user;
        int yourUserId = UserSession.getLog_user().getId();
        int otherUserId = user.getId();
        List<Message> messages = DatabaseConnector.getMessagesBetweenUsers(yourUserId, otherUserId);

        // Clear the chatVbox to prepare for new messages
        feed.getChildren().clear();
        AnchorPane userBox = Test.createUserProfile(user.getFirstName() + " " + user.getLastName(), Test.returnUserProfileImage(user.getProfilePicture()), 50);
        feed.getChildren().add(userBox);

        for (Message message : messages) {
            if (message.getSender().getId() == yourUserId) {
                // Message sent by the logged-in user
                displaySentMessage(message);
            } else {
                // Message received from the other user
                displayReceivedMessage(message);
            }
        }

        TextField messagefield = new TextField();
        Button send = new Button("send");

        // Set maximum width for the message containers
        double maxWidth = 550.0; // Adjust this value as needed
        feed.getChildren().forEach(node -> {
            if (node instanceof HBox) {
                ((HBox) node).setMaxWidth(maxWidth);
            }
        });

        // Adjust the positioning of TextField and Button
        HBox inputBox = new HBox(messagefield, send);
        inputBox.setSpacing(10); // Adjust spacing as needed
        inputBox.setAlignment(Pos.CENTER);
        footer.getChildren().add(inputBox);

        send.setOnMouseClicked(e -> {
            sendMessage(messagefield.getText());
            messagefield.clear();
            Platform.runLater(() -> {
                scroll.setVvalue(2.0);
            });
        });
    }

    public void displaySentMessage(Message message) {
        HBox sentMessage = createMessageContainer(message.getMessageText(), message.getTimestamp(), true);
        Platform.runLater(() -> {
            feed.getChildren().add(sentMessage);
            adjustMessageAppearance();
            Platform.runLater(() -> {
                scroll.setVvalue(2.0);
            });
        });
    }

    public void displayReceivedMessage(Message message) {
        HBox receivedMessage = createMessageContainer(message.getMessageText(), message.getTimestamp(), false);
        Platform.runLater(() -> {
            feed.getChildren().add(receivedMessage);
            adjustMessageAppearance();
            Platform.runLater(() -> {
                scroll.setVvalue(2.0);
            });
        });
    }
    private void adjustMessageAppearance() {
        // Adjust the appearance of messages in the feed
        double maxWidth = 600.0; // Adjust this value as needed
        feed.getChildren().forEach(node -> {
            if (node instanceof HBox) {
                ((HBox) node).setMaxWidth(maxWidth);
            }
        });
    }
    private static HBox createMessageContainer(String content, Timestamp timestamp, boolean sentByCurrentUser) {
        HBox messageBox = new HBox();
        Label messageLabel = new Label(content);
        LocalDateTime localDateTime = timestamp.toLocalDateTime();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM");
        String formattedDateTime = localDateTime.format(formatter);
        Label timestampLabel = new Label(formattedDateTime) ; // Use the timestamp

        messageLabel.setWrapText(true);
        messageLabel.setPadding(new Insets(10)); // Add padding to the message label

        // Customize the appearance of the message box and labels based on sender
        if (sentByCurrentUser) {
            messageBox.setAlignment(Pos.CENTER_RIGHT);
            messageLabel.setTranslateX(-5);
            messageLabel.setStyle("-fx-background-color: #DCF8C6; -fx-background-radius: 10; -fx-padding: 8px;");
            timestampLabel.setStyle("-fx-font-size:8px; -fx-font-weight: bold; -fx-text-fill: #999999; -fx-padding: 2px 4px; -fx-background-color: #f2f2f2; -fx-background-radius: 4px;");
        } else {
            messageBox.setAlignment(Pos.CENTER_LEFT);
            messageLabel.setTranslateX(5);
            messageLabel.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 10; -fx-padding: 8px;");
            timestampLabel.setStyle("-fx-font-size: 8px; -fx-font-weight: bold; -fx-text-fill: #999999; -fx-padding: 2px 4px; -fx-background-color: #f2f2f2; -fx-background-radius: 4px;");
        }

        // Add message content and timestamp labels to the messageBox
        VBox messageContent = new VBox(messageLabel, timestampLabel);
        messageContent.setSpacing(0); // Adjust the spacing between message content and timestamp
        messageBox.getChildren().add(messageContent);

        return messageBox;
    }
    private void connectToServer(int yourUserId, int otherUserId) {
        if (activeClient != null) {
            activeClient.closeClient();
        }
        try {
            Client client = new Client("localhost", yourUserId + 1000, this);
            Thread clientThread = new Thread(client);
            clientThread.start();
            activeClient = client;
            System.out.println("Connected to server successfully.");
            logger.debug("connected to server successfully ");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error connecting to server: " + e.getMessage());
            logger.error("eroor");
        }
    }
    private void sendMessage(String message) {
        // Get the IDs of the sender and receiver
        int yourUserId = UserSession.getLog_user().getId();
        int otherUserId = currentChatUser.getId();

        // Save the message to the database
        boolean messageSent = DatabaseConnector.saveMessageToDatabase(yourUserId, otherUserId, message);

        if (currentChatUser!=null) {

            Message msg=new Message(message,Timestamp.valueOf(LocalDateTime.now()));
            displaySentMessage(msg);
            if (activeClient != null  ) {
                activeClient.sendMessage(message);
            }}
        // Create unique ports for each user pair or utilize a different strategy to differentiate communication

    }
}




