package com.example.demo.src.help.report.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class PostReportDelReq {
    private String User_id;
    private String ReportedUser_id;
}
