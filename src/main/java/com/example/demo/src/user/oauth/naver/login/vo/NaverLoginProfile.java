package com.example.demo.src.user.oauth.naver.login.vo;

import lombok.Data;
@Data
public class NaverLoginProfile {
    // 동일인 식별 정보는 네이버 아이디마다 고유하게 발급되는 값입니다.
    private String id;
    // 사용자 정보 : 이메일
    private String email;
}
