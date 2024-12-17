package com.pricehub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 发送降价通知邮件
     * 
     * @param to 邮件接收者
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    public void sendPriceDropEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("zjuheadmaster@zju.edu.cn"); 
            helper.setTo(to);                         // 收件人邮箱
            helper.setSubject(subject);               // 邮件主题
            helper.setText(content, true);             // 邮件内容（支持HTML格式）

            // 发送邮件
            mailSender.send(message);
            System.out.println("邮件发送成功！");
        } catch (MailException | MessagingException e) {
            e.printStackTrace();
            System.out.println("邮件发送失败！");
        }
    }
}
