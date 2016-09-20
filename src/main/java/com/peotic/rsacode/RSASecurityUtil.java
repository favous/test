package com.peotic.rsacode;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;

import javax.crypto.Cipher;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class RSASecurityUtil {
    
    /** 指定加密算法为RSA */
    private static final String ALGORITHM = "RSA";
    
    /** 密钥长度，用来初始化 */
    private static final int KEY_SIZE = 1024;
    
    public static Key[] generateKeyPair() throws Exception {
        return generateKeyPair(null);
    }

    public static Key[] generateKeyPair(String seed) throws Exception {

        /** RSA算法要求有一个可信任的随机数源 */
        SecureRandom secureRandom = new SecureRandom();

        if (seed != null && !seed.equals("")){
            secureRandom.setSeed(seed.getBytes()); 
        }
        
        /** 为RSA算法创建一个KeyPairGenerator对象 */
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);

        /** 利用上面的随机数据源初始化这个KeyPairGenerator对象 */
        keyPairGenerator.initialize(KEY_SIZE, secureRandom);

        /** 生成密匙对 */
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        /** 得到公钥 */
        Key publicKey = keyPair.getPublic();

        /** 得到私钥 */
        Key privateKey = keyPair.getPrivate();
        
        return new Key[]{publicKey, privateKey};
    }
    
    /**
     * 加密方法
     * @param source 源数据
     * @return
     * @throws Exception
     */
    public static String encrypt(String source, Key key) throws Exception {
        /** 得到Cipher对象来实现对源数据的RSA加密 */
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        /** 执行加密操作 */
        byte[] bytes = cipher.doFinal(source.getBytes());
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(bytes);
    }

    /**
     * 解密算法
     * @param cryptograph    密文
     * @return
     * @throws Exception
     */
    public static String decrypt(String cryptograph, Key key) throws Exception {
        /** 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] bytes = decoder.decodeBuffer(cryptograph);
        
        /** 执行解密操作 */
        byte[] b = cipher.doFinal(bytes);
        return new String(b);
    }

    public static void main(String[] args) throws Exception {
        
        Key[] keys = generateKeyPair("ssfef");
        String source = "恭喜发财!";// 要加密的字符串
        
        String cryptograph = encrypt(source, keys[0]);// 生成的密文
        System.out.println("加密后的字符串为：" + cryptograph);
        String target = decrypt(cryptograph, keys[1]);// 解密密文
        System.out.println("解密后的字符串为：" + target);
        

        
        String cryptograph2 = encrypt(source, keys[1]);// 生成的密文
        System.out.println("加密后的字符串为：" + cryptograph2);
        String target2 = decrypt(cryptograph2, keys[0]);// 解密密文
        System.out.println("解密后的字符串为：" + target2);
        
        try {
            KeyGenerater generater = new KeyGenerater();
            generater.generate("seed");
            
            String signText = Signaturer.sign(generater.getPriKey(), "我的老家");
            boolean result = SignProvider.verify(generater.getPubKey(), "我的老家", signText);
            System.out.println(signText);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
