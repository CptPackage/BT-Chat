package io.cptpackage.bluetoothchat.security;

import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import static io.cptpackage.bluetoothchat.security.SecurityConstants.*;
/**
 * Encrypts/Decrypts incoming messages and generates hash-key
 *
 * */
public class CryptoAgent {
    private static final String TAG = "EncryptionAgent";
    private String messageContent;

    public CryptoAgent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getEncryptedMessage() {
        String encryptedString = "";
        try {
            SecretKeySpec secretKey = getKey();
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedPayload = cipher.doFinal(messageContent.getBytes(StandardCharsets.UTF_8));
            encryptedString = Base64.encodeToString(encryptedPayload, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(TAG, "getEncryptedMessage: ", e);
        }
        return encryptedString;
    }

    public String getDecryptedMessage() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        String decryptedString = "";
        SecretKeySpec secretKey = getKey();
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedPayload = Base64.decode(messageContent, Base64.DEFAULT);
        byte[] decryptedPayload = cipher.doFinal(decodedPayload);
        decryptedString = new String(decryptedPayload);
        return decryptedString;
    }

    private SecretKeySpec getKey() throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(KEY_HASHING_ALGORITHM);
        byte[] secretKeyBytes = ENCRYPTION_SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        messageDigest.update(secretKeyBytes);
        byte[] digestedBytes = messageDigest.digest();
        return new SecretKeySpec(digestedBytes, ENCRYPTION_ALGORITHM);
    }

}
