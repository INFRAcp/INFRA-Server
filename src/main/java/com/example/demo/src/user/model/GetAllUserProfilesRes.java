package com.example.demo.src.user.model;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAllUserProfilesRes {
    private String user_prPhoto;
    private String user_nickname;
    private List<String> user_prAbility;
    private float user_grade;
    private List<String> user_prKeyword;
}
