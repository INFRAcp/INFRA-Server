package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.s3.S3Service;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.jwt.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    @Autowired
    private final S3Service s3Service;

    @Autowired
    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService, S3Service s3Service) {
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
        this.s3Service = s3Service;
    }


    /**
     * 회원가입 API
     * [POST] /user/sign-up
     *
     * @param postUserReq - id, pw, email, phone, nickname
     * @return BaseResponse
     * @author yunhee, yewon
     */
    @PostMapping("/sign-up")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) throws BaseException {
        if (postUserReq.getUser_id() == null || postUserReq.getUser_pw() == null || postUserReq.getUser_nickname() == null
                || postUserReq.getUser_email() == null || postUserReq.getUser_phone() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_INFO);
        }

        PostUserRes postUserRes = userService.createUser(postUserReq);
        return new BaseResponse<>(postUserRes);
    }

    /**
     * 로그인 API
     * [POST] /user/logIn
     *
     * @param postLoginReq - id, pw
     * @return BaseResponse
     * @author yunhee
     */
    @PostMapping("/log-in")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) throws BaseException {
        if (postLoginReq.getUser_id() == null || postLoginReq.getUser_pw() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_INFO);
        }
        PostLoginRes postLoginRes = userService.logIn(postLoginReq);
        return new BaseResponse<>(postLoginRes);
    }

    /**
     * ID 중복 체크 API
     * [GET] /user/valid-id/:user_id
     *
     * @param user_id
     * @return BaseResponse
     * @author yunhee
     */
    @GetMapping("/valid-id/{user_id}")
    public BaseResponse<String> validId(@PathVariable("user_id") String user_id) throws BaseException {
        if (!isRegexId(user_id)) {   // id 형식 체크
            return new BaseResponse<>(POST_USERS_INVALID_ID);
        }

        if (userProvider.checkId(user_id) == 1) {
            throw new BaseException(POST_USERS_EXISTS_ID);
        }
        return new BaseResponse<>("사용가능한 아이디입니다.");
    }

    /**
     * 닉네임 중복 체크 API (회원가입시 체크하지만 소셜 로그인으로 들어온 사용자를 위한 단독 체크 API)
     * [GET] /user/valid-nickname/:user_nickname
     *
     * @param getNicknameReq - nickname
     * @return
     * @author yewon
     */
    @ResponseBody
    @PostMapping("/valid-nickname")
    public BaseResponse<String> validNickname(@RequestBody GetNicknameReq getNicknameReq) {
        try {
            if (userProvider.checkNickname(getNicknameReq.getUser_nickname()) == 1) {
                throw new BaseException(POST_USERS_EXISTS_NICKNAME);
            }
            return new BaseResponse<>("사용 가능한 닉네임입니다.");
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 비밀번호 변경 API
     * [PATCH] /user/update-pw/:userId
     */
    @PatchMapping("/update-pw/{userId}")
    public BaseResponse<String> modifyUserName(@PathVariable("userId") String userId, @RequestBody User user) throws BaseException {
        jwtService.JwtEffectiveness(userId, jwtService.getUserId());

        if (user.getUser_pw() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_INFO);
        }

        userService.modifyUserPw(PatchUserReq.builder().user_id(userId).user_pw(user.getUser_pw()).build());

        String result = "비밀번호가 성공적으로 변경되었습니다.";
        return new BaseResponse<>(result);
    }

    /**
     * 비밀번호 초기화 API
     * [PATCH] /user/reset-pw
     *
     * @param user - phone 정보 들어와야 함
     * @return BaseResponse
     * @author yunhee
     */
    @PatchMapping("/reset-pw")
    public BaseResponse<String> resetPw(@RequestBody User user) throws BaseException {
        if (user.getUser_phone() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_INFO);
        }
        User userInfo = userProvider.getEmailFromPhone(user.getUser_phone());
        if (userInfo == null)
            return new BaseResponse<>(NOT_EXISTS_EMAIL);

        userService.resetPwMail(userInfo.getUser_id(), userInfo.getUser_email()); // 비밀번호 변경후 메일 전송
        return new BaseResponse<>("임시 비밀번호가 성공적으로 발송되었습니다.");
    }


    /**
     * 회원정보조회 API
     * [POST] /user/:userId
     *
     * @param user_id
     * @return
     * @author yewon
     */
    @GetMapping("/{user_id}")
    public BaseResponse<List<GetUserRes>> getUser(@PathVariable("user_id") String user_id) throws BaseException {
        jwtService.JwtEffectiveness(user_id, jwtService.getUserId());

        List<GetUserRes> getUserRes = userProvider.getUser(user_id);
        return new BaseResponse<>(getUserRes);
    }

    /**
     * 회원탈퇴  API
     * [PATCH] /user/:userId
     *
     * @param user_id
     * @return
     * @author yewon
     */
    @PatchMapping("/{user_id}")
    public BaseResponse<String> delUser(@PathVariable("user_id") String user_id) throws BaseException {
        jwtService.JwtEffectiveness(user_id, jwtService.getUserId());

        userService.delUser(user_id);
        String result = "탈퇴가 정상적으로 처리되었습니다.";
        return new BaseResponse<>(result);
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
    public BaseResponse<PostProfileRes> createProfile(@PathVariable("user_id") String user_id, @RequestBody PostProfileReq postProfileReq) throws BaseException {
        jwtService.JwtEffectiveness(user_id, jwtService.getUserId());

        PostProfileRes postProfileRes = userService.createProfile(user_id, postProfileReq);
        return new BaseResponse<>(postProfileRes);
    }


    /**
     * 소개 페이지 내용 조회 API
     * [GET] /user/profile/:userId
     *
     * @param userId
     * @return BaseResponse
     * @author yunhee, yewon, shinhyeon(s3)
     */
    @GetMapping("/profile/{userId}")
    public BaseResponse<GetProfileRes> getProfile(@PathVariable("userId") String userId) throws BaseException {
        GetProfileRes getProfileRes = userProvider.getProfile(userId);
        // 프로필 사진 가져오기
        String user_prPhoto = userProvider.getPrPhoto(getProfileRes.getUser_nickname());
        getProfileRes.setUser_prPhoto(user_prPhoto);
        return new BaseResponse<>(getProfileRes);
    }

    /**
     * 소개 페이지 수정 API
     * [PATCH] /user/profile/:userId
     *
     * @param user_id
     * @param patchProfileReq
     * @return
     * @author yewon
     */
    @PatchMapping("/profile/{user_id}")
    public BaseResponse<PatchProfileRes> modifyProfile(@PathVariable("user_id") String user_id, @RequestBody PatchProfileReq patchProfileReq) throws BaseException{
            jwtService.JwtEffectiveness(user_id, jwtService.getUserId());
            PatchProfileRes patchProfileRes = userService.modifyProfile(user_id, patchProfileReq);
            return new BaseResponse<>(patchProfileRes);
    }


    /**
     * 내 정보 조회(PR) API
     * [GET] /user/profile/info/userId
     *
     * @param user_id
     * @return
     * @author yewon
     */
    @GetMapping("/profile/info/{user_id}")
    public BaseResponse<GetInfoRes> getInfo (@PathVariable("user_id") String user_id) throws BaseException{
            jwtService.JwtEffectiveness(user_id, jwtService.getUserId());   // jwt token 검증
            GetInfoRes getInfoRes = userProvider.getInfo(user_id);  // user_id로 정보 조회
            // 프로필 사진 가져오기
            String user_prPhoto = userProvider.getPrPhoto(getInfoRes.getUser_nickname());
            getInfoRes.setUser_prPhoto(user_prPhoto);
            return new BaseResponse<>(getInfoRes);
    }

    /**
     * 내 정보 수정(PR) API
     * [PATCH] /user/profile/info/userId
     *
     * @param user_id
     * @return
     * @author yewon, shinhyeon(s3)
     */
    @ResponseBody
    @PatchMapping("/profile/info/{user_id}")
    public BaseResponse<String> modifyInfo(@PathVariable("user_id") String user_id, @RequestParam("user_nickname") String user_nickname, @RequestParam("user_prPhoto") String user_prPhoto, @RequestParam(value = "images", required = false) MultipartFile multipartFile) throws IOException, BaseException {
            jwtService.JwtEffectiveness(user_id, jwtService.getUserId());   // jwt token 검증

            if(user_prPhoto.equals("등록")){ // 프로필 사진 등록
                // s3에 업로드
                String imgPath = s3Service.uploadPrphoto(multipartFile, "test/prphoto");
                // db에 반영 (user_prPhoto)
                s3Service.uploadPrphoto(imgPath, user_id);
            }
            else if(user_prPhoto.equals("삭제")){ // 프로필 사진 삭제
                s3Service.delPrphoto(user_id);
            }
            else {
                return new BaseResponse<>(INVALID_ODER_PRPHOTO);
            }

            // 닉네임 변경
            userService.modifyInfo(user_id, user_nickname);

            String result = "정상으로 수정되었습니다.";
            return new BaseResponse<>(result);
    }

    /**
     * 전체 유저 프로필 조회 API
     * @return  사진, 닉네임, 능력, 평점, 키워드
     * @throws BaseException
     * @author yewon
     */
    @GetMapping("/profile/all")
    public BaseResponse<List<GetAllUserProfilesRes>> getAllProfile() throws BaseException {
        List<GetAllUserProfilesRes> getAllUserProfilesRes = userProvider.getAllProfile();    // 닉네임, 평점 가져오기
        // 프로필 사진 가져오기
        for (int i = 0; i < getAllUserProfilesRes.size(); i++) {
            // 모든 유저의 사진 가져와서 저장(가져올 때는 null값) -> 위에서 가져온 닉네임을 통해 getPrPhoto 메소드를 호출하여 각 유저의 사진을 가져와서 저장!
            getAllUserProfilesRes.get(i).setUser_prPhoto(userProvider.getPrPhoto(getAllUserProfilesRes.get(i).getUser_nickname()));
        }
        // 능력(user_ability) 가져오기
        for (int i = 0; i < getAllUserProfilesRes.size(); i++) {
            getAllUserProfilesRes.get(i).setUser_prAbility(userProvider.getAbility(getAllUserProfilesRes.get(i).getUser_id()));
        }
        // 키워드(user_keyword) 가져오기
        for (int i = 0; i < getAllUserProfilesRes.size(); i++) {
            getAllUserProfilesRes.get(i).setUser_prKeyword(userProvider.getKeyword(getAllUserProfilesRes.get(i).getUser_id()));
        }
        return new BaseResponse<>(getAllUserProfilesRes);
    }
}


