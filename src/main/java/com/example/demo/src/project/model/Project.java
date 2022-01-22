package com.example.demo.src.project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class Project {
    private int pj_num;
    private String User_id;
    private int pj_views;
    private String pj_header;
    private String pj_field;
    private String pj_name;
    private String pj_subField;
    private String pj_progress;
    private String pj_deadline;
    private int pj_total_person;
    private int pj_recruit_person;
    private String pj_time;

}
