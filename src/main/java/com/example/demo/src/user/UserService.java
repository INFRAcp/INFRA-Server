package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.mail.MailService;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;

@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log 처리부분: Log를 기록하기 위해 필요한 함수입니다.

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;
    private final MailService mailService;

    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService, MailService mailService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;
        this.mailService = mailService;
    }

    /**
     * 회원가입
     *
     * @param postUserReq
     * @return PostUserRes - user_id, jwt
     * @throws BaseException
     * @author yunhee
     */
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        checkCreateUserRegex(postUserReq);  // 데이터 형식 체크

        if (userProvider.checkId(postUserReq.getUser_id()) == 1)  // id 중복 확인
            throw new BaseException(POST_USERS_EXISTS_ID);

        if (userProvider.checkNickname(postUserReq.getUser_nickname()) == 1)  // nickname 중복 확인
            throw new BaseException(POST_USERS_EXISTS_NICKNAME);

        if (userProvider.checkEmail(postUserReq.getUser_email()) == 1) // 이메일 중복 확인
            throw new BaseException(POST_USERS_EXISTS_EMAIL);

        if (userProvider.checkPhone(postUserReq.getUser_phone()) == 1) // 전화번호 중복 확인
            throw new BaseException(POST_USERS_EXISTS_PHONE);

        String checkPossible;
        try {   // 가입 가능 여부 체크
            checkPossible = userDao.checkPossibleSignUp(postUserReq.getUser_phone());
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

        if (checkPossible.equals("DEL"))
            throw new BaseException(FAILED_TO_SIGNUP_DEL_USER);
        else if (checkPossible.equals("OUT"))
            throw new BaseException(FAILED_TO_SIGNUP_OUT_USER);
        else if (checkPossible.equals("ALREADY_USER"))
            throw new BaseException(FAILED_TO_SIGNUP_ALREADY_USER);

        String pwd;
        try {   // 비밀번호 암호화
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserReq.getUser_pw()); // 암호화코드
            postUserReq.setUser_pw(pwd);
        } catch (Exception ignored) { // 암호화가 실패하였을 경우 에러 발생
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        try {
            userDao.createUser(postUserReq);
            String userId = postUserReq.getUser_id();
            String jwt = jwtService.createJwt(userId);
            return new PostUserRes(userId, jwt);
        } catch (Exception exception) { // DB에 이상이 있는 경우
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 비밀번호 변경
     *
     * @param patchUserReq
     * @return
     * @throws BaseException
     * @author yunhee
     */
    public void modifyUserPw(PatchUserReq patchUserReq) throws BaseException {
        String pwd;
        try {   // 비밀번호 암호화
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(patchUserReq.getUser_pw()); // 암호화코드
            patchUserReq.setUser_pw(pwd);
        } catch (Exception ignored) { // 암호화가 실패하였을 경우 에러 발생
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        try {
            int result = userDao.modifyUserPw(patchUserReq);
            if (result == 0) { // 변경 실패
                throw new BaseException(MODIFY_FAIL_USERPW);
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 회원탈퇴 API
     *
     * @param user_id
     * @return
     * @throws BaseException
     * @author yewon
     */
    public void delUser(String user_id) throws BaseException {
        try {
            userDao.delUser(user_id);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 패스워드 초기화 후 메일 보내기
     *
     * @param email
     * @throws BaseException
     * @author yunhee
     */
    public void resetPwMail(String id, String email) throws BaseException {
        String pw = RandomStringUtils.randomAlphanumeric(12);
        PatchUserReq patchUserReq = new PatchUserReq(id, pw);
        try {
            modifyUserPw(patchUserReq); // 비밀번호 변경
            mailService.sendResetPwMail(email, pw);
        } catch (Exception exception) { // DB에 이상이 있는 경우
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 회원 가입 정보 regex 체크
     *
     * @param postUserReq
     * @throws BaseException
     * @author yunhee
     */
    public void checkCreateUserRegex(PostUserReq postUserReq) throws BaseException {
        if (!isRegexId(postUserReq.getUser_id())) {   // id 형식 체크
            throw new BaseException(POST_USERS_INVALID_ID);
        }
        if (!isRegexPw(postUserReq.getUser_pw())) {    // 비밀번호 형식 체크
            throw new BaseException(POST_USERS_INVALID_PW);
        }
        if (!isRegexName(postUserReq.getUser_name())) {   // 이름 형식 체크
            throw new BaseException(POST_USERS_INVALID_NAME);
        }
        if (!isRegexPhone(postUserReq.getUser_phone())) {    // 핸드폰 번호 형식 체크
            throw new BaseException(POST_USERS_INVALID_PHONE);
        }
        if (!isRegexEmail(postUserReq.getUser_email())) {    // 이메일 형식 체크
            throw new BaseException(POST_USERS_INVALID_EMAIL);
        }
    }

    /**
     * 소개 페이지 작성 API
     *
     * @param postProfileReq
     * @return
     * @throws BaseException
     * @author yewon
     */
    public PostProfileRes createProfile(String user_id, PostProfileReq postProfileReq) throws BaseException {
        /** 소개 페이지 작성 예외처리 **/
        // 필수로 입력해야 할 정보(닉네임, 소개글, 능력, 키워드) 미입력시 예외 발생
        if (postProfileReq.getUser_prProfile() == null || postProfileReq.getUser_prAbility() == null
                || postProfileReq.getUser_prKeyword() == null) {
            throw new BaseException(POST_USERS_PROFILE_EMPTY_INFO);
        }
        // 소개글(profile)은 최소 10자 이상 작성 - 그 미만일 경우 예외 발생
        if (postProfileReq.getUser_prProfile().length() < 10) {
            throw new BaseException(POST_USER_PROFILE_MIN_PROFILE);
        }
        // 능력(ability)는 최소 1글자 이상 입력, 총 개수는 무제한 - 빈 값이 들어올 경우 예외 발생
        for (int i = 0; i < postProfileReq.getUser_prAbility().length; i++) {
            if (postProfileReq.getUser_prAbility()[i].trim().length() == 0) {
                throw new BaseException(POST_USER_PROFILE_MIN_ABILITY);
            }
        }
        // 키워드는 최대 6개를 입력할 수 있음 - 6개 초과시 예외 발생
        if (postProfileReq.getUser_prKeyword().length > 6) {
            throw new BaseException(POST_USERS_PROFILE_KEYWORD_COUNT);
        }
        // 키워드의 글자 수는 최소 1글자 이상 최대 5글자 이하 - 빈 값이나 6글자 이상일 경우에 예외 발생
        for (int i = 0; i < postProfileReq.getUser_prKeyword().length; i++) {
            if (postProfileReq.getUser_prKeyword()[i].length() > 5 || postProfileReq.getUser_prKeyword()[i].trim().length() == 0) {
                throw new BaseException(POST_USERS_PROFILE_KEYWORD_WORD_COUNT);
            }
        }
        try {
            String result = userDao.createProfile(user_id, postProfileReq);
            return new PostProfileRes(result);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}