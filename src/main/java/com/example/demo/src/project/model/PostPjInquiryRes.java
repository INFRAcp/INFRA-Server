package com.example.demo.src.project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostPjInquiryRes {
    private int pj_num;
    private String pj_header;
    private int pj_views;
    private String pj_categoryName;
    private String pj_name;
    private int pj_subCategoryNum;
    private String pj_progress;
    private String pj_deadline;
    private int pj_totalPerson;
    private int pj_recruitPerson;
    private String pj_time;
    List<String> pj_photo;
}
