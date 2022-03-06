package com.example.demo.src.user.model;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatchInfoReq {
    private String user_nickname;
    //private String user_prPhoto;  // TODO 사진 수정 부분 추가 예정
}

