package com.example.demo.src.user.oauth.naver.login;

import com.example.demo.config.BaseException;
import com.example.demo.src.user.oauth.naver.login.vo.NaverLoginProfile;
import com.example.demo.src.user.oauth.naver.login.vo.NaverLoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class NaverLoginController {


    @Autowired
    private NaverLoginService service;

    public NaverLoginController(NaverLoginService naverLoginService) {
        this.service = service;
    }


    @GetMapping("/")
    public String index(){
        return "index";
    }

    /**
     * 네이버 로그인 API
     * @param resValue
     * @return
     * @throws BaseException
     * @author 예원, 성식
     */
    @GetMapping("/user/naver")
    public @ResponseBody String NaverLoginCallback(@RequestParam Map<String, String> resValue) throws BaseException {

        // code 를 받아오면 code 를 사용하여 access_token를 발급받는다.
        final NaverLoginVo naverLoginVo = service.requestNaverLoginAcceccToken(resValue, "authorization_code");

        // access_token를 사용하여 사용자의 고유 id값을 가져온다.
        final NaverLoginProfile naverLoginProfile = service.requestNaverLoginProfile(naverLoginVo);
//        System.out.println(naverLoginProfile.getEmail());
        service.insertInfo(naverLoginProfile.getEmail());
        return naverLoginProfile.toString();
    }

}