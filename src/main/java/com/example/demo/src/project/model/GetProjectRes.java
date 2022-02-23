package com.example.demo.src.project.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class GetProjectRes {
    private int pj_num;
    private String pj_header;
    private String pj_categoryName;
    private String pj_name;
    private String pj_progress;
    private String pj_deadline;
    private int pj_totalPerson;
    private int pj_recruitPerson;
    private String pj_recruit;
    private int pj_daysub;
    List<String> pj_photo;
}
