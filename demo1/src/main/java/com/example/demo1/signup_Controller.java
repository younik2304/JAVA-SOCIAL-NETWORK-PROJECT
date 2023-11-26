package com.example.demo1;
import java.io.ByteArrayInputStream;
import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.nio.ByteBuffer;
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
            Image image = new Image(selectedFile.toURI().toString());
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
            Test.showAlert("A field is empty ","All fields are necessary");
        } else {
            insertUser(firstNameText, lastNameText, phoneNumberText, addressText,  emailText, passwordText,  genderText);


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

            // SQL query with a prepared statement
            String sql = "INSERT INTO users (firstname, lastname, phonenumber, address, email, password, gender, profilepicture) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, phoneNumber);
            preparedStatement.setString(4, address);
            preparedStatement.setString(5, email);
            preparedStatement.setString(6, password);
            preparedStatement.setString(7, gender);

            // Check if a profile picture is selected
            if (profilePic.getImage() != null) {
                // Convert the JavaFX Image to a byte array
                byte[] imageData = convertImageToByteArray(profilePic.getImage());

                // Create a ByteArrayInputStream from the byte array
                ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);

                // Set the binary image data to the prepared statement
                preparedStatement.setBlob(8, inputStream);
            } else {
                preparedStatement.setNull(8, Types.BLOB);
            }

            // Execute the insert statement
            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                // Get the generated keys (if any)
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    System.out.println("User ID: " + userId);
                    // Optionally, you can store or use the generated user ID
                }

                // Close the prepared statement
                preparedStatement.close();
                databaseConnector.closeConnection();

                Test.showAlert("Signup successful", "Now you'll be directed to the login ");
            } else {
                // No rows were inserted
                System.out.println("No rows were inserted.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
    private byte[] convertImageToByteArray(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        PixelReader pixelReader = image.getPixelReader();
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 * width * height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);
                int argb = (int) ((color).getOpacity() * 255) << 24 |
                        (int) (color.getRed() * 255) << 16 |
                        (int) (color.getGreen() * 255) << 8 |
                        (int) (color.getBlue() * 255);
                byteBuffer.putInt(argb);
            }
        }

        return byteBuffer.array();
    }
}
