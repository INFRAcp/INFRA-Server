package com.example.demo.src.project;

import com.example.demo.config.BaseException;
import com.example.demo.src.help.qa.model.PostQaReq;
import com.example.demo.src.project.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

import javax.transaction.Transactional;
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

    //프로젝트 등록
    public PostPjRegisterRes registrationPj(PostPjRegisterReq postPjRegisterReq) throws BaseException {
        try {
            String pjRegisterSucese = projectDao.pjRegistration(postPjRegisterReq);
            return new PostPjRegisterRes(pjRegisterSucese);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //프로젝트 수정
    public PatchPjModifyRes pjModify(PatchPjModifyReq patchPjModifyReq) throws BaseException {
        try {
            String PjModify = projectDao.pjModify(patchPjModifyReq);
            return new PatchPjModifyRes(PjModify);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //프로젝트 삭제
    public DelPjDelRes pjDel(DelPjDelReq getPjDelReq) throws BaseException {
        try {
            String pjDel = projectDao.pjDel(getPjDelReq);
            return new DelPjDelRes(pjDel);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //프로젝트 지원
    public PostPjApplyRes pjApply(PostPjApplyReq postPjApplyReq) throws BaseException {
        try {
            String pjApplyName = projectDao.pjApply(postPjApplyReq);
            return new PostPjApplyRes(pjApplyName);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //프로젝트 신청한 유저 승인
    public PatchPjApproveRes pjApprove(PatchPjApproveReq patchPjApproveReq) throws BaseException {
        try {
            String PjApprove = projectDao.pjApprove(patchPjApproveReq);
            return new PatchPjApproveRes(PjApprove);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 팀원 평가 등록
     *
     * @param PostEvalReq
     * @return x
     * @throws BaseException
     * @author shinhyeon
     */
    public void uploadEval(PostEvalReq postEvalReq) throws BaseException {
        try {
            projectDao.uploadEval(postEvalReq);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 팀원 평가 수정
     *
     * @param PatchEvalReq
     * @return x
     * @throws BaseException
     * @author shinhyeon
     */
    public void modifyEval(PatchEvalReq patchEvalReq) throws BaseException {
        try {
            projectDao.modifyEval(patchEvalReq);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 팀원 평가 삭제
     *
     * @param PatchEvalDelReq
     * @return x
     * @throws BaseException
     * @author shinhyeon
     */
    public void delEval(PatchEvalDelReq patchEvalDelReq) throws BaseException {
        try {
            projectDao.delEval(patchEvalDelReq);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
