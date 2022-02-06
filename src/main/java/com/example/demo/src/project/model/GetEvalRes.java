package com.example.demo.src.project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class GetEvalRes {
    private String user_id;
    private String passiveUser_id;
    private Integer pj_num;
    private String opinion;
    private Float responsibility;
    private Float ability;
    private Float teamwork;
    private Float leadership;
}
