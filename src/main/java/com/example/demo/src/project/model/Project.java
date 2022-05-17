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
    private String user_id;
    private int pj_views;
    private String pj_header;
    private String pj_categoryNum;
    private String pj_subCategoryNum;
    private String pj_progress;
    private String pj_deadline;
    private int pj_totalPerson;
    private int pj_recruitPerson;
    private String pj_time;

}
