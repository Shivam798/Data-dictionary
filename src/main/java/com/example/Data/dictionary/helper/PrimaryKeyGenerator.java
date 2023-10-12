package com.example.Data.dictionary.helper;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Component
public class PrimaryKeyGenerator {
    public String generatePrimaryKey(List<String> primaryVariablesList) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String primaryVariablesString=primaryVariablesList.toString();

        // Convert the input string to bytes
        byte[] inputBytes = primaryVariablesString.getBytes(StandardCharsets.UTF_8);

        // Update the digest with the input bytes
        byte[] hashBytes = digest.digest(inputBytes);

        // Convert the hash bytes to a hexadecimal string
        StringBuilder hexString = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String hex = Integer.toHexString(0xff & hashByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
