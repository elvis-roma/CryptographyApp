package org.example;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {

    public static void main(String[] args) {
        try {
            // Server socket
            ServerSocket serverSocket = new ServerSocket(12345);

            // Wait for client connection
            System.out.println("Server is waiting for a client to connect...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected.");

            // Generate key and IV
            SecretKey key = CryptoUtils.generateKey(256);
            IvParameterSpec iv = CryptoUtils.generateIv();

            // Send key and IV to the client
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.writeObject(key.getEncoded());
            out.writeObject(iv.getIV());

            // Communication loop
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            String input;
            do {
                // Receive encrypted message from the client
                input = reader.readLine();
                if (!input.equals("finish")) {
                    // Decrypt the message
                    String decryptedMessage = CryptoUtils.decrypt("AES/CBC/PKCS5Padding", input, key, iv);
                    System.out.println("Received from client: " + decryptedMessage);

                    // Send an acknowledgment
                    String response = "Server received: " + decryptedMessage;
                    String encryptedResponse = CryptoUtils.encrypt("AES/CBC/PKCS5Padding", response, key, iv);
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
