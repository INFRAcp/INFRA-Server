package com.example.demo.src.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MailTest {
    @Autowired
    private final MailService mailService;

    @Autowired
    public MailTest(MailService mailService) {
        this.mailService = mailService;
    }

    @GetMapping("/test/mail/{email}")
    public String sendTestMail(@PathVariable("email") String email) {
        try {
            mailService.sendResetPwMail(email, "5555511214545");
            return "success";
        } catch (Exception e) {
            return "fail";
        }
    }
}
