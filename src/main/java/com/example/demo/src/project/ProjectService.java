package com.example.demo.src.project;

import com.example.demo.config.BaseException;
import com.example.demo.src.project.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ProjectService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProjectDao projectDao;
    private final ProjectProvider projectProvider;
    private final JwtService jwtService;

    @Autowired
    public ProjectService(ProjectDao projectDao, ProjectProvider projectProvider, JwtService jwtService) {
        this.projectDao = projectDao;
        this.projectProvider = projectProvider;
        this.jwtService = jwtService;
    }

    /**
     * 프로젝트 등록
     *
     * @param postPjRegisterReq
     * @return PostPjRegisterRes 프로젝트 이름
     * @author 한규범
     */
    public PostPjRegisterRes registrationPj(PostPjRegisterReq postPjRegisterReq) throws BaseException {
        try {
            String pjRegisterSucese = projectDao.pjRegistration(postPjRegisterReq);
            return new PostPjRegisterRes(pjRegisterSucese);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 프로젝트 수정
     *
     * @param patchPjModifyReq
     * @return PatchPjModifyRes 프로젝트 이름
     * @author 한규범
     */
    public PatchPjModifyRes pjModify(PatchPjModifyReq patchPjModifyReq) throws BaseException {
        try {
            String PjModify = projectDao.pjModify(patchPjModifyReq);
            return new PatchPjModifyRes(PjModify);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 프로젝트 삭제
     *
     * @param delPjDelReq
     * @return DelPjDelRes 결과 메시지
     * @author 한규범
     */
    public DelPjDelRes pjDel(DelPjDelReq delPjDelReq) throws BaseException {
        try {
            String pjDel = projectDao.pjDel(delPjDelReq);
            return new DelPjDelRes(pjDel);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 프로젝트 지원
     *
     * @param postPjApplyReq
     * @return PostPjApplyRes 완료 메시지
     * @author 한규범
     */
    public PostPjApplyRes pjApply(PostPjApplyReq postPjApplyReq) throws BaseException {
        try {
            String pjApplyName = projectDao.pjApply(postPjApplyReq);
            return new PostPjApplyRes(pjApplyName);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 프로젝트신청한 유저 승인
     *
     * @param patchPjApproveReq
     * @return PatchPjApproveRes 완료 메시지
     * @author 윤성식
     */
    public PatchPjApproveRes pjApprove(PatchPjApproveReq patchPjApproveReq) throws BaseException {
        try {
            String PjApprove = projectDao.pjApprove(patchPjApproveReq);
            return new PatchPjApproveRes(PjApprove);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //프로젝트 찜 등록
    public PostLikeRegisterRes likeRegister(PostLikeRegisterReq postLikeRegisterReq) throws BaseException {
        try {
            String postLikeRegisterRes = projectDao.likeRegister(postLikeRegisterReq);
            return new PostLikeRegisterRes(postLikeRegisterRes);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //프로젝트 찜 삭제
    public PostLikeRegisterRes likeDel(PostLikeRegisterReq postLikeRegisterReq) throws BaseException {
        try {
            String postLikeDelRes = projectDao.likeDel(postLikeRegisterReq);
            return new PostLikeRegisterRes(postLikeDelRes);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 프로젝트 게시 날짜 관련 오류
     *
     * @param pj_deadline
     * @param pj_startTerm
     * @param pj_endTerm
     * @throws BaseException
     * @author 한규범
     */
    public void PjDateCheck(LocalDate pj_deadline, LocalDate pj_startTerm, LocalDate pj_endTerm) throws BaseException {
        if (pj_deadline.isBefore(pj_startTerm)) {
            throw new BaseException(POST_PROJECT_DEADLINE_BEFORE_START);
        }
        if (pj_endTerm.isBefore(pj_startTerm)) {
            throw new BaseException(POST_PROJECT_END_BEFORE_START);
        }
    }

    /**
     * 프로젝트 null 값 확인
     *
     * @param pj_header
     * @param pj_field
     * @param pj_content
     * @param pj_name
     * @param pj_subField
     * @param pj_progress
     * @param pj_endTerm
     * @param pj_startTerm
     * @param pj_deadline
     * @param pj_totalPerson
     * @throws BaseException
     * @author 한규범
     */
    public void PjNullCheck(String pj_header, String pj_field, String pj_content, String pj_name, String pj_subField, String pj_progress, LocalDate pj_endTerm, LocalDate pj_startTerm, LocalDate pj_deadline, int pj_totalPerson) throws BaseException {
        if (pj_header == null) {
            throw new BaseException(POST_PROJECT_EMPTY_HEADER);
        }
        if (pj_field == null) {
            throw new BaseException(POST_PROJECT_EMPTY_FIELD);
        }
        if (pj_content == null) {
            throw new BaseException(POST_PROJECT_EMPTY_CONTENT);
        }
        if (pj_name == null) {
            throw new BaseException(POST_PROJECT_EMPTY_NAME);
        }
        if (pj_subField == null) {
            throw new BaseException(POST_PROJECT_EMPTY_SUBFIELD);
        }
        if (pj_progress == null) {
            throw new BaseException(POST_PROJECT_EMPTY_PROGRESS);
        }
        if (pj_endTerm == null) {
            throw new BaseException(POST_PROJECT_EMPTY_END_TERM);
        }
        if (pj_startTerm == null) {
            throw new BaseException(POST_PROJECT_EMPTY_START_TERM);
        }
        if (pj_deadline == null) {
            throw new BaseException(POST_PROJECT_EMPTY_DEADLINE);
        }
        if (pj_totalPerson == 0) {
            throw new BaseException(POST_PROJECT_EMPTY_TOTAL_PERSON);
        }
    }

    /**
     * 키워드 값 확인 프로젝트 5글자, 6개 제한
     *
     * @param hashtag
     * @throws BaseException
     * @author 한규범
     */
    public void PjKeywordCheck(String[] hashtag) throws BaseException {
        if (hashtag.length > 7) {
            throw new BaseException(POST_PROJECT_KEYWORD_CNT_EXCEED);
        }
        for (int j = 0; j < hashtag.length; j++) {
            if (hashtag[j].length() > 6) {
                throw new BaseException(POST_PROJECT_KEYWORD_EXCEED);
            }
        }
    }


}

