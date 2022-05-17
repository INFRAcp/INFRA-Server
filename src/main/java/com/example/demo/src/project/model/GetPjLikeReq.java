package com.example.demo.src.project.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetPjLikeReq {
    private String user_id;
}
