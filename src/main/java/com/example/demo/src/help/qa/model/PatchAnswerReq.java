package com.example.demo.src.help.qa.model;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class PatchAnswerReq {
    @NotBlank
    private String qa_a;
}
