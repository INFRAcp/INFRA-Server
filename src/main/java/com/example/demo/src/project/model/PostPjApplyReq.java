package com.example.demo.src.project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostPjApplyReq {
    private String user_id;
    private int pj_num;
}
