package org.example;

import java.io.*;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Scanner;

public class RSAClient {

    public static void main(String[] args) {
        try {
            // Connect to the server
            Socket socket = new Socket("localhost", 12345);

            // Receive public key from the server
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            PublicKey publicKey = (PublicKey) in.readObject();

            // Generate and store RSA key pair (for demonstration purposes)
            KeyPair keyPair = RSAUtils.generateRSAKeyPair();
            RSAUtils.storePublicKeyToFile(keyPair.getPublic(), "public.key");
            RSAUtils.storePrivateKeyToFile(keyPair.getPrivate(), "private.key");

            // Communication loop
            Scanner scanner = new Scanner(System.in);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String input;
            do {
                // Get user input
                System.out.print("Enter a message (type 'finish' to end): ");
                input = scanner.nextLine();

                // Encrypt and send the message
                long rsaEncryptionStartTime = System.nanoTime();
                String encryptedMessage = RSAUtils.encryptString(input, publicKey);
                long rsaEncryptionEndTime = System.nanoTime();
                long rsaEncryptionTime = rsaEncryptionEndTime - rsaEncryptionStartTime;
                System.out.println("RSA Encryption Time: " + rsaEncryptionTime + " nanoseconds");
                writer.write(encryptedMessage + "\n");
                writer.flush();

                // Receive acknowledgment from the server
                String response = reader.readLine();
                System.out.println("Server response: " + response);
            } while (!input.equals("finish"));

            // Close resources
            socket.close();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
}
