package com.example.demo1;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class UserButton extends Button {

    private ImageView userImageView;
    private String userName;

    public UserButton(String userName, Image userImage) {
        this.userName = userName;
        this.userImageView = new ImageView(userImage);

        initialize();
    }

    private void initialize() {
        setGraphicTextGap(10); // Adjust the gap between text and image

        // Set the content of the button to an HBox with text and image
        setGraphicText(userName, userImageView);
    }

    private void setGraphicText(String text, ImageView image) {
        HBox hbox = new HBox();
        hbox.getChildren().addAll(image, new javafx.scene.control.Label(text));
        setGraphic(hbox);
    }

    // You can add additional methods or properties as needed

    public void setUserName(String userName) {
        this.userName = userName;
        setGraphicText(userName, userImageView);
    }

    public void setUserImage(Image userImage) {
        this.userImageView.setImage(userImage);
    }
}
