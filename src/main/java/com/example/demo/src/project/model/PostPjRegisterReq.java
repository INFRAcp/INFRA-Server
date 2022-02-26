package com.example.demo.src.project.model;

import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class PostPjRegisterReq {
    private int pj_num;
    private String user_id;
    private int pj_views=0;
    private String pj_header=null;
    private String pj_categoryName =null;
    private String pj_categoryNum = null;

    private String pj_content=null;
    private String pj_name=null;
    private String pj_subCategoryNum=null;
    private String pj_subCategoryName=null;
    private String pj_progress=null;
    private String pj_endTerm =null;

    private String pj_startTerm =null;
    private String pj_deadline=null;
    private int pj_totalPerson =0;
    private int pj_recruitPerson;
    private Timestamp pj_time;
    private String pj_status;

    private String [] hashtag =null;
}
