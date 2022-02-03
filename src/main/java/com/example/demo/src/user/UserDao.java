package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 회원가입
     *
     * @param postUserReq - id, pw, nickname, phone, email, name
     * @author yunhee
     */
    public void createUser(PostUserReq postUserReq) {
        String createUserQuery = "insert into User (user_id, user_pw, user_nickname, user_grade, user_phone, " +
                "user_email, user_name) VALUES (?,?,?,0,?,?,?)"; // 실행될 동적 쿼리문
        Object[] createUserParams = new Object[]{postUserReq.getUser_id(), postUserReq.getUser_pw(), postUserReq.getUser_nickname(), postUserReq.getUser_phone(),
                postUserReq.getUser_email(), postUserReq.getUser_name()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);
    }

    /**
     * id 증복 체크
     *
     * @param id
     * @return int - 이미 존재하면 1, 없으면 0
     * @author yunhee
     */
    public int checkId(String id) {
        String checkIdQuery = "select exists(select user_id from User where user_id = ? and user_status REGEXP 'ACTIVE|STOP')";
        return this.jdbcTemplate.queryForObject(checkIdQuery, int.class, id);
    }

    /**
     * nickname 중복 체크
     *
     * @param nickname
     * @return int - 이미 존재하면 1, 없으면 0
     * @author yunhee
     */
    public int checkNickname(String nickname) {
        String checkNicknameQuery = "select exists(select user_nickname from User where user_nickname = ? and user_status REGEXP 'ACTIVE|STOP')";
        return this.jdbcTemplate.queryForObject(checkNicknameQuery, int.class, nickname);
    }

    /**
     * email 중복 체크
     *
     * @param email
     * @return int - 이미 존재하면 1, 없으면 0
     * @author yunhee
     */
    public int checkEmail(String email) {
        String checkEmailQuery = "select exists(select user_email from User where user_email = ? and user_status REGEXP 'ACTIVE|STOP')";
        return this.jdbcTemplate.queryForObject(checkEmailQuery, int.class, email);
    }

    /**
     * 핸드폰 번호 중복 체크
     *
     * @param phone
     * @return int - 이미 존재하면 1, 없으면 0
     * @author yunhee
     */
    public int checkPhone(String phone) {
        String checkPhoneQuery = "select exists(select user_phone from User where user_phone = ? and user_status REGEXP 'ACTIVE|STOP')";
        return this.jdbcTemplate.queryForObject(checkPhoneQuery, int.class, phone);
    }

    /**
     * 가입 가능여부 체크
     *
     * @param phone
     * @return String - 가입 가능시 : POSSIBLE, 이미 회원 : ALREADY_USER, 강제 탈퇴된 회원 : OUT, 탈퇴한 회원 : OUT
     * @author yunhee
     */
    public String checkPossibleSignUp(String phone) {
        String checkPossibleQuery = "SELECT CASE WHEN (count(*) = 0) THEN 'POSSIBLE' " +
                "WHEN ((u.user_status = 'OUT' and TIMESTAMPDIFF(MONTH, u.user_leaveTime, now()) < 3) " +
                "or (u.user_status = 'DEL' and TIMESTAMPDIFF(WEEK, u.user_leaveTime, now()) < 1)) THEN u.user_status " +
                "WHEN (u.user_status REGEXP 'ACTIVE|STOP') then 'ALREADY_USER' ELSE 'POSSIBLE' END as result " +
                "FROM (SELECT user_status, user_leaveTime FROM User WHERE user_phone = ?" +
                "ORDER BY user_registerTime DESC limit 1) u";
        return this.jdbcTemplate.queryForObject(checkPossibleQuery, String.class, phone);
    }

    /**
     * 비밀번호 변경
     *
     * @param patchUserReq
     * @return int - 변경된 행의 수
     * @author yunhee
     */
    public int modifyUserPw(PatchUserReq patchUserReq) {
        String modifyUserNameQuery = "update User set user_pw = ?, user_modifyPwTime = now() where user_id = ? and user_status REGEXP 'ACTIVE|STOP'";
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getUser_pw(), patchUserReq.getUser_id()};
        return this.jdbcTemplate.update(modifyUserNameQuery, modifyUserNameParams);
    }


    /**
     * 로그인
     *
     * @param postLoginReq - id
     * @return User - id, pw
     * @author yunhee
     */
    public User getPwd(PostLoginReq postLoginReq) {
        String getPwdQuery = "select user_id, user_pw from User where user_id = ?";
        String getPwdParams = postLoginReq.getUser_id();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs, rowNum) -> User.builder().user_id(rs.getString("user_id")).
                        user_pw(rs.getString("user_pw")).build(), getPwdParams);
    }

    /**
     * 회원정보 조회 API
     *
     * @param user_id
     * @return List 아이디,닉네임
     * @author yewon
     */
    public List<GetUserRes> getUser(String user_id) {
        String getUserQuery = "select user_id, user_phone, user_email" +
                " from User where user_id = ?";
        String getUserParams = user_id;
        return this.jdbcTemplate.query(getUserQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getString("user_id"),
                        //rs.getString("user_pw"),
                        rs.getString("user_phone"),
                        rs.getString("user_email")),

                getUserParams);
    }

    /**
     * 회원탈퇴 API
     *
     * @param user_id
     * @author yewon
     */
    public void delUser(String user_id) {
        String delUserQuery = "update User set user_status = 'DEL', user_leaveTime = now() where user_id = ?";
        String delUserParams = user_id;
        this.jdbcTemplate.update(delUserQuery, delUserParams);
    }

    /**
     * phone에 해당하는 email 정보 가져오기
     *
     * @param phone
     * @return String - email
     * @author yunhee
     */
    public User getEmailFromPhone(String phone) {
        String getEmailQuery = "select user_id, user_email from User where user_phone=? and user_status REGEXP 'ACTIVE|STOP'";
        return this.jdbcTemplate.queryForObject(getEmailQuery,
                (rs, rowNum) -> User.builder().user_id(rs.getString("user_id")).
                        user_email(rs.getString("user_email")).build(), phone);
    }

    /**
     * 프로필 링크 가져오기
     *
     * @param userId
     * @return List - 프로필 링크
     * @author yunhee
     */
    public List<String> getUserLink(String userId) {
        String getUserLinkQuery = "select user_prLink from User_link where user_id = ?";
        return this.jdbcTemplate.queryForList(getUserLinkQuery, String.class, userId);
    }

    /**
     * 능력(user_prAbility) 가져오기
     *
     * @param userId
     * @return List - 능력
     * @author yunhee
     */
    public List<String> getUserPrAbility(String userId) {
        String getUserPrAbilityQuery = "select user_prAbility from User_ability where user_id = ?";
        return this.jdbcTemplate.queryForList(getUserPrAbilityQuery, String.class, userId);
    }

    /**
     * 키워드(user_prKeyword) 가져오기
     *
     * @param userId
     * @return List
     * @author yunhee
     */
    public List<String> getUserPrKeyword(String userId) {
        String getUserPrKeywordQuery = "select user_prKeyword from User_keyword where user_id = ?";
        return this.jdbcTemplate.queryForList(getUserPrKeywordQuery, String.class, userId);
    }

    /**
     * 소개 페이지 관련 user 테이블에서 정보 가져오기
     *
     * @param userId
     * @return User - user_nickname, user_prPhoto, user_prProfile
     * @author yunhee
     */
    public User getUserProfileInfo(String userId) {
        String getUserProfileInfoQuery = "select user_nickname, user_prPhoto, user_prProfile from User where user_id = ?";
        return this.jdbcTemplate.queryForObject(getUserProfileInfoQuery,
                (rs, rowNum) -> User.builder().user_nickname(rs.getString("user_nickname")).
                        user_prPhoto(rs.getString("user_prPhoto")).
                        user_prProfile(rs.getString("user_prProfile")).build(), userId);
    }
}