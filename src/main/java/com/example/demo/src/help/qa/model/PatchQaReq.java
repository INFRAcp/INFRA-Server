package com.example.demo.src.help.qa.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class PatchQaReq {
    private int qa_num;
    private String qa_q;
}
