package com.example.demo.src.sms.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PostSmsReq {
    private String recipientPhoneNumber;
    private String title;
    private String content;

}
