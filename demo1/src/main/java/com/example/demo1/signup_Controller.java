package com.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class signup_Controller {

    @FXML
    private TextField FirstName;

    @FXML
    private TextField LastName;

    @FXML
    private TextField addressField;

    @FXML
    private TextField emailField;

    @FXML
    private ComboBox<String> genderComboBox;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private ImageView profilePic;

    @FXML
    private Button signUpButton;

    @FXML
    private Label signUpLabel;

    public void initialize() {
        List<String> genderOptions = Arrays.asList("Male", "Female");
        genderComboBox.getItems().addAll(genderOptions);
    }

    @FXML
    public void handleProfilePictureButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a Profile Picture");

        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);

        File selectedFile = fileChooser.showOpenDialog(Test.getStage(signUpButton));
        if (selectedFile != null) {
            // Get the file path without the file: prefix
            String imagePath = selectedFile.toURI().getPath();

            // Upload the image to Cloudinary using the file path
            CloudinaryImageUtility.uploadImage(imagePath, emailField.getText() + ".png");

            // Display the image in your UI if needed
            Image image = new Image("file:" + imagePath);
            profilePic.setImage(image);
        }
    }



    @FXML
    public void handleSignUpButton() {
        String firstNameText = FirstName.getText();
        String lastNameText = LastName.getText();
        String emailText = emailField.getText();
        String passwordText = passwordField.getText();
        String genderText = genderComboBox.getValue();
        String phoneNumberText = phoneNumberField.getText();
        String addressText = addressField.getText();

        if (firstNameText.isEmpty() || lastNameText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty() || genderText == null) {
            Test.showAlert("A field is empty ", "All fields are necessary");
        } else {
            insertUser(firstNameText, lastNameText, phoneNumberText, addressText, emailText, passwordText, genderText);


            // Use the getStage method to get the current stage
            Stage currentStage = Test.getStage(signUpButton);

            // Use the switchScene method to switch to the new scene
            SceneSwitcher.switchScene("login.fxml", currentStage, "login");
        }
    }

    public void insertUser(String firstName, String lastName, String phoneNumber, String address, String email, String password, String gender) {
        try {
            DatabaseConnector databaseConnector = new DatabaseConnector();
            Connection connection = databaseConnector.getConnection();

            // Check if a user with the same email already exists
            String checkIfExistsQuery = "SELECT * FROM users WHERE email=?";
            try (PreparedStatement checkIfExistsStatement = connection.prepareStatement(checkIfExistsQuery)) {
                checkIfExistsStatement.setString(1, email);

                try (ResultSet existingUserResultSet = checkIfExistsStatement.executeQuery()) {
                    if (existingUserResultSet.next()) {
                        Test.showAlert("User exists", "There is already a user with the same email " + email + " in the database.");
                        return;  // Exit if the user already exists
                    }
                }
            }

            // No user with the same email, proceed with insertion
            String insertQuery = "INSERT INTO users (firstname, lastname, phonenumber, address, email, password, gender, profilepicture) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, firstName);
                preparedStatement.setString(2, lastName);
                preparedStatement.setString(3, phoneNumber);
                preparedStatement.setString(4, address);
                preparedStatement.setString(5, email);
                preparedStatement.setString(6, password);
                preparedStatement.setString(7, gender);
                if(profilePic.getImage()!=null){
                    preparedStatement.setString(8,email+".png");
                }else {
                    preparedStatement.setString(8, "default.png");
                }

                int rowsInserted = preparedStatement.executeUpdate();

            } catch (SQLException e) {
                // Handle database exceptions
                throw new RuntimeException("Database error: " + e.getMessage(), e);
            } finally {
                // Close resources in a finally block to ensure they are closed regardless of exceptions
                databaseConnector.closeConnection();
            }
        } catch (SQLException e) {
            // Handle SQL connection exceptions
            throw new RuntimeException("Error connecting to the database: " + e.getMessage(), e);
        }
    }
}