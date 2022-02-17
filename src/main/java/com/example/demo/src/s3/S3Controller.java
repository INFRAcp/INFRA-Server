package com.example.demo.src.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class S3Controller {

    private final S3Service s3Service;
    private final S3Provider s3Provider;

    // 이미지 업로드
    @PostMapping("/prphoto/{user_id}")
    public String uploadPrphoto(@PathVariable("user_id") String user_id, @RequestParam("images") MultipartFile multipartFile) throws IOException {
        // s3에 업로드
        String imgPath = s3Service.uploadPrphoto(multipartFile, "prphoto");
        // db에 반영 (user_prPhoto)
        s3Service.uploadPrphoto(imgPath, user_id);

        return "업로드 완료";
    }

    // 이미지 불러오기
    @GetMapping("/prphoto/{user_id}")
    public String getPrphoto(@PathVariable("user_id") String user_id){
        String imgPath = s3Provider.getPrphoto(user_id);
        return imgPath;
    }
}