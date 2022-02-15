package com.example.demo.src.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class S3Controller {

    private final S3Uploader s3Uploader;

    @PostMapping("/images")
    public String upload(@RequestParam("images") MultipartFile multipartFile) throws IOException {
        s3Uploader.upload(multipartFile, "static");
        return "s3에 업로드 완료.";
    }
}