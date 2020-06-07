package com.demo.springboot.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * springboot整合发送邮件
 */
@RestController
public class SpringbootDemoController {

    private Logger log = LoggerFactory.getLogger(SpringbootDemoController.class);

    @Autowired
    private JavaMailSenderImpl javaMailSender;

    @PostMapping("/send")
    public String sendMail(){
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject("Java测试发送邮件");
            message.setText("肖川小宝贝，我有个恋爱想和你谈一谈!!!!!!");
            message.setFrom("676382250@qq.com");
            message.setTo("li676382250@163.com");
            javaMailSender.send(message);
            log.info("邮件发送成功");
            return "发送成功";
        }catch (Exception e){
            log.info("邮件发送失败，" + e);
            return "邮件发送失败";
        }
    }
}
