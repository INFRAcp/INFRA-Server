package com.example.demo.src.mail;

import com.example.demo.config.BaseException;
import com.example.demo.src.mail.model.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

import static com.example.demo.config.BaseResponseStatus.EMAIL_AUTH_ERROR;
import static com.example.demo.config.BaseResponseStatus.EMAIL_ERROR;

@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.nickname} <${spring.mail.username}>")
    private String FROM;

    /**
     * 임시 비밀번호 발송
     *
     * @param email
     * @param userPw
     * @throws BaseException
     * @author yunhee
     */
    public void sendResetPwMail(String email, String userPw) throws BaseException {
        Mail mail = new Mail();
        mail.setAddress(email);
        mail.setTitle("[인프라] 임시 비밀번호");
        mail.setMessage("비밀번호 : " + userPw);
        try {
            sendMail(mail);
        } catch (BaseException exception){
            throw new BaseException(exception.getStatus());
        } catch (Exception exception) {
            throw new BaseException(EMAIL_ERROR);
        }
    }


    /**
     * 메일 발송
     *
     * @param mail
     * @throws BaseException
     * @author yunhee
     */
    public void sendMail(Mail mail) throws BaseException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(FROM);
            helper.setSubject(mail.getTitle());
            helper.setText(mail.getMessage());
            helper.setTo(mail.getAddress());

            mailSender.send(message);
        } catch (MailAuthenticationException e) {
//            e.printStackTrace();
            throw new BaseException(EMAIL_AUTH_ERROR);
        } catch (Exception e) {
            throw new BaseException(EMAIL_ERROR);
        }
    }
}