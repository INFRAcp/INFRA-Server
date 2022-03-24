package com.example.demo.src.project.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetMyPjInquiryRes {
    //project
    private int pj_num;
    private String pj_header;
    private String pj_categoryName;
    private int pj_subCategoryNum;
    private String pj_progress;
    private String pj_deadline;
    private int pj_totalPerson;
    private int pj_recruitPerson;
    List<String> pj_photo;
    private String pj_recruit;
    private int pj_daysub;
    private String [] hashtag;
}
