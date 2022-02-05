package com.example.demo.src.user.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class PostProfileReq {
    String user_prPhoto;
    String user_prProfile;
    String user_prAbility;
    String user_prLink;
    String user_prKeyword;
    String pj_request;
}
