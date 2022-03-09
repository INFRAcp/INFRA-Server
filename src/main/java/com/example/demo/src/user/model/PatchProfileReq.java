package com.example.demo.src.user.model;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatchProfileReq {
    private String user_prProfile;  // 소개글
    private String [] user_prAbility = null;    // 능력
    // TODO : 프로젝트 제목 수정 부분 회의 이후에 추가 예정
    //private String [] pj_header = null;   // 진행한 프로젝트 이름
    private String [] user_prLink = null;   // 링크
    private String [] user_prKeyword = null;    // 해시태그(밑에 넣는 키워드)
}
