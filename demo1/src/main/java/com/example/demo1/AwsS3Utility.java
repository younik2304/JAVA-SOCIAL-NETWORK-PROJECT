package com.example.demo1;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class AwsS3Utility {

    private static final String BUCKET_NAME = "younik2304";
    private static final String KEY_PREFIX = "content/";

    private static final S3Client s3Client = S3Client.builder()
            .region(Region.EU_WEST_3) // Replace with your region
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();

    public static void uploadImage(String localImagePath, String fileName) {
        try {
            s3Client.putObject(PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(KEY_PREFIX + fileName)
                    .build(), Path.of(localImagePath));
            System.out.println("Image uploaded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] getImageContent(String fileName) {
        try (ResponseInputStream<GetObjectResponse> s3ObjectResponseInputStream = s3Client.getObject(GetObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(KEY_PREFIX + fileName)
                .build())) {

            // Read the content from the ResponseInputStream into a byte array
            InputStream inputStream = s3ObjectResponseInputStream;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }

            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
