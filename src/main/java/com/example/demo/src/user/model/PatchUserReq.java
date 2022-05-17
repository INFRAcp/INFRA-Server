package com.example.demo.src.user.model;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatchUserReq {
    private String user_id;
    private String user_pw;
}