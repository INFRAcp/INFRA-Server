package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.user.model.PatchUserReq;
import com.example.demo.src.user.model.PostUserReq;
import com.example.demo.src.user.model.PostUserRes;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

/**
 * Service란?
 * Controller에 의해 호출되어 실제 비즈니스 로직과 트랜잭션을 처리: Create, Update, Delete 의 로직 처리
 * 요청한 작업을 처리하는 관정을 하나의 작업으로 묶음
 * dao를 호출하여 DB CRUD를 처리 후 Controller로 반환
 */
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log 처리부분: Log를 기록하기 위해 필요한 함수입니다.

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;


    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

    }

    // 회원가입(POST)
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        // id 중복 확인
        if (userProvider.checkId(postUserReq.getId()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_ID);
        }

        // 이메일 중복 확인
        if (userProvider.checkEmail(postUserReq.getEmail()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }

        // 전화번호 중복 확인
        if (userProvider.checkPhone(postUserReq.getPhone()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_PHONE);
        }

        String pwd;
        try {
            // 비밀번호 암호화
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserReq.getPw()); // 암호화코드
            postUserReq.setPw(pwd);
        } catch (Exception ignored) { // 암호화가 실패하였을 경우 에러 발생
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        try {
            userDao.createUser(postUserReq);
            String userId = postUserReq.getId();
            String jwt = jwtService.createJwt(userId);
            return new PostUserRes(userId, jwt);
        } catch (Exception exception) { // DB에 이상이 있는 경우
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 비밀번호 변경(Patch)
    public void modifyUserPw(PatchUserReq patchUserReq) throws BaseException {
        String pwd;
        try {   // 비밀번호 암호화
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(patchUserReq.getPw()); // 암호화코드
            patchUserReq.setPw(pwd);
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
}
