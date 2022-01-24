package com.example.demo.src.user;


import com.example.demo.src.user.model.PatchUserReq;
import com.example.demo.src.user.model.PostLoginReq;
import com.example.demo.src.user.model.PostUserReq;
import com.example.demo.src.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class UserDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 회원가입
    public void createUser(PostUserReq postUserReq) {
        String createUserQuery = "insert into User (user_id, user_pw, user_nickname, user_grade, user_phone, " +
                "user_email, user_name)" +
                " VALUES (?,?,?,0,?,?,?)"; // 실행될 동적 쿼리문
        Object[] createUserParams = new Object[]{postUserReq.getUser_id(), postUserReq.getUser_pw(), postUserReq.getUser_nickname(), postUserReq.getUser_phone(),
                postUserReq.getUser_email(), postUserReq.getUser_name()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);
    }

    // id 확인
    public int checkId(String id) {
        String checkIdQuery = "select exists(select user_id from User where user_id = ?)";
        return this.jdbcTemplate.queryForObject(checkIdQuery, int.class, id);
    }

    // 이메일 확인
    public int checkEmail(String email) {
        String checkEmailQuery = "select exists(select user_email from User where user_email = ?)";
        return this.jdbcTemplate.queryForObject(checkEmailQuery, int.class, email);
    }

    // 핸드폰 번호 확인
    public int checkPhone(String phone) {
        String checkPhoneQuery = "select exists(select user_phone from User where user_phone = ?)";
        return this.jdbcTemplate.queryForObject(checkPhoneQuery, int.class, phone);
    }

    // 비밀번호 변경
    public int modifyUserPw(PatchUserReq patchUserReq) {
        String modifyUserNameQuery = "update User set user_pw = ? where user_id = ? ";
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getUser_pw(), patchUserReq.getUser_id()};
        return this.jdbcTemplate.update(modifyUserNameQuery, modifyUserNameParams);
    }


    // 로그인
    public User getPwd(PostLoginReq postLoginReq) {
        String getPwdQuery = "select user_id, user_pw from User where user_id = ?";
        String getPwdParams = postLoginReq.getUser_id();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs, rowNum) -> User.builder().user_id(rs.getString("user_id")).
                        user_pw(rs.getString("user_pw")).build(), getPwdParams);
    }
}