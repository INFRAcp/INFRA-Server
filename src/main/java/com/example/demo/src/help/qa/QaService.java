package com.example.demo.src.help.qa;

import com.example.demo.config.BaseException;
import com.example.demo.src.help.qa.model.PatchAnswerReq;
import com.example.demo.src.help.qa.model.PatchQaReq;
import com.example.demo.src.help.qa.model.PostQaReq;
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

    @Autowired
    public QaService(QaDao qaDao, QaProvider qaProvider) {
        this.qaDao = qaDao;
        this.qaProvider = qaProvider;
    }

    /**
     * 해당 qa_num을 갖는 질문 수정
     *
     * @param qa_num
     * @param patchQaReq  - qa_q
     * @return
     * @throws BaseException
     * @author shinhyeon, yunhee
     */
    @Transactional
    public void modifyQa(String userIdByJwt, int qa_num, PatchQaReq patchQaReq) throws BaseException {
        String userId = qaProvider.getUserIdByQaNum(qa_num);
        if (!userIdByJwt.equals(userId))
            throw new BaseException(INVALID_USER_JWT);
        try {
            int result = qaDao.modifyQa(qa_num, patchQaReq);
            if (result == 0) {
                throw new BaseException(MODIFY_FAIL_QA);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * qa 등록
     *
     * @param postQaReq - (user_id, qa_q)
     * @return
     * @throws BaseException
     * @author shinhyeon
     */
    @Transactional
    public void createQa(PostQaReq postQaReq) throws BaseException {
        try {
            if (qaDao.createQa(postQaReq) == 0) {
                throw new BaseException(FAIL_TO_CREATE_QA);
            }
        } catch (Exception exception) {
            if (exception instanceof BaseException)
                throw new BaseException(((BaseException) exception).getStatus());
            else
                throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 질문 삭제
     *
     * @param qa_num
     * @return
     * @throws BaseException
     * @author shinhyeon
     */
    public void deleteQa(int qa_num) throws BaseException {
        try {
            int result = qaDao.deleteQa(qa_num);
            if (result == 0) {
                throw new BaseException(DELETE_FAIL_QA);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 해당 qa_num을 갖는 질문 답변
     *
     * @param patchAnswerReq
     * @return
     * @throws BaseException
     * @author shinhyeon
     */
    public void answerQa(int qa_num, PatchAnswerReq patchAnswerReq) throws BaseException {
        try {
            int result = qaDao.answerQa(qa_num, patchAnswerReq);
            if (result == 0) {
                throw new BaseException(MODIFY_FAIL_ANSWER);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
