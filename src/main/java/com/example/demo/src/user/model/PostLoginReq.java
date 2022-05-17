package com.example.demo.src.user.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLoginReq {
    private String user_id;
    private String user_pw;
}
