//package com.peotic.springmail;
//
//import java.util.ArrayList;
//
//import org.springframework.mail.MailException;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//
//public class MailSender extends JavaMailSenderImpl implements JavaMailSender {
//    
//    // Accounts
//    private ArrayList<String> userNameList;
//    private ArrayList<String> passwordList;
//    private int currentMailId = 0;
//
//    @Override
//    protected void doSend(MimeMessage[] mimeMessage, Object[] object) throws MailException {
//        super.doSend(mimeMessage, object);
//        
//        // Next Accounts
//        currentMailId = (currentMailId + 1) % userNameList.size();
//        super.setUsername(userNameList.get(currentMailId));
//        super.setPassword(passwordList.get(currentMailId));
//    }
//
//    @Override
//    public void setUsername(String username) {
//        if (userNameList == null)
//            userNameList = new ArrayList<String>();
//        // split usernmae in ,
//        String[] userNames = username.split(",");
//        if (userNames != null) {
//            for (String user : userNames) {
//                userNameList.add(user);
//            }
//        }
//        // before send super.setXxx()       // accounts
//        super.setUsername(userNameList.get(currentMailId));
//    }
//    
//    @Override
//    public void setPassword(String password) {
//        if (passwordList == null)
//            passwordList = new ArrayList<String>();
//        // split password in ,
//        String[] passwords = password.split(",");
//        if (passwords != null) {
//            for (String pw : passwords) {
//                passwordList.add(pw);
//            }
//        }
//        // before send super.setXxx()   // accounts
//        super.setPassword(passwordList.get(currentMailId));
//    }
//}