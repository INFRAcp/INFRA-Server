package com.example.demo.src.help.qa.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class PostQaRes {
    private int QA_num; // 추후 table에 auto_inrement 적용 하고, 제거할 코드
    private String User_id;
    private String QA_q;
    private String QA_a;
}