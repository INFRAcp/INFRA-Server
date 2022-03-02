package com.example.demo.src.project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostUserApplyRes {
    private int pj_num;
    private String pj_inviteStatus;

    private int pj_views;
    private String pj_header;
}
