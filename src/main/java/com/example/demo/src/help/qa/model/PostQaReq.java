package com.example.demo.src.help.qa.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class PostQaReq {
//    private int QA_num; 자동으로 증가하게 해야됨
    private String User_id;
    private String QA_q;
    private String QA_a;
}
