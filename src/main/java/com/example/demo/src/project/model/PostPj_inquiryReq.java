package com.example.demo.src.project.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//유저가 조회했던 프로젝트 조회 Req
public class PostPj_inquiryReq {
    private String user_id;
}
