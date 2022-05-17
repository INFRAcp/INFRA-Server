package com.example.demo.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import static com.example.demo.config.BaseResponseStatus.*;

@RestControllerAdvice
public class RestControllerExceptionAdvice {
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BaseResponse noHandlerFoundHandler(NoHandlerFoundException e) {
        return new BaseResponse(URI_NOT_EXIST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public BaseResponse methodNotSupportedHandler(HttpRequestMethodNotSupportedException e) {
        return new BaseResponse(METHOD_NOT_EXIST);
    }

    @ExceptionHandler(BaseException.class)
    public BaseResponse baseExceptionHandler(BaseException e) {
        return new BaseResponse(e.getStatus());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse requestBodyFormatErrorHandler(Exception e) {
        return new BaseResponse(REQUEST_BODY_FORMAT_ERROR);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse mediaTypeNotSupportedHandler(Exception e) {
        return new BaseResponse(HTTP_MEDIA_TYPE_NOT_SUPPORTED);
    }
}