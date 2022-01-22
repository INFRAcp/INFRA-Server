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
    private String pj_field=null;

    private String pj_content=null;
    private String pj_name=null;
    private String pj_subField=null;
    private String pj_progress=null;
    private LocalDate pj_end_term=null;

    private LocalDate pj_start_term=null;
    private LocalDate pj_deadline=null;
    private int pj_total_person=0;

    private String [] keyword;

}
