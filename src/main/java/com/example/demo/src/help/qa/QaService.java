package com.example.demo.src.help.qa;

import com.example.demo.config.BaseException;
import com.example.demo.src.help.qa.model.PatchQaReq;
import com.example.demo.src.help.qa.model.PostQaReq;
import com.example.demo.src.help.qa.model.PostQaRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

@Service

public class QaService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final QaDao qaDao;
    private final QaProvider qaProvider;

    @Autowired //readme 참고
    public QaService(QaDao qaDao, QaProvider qaProvider) {
        this.qaDao = qaDao;
        this.qaProvider = qaProvider;
    }

    // 해당 QA_num을 갖는 질문 삭제
    public void deleteQa(int QA_num) throws BaseException{
        try {
            int result = qaDao.deleteQa(QA_num); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)입니다.
            if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                throw new BaseException(DELETE_FAIL_QA);
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 해당 QA_num을 갖는 질문 수정
    public void modifyQa(PatchQaReq patchQaReq) throws BaseException{
        try {
            int result = qaDao.modifyQa(patchQaReq);
            if (result == 0) {
                throw new BaseException(MODIFY_FAIL_QA);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // QA 등록
    @Transactional
    public void uploadQa(PostQaReq postQaReq) throws BaseException{
        try {
            qaDao.uploadQa(postQaReq);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
