package org.example;

import javax.crypto.*;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import java.util.Base64;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        try {
            // Connect to the server
            Socket socket = new Socket("localhost", 12345);

            // Receive key and IV from the server
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            byte[] keyBytes = (byte[]) in.readObject();
            byte[] ivBytes = (byte[]) in.readObject();
            SecretKey key = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec iv = new IvParameterSpec(ivBytes);

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
                String encryptedMessage = CryptoUtils.encrypt("AES/CBC/PKCS5Padding", input, key, iv);
                writer.write(encryptedMessage + "\n");
                writer.flush();

                // Receive acknowledgment from the server
                String response = reader.readLine();
                System.out.println("Server response: " + response);
            } while (!input.equals("finish"));

            // Close resources
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static SecretKey generateAESKey() throws Exception {
        return new SecretKeySpec("0123456789abcdef".getBytes(StandardCharsets.UTF_8), "AES");
    }
    private static String encryptAES(String plainText, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
}
