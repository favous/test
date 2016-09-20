package com.peotic.rsacode;

public class Test {
    
    public static void main(String[] args) {
        try {
            KeyGenerater generater = new KeyGenerater();
            generater.generate("seed");
            
            String signText = Signaturer.sign(generater.getPriKey(), "我的老家");
            boolean result = SignProvider.verify(generater.getPubKey(), "我的老家", signText);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
