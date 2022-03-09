package com.example.demo.src.user.model;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetInfoRes {
    private String user_nickname;
    private String user_prPhoto;
}
