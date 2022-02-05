package com.example.demo.src.project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class PatchPjModifyReq {
    private int pj_num=0;
    private String user_id = null;
    private String pj_header=null;
    private String pj_categoryNum =null;

    private String pj_content=null;
    private String pj_name=null;
    private String pj_subCategoryNum =null;
    private String pj_progress=null;
    private LocalDate pj_endTerm =null;

    private LocalDate pj_startTerm =null;
    private LocalDate pj_deadline=null;
    private int pj_totalPerson =0;

    private String [] hashtag;
}
