package com.example.demo.src.project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchPjApproveReq {
    private String user_id;
    private Integer pj_num;
    private String pj_inviteStatus;
}
