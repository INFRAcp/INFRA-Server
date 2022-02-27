package com.example.demo.src.help.qa.model;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
public class PostQaReq {
    @NotBlank
    private String user_id;

    @NotBlank
    private String qa_q;
}
