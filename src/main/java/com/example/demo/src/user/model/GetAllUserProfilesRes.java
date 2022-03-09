package com.example.demo.src.user.model;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAllUserProfilesRes {
    private String user_id;
    private String user_prPhoto;
    private String user_nickname;
    private String [] user_prAbility;
    private float user_grade;
    private String [] user_prKeyword;
}
