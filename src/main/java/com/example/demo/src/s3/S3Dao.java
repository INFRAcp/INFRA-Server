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

    /**
     * 프로필 사진 업로드
     * @param imgPath
     * @param user_id
     * @author shinhyeon
     */
    public void uploadPrPhoto(String imgPath, String user_id) {
        String uploadPrPhotoQuery = "UPDATE User set user_prPhoto = ? where user_id = ?";
        Object[] uploadPrPhotoParam = new Object[]{imgPath, user_id};

        this.jdbcTemplate.update(uploadPrPhotoQuery, uploadPrPhotoParam);
    }

    /**
     * 프로필 사진 불러오기
     * @param user_id
     * @return String (이미지 경로)
     * @author shinhyeon
     */
    public String getPrphoto(String user_id) {
        String getPrphotoQuery = "SELECT user_prPhoto from User Where user_id = ?";

        return this.jdbcTemplate.queryForObject(getPrphotoQuery, new String[]{user_id}, String.class);
    }

    /**
     * 프로젝트 사진 업로드
     * @param imgPath
     * @param pj_num
     * @author shinhyeon
     */
    public void uploadPjPhoto(String imgPath, int pj_num) {
        String uploadPjPhotoQuery = "INSERT INTO Pj_photo (pj_num, pjPhoto) VALUES (?,?)";
        Object[] uploadPjPhotoParam = new Object[]{pj_num, imgPath};

        this.jdbcTemplate.update(uploadPjPhotoQuery, uploadPjPhotoParam);
    }
}
