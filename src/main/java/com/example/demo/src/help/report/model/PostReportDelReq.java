package com.example.demo.src.help.report.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class PostReportDelReq {
    private String user_id;
    private String reportedUser_id;
}
