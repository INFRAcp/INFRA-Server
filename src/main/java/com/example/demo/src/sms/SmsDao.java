package com.example.demo.src.sms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class SmsDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private void setJdbcTemplate(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public boolean phoneCheck(String recipientPhoneNumber) {
        String phoneCheckQuery = "Select count(*) from User where user_phone = ?";
        String recipientPhoneNumberHypen = recipientPhoneNumber.substring(0, 3) + "-" + recipientPhoneNumber.substring(3, 7) + "-" + recipientPhoneNumber.substring(7);
        if (this.jdbcTemplate.queryForObject(phoneCheckQuery,int.class,recipientPhoneNumberHypen) == 0){
            return true;
        }else{
            return false;
        }
    }
}
