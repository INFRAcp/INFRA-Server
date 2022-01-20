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
    private String id;
    private String pw;
    private String nickname;
    private float grade;
    private String phone;
    private String email;
    private String name;
    private String prPhoto;
    private String prProfile;
}
