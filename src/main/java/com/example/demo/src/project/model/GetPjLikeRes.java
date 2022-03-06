package com.example.demo.src.project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetPjLikeRes {
    private int pj_num;
    private String pj_header;
    private int pj_views;
    private String pj_categoryName;
    private String pj_subCategoryNum;
    private String pj_progress;
    private String pj_deadline;
    private int pj_totalPerson;
    private int pj_recruitPerson;
    private String pj_time;
    List<String> pj_photo;
    private String pj_recruit;
    private int pj_daysub;
    private int pj_like;
    private String [] hashtag;
}
