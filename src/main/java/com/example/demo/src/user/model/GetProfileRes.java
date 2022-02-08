package com.example.demo.src.user.model;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetProfileRes {
    String user_nickname;
    Float user_grade;
    String user_prPhoto;
    String user_prProfile;
    List<String> user_prAbility;
    List<String> user_prLink;
    List<String> user_prKeyword;
    List<String> pj_request;
}
