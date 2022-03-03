package com.example.demo.src.project.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class GetContactRes {
    private String user_id;
    private int pj_views;
    private String pj_categoryName;
    private String pj_subCategoryName;
    private String pj_content;
    private String pj_name;
    private String pj_progress;
    private String pj_endTerm;
    private String pj_startTerm;
    private String pj_deadline;
    private String pj_totalPerson;
    private String pj_recruitPerson;
    private String user_nickname;
    private String user_prPhoto;
    private String[] hashtag;
    private int pjLikeCount;
}
