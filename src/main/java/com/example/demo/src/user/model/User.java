package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class User {
    private String user_id;
    private String user_pw;
    private String user_nickname;
    private float user_grade;
    private String user_phone;
    private String user_email;
    private String user_prPhoto;
    private String user_prProfile;
}