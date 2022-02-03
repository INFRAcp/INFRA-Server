package com.example.demo.src.sms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostSmsRequest {
    private String type;
    private String contentType;
    private String countryCode;
    private String from;
    private String Content;
    private List<PostMessageModel> messages;
}
