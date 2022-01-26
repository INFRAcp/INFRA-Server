package com.example.demo.src.help.report.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class PostReportDelRes {
    private String user_id;
    private String reportedUser_id;
}
