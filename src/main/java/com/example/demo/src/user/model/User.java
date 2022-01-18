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
    private String userId;
    private String userPw;
    private String userNickname;
    private float userGrade;
    private String userPhone;
    private String userEmail;
    private String userName;
    private String userPrPhoto;
    private String userPrProfile;
}
