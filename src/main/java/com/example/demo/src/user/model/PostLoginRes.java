package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
/**
 * Res.java : From Server To Client
 * 로그인의 결과(Respone)를 보여주는 데이터의 형태
 */
public class PostLoginRes {
    private String user_id;
    private String jwtAccess;
    private String jwtRefresh;
    private String user_name;
    private String user_nickname;
}
