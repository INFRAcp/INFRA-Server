package com.example.demo.src.help.qa.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class GetQaRes {
    private Integer QA_num;
    private String User_id;
    private String QA_q;
    private String QA_a;
}
