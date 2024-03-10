package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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
            long startTime = System.currentTimeMillis();
            KeyPair keyPair = RSAUtils.generateRSAKeyPair();
            long endTime = System.currentTimeMillis();
            System.out.println("RSA Key Generation Time ("
                    + 2048 + " bits): " + (endTime - startTime) + " milliseconds");
            RSAUtils.storePublicKeyToFile(keyPair.getPublic(), "public.key");
            RSAUtils.storePrivateKeyToFile(keyPair.getPrivate(), "private.key");
            RSAUtils.measureRSAKeyGenerationMemory();

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
                    long rsaDecryptionStartTime = System.nanoTime();
                    String decryptedMessage = RSAUtils.decryptString(input, keyPair.getPrivate());
                    long rsaDecryptionEndTime = System.nanoTime();
                    long rsaDecryptionTime = rsaDecryptionEndTime - rsaDecryptionStartTime;
                    System.out.println("RSA Decryption Time: " + rsaDecryptionTime + " nanoseconds");
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
            MemoryUsageComparison.measureRSAMemoryUsage();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
}
