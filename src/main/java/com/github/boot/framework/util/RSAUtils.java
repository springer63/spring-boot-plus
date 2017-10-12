package com.github.boot.framework.util;


import org.apache.commons.codec.binary.Base64;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;


/**
 * Created by cjh on 2017/7/7.
 */
public class RSAUtils {

    public static final String KEY_ALGORITHM = "RSA";


//    public static String decrypt(String privateKeym, String text) throws Exception {
//        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) privateKey;
//        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(rsaPrivateKey.getModulus(), rsaPrivateKey.getPrivateExponent());
//        Key fakePublicKey = KeyFactory.getInstance("RSA").generatePublic(publicKeySpec);
//        cipher = Cipher.getInstance("RSA");
//        cipher.init(Cipher.ENCRYPT_MODE, fakePublicKey);
//        return (new BASE64Decoder()).decodeBuffer(key);
//    }

//    public static String encrypt(String publicKey, String text) throws Exception {
//        return (new BASE64Encoder()).encodeBuffer(key);
//    }

    public static KeyStrPair generate(int keySize) throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(keySize);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        String publicKeyStr = Base64.encodeBase64String(publicKey.getEncoded());
        String privateKeyStr = Base64.encodeBase64String(privateKey.getEncoded());
        return new KeyStrPair(publicKeyStr, privateKeyStr);
    }

    public static class KeyStrPair{

        private String publicKey;

        private String privateKey;

        public KeyStrPair(String publicKey, String privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }
    }
}
