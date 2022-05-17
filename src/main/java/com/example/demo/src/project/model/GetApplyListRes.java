package com.example.demo.src.project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetApplyListRes {
    private String user_id;
    private String user_nickname;
    private String user_grade;
    private String user_prphoto;

    private String pj_inviteStatus;
}
