package com.example.demo.src.user.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class PostProfileReq {
    String user_prPhoto;
    String user_prProfile;
    List<String> user_prAbility;
    List<String> user_prLink;
    List<String> user_prKeyword;
    List<String> pj_request;
}
