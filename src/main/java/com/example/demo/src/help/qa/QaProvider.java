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

    /**
     * 모든 질문 조회
     *
     * @param
     * @return List<질문 번호, 아이디, 질문, 답변, 질문한 시간, 답변한 시간, status>
     * @throws BaseException
     * @author shinhyeon
     */
    public List<GetQaRes> getQa() throws BaseException{
        try{
            List<GetQaRes> getQaRes = qaDao.getQaRes();
            return getQaRes;
        }
        catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }

    /**
     * 특정 유저 질문 조회
     *
     * @param user_id
     * @return List<질문 번호, 아이디, 질문, 답변, 질문한 시간, 답변한 시간, status>
     * @throws BaseException
     * @author shinhyeon
     */
    public List<GetQaRes> getQaByUser_id(String user_id) throws BaseException {
        try{
            List<GetQaRes> getQaRes = qaDao.getQaByUserId(user_id);
            return getQaRes;
        }
        catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }

    /**
     * 특정 질문 조회
     *
     * @param  qa_num
     * @return 질문 번호, 아이디, 질문, 답변, 질문한 시간, 답변한 시간, status
     * @throws BaseException
     * @author shinhyeon
     */
    public GetQaRes getQaByQaNum(int qa_num) throws BaseException {
        try{
            GetQaRes getQaRes = qaDao.getQaByQaNum(qa_num);
            return getQaRes;
        }
        catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }
}
