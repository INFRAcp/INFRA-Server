package com.example.demo;

import lombok.Getter;

@Getter
public class ApiResponse {
    private boolean isSuccess;
    private int code;
    private String message;
    private Object result;
}
