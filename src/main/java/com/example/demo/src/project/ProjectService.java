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
        // 평가 점수 범위 validation
        EvalScoreCheck(postEvalReq.getResponsibility(), postEvalReq.getAbility(), postEvalReq.getTeamwork(), postEvalReq.getLeadership());
        // 프로젝트 참여 인원 validation
        EvalMemberCheck(postEvalReq.getUser_id(), postEvalReq.getPassiveUser_id(), postEvalReq.getPj_num());

        try {
            projectDao.uploadEval(postEvalReq);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 평가 점수 범위 validation 함수
     *
     * @param responsibility
     * @param ability
     * @param teamwork
     * @param leadership
     * @throws BaseException
     * @author shinhyeon
     */
    private void EvalScoreCheck(float responsibility, float ability, float teamwork, float leadership) throws BaseException {
        if (responsibility < 0 || responsibility > 5) {
            throw new BaseException(POST_PROJECT_EVALUATE_SCORE);
        }
        if (ability < 0 || ability > 5) {
            throw new BaseException(POST_PROJECT_EVALUATE_SCORE);
        }
        if (teamwork < 0 || teamwork > 5) {
            throw new BaseException(POST_PROJECT_EVALUATE_SCORE);
        }
        if (leadership < 0 || leadership > 5) {
            throw new BaseException(POST_PROJECT_EVALUATE_SCORE);
        }
    }

    /**
     * 프로젝트 참여 인원 validation 함수
     *
     * @param user_id
     * @param passiveUser_id
     * @param pj_num
     * @throws BaseException
     * @author shinhyeon
     */
    private void EvalMemberCheck(String user_id, String passiveUser_id, Integer pj_num) throws BaseException {
        String pj_inviteStatus_user = projectProvider.getPjInviteStatus1(user_id, pj_num);
        String pj_inviteStatus_passiveUser = projectProvider.getPjInviteStatus2(passiveUser_id, pj_num);

        if (!pj_inviteStatus_user.equals("승인완료")) {
            throw new BaseException(PROJECT_EVALUATE_AUTHORITY);
        }
        if (!pj_inviteStatus_passiveUser.equals("승인완료")) {
            throw new BaseException(PROJECT_MEMBER);
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
        // 평가 점수 범위 validation
        EvalScoreCheck(patchEvalReq.getResponsibility(), patchEvalReq.getAbility(), patchEvalReq.getTeamwork(), patchEvalReq.getLeadership());
        // 프로젝트 참여 인원 validation
        EvalMemberCheck(patchEvalReq.getUser_id(), patchEvalReq.getPassiveUser_id(), patchEvalReq.getPj_num());
        // 팀원평가 존재 유무 validation
        EvalCheck(patchEvalReq.getUser_id(), patchEvalReq.getPassiveUser_id(), patchEvalReq.getPj_num());

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
        // 프로젝트 참여 인원 validation
        EvalMemberCheck(patchEvalDelReq.getUser_id(), patchEvalDelReq.getPassiveUser_id(), patchEvalDelReq.getPj_num());
        // 팀원평가 존재 유무 validation
        EvalCheck(patchEvalDelReq.getUser_id(), patchEvalDelReq.getPassiveUser_id(), patchEvalDelReq.getPj_num());

        try {
            projectDao.delEval(patchEvalDelReq);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 팀원평가 존재 유무 validation 함수
     *
     * @param user_id
     * @param passiveUser_id
     * @param pj_num
     * @throws BaseException
     * @author shinhyeon
     */
    private void EvalCheck(String user_id, String passiveUser_id, Integer pj_num) throws BaseException {
        Integer evalCheck = projectProvider.getEvalCheck(user_id, passiveUser_id, pj_num);

        if (evalCheck != 1) throw new BaseException(PROJECT_EVALUATE);
    }
}
