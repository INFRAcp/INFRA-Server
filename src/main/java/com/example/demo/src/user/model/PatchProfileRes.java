package com.example.demo.src.user.model;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatchProfileRes {
    private String user_prProfile;  // 소개글
    private String [] user_prAbility = null;    // 능력
    private String [] pj_name = null;   // 진행한 프로젝트 이름
    private String [] user_prLink = null;
    private String [] user_prKeyword = null;
}
