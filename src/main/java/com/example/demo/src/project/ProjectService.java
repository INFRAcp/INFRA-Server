package com.example.demo.src.project;

import com.example.demo.config.BaseException;
import com.example.demo.src.project.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ProjectService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProjectDao projectDao;
    private final ProjectProvider projectProvider;
    private final JwtService jwtService;

    @Autowired
    public ProjectService(ProjectDao projectDao, ProjectProvider projectProvider, JwtService jwtService){
        this.projectDao = projectDao;
        this.projectProvider = projectProvider;
        this.jwtService = jwtService;
    }

    /**
     * 프로젝트 등록
     * @param postPjRegisterReq
     * @return PostPjRegisterRes 프로젝트 이름
     * @author 한규범
     */
    public PostPjRegisterRes registrationPj(PostPjRegisterReq postPjRegisterReq) throws BaseException{
        try{
            String pjRegisterSucese = projectDao.pjRegistration(postPjRegisterReq);
            return new PostPjRegisterRes(pjRegisterSucese);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     *프로젝트 수정
     * @param patchPjModifyReq
     * @return PatchPjModifyRes 프로젝트 이름
     * @author 한규범
     */
    public PatchPjModifyRes pjModify(PatchPjModifyReq patchPjModifyReq)throws BaseException{
        try {
            String PjModify = projectDao.pjModify(patchPjModifyReq);
            return new PatchPjModifyRes(PjModify);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 프로젝트 삭제
     * @param delPjDelReq
     * @return DelPjDelRes 결과 메시지
     * @author 한규범
     */
    public DelPjDelRes pjDel(DelPjDelReq delPjDelReq) throws BaseException{
        try {
            String pjDel = projectDao.pjDel(delPjDelReq);
            return new DelPjDelRes(pjDel);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 프로젝트 지원
     * @param postPjApplyReq
     * @return PostPjApplyRes 완료 메시지
     * @author 한규범
     */
    public PostPjApplyRes pjApply(PostPjApplyReq postPjApplyReq) throws BaseException{
        try {
            String pjApplyName = projectDao.pjApply(postPjApplyReq);
            return new PostPjApplyRes(pjApplyName);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 프로젝트신청한 유저 승인
     * @param patchPjApproveReq
     * @return PatchPjApproveRes 완료 메시지
     * @author 윤성식
     */
    public PatchPjApproveRes pjApprove(PatchPjApproveReq patchPjApproveReq) throws BaseException{
        try{
            String PjApprove = projectDao.pjApprove(patchPjApproveReq);
            return new PatchPjApproveRes(PjApprove);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
