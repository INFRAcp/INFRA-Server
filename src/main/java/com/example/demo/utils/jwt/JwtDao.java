package com.example.demo.utils.jwt;

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
        String getRefreshTokenQuery = "SELECT refreshToken FROM User_refreshToken WHERE idx = ?";
        return this.jdbcTemplate.queryForObject(getRefreshTokenQuery, String.class, refreshTokenIdx);
    }
}
