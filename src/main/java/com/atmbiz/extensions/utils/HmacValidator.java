package com.atmbiz.extensions.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HmacValidator {
    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    /**
     * Validates the provided HMAC with a payload and a secret key.
     *
     * @param providedHmac the provided HMAC
     * @param payload      the payload
     * @param secret       the secret key
     * @return true if the provided HMAC is valid, false otherwise
     */
    public static boolean isValid(String providedHmac, String payload, String secret) {
        try {
            String calculatedHmac = calculateHmac(payload, secret);
            return calculatedHmac.equals(providedHmac);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to validate HMAC", ex);
        }
    }

    /**
     * Calculates the HMAC for a payload with a secret key.
     *
     * @param data   the payload
     * @param secret the secret key
     * @return the calculated HMAC
     * @throws NoSuchAlgorithmException if the algorithm does not exist
     * @throws InvalidKeyException      if the key is invalid
     */
    public static String calculateHmac(String data, String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(rawHmac);
    }
}