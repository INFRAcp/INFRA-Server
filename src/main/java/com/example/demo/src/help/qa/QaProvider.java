package com.example.demo.src.help.qa;

import com.example.demo.config.BaseException;
import com.example.demo.src.help.qa.model.GetQaRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

import java.util.List;

//import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service

public class QaProvider {

    @Autowired
    private final QaDao qaDao;

    final Logger logger = LoggerFactory.getLogger(this.getClass());


    public QaProvider(QaDao qaDao) {
        this.qaDao = qaDao;
    }

    // 모든 질문 조회
    public List<GetQaRes> getQa() throws BaseException{
        try{
            List<GetQaRes> getQaRes = qaDao.getQaRes();
            return getQaRes;
        }
        catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }

    // 특정 질문 조회 (해당 User_id 를 갖는)
    public List<GetQaRes> getQaByUser_id(String User_id) throws BaseException {
        try{
            List<GetQaRes> getQaRes = qaDao.getQaByUserId(User_id);
            return getQaRes;
        }
        catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }

    // 특정 질문 조회 (해당 qa_num 를 갖는)
    public List<GetQaRes> getQaByQaNum(int qa_num) throws BaseException {
        try{
            List<GetQaRes> getQaRes = qaDao.getQaByQaNum(qa_num);
            return getQaRes;
        }
        catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }
}
