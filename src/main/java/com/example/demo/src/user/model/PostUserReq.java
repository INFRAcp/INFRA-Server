package com.example.demo.src.user.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostUserReq {
    private String id;
    private String pw;
    private String nickname;
    private String phone;
    private String email;
    private String name;
    private String prPhoto;
    private String prProfile;
}