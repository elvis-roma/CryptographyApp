package org.example;

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import java.util.Base64;

public class RSAUtils {

    // Generate RSA Key Pair
    public static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    public static void storePrivateKeyToFile(PrivateKey privateKey, String fileName) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(privateKey.getEncoded());
        }
    }

    // Read Private Key from a File
    public static PrivateKey readPrivateKeyFromFile(String fileName) throws Exception {
        byte[] privateKeyBytes = Files.readAllBytes(Paths.get(fileName));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        return keyFactory.generatePrivate(privateKeySpec);
    }

    // Store Public Key in a File
    public static void storePublicKeyToFile(PublicKey publicKey, String fileName) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(publicKey.getEncoded());
        }
    }

    // Read Public Key from a File
    public static PublicKey readPublicKeyFromFile(String fileName) throws Exception {
        byte[] publicKeyBytes = Files.readAllBytes(Paths.get(fileName));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        return keyFactory.generatePublic(publicKeySpec);
    }

    // Encrypt String with Public Key
    public static String encryptString(String input, PublicKey publicKey) throws Exception {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedBytes = encryptCipher.doFinal(inputBytes);

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Decrypt String with Private Key
    public static String decryptString(String encryptedInput, PrivateKey privateKey) throws Exception {
        Cipher decryptCipher = Cipher.getInstance("RSA");
        decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedInput);
        byte[] decryptedBytes = decryptCipher.doFinal(encryptedBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
