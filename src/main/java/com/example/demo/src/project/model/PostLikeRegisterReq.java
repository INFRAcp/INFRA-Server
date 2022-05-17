package com.example.demo.src.project.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//프로젝트 찜 등록 및 삭제
public class PostLikeRegisterReq {
    private String user_id;
    private int pj_num;
}
