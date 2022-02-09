package com.example.demo.src.user.model;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetProfileRes {
    private String user_nickname;
    private float user_grade;
    private String user_prPhoto;
    private String user_prProfile;
    private List<String> user_prAbility;
    private List<String> user_prLink;
    private List<String> user_prKeyword;
    private List<String> pj_name;
}
