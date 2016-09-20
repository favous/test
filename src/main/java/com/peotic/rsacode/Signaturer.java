package com.peotic.rsacode;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;

public class Signaturer {
    
    /** 指定加密算法为RSA */
    private static final String ALGORITHM_CODE = "RSA";
    private static final String ALGORITHM_SIGN = "MD5withRSA";
    
    /**
     * 
     * Description:数字签名
     *
     * @param privateKeyText
     * @param plainText
     * @return
     * @throws Exception 
     */
    public static String sign(String privateKeyText, String plainText) throws Exception {
        PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKeyText));
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_CODE);
        PrivateKey prikey = keyFactory.generatePrivate(priPKCS8);

        // 用私钥对信息生成数字签名
        Signature signature = Signature.getInstance(ALGORITHM_SIGN);
        signature.initSign(prikey);
        signature.update(plainText.getBytes());
        byte[] signed = Base64.encodeToByte(signature.sign());
        return new String(signed);
    }
}