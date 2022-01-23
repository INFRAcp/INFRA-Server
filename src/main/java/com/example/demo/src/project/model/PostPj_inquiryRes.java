package com.example.demo.src.project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostPj_inquiryRes {
    private int pj_num;
    private String pj_header;
    private int pj_views;
    private String pj_field;
    private String pj_name;
    private String pj_subField;
    private String pj_progress;
    private String pj_deadline;
    private int pj_totalPerson;
    private int pj_recruitPerson;
    private String pj_time;
}
