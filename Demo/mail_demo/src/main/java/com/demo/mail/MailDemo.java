package com.demo.mail;

import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * 使用Java原生API发送简单邮件，smtp/pop3协议，smtp是邮件发送协议，pop3是邮件接收协议
 */
public class MailDemo {
    public static void main(String[] args) throws Exception {

        // 属性封装
        Properties properties = new Properties();
        // 设置QQ邮件服务器
        properties.setProperty("mail.host", "smtp.qq.com");
        // 邮件发送协议
        properties.setProperty("mail.transport.protocol", "smtp");
        // 需要验证用户名和密码
        properties.setProperty("mail.smtp.auth", "true");

        // QQ邮箱还需要设置SSL加密
        MailSSLSocketFactory mailSSLSocketFactory = new MailSSLSocketFactory();
        mailSSLSocketFactory.setTrustAllHosts(true);
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.ssl.socketFactory", mailSSLSocketFactory);

        /**
         * 发送邮件的五个步骤
         * 1. 创建定义整个应用程序所需要的的环境信息的session对象
         * 2. 通过session对象获取transport对象
         * 3. 使用邮箱的用户名和授权码连接邮件服务器
         * 4. 创建邮件
         * 5. 发送邮件
         */

        // 1. 创建定义整个应用程序所需要的的环境信息的session对象
        Session session = Session.getDefaultInstance(properties, new Authenticator(){
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("676382250@qq.com", "cteblafzbrtkbfcj");
            }
        });

        // 开启debug可以看到发送邮件时的信息
        session.setDebug(true);

        // 2. 通过session对象获取transport对象
        Transport transport = session.getTransport();

        // 3. 使用邮箱的用户名和授权码连接邮件服务器
        transport.connect("smtp.qq.com", "676382250@qq.com", "cteblafzbrtkbfcj");

        // 4. 创建邮件
        Message message = new MimeMessage(session);

        // 设置发件人
        message.setFrom(new InternetAddress("676382250@qq.com"));
        // 设置邮件收件人
        message.setRecipient(Message.RecipientType.TO, new InternetAddress("li676382250@163.com"));
        // 设置邮件主题
        message.setSubject("测试java邮件发送");
        // 设置邮件内容
        message.setContent("肖川小宝贝，我有一个恋爱想和你谈一谈!!!", "text/html;charset=UTF-8");

        // 5. 发送邮件
        transport.sendMessage(message, message.getAllRecipients());

        transport.close();
    }
}
