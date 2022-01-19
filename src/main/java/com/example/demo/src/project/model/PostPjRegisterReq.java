package com.example.demo.src.project.model;

import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostPjRegisterReq {
    private int pj_num;
    private String user_id;
    private int pj_views;
    private String pj_header =null;
    private String pj_field;

    private String pj_content;
    private String pj_name;
    private String pj_subField;
    private String pj_progress;
    private LocalDate pj_end_term;

    private LocalDate pj_start_term;
    private LocalDate pj_deadline;
    private int pj_total_person;
    private int pj_recruit_person;
    private Timestamp pj_time;

    private String keyword1 = null;
    private String keyword2 = null;
    private String keyword3 = null;
    private String keyword4 = null;
//    private List<String> keyword;
}
