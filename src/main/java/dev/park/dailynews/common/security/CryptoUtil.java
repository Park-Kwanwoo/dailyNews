package dev.park.dailynews.common.security;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class CryptoUtil {

    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "1234567890123456";

    public static String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(plainText.getBytes());
        return Base64.getUrlEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String encryptedText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decoded = Base64.getUrlDecoder().decode(encryptedText);
        byte[] decrypted = cipher.doFinal(decoded);

        return new String(decrypted);
    }
}
