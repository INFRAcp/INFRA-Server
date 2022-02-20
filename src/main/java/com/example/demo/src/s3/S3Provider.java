package com.example.demo.src.s3;

import com.example.demo.config.BaseException;
import com.example.demo.src.help.qa.QaDao;
import com.example.demo.src.help.qa.model.GetQaRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.RESPONSE_ERROR;

@Service
public class S3Provider {

    private final S3Dao s3Dao;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public S3Provider(S3Dao s3Dao) {
        this.s3Dao = s3Dao;
    }

    /**
     * 이미지 불러오기
     * @param user_id
     * @return String (이미지 경로)
     * @author shinhyeon
     */
    public String getPrphoto(String user_id) throws BaseException {
        try {
            String imgPath = s3Dao.getPrphoto(user_id);
            return imgPath;
        } catch (Exception exception) {
            throw new BaseException(RESPONSE_ERROR);
        }
    }
}
