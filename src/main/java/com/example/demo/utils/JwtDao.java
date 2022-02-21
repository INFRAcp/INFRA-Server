package com.example.demo.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class JwtDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public String getRefreshToken(String refreshTokenIdx) {
        System.out.println("리프레시 토큰 가져올게요");
        String getRefreshTokenQuery = "SELECT refreshToken FROM User_refreshToken WHERE idx = ?";
        return this.jdbcTemplate.queryForObject(getRefreshTokenQuery, String.class, refreshTokenIdx);
    }
}
