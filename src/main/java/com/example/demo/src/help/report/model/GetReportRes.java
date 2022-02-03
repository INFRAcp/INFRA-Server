package com.example.demo.src.help.report.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class GetReportRes {
    private String reportedUser_id;
    private String rp_category;
    private String rp_field;
    private String rp_opinion;
}
