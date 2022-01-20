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

    // 회원가입
    public void createUser(PostUserReq postUserReq) {
        String createUserQuery = "insert into User (User_id, User_Pw, User_nickname, User_Grade, User_Phone, " +
                "User_email, User_name, User_pr_photo, User_pr_profile)" +
                " VALUES (?,?,?,0,?,?,?,?,?)"; // 실행될 동적 쿼리문
        Object[] createUserParams = new Object[]{postUserReq.getId(), postUserReq.getPw(), postUserReq.getNickname(), postUserReq.getPhone(),
                postUserReq.getEmail(), postUserReq.getName(), postUserReq.getPrPhoto(), postUserReq.getPrProfile()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);
    }

    // id 확인
    public int checkId(String id) {
        String checkIdQuery = "select exists(select User_id from User where User_id = ?)";
        return this.jdbcTemplate.queryForObject(checkIdQuery, int.class, id);
    }

    // 이메일 확인
    public int checkEmail(String email) {
        String checkEmailQuery = "select exists(select User_email from User where User_email = ?)";
        return this.jdbcTemplate.queryForObject(checkEmailQuery, int.class, email);
    }

    // 핸드폰 번호 확인
    public int checkPhone(String phone) {
        String checkPhoneQuery = "select exists(select User_Phone from User where User_phone = ?)";
        return this.jdbcTemplate.queryForObject(checkPhoneQuery, int.class, phone);
    }

    // 비밀번호 변경
    public int modifyUserPw(PatchUserReq patchUserReq) {
        String modifyUserNameQuery = "update User set user_pw = ? where user_Id = ? ";
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getPw(), patchUserReq.getId()};
        return this.jdbcTemplate.update(modifyUserNameQuery, modifyUserNameParams);
    }


    // 로그인
    public User getPwd(PostLoginReq postLoginReq) {
        String getPwdQuery = "select User_id, User_Pw from User where User_id = ?";
        String getPwdParams = postLoginReq.getId();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs, rowNum) -> User.builder().id(rs.getString("User_id")).
                        pw(rs.getString("User_Pw")).build(), getPwdParams);
    }


    // User 테이블에 존재하는 전체 유저들의 정보 조회
    public List<GetUserRes> getUsers() {
        String getUsersQuery = "select * from User"; //User 테이블에 존재하는 모든 회원들의 정보를 조회하는 쿼리
        return this.jdbcTemplate.query(getUsersQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("nickname"),
                        rs.getString("Email"),
                        rs.getString("password")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        ); // 복수개의 회원정보들을 얻기 위해 jdbcTemplate 함수(Query, 객체 매핑 정보)의 결과 반환(동적쿼리가 아니므로 Parmas부분이 없음)
    }

    // 해당 nickname을 갖는 유저들의 정보 조회
    public List<GetUserRes> getUsersByNickname(String nickname) {
        String getUsersByNicknameQuery = "select * from User where nickname =?"; // 해당 이메일을 만족하는 유저를 조회하는 쿼리문
        String getUsersByNicknameParams = nickname;
        return this.jdbcTemplate.query(getUsersByNicknameQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("nickname"),
                        rs.getString("Email"),
                        rs.getString("password")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getUsersByNicknameParams); // 해당 닉네임을 갖는 모든 User 정보를 얻기 위해 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    // 해당 userIdx를 갖는 유저조회
    public GetUserRes getUser(int userIdx) {
        String getUserQuery = "select * from User where userIdx = ?"; // 해당 userIdx를 만족하는 유저를 조회하는 쿼리문
        int getUserParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("nickname"),
                        rs.getString("Email"),
                        rs.getString("password")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getUserParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }
}
