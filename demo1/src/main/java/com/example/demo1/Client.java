package com.example.demo1;


import javafx.application.Platform;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Client implements Runnable {
    private final String host;
    private static  int port;
    private Socket client;

    private final home_Controller homecontroller; // Reference to homecontroller instance

    public Client(String host, int port, home_Controller homecontroller) {
        this.host = host;
        this.port = port;
        this.homecontroller = homecontroller; // Assign homecontroller instance
    }


    @Override
    public void run() {
        try {
            System.out.println("Connecting to server...");
            System.out.println("Server Host: " + host);
            System.out.println("Server Port: " + port);
            client = new Socket("localhost", port);
            DataInputStream in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

            String data2;
            while (true) {
                if (in.available() > 0) {
                    String data = in.readUTF();
                    System.out.println("Server(partie client): " + data);
                    Message msg=new Message(data, Timestamp.valueOf(LocalDateTime.now()));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {

        }
    }
    public void sendMessage(String message) {
        try {
            if (client != null) {
                DataOutputStream out = new DataOutputStream(client.getOutputStream());
                out.writeUTF(message+" "+UserSession.getLog_user().getId());
                out.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {

        }

    }

    public  void closeClient() {
        try {
            if (client != null) {
                client.close();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getHost() {
        return host;
    }

    public static int getPort() {
        return port;
    }

    public Socket getClient() {
        return client;
    }

    public void setClient(Socket client) {
        this.client = client;
    }

    public home_Controller gethomecontroller() {
        return homecontroller;
    }
}
