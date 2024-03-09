package org.example;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;

public class RSAServer {

    public static void main(String[] args) {
        try {
            // Server socket
            ServerSocket serverSocket = new ServerSocket(12345);

            // Wait for client connection
            System.out.println("Server is waiting for a client to connect...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected.");

            // Generate and store RSA key pair
            KeyPair keyPair = RSAUtils.generateRSAKeyPair();
            RSAUtils.storePublicKeyToFile(keyPair.getPublic(), "public.key");
            RSAUtils.storePrivateKeyToFile(keyPair.getPrivate(), "private.key");

            // Send public key to the client
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.writeObject(keyPair.getPublic());

            // Communication loop
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            String input;
            do {
                // Receive encrypted message from the client
                input = reader.readLine();
                if (!input.equals("finish")) {
                    // Decrypt the message
                    String decryptedMessage = RSAUtils.decryptString(input, keyPair.getPrivate());
                    System.out.println("Received from client: " + decryptedMessage);

                    // Send an acknowledgment
                    String response = "Server received: " + decryptedMessage;
                    String encryptedResponse = RSAUtils.encryptString(response, keyPair.getPublic());
                    writer.write(encryptedResponse + "\n");
                    writer.flush();
                }
            } while (!input.equals("finish"));

            // Close resources
            clientSocket.close();
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
