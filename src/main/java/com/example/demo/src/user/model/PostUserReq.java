package com.example.demo.src.user.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostUserReq {
    private String user_id;
    private String user_pw;
    private String user_nickname;
    private String user_phone;
    private String user_email;
    private String user_name;
}