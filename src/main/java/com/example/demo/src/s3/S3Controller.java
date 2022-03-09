package com.example.demo.src.s3;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.utils.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.example.demo.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@RestController
public class S3Controller {

    @Autowired
    private final S3Service s3Service;
    @Autowired
    private final S3Provider s3Provider;
    @Autowired
    private final JwtService jwtService;

    /**
     * 프로필 사진 불러오기
     * @param user_id
     * @return String (이미지 경로)
     * @author shinhyeon
     */
    @ResponseBody
    @GetMapping("/prphoto")
    public BaseResponse<String> getPrphoto(@RequestParam(required = false) String user_id) throws BaseException {
        String imgPath = s3Provider.getPrphoto(user_id);
        return new BaseResponse<>(imgPath);
    }

}