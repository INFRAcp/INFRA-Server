package com.example.demo.src.help.qa.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor

public class PatchQaReq {
    private String qa_q;
    private Timestamp qa_qTime;
}
