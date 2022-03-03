package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.GetProfileRes;
import com.example.demo.src.user.model.GetUserRes;
import com.example.demo.src.user.model.User;
import com.example.demo.utils.jwt.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.NOT_EXISTS_USER_ID;

@Service
public class UserProvider {
    private final UserDao userDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }




    /**
     * id 중복 체크
     *
     * @param id
     * @return int - 이미 존재하면 1, 없으면 0
     * @throws BaseException
     * @author yunhee
     */
    @Transactional(readOnly = true)
    public int checkId(String id) throws BaseException {
        try {
            return userDao.checkId(id);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 이메일 중복 체크
     *
     * @param email
     * @return int - 이미 존재하면 1, 없으면 0
     * @throws BaseException
     * @author yunhee
     */
    @Transactional(readOnly = true)
    public int checkEmail(String email) throws BaseException {
        try {
            return userDao.checkEmail(email);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 핸드폰 번호 중복 체크
     *
     * @param phone
     * @return int - 이미 존재하면 1, 없으면 0
     * @throws BaseException
     * @author yunhee
     */
    @Transactional(readOnly = true)
    public int checkPhone(String phone) throws BaseException {
        try {
            return userDao.checkPhone(phone);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * nickname 중복 체크
     *
     * @param user_nickname
     * @return int - 이미 존재하면 1, 없으면 0
     * @throws BaseException
     * @author yunhee, yewon
     */
    @Transactional(readOnly = true)
    public int checkNickname(String user_nickname) throws BaseException {
        try {
            return userDao.checkNickname(user_nickname);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * phone에 해당하는 email 정보 가져오기
     * @param phone
     * @return User
     * @throws BaseException
     * @author yunhee
     */
    @Transactional(readOnly = true)
    public User getEmailFromPhone(String phone) throws BaseException {
        try {
            return userDao.getEmailFromPhone(phone);
        } catch (IncorrectResultSizeDataAccessException error) {
            return null;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 회원정보 조회 API
     *
     * @param user_id
     * @return List 아이디, 닉네임, 전화번호, 이메일, 이름
     * @throws BaseException
     * @author yewon
     */
    @Transactional(readOnly = true)
    public List<GetUserRes> getUser(String user_id) throws BaseException {
        try {
            List<GetUserRes> getUserRes = userDao.getUser(user_id);
            return getUserRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 소개 페이지 내용 조회
     *
     * @param userId
     * @return
     * @throws BaseException
     * @author yunhee, yewon
     */
    @Transactional(readOnly = true)
    public GetProfileRes getProfile(String userId) throws BaseException {
        try {
            GetProfileRes getProfileRes = new GetProfileRes();

            User user = userDao.getUserProfileInfo(userId); // 닉네임, 평가점수, 프로필, 사진
            getProfileRes.setUser_nickname(user.getUser_nickname());
            getProfileRes.setUser_grade(user.getUser_grade());
            getProfileRes.setUser_prProfile(user.getUser_prProfile());
            getProfileRes.setUser_prPhoto(user.getUser_prPhoto());

            List<String> ability = userDao.getUserPrAbility(userId);    // 능력
            List<String> keyword = userDao.getUserPrKeyword(userId);    // 키워드
            List<String> link = userDao.getUserLink(userId);    // 프로필 링크

            // 능력, 키워드, 링크는 필수로 들어와야하는 값
            if (!ability.isEmpty())
                getProfileRes.setUser_prAbility(ability);
            if (!keyword.isEmpty())
                getProfileRes.setUser_prKeyword(keyword);
            if (!link.isEmpty())
                getProfileRes.setUser_prLink(link);

            // 프로젝트 리스트
            List<String> project = userDao.getUserProject(userId);
            getProfileRes.setPj_name(project);


            return getProfileRes;
        } catch (IncorrectResultSizeDataAccessException error) {
            throw new BaseException(NOT_EXISTS_USER_ID);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 프로필 사진 가져오기
     * @param user_nickname
     * @return String
     * @author shinhyeon
     */
    public String getPrPhoto(String user_nickname) {
        String user_prPhoto = null;
        user_prPhoto = userDao.getPrphoto(user_nickname);
        if(user_prPhoto == null) user_prPhoto = "https://infra-infra-bucket.s3.ap-northeast-2.amazonaws.com/prphoto/infra_profile.png";
        return user_prPhoto;
    }
}