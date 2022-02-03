package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexId;

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
     *
     * @param postUserReq - id, pw, name, email, phone, nickname
     * @return BaseResponse
     * @author yunhee
     */
    @ResponseBody
    @PostMapping("/sign-up")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        if (postUserReq.getUser_id() == null || postUserReq.getUser_pw() == null || postUserReq.getUser_nickname() == null
                || postUserReq.getUser_email() == null || postUserReq.getUser_name() == null || postUserReq.getUser_phone() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_INFO);
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
     *
     * @param postLoginReq - id, pw
     * @return BaseResponse
     * @author yunhee
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
     *
     * @param user_id
     * @return BaseResponse
     * @author yunhee
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
     * 비밀번호 초기화 API
     * [PATCH] /user/reset-pw
     *
     * @param user - phone 정보 들어와야 함
     * @return BaseResponse
     * @author yunhee
     */
    @ResponseBody
    @PatchMapping("/reset-pw")
    public BaseResponse<String> resetPw(@RequestBody User user) {
        if (user.getUser_phone() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_INFO);
        }
        try {
            User userInfo = userProvider.getEmailFromPhone(user.getUser_phone());
            if (userInfo == null)
                return new BaseResponse<>(NOT_EXISTS_EMAIL);

            userService.resetPwMail(userInfo.getUser_id(), userInfo.getUser_email()); // 비밀번호 변경후 메일 전송
            return new BaseResponse<>("임시 비밀번호가 성공적으로 발송되었습니다.");
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
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
     * 소개 페이지 작성 API
     * [POST] /user/profile
     *
     * @param postProfileReq
     * @return profile, photo, ability, link, keyword, request(project)
     * @author yewon
     */
    @ResponseBody
    @PostMapping("/profile/{user_id}")
    public BaseResponse<PostProfileRes> createProfile (@PathVariable ("user_id") String user_id, @RequestBody PostProfileReq postProfileReq) {
        if (postProfileReq.getUser_prProfile() == null || postProfileReq.getUser_prAbility() == null
        || postProfileReq.getUser_prKeyword() == null) {
            return new BaseResponse<>(POST_USERS_PROFILE_EMPTY_INFO);   // 필수로 입력해야 할 정보(닉네임, 소개글, 능력, 키워드)
        }
        if (postProfileReq.getUser_prKeyword().size() <= 6) {       // 키워드는 총 6개를 입력해야 함.
            return new BaseResponse<>(POST_USERS_PROFILE_KEYWORD_COUNT);
        }
        for (int i = 0; i<postProfileReq.getUser_prKeyword().size(); i++) {
            if (postProfileReq.getUser_prKeyword().get(i).length() > 5) {
                return new BaseResponse<>(POST_USERS_PROFILE_KEYWORD_WORD_COUNT);   // 키워드의 글자 수는 5글자 이하
            }
        }
        try {
            PostProfileRes postProfileRes = userService.createProfile(user_id, postProfileReq);
            return new BaseResponse<>(postProfileRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 소개 페이지 내용 조회 API
     * [GET] /user/profile/:userId
     *
     * @param userId
     * @return BaseResponse
     * @author yunhee
     */
    @ResponseBody
    @GetMapping("/profile/{userId}")
    public BaseResponse<GetProfileRes> getProfile(@PathVariable("userId") String userId) {
        try {
            GetProfileRes getProfileRes = userProvider.getProfile(userId);
            return new BaseResponse<>(getProfileRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}


