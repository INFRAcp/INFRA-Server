package com.example.demo.src.project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class PatchEvalDelReq {
    private String user_id;
    private String passiveUser_id;
    private Integer pj_num;
}
