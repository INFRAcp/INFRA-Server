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

    /**
     * 회원가입
     *
     * @param postUserReq
     * @return PostUserRes - user_id, jwt
     * @throws BaseException
     * @author yunhee
     */
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        if (userProvider.checkId(postUserReq.getUser_id()) == 1) {  // id 중복 확인
            throw new BaseException(POST_USERS_EXISTS_ID);
        }
        if (userProvider.checkNickname(postUserReq.getUser_nickname()) == 1) {  // nickname 중복 확인
            throw new BaseException(POST_USERS_EXISTS_NICKNAME);
        }
        if (userProvider.checkEmail(postUserReq.getUser_email()) == 1) { // 이메일 중복 확인
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }
        if (userProvider.checkPhone(postUserReq.getUser_phone()) == 1) { // 전화번호 중복 확인
            throw new BaseException(POST_USERS_EXISTS_PHONE);
        }

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

}