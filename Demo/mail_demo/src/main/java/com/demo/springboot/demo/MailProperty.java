package com.demo.springboot.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

//@Configuration
public class MailProperty {

    //@Bean
    public JavaMailSenderImpl javaMailSender(){
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setUsername("676382250@qq.com");
        javaMailSender.setHost("smtp.qq.com");
        javaMailSender.setPassword("cteblafzbrtkbfcj");
        return javaMailSender;
    }
}
