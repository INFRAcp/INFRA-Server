package com.example.demo.src.user.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class PostProfileReq {
    private String user_prProfile;
    private String [] user_prAbility = null;
    private String [] user_prLink= null;
    private String [] user_prKeyword = null;
}
