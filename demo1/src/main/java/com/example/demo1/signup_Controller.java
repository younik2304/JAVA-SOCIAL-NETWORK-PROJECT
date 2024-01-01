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
    private static String imagepath="";

    @FXML
    private Button signUpButton;

    @FXML
    private Label signUpLabel;
    @FXML
    private Button login ;
    @FXML
    void handleLoginButtonAction() {
        try {
            SceneSwitcher.switchScene("login.fxml",Test.getStage(signUpLabel),"login");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }



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
            //
            // Display the image in your UI if needed
            Image image = new Image("file:" + imagePath);
            profilePic.setImage(image);
            imagepath=imagePath;
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
            int userId = insertUser(firstNameText, lastNameText, phoneNumberText, addressText, emailText, passwordText, genderText);

            if (userId != -1) {
                CloudinaryImageUtility.uploadProfileImage(imagepath, String.valueOf(userId));
                updateUserProfilePicture(userId, String.valueOf(userId));

                // Use the getStage method to get the current stage
                Stage currentStage = Test.getStage(signUpButton);

                // Use the switchScene method to switch to the new scene
                SceneSwitcher.switchScene("login.fxml", currentStage, "login");
            } else {
                // Handle the case where user ID is not valid
                System.out.println("Failed to get valid user ID");
            }



            // Use the getStage method to get the current stage

        }
    }

    public int insertUser(String firstName, String lastName, String phoneNumber, String address, String email, String password, String gender) {
        int returnedid = -1;
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
                        return -1;  // Return -1 to indicate that the user already exists
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
                preparedStatement.setString(8, "default.png");

                int rowsInserted = preparedStatement.executeUpdate();

                if (rowsInserted > 0) {
                    // Get the generated keys (if any)
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

                    if (generatedKeys.next()) {
                        // Return the generated user ID
                        returnedid= generatedKeys.getInt(1);
                    } else {
                        // Handle the case where no keys were generated
                        throw new RuntimeException("No generated keys were obtained.");
                    }
                } else {
                    // Handle the case where no rows were inserted
                    throw new RuntimeException("No rows were inserted.");
                }
                return returnedid;
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
    public void updateUserProfilePicture(int userId, String newProfilePicture) {
        try {
            DatabaseConnector databaseConnector = new DatabaseConnector();
            Connection connection = databaseConnector.getConnection();

            // Update the user with the new profile picture
            String updateQuery = "UPDATE users SET profilepicture=? WHERE id=?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setString(1, newProfilePicture);
                updateStatement.setInt(2, userId);

                int rowsUpdated = updateStatement.executeUpdate();

                if (rowsUpdated > 0) {
                    System.out.println("User profile picture updated successfully.");
                } else {
                    System.out.println("No rows were updated. User may not exist.");
                }
            } catch (SQLException e) {
                // Handle database exceptions
                throw new RuntimeException("Database error: " + e.getMessage(), e);
            } finally {
                // Close resources in a finally block to ensure they are closed regardless of exceptions
                databaseConnector.closeConnection();
            }
        } catch (Exception e) {
            // Handle SQL connection exceptions
            throw new RuntimeException("Error connecting to the database: " + e.getMessage(), e);
        }
    }


}