package com.moviedb.webservice_MVC_Movie.controller;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Security {

    private static final String initVector = "initVectorKey123";
    private static final String key = "aesEncryptionKey";

    public String encrypt(String toBeEncrypted) {

        String encryptedString = null;
        byte[] encryptedByteArray = null;

        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            encryptedByteArray = cipher.doFinal(toBeEncrypted.getBytes());
            encryptedString = Base64.encodeBase64String(encryptedByteArray);
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        return encryptedString;
    }

    public String decrypt(byte[] encrypted) {

        String original = null;

        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            original = new String(cipher.doFinal(Base64.decodeBase64(encrypted)));
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        return original;
    }


}
