package com.example.demo.src.help.qa.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class Qa {
    private int qa_num;
    private String user_id;
    private String qa_q;
    private String qa_a;
}
