package com.example.demo.src.mail;

import com.example.demo.src.mail.model.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.nickname} <${spring.mail.username}>")
    private String FROM;

    public void sendResetPwMail(String email, String userPw) {
        Mail mail = new Mail();
        mail.setAddress(email);
        mail.setTitle("[인프라] 임시 비밀번호");
        mail.setMessage("비밀번호 : " + userPw);
        sendMail(mail);
    }

    public void sendMail(Mail mail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(FROM);
            helper.setSubject(mail.getTitle());
            helper.setText(mail.getMessage());
            helper.setTo(mail.getAddress());

            mailSender.send(message);
        } catch (MailAuthenticationException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("계정 인증 실패");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}