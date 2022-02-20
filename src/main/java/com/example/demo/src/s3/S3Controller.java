package com.example.demo.src.s3;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.utils.JwtService;

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
     * 이미지 업로드
     * @param user_id
     * @param multipartFile
     * @return  String
     * @throws IOException
     * @author shinhyeon
     */
    @ResponseBody
    @PostMapping("/prphoto/{user_id}")
    public BaseResponse<String> uploadPrphoto(@PathVariable("user_id") String user_id, @RequestParam("images") MultipartFile multipartFile) throws IOException {

        try{
            // jwt
            String userIdByJwt = jwtService.getUserId();
            if(!user_id.equals(userIdByJwt)){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            // s3에 업로드
            String imgPath = s3Service.uploadPrphoto(multipartFile, "prphoto");
            // db에 반영 (user_prPhoto)
            s3Service.uploadPrphoto(imgPath, user_id);

            return new BaseResponse<>("업로드 완료");

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 이미지 불러오기
     * @param user_id
     * @return String (이미지 경로)
     * @author shinhyeon
     */
    @ResponseBody
    @GetMapping("/prphoto/{user_id}")
    public BaseResponse<String> getPrphoto(@PathVariable("user_id") String user_id){
        String imgPath = s3Provider.getPrphoto(user_id);
        return new BaseResponse<>(imgPath);
    }
}