package com.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class UserInfos {

    @FXML
    private Label firstname;

    @FXML
    private Label phonenumber;

    @FXML
    private ImageView profilepicture;

    @FXML
    private Label adress;

    @FXML
    private Label email;

    @FXML
    private Label lastname;


    public void initialize() {
        Image prfp=Test.returnUserProfileImage(UserSession.getLog_user().getProfilePicture());
        profilepicture.setImage(prfp);
        firstname.setText(UserSession.getLog_user().getFirstName());
        lastname.setText(UserSession.getLog_user().getLastName());
        adress.setText(UserSession.getLog_user().getAddress());
        phonenumber.setText(UserSession.getLog_user().getPhone_number());
        email.setText(UserSession.getLog_user().getEmail());
    }
}
