package com.example.demo1;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class CloudinaryImageUtility {

    private static final String CLOUDINARY_CLOUD_NAME = "dxowlkkm8";
    private static final String CLOUDINARY_API_KEY = "952535237956129";
    private static final String CLOUDINARY_API_SECRET = "xoqZLdR9OQ1sKcauH-4EP17CNy8";

    private static final Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", CLOUDINARY_CLOUD_NAME,
            "api_key", CLOUDINARY_API_KEY,
            "api_secret", CLOUDINARY_API_SECRET
    ));

    public static void uploadProfileImage(String localImagePath, String fileName) {
        try {
            // Specify the desired compression quality (e.g., 80 for 80% quality)
            int compressionQuality = 100;

            // Create a Transformation object to set the quality parameter
            Transformation transformation = new Transformation().quality(compressionQuality);

            // Upload the image to Cloudinary with the specified quality setting and format
            Map<String, Object> uploadResult = cloudinary.uploader().upload(localImagePath, ObjectUtils.asMap(
                    "public_id", "profilePictures/"+fileName,
                    "transformation", transformation,
                    "format", "png" // Set the format parameter
            ));

            // Print the result (optional)
            System.out.println(uploadResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }public static void uploadPublicationImage(String localImagePath, String fileName) {
        try {
            // Specify the desired compression quality (e.g., 80 for 80% quality)
            int compressionQuality = 100;

            // Create a Transformation object to set the quality parameter
            Transformation transformation = new Transformation().quality(compressionQuality);

            // Upload the image to Cloudinary with the specified quality setting and format
            Map<String, Object> uploadResult = cloudinary.uploader().upload(localImagePath, ObjectUtils.asMap(
                    "public_id", "publications/"+fileName,
                    "transformation", transformation,
                    "format", "png" // Set the format parameter
            ));

            // Print the result (optional)
            System.out.println(uploadResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void downloadProfileImage(String fileName, String destinationPath) {
        try {
            // Construct the Cloudinary URL for the image
            String imageUrl = "https://res.cloudinary.com/" + CLOUDINARY_CLOUD_NAME + "/image/upload/profilePictures/" + fileName;

            // Open a connection to the Cloudinary URL
            URL url = new URL(imageUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            // Set up input stream to read the image
            try (InputStream inputStream = httpURLConnection.getInputStream()) {
                // Save the downloaded image to the specified destination path
                Path destination = Paths.get(destinationPath, fileName+".png" );
                try (FileOutputStream outputStream = new FileOutputStream(destination.toFile())) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
            }

            System.out.println("Image downloaded successfully to: " + destinationPath);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error downloading image: " + e.getMessage());
        }
    }
    public static void downloadPublicationImage(String fileName, String destinationPath) {
        try {
            // Construct the Cloudinary URL for the image
            String imageUrl = "https://res.cloudinary.com/" + CLOUDINARY_CLOUD_NAME + "/image/upload/publications/" + fileName;

            // Open a connection to the Cloudinary URL
            URL url = new URL(imageUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            // Set up input stream to read the image
            try (InputStream inputStream = httpURLConnection.getInputStream()) {
                // Save the downloaded image to the specified destination path
                Path destination = Paths.get(destinationPath, fileName+".png" );
                try (FileOutputStream outputStream = new FileOutputStream(destination.toFile())) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
            }

            System.out.println("Image downloaded successfully to: " + destinationPath);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error downloading image: " + e.getMessage());
        }
    }
    public static String getImageUrl(String fileName) {
        return cloudinary.url().transformation(new Transformation().width(300).height(300).crop("fill"))
                .generate("your_cloudinary_folder/" + fileName + ".jpg");
    }
    public static void main(String[] args) {
        //uploadImage("C:\\Users\\LENOVO\\OneDrive\\Images\\chat.png","1");
        //uploadImage("C:\\\\Users\\\\LENOVO\\\\Documents\\\\java_project\\\\demo1\\\\src\\\\main\\\\resources\\\\IMAGES\\homefeed.png","3");
        //uploadImage("C:\\\\Users\\\\LENOVO\\\\Documents\\\\java_project\\\\demo1\\\\src\\\\main\\\\resources\\\\IMAGES\\default.png","4");
        //downloadProfileImage("default.png", "C:\\Users\\LENOVO\\Documents\\java_project\\demo1\\src\\main\\resources\\IMAGES");
    }
}
