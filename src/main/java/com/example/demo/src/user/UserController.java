package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;

@RestController
@RequestMapping("/user")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;


    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService) {
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }


    /**
     * 회원가입 API
     * [POST] /user/sign-up
     */
    @ResponseBody
    @PostMapping("/sign-up")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        if (postUserReq.getUser_id() == null || postUserReq.getUser_pw() == null || postUserReq.getUser_nickname() == null
                || postUserReq.getUser_email() == null || postUserReq.getUser_name() == null || postUserReq.getUser_phone() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_INFO);
        }

        if (!isRegexId(postUserReq.getUser_id())) {   // id 형식 체크
            return new BaseResponse<>(POST_USERS_INVALID_ID);
        }
        if (!isRegexPw(postUserReq.getUser_pw())) {    // 비밀번호 형식 체크
            return new BaseResponse<>(POST_USERS_INVALID_PW);
        }
        if (!isRegexName(postUserReq.getUser_name())) {   // 이름 형식 체크
            return new BaseResponse<>(POST_USERS_INVALID_NAME);
        }
        if (!isRegexPhone(postUserReq.getUser_phone())) {    // 핸드폰 번호 형식 체크
            return new BaseResponse<>(POST_USERS_INVALID_PHONE);
        }
        if (!isRegexEmail(postUserReq.getUser_email())) {    // 이메일 형식 체크
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        try {
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 로그인 API
     * [POST] /user/logIn
     */
    @ResponseBody
    @PostMapping("/log-in")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) {
        if (postLoginReq.getUser_id() == null || postLoginReq.getUser_pw() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_INFO);
        }
        try {
            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * ID 중복 체크 API
     * [GET] /user/valid-id/:user_id
     */

    @ResponseBody
    @GetMapping("/valid-id/{user_id}")
    public BaseResponse<String> validId(@PathVariable("user_id") String user_id) {
        if (!isRegexId(user_id)) {   // id 형식 체크
            return new BaseResponse<>(POST_USERS_INVALID_ID);
        }

        try {
            if (userProvider.checkId(user_id) == 1) {
                throw new BaseException(POST_USERS_EXISTS_ID);
            }
            return new BaseResponse<>("사용가능한 아이디입니다.");
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 비밀번호 변경 API
     * [PATCH] /user/update-pw/:userId
     */
    @ResponseBody
    @PatchMapping("/update-pw/{userId}")
    public BaseResponse<String> modifyUserName(@PathVariable("userId") String userId, @RequestBody User user) {
        try {
            String userIdByJwt = jwtService.getUserId();    //jwt에서 id 추출
            if (!userId.equals(userIdByJwt)) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if (user.getUser_pw() == null) {
                return new BaseResponse<>(POST_USERS_EMPTY_INFO);
            }

            userService.modifyUserPw(PatchUserReq.builder().user_id(userId).user_pw(user.getUser_pw()).build());

            String result = "비밀번호가 성공적으로 변경되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 회원정보조회 API
     * [POST] /user/{user_id}
     *
     * @param user_id
     * @return
     * @author yewon
     */
    @ResponseBody
    @GetMapping("/{user_id}")
    public BaseResponse<List<GetUserRes>> getUser(@PathVariable("user_id") String user_id) {
        try {
            String userIdByJwt = jwtService.getUserId();
            if (!user_id.equals(userIdByJwt)) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetUserRes> getUserRes = userProvider.getUser(user_id);
            return new BaseResponse<>(getUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 회원탈퇴  API
     * [PATCH] /user/{user_id}
     *
     * @param user_id
     * @return
     * @author yewon
     */
    @ResponseBody
    @PatchMapping("/{user_id}")
    public BaseResponse<String> delUser(@PathVariable("user_id") String user_id) {
        try {
            String userIdByJwt = jwtService.getUserId();
            if (!user_id.equals(userIdByJwt)) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            userService.delUser(user_id);
            String result = "탈퇴가 정상적으로 처리되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 카카오 로그인 API
     * [POST] /user/kakao
     *
     * @param code
     * @return
     * @author yewon
     */

    @GetMapping("/kakao")
    public String kakaoLogin(@RequestParam("code") String code) throws UnsupportedEncodingException {
        System.out.println("kakao code : " + code);    // 인가코드 리턴
        String access_Token = UserService.getAccessToken(code);     // access_token 가져오기
        //System.out.println("controller access_token : " + access_Token);
        UserService.getUserInfo(access_Token);
        //HashMap<String, Object> userInfo = UserService.getUserInfo(access_Token);
        //System.out.println(userInfo);   // 사용자 정보
        return "로그인 되었습니다.";
    }

    /**
     * 카카오 로그아웃 API
     * @param session
     * @return
     * @author yewon
     */
    @GetMapping("/kakao/logout")
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
    @GetMapping("/kakao/unlink")
    public String sessionOut(HttpSession session) {
        String access_Token = (String)session.getAttribute("access_Token");
        session.invalidate();
        if(access_Token != null && !"".equals(access_Token)){
            UserService.kakaoLogout(access_Token);
            //session.removeAttribute("userId");
        }else{
            System.out.println("access_Token is null");
            return "redirect:/";
        }
        return "연결이 끊어졌습니다.";
    }

}


