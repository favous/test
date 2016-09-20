package com.peotic.rsacode;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

public class SignProvider {
    private SignProvider() {

    }

    /**
     * 
     * Description:校验数字签名,此方法不会抛出任务异常,成功返回true,失败返回false,要求全部参数不能为空
     * 
     * @param publicKeyText 公钥,base64编码
     * @param plainText 明文
     * @param signText 数字签名的密文,base64编码
     * @return 校验成功返回true 失败返回false
     */
    public static boolean verify(String publicKeyText, String plainText, String signText) {
        try {
            // 解密由base64编码的公钥,并构造X509EncodedKeySpec对象
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(Base64.decode(publicKeyText));
            // RSA对称加密算法
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            // 取公钥匙对象
            PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
            // 解密由base64编码的数字签名
            byte[] signed = Base64.decode(signText);
            Signature signature = Signature.getInstance("MD5withRSA");
            signature.initVerify(pubKey);
            signature.update(plainText.getBytes());
            // 验证签名是否正常
            if (signature.verify(signed))
                return true;
            else
                return false;
        } catch (Throwable e) {
            System.out.println("校验签名失败");
            e.printStackTrace();
            return false;
        }
    }
}