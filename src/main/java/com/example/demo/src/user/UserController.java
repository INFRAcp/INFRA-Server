package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.PostLoginReq;
import com.example.demo.src.user.model.PostLoginRes;
import com.example.demo.src.user.model.PostUserReq;
import com.example.demo.src.user.model.PostUserRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
