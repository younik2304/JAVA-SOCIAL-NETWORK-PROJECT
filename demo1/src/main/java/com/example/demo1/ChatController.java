package com.example.demo1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChatController {

    @FXML
    private HBox userprofile;

    @FXML
    private ScrollPane scroll;

    @FXML
    private VBox chat;

    @FXML
    private TextField message_field;

    @FXML
    private Button sendMessage;

    // Add your initialization logic or event handlers here

    @FXML
    private void initialize() {

    }



    public VBox setChat(User user, home_Controller home) {
        VBox chatContent = new VBox();  // Create a VBox to hold the chat content

        home.currentChatUser = user;
        int yourUserId = UserSession.getLog_user().getId();
        int otherUserId = user.getId();
        List<Message> messages = DatabaseConnector.getMessagesBetweenUsers(yourUserId, otherUserId);

        // Clear the chatVbox to prepare for new messages
        userprofile.getChildren().clear();
        userprofile.getChildren().add(Test.createUserProfile(user.getFirstName() + " " + user.getLastName(), Test.returnUserProfileImage(user.getProfilePicture()), 50));
        for (Message message : messages) {
            if (message.getSender().getId() == yourUserId) {
                // Message sent by the logged-in user
                displaySentMessage(message, chatContent);
            } else {
                // Message received from the other user
                displayReceivedMessage(message, chatContent);
            }
        }
        sendMessage.setOnMouseClicked(e -> {
            sendMessage(message_field.getText(), home, chatContent);
            message_field.clear();
            Platform.runLater(() -> {
                scroll.setVvalue(1.0);
            });
        });

        // Clear the chatContent before adding scroll
        scroll.setContent(chatContent);

        return chat;  // Return the VBox containing the chat content
    }

    public void displaySentMessage(Message message, VBox chatContent) {
        HBox sentMessage = createMessageContainer(message.getMessageText(), message.getTimestamp(), true);
        Platform.runLater(() -> {
            chatContent.getChildren().add(sentMessage);
            adjustMessageAppearance();
            Platform.runLater(() -> {
                scroll.setVvalue(1.0);
            });
        });
          // Add the sent message to the VBox
    }

    public void displayReceivedMessage(Message message, VBox chatContent) {
        HBox receivedMessage = createMessageContainer(message.getMessageText(), message.getTimestamp(), false);
        Platform.runLater(() -> {
            chatContent.getChildren().add(receivedMessage);  // Add the received message to the VBox
            adjustMessageAppearance();
            Platform.runLater(() -> {
                scroll.setVvalue(1.0);
            });
        });

    }

    private void sendMessage(String message, home_Controller home, VBox chatContent) {
        // Get the IDs of the sender and receiver
        int yourUserId = UserSession.getLog_user().getId();
        int otherUserId = home.currentChatUser.getId();

        // Save the message to the database
        boolean messageSent = DatabaseConnector.saveMessageToDatabase(yourUserId, otherUserId, message);

        if (home.currentChatUser != null) {

            Message msg = new Message(message, Timestamp.valueOf(LocalDateTime.now()));
            displaySentMessage(msg, chatContent);
            if (home.activeClient != null) {
                home.activeClient.sendMessage(message);

            }
        }
        // Create unique ports for each user pair or utilize a different strategy to differentiate communication
    }

    private void adjustMessageAppearance() {
        // Adjust the appearance of messages in the feed
        double maxWidth = 600.0; // Adjust this value as needed
        chat.getChildren().forEach(node -> {
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

}
