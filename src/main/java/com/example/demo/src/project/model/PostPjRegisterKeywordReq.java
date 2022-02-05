package com.example.demo.src.project.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostPjRegisterKeywordReq {
    private int pj_num;
    private String hashtag1;
    private String hashtag2;
    private String hashtag3;
    private String hashtag4;
}
