package com.example.demo.src.project;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.project.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
            userIdJwt(postPjRegisterReq.getUser_id(), jwtService.getUserId());
            PjDateCheck(postPjRegisterReq.getPj_deadline(), postPjRegisterReq.getPj_startTerm(), postPjRegisterReq.getPj_endTerm());
            PjNullCheck(postPjRegisterReq.getPj_header(), postPjRegisterReq.getPj_categoryName(), postPjRegisterReq.getPj_content(), postPjRegisterReq.getPj_name(), postPjRegisterReq.getPj_subCategoryName(), postPjRegisterReq.getPj_progress(), postPjRegisterReq.getPj_endTerm(), postPjRegisterReq.getPj_startTerm(), postPjRegisterReq.getPj_deadline(), postPjRegisterReq.getPj_totalPerson());
            PjKeywordCheck(postPjRegisterReq.getHashtag());
            postPjRegisterReq.setPj_categoryNum(projectProvider.getPjCategoryNum(postPjRegisterReq.getPj_categoryName()));
            postPjRegisterReq.setPj_subCategoryNum(projectProvider.getPjSubCategoryNum(postPjRegisterReq.getPj_subCategoryName()));
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
            userIdJwt(patchPjModifyReq.getUser_id(), jwtService.getUserId());
            PjDateCheck(patchPjModifyReq.getPj_deadline(), patchPjModifyReq.getPj_startTerm(), patchPjModifyReq.getPj_endTerm());
            PjNullCheck(patchPjModifyReq.getPj_header(), patchPjModifyReq.getPj_categoryNum(), patchPjModifyReq.getPj_content(), patchPjModifyReq.getPj_name(), patchPjModifyReq.getPj_subCategoryNum(), patchPjModifyReq.getPj_progress(), patchPjModifyReq.getPj_endTerm(), patchPjModifyReq.getPj_startTerm(), patchPjModifyReq.getPj_deadline(), patchPjModifyReq.getPj_totalPerson());
            PjKeywordCheck(patchPjModifyReq.getHashtag());

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
            userIdJwt(delPjDelReq.getUser_id(), jwtService.getUserId());
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
            userIdJwt(postPjApplyReq.getUser_id(), jwtService.getUserId());
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
            userIdJwt(patchPjApproveReq.getUser_id(), jwtService.getUserId());
            String PjApprove = projectDao.pjApprove(patchPjApproveReq);
            return new PatchPjApproveRes(PjApprove);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 프로젝트 찜 등록
     * @param postLikeRegisterReq
     * @return 등록 완료된 메세지
     * @author 윤성식
     */
    public PostLikeRegisterRes likeRegister(PostLikeRegisterReq postLikeRegisterReq) throws BaseException {
        try {
            userIdJwt(postLikeRegisterReq.getUser_id(), jwtService.getUserId());
            String postLikeRegisterRes = projectDao.likeRegister(postLikeRegisterReq);
            return new PostLikeRegisterRes(postLikeRegisterRes);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 프로젝트 찜 삭제
     * @param postLikeRegisterReq
     * @return 찜 삭제된 메세지
     * @author 윤성식
     */
    public PostLikeRegisterRes likeDel(PostLikeRegisterReq postLikeRegisterReq) throws BaseException {
        try {
            userIdJwt(postLikeRegisterReq.getUser_id(), jwtService.getUserId());

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
    public void PjDateCheck(String pj_deadline, String pj_startTerm, String pj_endTerm) throws BaseException {
        LocalDate pj_deadlineLd = LocalDate.parse(pj_deadline, DateTimeFormatter.ISO_DATE);
        LocalDate pj_startTermLd = LocalDate.parse(pj_startTerm, DateTimeFormatter.ISO_DATE);
        LocalDate pj_endTermLd = LocalDate.parse(pj_endTerm, DateTimeFormatter.ISO_DATE);

        if (pj_deadlineLd.isBefore(pj_startTermLd)) {
            throw new BaseException(POST_PROJECT_DEADLINE_BEFORE_START);
        }
        if (pj_endTermLd.isBefore(pj_startTermLd)) {
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
    public void PjNullCheck(String pj_header, String pj_field, String pj_content, String pj_name, String pj_subField, String pj_progress, String pj_endTerm, String pj_startTerm, String pj_deadline, int pj_totalPerson) throws BaseException {
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
     * 키워드 값 확인 5글자, 6개 제한
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

    /**
     * 팀원 평가 등록
     *
     * @param postEvalReq
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
     * @param patchEvalReq
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
     * @param patchEvalDelReq
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

    /**
     * @param getProjectRes
     * @author 한규범
     */
    public void recruit(List<GetProjectRes> getProjectRes) {
        for (int i = 0; i < getProjectRes.size(); i++) {
            if (getProjectRes.get(i).getPj_daysub() <= 2 && getProjectRes.get(i).getPj_daysub() >= 0) {
                getProjectRes.get(i).setPj_recruit("마감임박");
            }
        }
    }

    /**
     * 유저 JWT 유효성 검사
     * @param userId
     * @param userIdByJwt
     * @return BaseResponse
     * @author 한규범, 강윤희
     */
    public void userIdJwt(String userId, String userIdByJwt) throws BaseException {
        if (userIdByJwt.equals("만료")) {
            throw new BaseException(EXPIRATION_REFRESH_JWT);
        }else if (userIdByJwt.equals("재발급")) {
            throw new BaseException(EXPIRATION_ACCESS_JWT);
        }else if(!userId.equals(userIdByJwt)){
            throw new BaseException(INVALID_USER_JWT);
            }
        }


    public void rejectCheck(PostPjApplyRes postPjApplyRes) throws BaseException {
        if(postPjApplyRes.getComment().equals("거절")){
            throw new BaseException(POST_PROJECT_REJECT_RESTART);
        }
    }

    public void coincideCheck(PostPjApplyRes postPjApplyRes) throws BaseException{
        if(postPjApplyRes.getComment().equals("중복")){
            throw new BaseException(POST_PROJECT_COINCIDE_CHECK);
        }
    }
}
