//package com.peotic.springmail;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.mail.internet.MimeMessage;
//
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.InputStreamSource;
//import org.springframework.core.io.Resource;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.ui.velocity.VelocityEngineUtils;
//
///**
// * Spring mail 使用多个账号发送带有附件的HTML邮件
// * 
// * 1.JavaMail的API和Spring的邮件抽象层的对比
// *      优点:Spring的邮件抽象层简化了代码量，并能充分利用IOC功能.
// *      缺点:要使用部分Spring API，使程序与第三方框架耦合.
// * 
// * 2.Velocity允许我们在模版中设定变量，然后在运行时，动态的将数据插入到模版中，替换这些变量。
// * @author Administrator
// *
// * BUG: 
// *      附件中文乱码
// */
//public class Main {
//    public static void main(String[] args) {
//        
//        /* 1.Spring IOC 获得 Bean */
//        ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"applicationContext.xml"});
//        JavaMailSenderImpl sender = (JavaMailSenderImpl) context.getBean("mailSender");     // 可以发送带有附件的邮件
//        VelocityEngine velocityEngine = (VelocityEngine)context.getBean("velocityEngine");
//        
//        try {
//            /* 2.邮件内容(VelocityEngine) */
//            String templateLocation = "VM_global_library.vm";   // 默认模板(VelocityModel)
//            Map<String, String> model = new HashMap<String, String>();
//            model.put("CONTENT", "内容");
//            String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateLocation, model);
//            
//            /* 3.发送邮件(JavaMailSender) */
//            MimeMessage message = sender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");// 处理中文编码
//            
//            // 信息
//            helper.setSubject("主题");                                                        // 主题
//            helper.setFrom(sender.getUsername());                                           // 发件人
//            helper.setTo("you@mail.com");                                                   // 收件人
//            helper.setText(text, true);                                                     // 内容(HTML)
//            String contentId = "inline";
//            Resource resource = new ClassPathResource("inline.jpg");
//            String attachmentFilename = "attachment";
//            InputStreamSource inputStreamSource = new ClassPathResource("attachment.txt");
//            helper.addInline(contentId, resource);                                          // 附件(行内)
//            helper.addAttachment(attachmentFilename, inputStreamSource);                    // 附件
//            
//            // 发送
//            sender.send(message);
//            
//            // 发送成功
//        } catch (Exception e) {
//            // 没有发送成功
//        }
//    }
//}
// 