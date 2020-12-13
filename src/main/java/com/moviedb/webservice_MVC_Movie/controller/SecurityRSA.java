package com.moviedb.webservice_MVC_Movie.controller;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class SecurityRSA {

    private PublicKey publicKey = null;
    //private PrivateKey privateKey = null;


    public SecurityRSA (String publicKey_Base64) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if(publicKey == null) {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            X509EncodedKeySpec publicKeySpec = new
            X509EncodedKeySpec(Base64.getUrlDecoder().decode(publicKey_Base64.getBytes()));
            publicKey = keyFactory.generatePublic(publicKeySpec);
            }

        }

    public String encrypt(String toBeEncrypted) {
        String encrypted = "";
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedRaw = cipher.doFinal(toBeEncrypted.getBytes());
            encrypted = Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedRaw);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return encrypted;
    }



}
