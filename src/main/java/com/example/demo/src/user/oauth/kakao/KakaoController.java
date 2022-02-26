package com.example.demo.src.user.oauth.kakao;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/user/kakao")

public class KakaoController {

    @Autowired
    private final KakaoService kakaoService;

    public KakaoController(KakaoService kakaoService) {
        this.kakaoService = kakaoService;
    }

    /**
     * 카카오 로그인 API
     * [POST] /user/kakao
     *
     * @param code
     * @return
     * @author yewon
     */

    @GetMapping("")
    public String kakaoLogin(@RequestParam("code") String code) throws UnsupportedEncodingException {
        System.out.println("kakao code : " + code);    // 인가코드 리턴
        String access_Token = kakaoService.getAccessToken(code);     // access_token 가져오기
        //System.out.println("controller access_token : " + access_Token);
        kakaoService.getUserInfo(access_Token);
//        HashMap<String, Object> userInfo = UserService.getUserInfo(access_Token);
//        System.out.println(userInfo);   // 사용자 정보
        return "로그인 되었습니다.";
    }

    /**
     * 카카오 로그아웃 API
     * @param session
     * @return
     * @author yewon
     */
    @GetMapping("/logout")
    public String logOut(HttpSession session) {
        String access_Token = (String)session.getAttribute("access_Token");
        session.invalidate();
        return "로그아웃 되었습니다.";
    }

    /**
     * 카카오 연결 끊기 API
     * @param session
     * @return
     * @author yewon
     */
    @GetMapping("/unlink")
    public String sessionOut(HttpSession session) {
        String access_Token = (String)session.getAttribute("access_Token");
        session.invalidate();
        if(access_Token != null && !"".equals(access_Token)){
            kakaoService.kakaoLogout(access_Token);
            //session.removeAttribute("userId");
        }else{
            System.out.println("access_Token is null");
            return "redirect:/";
        }
        return "연결이 끊어졌습니다.";
    }

    /**
     * 카카오 회원탈퇴  API
     * [PATCH] /{user_email}
     *
     * @param user_email
     * @return
     * @author yewon
     */
    @ResponseBody
    @PatchMapping("/{user_email}")
    public BaseResponse<String> delUser(@PathVariable("user_email") String user_email) {
        try {
            kakaoService.delUser(user_email);
            String result = "탈퇴가 정상적으로 처리되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
