package com.example.demo.src.project.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//프로젝트에 참여한 팀원들 조회
public class PostPjParticipateReq {
    private int pj_num;
}
