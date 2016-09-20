package com.peotic.rsacode;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

public class KeyGenerater {
    
    /** 指定加密算法为RSA */
    private static final String ALGORITHM = "RSA";
    
    /** 密钥长度，用来初始化 */
    private static final int KEY_SIZE = 1024;
    
    private String priKey;
    private String pubKey;

    /*
     * 
     */
    public void generate() {
        generate(null);
    }

    /**
     * @param seed 参数一样，算出的key就一样, seed为空，每次算出的key是随机的
     */
    public void generate(String seed) {
        try {
            KeyPairGenerator keygen = KeyPairGenerator.getInstance(ALGORITHM);
         // 初始化随机产生器
            SecureRandom srandom = new SecureRandom();
            if (seed != null && !seed.equals("")){
                srandom.setSeed(seed.getBytes()); 
            }
            keygen.initialize(KEY_SIZE, srandom);
            KeyPair keys = keygen.genKeyPair();

            PublicKey pubkey = keys.getPublic();
            PrivateKey prikey = keys.getPrivate();

            pubKey = Base64.encodeToString(pubkey.getEncoded());
            priKey = Base64.encodeToString(prikey.getEncoded());

        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }

    public String getPriKey() {
        return priKey;
    }

    public String getPubKey() {
        return pubKey;
    }
}