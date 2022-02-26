package com.example.demo.src.user.oauth.google;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Random;

@Repository
public class GoogleDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 구글 로그인 사용자 정보 DB 저장 API
     * @param email
     * @author 예원, 성식
     */
    public void insertInfo(String email) {
        /* 이미 회원가입 된 사용자일 경우 함수를 실행하지 않고 나가기(로그인 시) - ACTIVE인 회원들 중 조회 가능 */
        String loginQuery = "select COUNT(*) from User where user_email = ? and user_status = 'ACTIVE'";
        if (this.jdbcTemplate.queryForObject(loginQuery, int.class, email) >= 1) {
            System.out.println("이미 회원가입이 완료된 사용자입니다");
            return;
        }
        // 아이디, 닉네임 등 난수 발생을 위한 random 클래스 생성
        Random random = new Random();
        String randomStr = String.valueOf(random.nextInt(1000));

        /* 이메일 주소(골뱅이 앞부분)를 nickname으로 넣어주기(처음 가입시 임의로 insert) */
        String[] emailArr = StringUtils.split(email, '@');
        String nickname = emailArr[0];
        /* 아이디를 랜덤한 문자열로 저장해주기(처음 가입 및 탈퇴 후 재가입시 임의로 insert) */
        String userId = emailArr[0] + randomStr;      // 0~99까지의 랜덤 숫자를 발생시켜 난수를 뒤에다가 덧붙여서 저장해주기

        /* 해당하는 닉네임이 DB에 존재하는지 안하는지 체크 - ACTIVE인 회원들 중 조회 가능 */
        // 1. 존재하지 않을 경우
        String inexistQuery = "select COUNT(*) from User where user_nickname = ? and user_status = 'ACTIVE'";
        if (this.jdbcTemplate.queryForObject(inexistQuery, int.class, nickname) == 0) {
            // 만약 COUNT가 0이라면(=중복된 닉네임이 없다면) 위에서 split 해준 값을 아이디, 닉네임에 모두 넣어주기
            String insertQuery = "insert into User (user_id, user_nickname, user_email, user_type) values (?, ?, ?, 'google')";
            this.jdbcTemplate.update(insertQuery, userId, nickname, email);
        }
        // 2. 존재할 경우
        else {
            // 0~99까지의 랜덤 숫자를 발생시켜 난수를 뒤에다가 덧붙여서 저장해주기
            nickname += randomStr;
            String existQuery = "insert into User (user_id, user_nickname, user_email, user_type) values (?, ?, ?, 'google')";
            this.jdbcTemplate.update(existQuery, userId, nickname, email);
        }
    }
}
