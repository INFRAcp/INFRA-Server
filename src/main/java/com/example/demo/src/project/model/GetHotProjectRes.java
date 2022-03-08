package com.example.demo.src.project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetHotProjectRes {
    private String user_id;
    private int pj_num;
    private String pj_header;
    private int pj_views;
    private int pj_views_1day;
    private String pj_categoryName;
    private String pj_subCategoryName;
    private String pj_progress;
    private String pj_deadline;
    private int pj_totalPerson;
    private int pj_recruitPerson;
    private String pj_recruit;
    private int pj_daysub;
    private int pj_like;
    private String [] hashtag;
    List<String> pj_photo;
}
