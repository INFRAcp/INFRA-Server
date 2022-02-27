package com.example.demo.src.help.qa.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonPropertyOrder({"qa_num", "user_id", "qa_q", "qa_qTime", "qa_a", "qa_aTime", "qa_status"})
public class GetQaRes {
    private Integer qa_num;
    private String user_id;
    private String qa_q;
    private String qa_a;
    private String qa_aTime;
    private String qa_qTime;
    private String qa_status;
}