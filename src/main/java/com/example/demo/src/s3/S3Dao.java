package com.example.demo.src.s3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class S3Dao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void uploadPrPhoto(String imgPath, String user_id) {
        String uploadPrPhotoQuery = "UPDATE User set user_prPhoto = ? where user_id = ?";
        Object[] uploadPrPhotoParam = new Object[]{imgPath, user_id};

        this.jdbcTemplate.update(uploadPrPhotoQuery, uploadPrPhotoParam);
    }

    public String getPrphoto(String user_id) {
        String getPrphotoQuery = "SELECT user_prPhoto from User Where user_id = ?";

        return this.jdbcTemplate.queryForObject(getPrphotoQuery, new String[]{user_id}, String.class);
    }
}
