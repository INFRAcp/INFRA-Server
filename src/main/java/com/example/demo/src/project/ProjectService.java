package com.example.demo.src.project;

import com.example.demo.config.BaseException;
import com.example.demo.src.project.model.*;
import com.example.demo.utils.jwt.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ProjectService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProjectDao projectDao;
    private final ProjectProvider projectProvider;
    final JwtService jwtService;

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
        //날짜 관련 검사
        PjDateCheck(postPjRegisterReq.getPj_deadline(), postPjRegisterReq.getPj_startTerm(), postPjRegisterReq.getPj_endTerm());
        // NULL 값 검사
        PjNullCheck(postPjRegisterReq.getPj_header(), postPjRegisterReq.getPj_categoryName(), postPjRegisterReq.getPj_content(), postPjRegisterReq.getPj_subCategoryName(), postPjRegisterReq.getPj_progress(), postPjRegisterReq.getPj_endTerm(), postPjRegisterReq.getPj_startTerm(), postPjRegisterReq.getPj_deadline(), postPjRegisterReq.getPj_totalPerson());
        //해시태그 관련 검사
        PjHashTagCheck(postPjRegisterReq.getHashtag());
        //카테고리 번호, 이름
        postPjRegisterReq.setPj_categoryNum(projectProvider.getPjCategoryNum(postPjRegisterReq.getPj_categoryName()));
        //세부 카테고리 번호, 이름
        postPjRegisterReq.setPj_subCategoryNum(projectProvider.getPjSubCategoryNum(postPjRegisterReq.getPj_subCategoryName()));
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
        //날짜 관련 검사
        PjDateCheck(patchPjModifyReq.getPj_deadline(), patchPjModifyReq.getPj_startTerm(), patchPjModifyReq.getPj_endTerm());
        // NULL 값 검사
        PjNullCheck(patchPjModifyReq.getPj_header(), patchPjModifyReq.getPj_categoryNum(), patchPjModifyReq.getPj_content(), patchPjModifyReq.getPj_subCategoryNum(), patchPjModifyReq.getPj_progress(), patchPjModifyReq.getPj_endTerm(), patchPjModifyReq.getPj_startTerm(), patchPjModifyReq.getPj_deadline(), patchPjModifyReq.getPj_totalPerson());
        //해시태그 관련 검사
        PjHashTagCheck(patchPjModifyReq.getHashtag());

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
        jwtService.JwtEffectiveness(delPjDelReq.getUser_id(), jwtService.getUserId());
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
     * 프로젝트 신청한 유저 승인, 거절
     *
     * @param patchPjMemberReq
     * @return PatchPjApproveRes 완료 메시지
     * @author shinhyeon
     */
    public PatchPjMemberRes pjAcceptRequest(PatchPjMemberReq patchPjMemberReq, String userIdByJwt) throws BaseException {
        // jwt id 가 해당 프로젝트의 팀장인지 확인
        String teamLeader = projectProvider.getTeamLeader(patchPjMemberReq.getPj_num());
        if (!userIdByJwt.equals(teamLeader)) {
            throw new BaseException(PROJECT_APPROVE_AUTHORITY);
        }

        // 이미 승인한 유저인지, 거절한 유저인지 확인
        String pj_inviteStatus = projectProvider.getPjInviteStatus(patchPjMemberReq.getUser_id(), patchPjMemberReq.getPj_num());
        if (pj_inviteStatus.equals("승인완료")) throw new BaseException(PROJECT_INVITESTATUS_ALREADY);
        if (pj_inviteStatus.equals("거절")) throw new BaseException(PROJECT_INVITESTATUS_REJECT);

        // 참여 요청 관리
        try {
            String res = null;

            // 유저 승인
            if (patchPjMemberReq.getPj_inviteStatus().equals("승인완료")) {
                res = projectDao.pjApprove(patchPjMemberReq);
            }

            // 유저 거절
            if (patchPjMemberReq.getPj_inviteStatus().equals("거절")) {
                res = projectDao.pjReject(patchPjMemberReq);
            }

            return new PatchPjMemberRes(res);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 프로젝트 팀원 강퇴
     *
     * @param patchPjMemberReq
     * @return PatchPjApproveRes 완료 메시지
     * @author shinhyeon
     */
    public PatchPjMemberRes pjKickOut(PatchPjMemberReq patchPjMemberReq, String userIdByJwt) throws BaseException {
        // jwt id 가 해당 프로젝트의 팀장인지 확인
        String teamLeader = projectProvider.getTeamLeader(patchPjMemberReq.getPj_num());
        if (!userIdByJwt.equals(teamLeader)) {
            throw new BaseException(PROJECT_APPROVE_AUTHORITY);
        }

        // 팀원만 강퇴 가능
        String pj_inviteStatus = projectProvider.getPjInviteStatus1(patchPjMemberReq.getUser_id(), patchPjMemberReq.getPj_num());
        if (!pj_inviteStatus.equals("승인완료")) throw new BaseException(PROJECT_KICK_OUT);

        try {
//             String PjApprove = projectDao.pjApprove(patchPjApproveReq);
//             return new PatchPjApproveRes(PjApprove);

            String res = projectDao.pjKickOut(patchPjMemberReq);

            return new PatchPjMemberRes(res);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 프로젝트 스크랩 등록
     * @param postLikeRegisterReq
     * @return 등록 완료된 메세지
     * @author 윤성식
     */
    public PostLikeRegisterRes likeRegister(PostLikeRegisterReq postLikeRegisterReq) throws BaseException {
        try {
            String postLikeRegisterRes = projectDao.likeRegister(postLikeRegisterReq);
            return new PostLikeRegisterRes(postLikeRegisterRes);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 프로젝트 스크랩 삭제
     * @param postLikeRegisterReq
     * @return 찜 삭제된 메세지
     * @author 윤성식
     */
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
    public void PjDateCheck(String pj_deadline, String pj_startTerm, String pj_endTerm) throws BaseException {
        LocalDate pj_deadlineLd = LocalDate.parse(pj_deadline, DateTimeFormatter.ISO_DATE);
        LocalDate pj_startTermLd = LocalDate.parse(pj_startTerm, DateTimeFormatter.ISO_DATE);
        LocalDate pj_endTermLd = LocalDate.parse(pj_endTerm, DateTimeFormatter.ISO_DATE);

        if (pj_startTermLd.isBefore(pj_deadlineLd)) {
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
     * @param pj_subField
     * @param pj_progress
     * @param pj_endTerm
     * @param pj_startTerm
     * @param pj_deadline
     * @param pj_totalPerson
     * @throws BaseException
     * @author 한규범
     */
    public void PjNullCheck(String pj_header, String pj_field, String pj_content, String pj_subField, String pj_progress, String pj_endTerm, String pj_startTerm, String pj_deadline, int pj_totalPerson) throws BaseException {
        if (pj_header == null) {
            throw new BaseException(POST_PROJECT_EMPTY_HEADER);
        }
        if (pj_field == null) {
            throw new BaseException(POST_PROJECT_EMPTY_FIELD);
        }
        if (pj_content == null) {
            throw new BaseException(POST_PROJECT_EMPTY_CONTENT);
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
    public void PjHashTagCheck(String[] hashtag) throws BaseException {
        String hashtagExtraction;

        if (hashtag.length > 7) {
            throw new BaseException(POST_PROJECT_KEYWORD_CNT_EXCEED);
        }

        for (int i = 0; i < hashtag.length; i++) {
            if (hashtag[i].length() > 6) {
                throw new BaseException(POST_PROJECT_KEYWORD_EXCEED);
            }
        }

        for(int j = 0; j < hashtag.length; j++){
            hashtagExtraction = hashtag[j];
            hashtag[j]=null;
            if(Arrays.asList(hashtag).contains(hashtagExtraction)==false){
                hashtag[j]=hashtagExtraction;
            }else{
                throw new BaseException(POST_PROJECT_HASHTAG_DUPLICATION);
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
     * 유저 등급 등록(or 최신화)
     *
     * @param user_id, grade
     * @throws BaseException
     * @ahthor shinhyeon
     */
    public void uploadGrade(String user_id, float grade) throws BaseException {
        try {
            float current_grade, final_grade;
            current_grade = projectProvider.getGrade(user_id);

            if (current_grade != 0.0) { // 유저 등급 최신화
                final_grade = (float) ((current_grade + grade) / 2.0);
                final_grade = (float) (Math.round(final_grade * 10) / 10.0); // 소수점 아래 첫째자리까지만 사용

            } else { // 유저 등급 등록 (아직 등록된 평가가 없어 등급이 없을 경우)
                final_grade = (float) (Math.round(grade * 10) / 10.0); // 소수점 아래 첫째자리까지만 사용
            }

            projectDao.uploadGrade(user_id, final_grade);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
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

    public String recruit(int pj_daysub) {
            if (pj_daysub <=2 && pj_daysub >= 0 ){
                return "마감임박";
            }else if(pj_daysub <0){
                return "마감";
            }
        return "모집중";
    }

    /**
     *
     * @param postPjApplyRes
     * @throws BaseException
     * @author 윤성식
     */
    public void rejectCheck(PostPjApplyRes postPjApplyRes) throws BaseException {
        if(postPjApplyRes.getComment().equals("거절")){
            throw new BaseException(POST_PROJECT_REJECT_RESTART);
        }
    }

    /**
     *
     * @param postPjApplyRes
     * @throws BaseException
     * @author 윤성식
     */
    public void coincideCheck(PostPjApplyRes postPjApplyRes) throws BaseException{
        if(postPjApplyRes.getComment().equals("중복")){
            throw new BaseException(POST_PROJECT_COINCIDE_CHECK);
        }
    }

    /**
     * 프로젝트 하나 접속
     * @param pj_num
     * @param user_id
     * @return
     * @throws BaseException
     * @author 한규범
     */
    public GetContactRes pjContact(int pj_num, String user_id) throws BaseException{
        try {
            GetContactRes getContactRes = projectDao.pjContact(pj_num, user_id);

            getContactRes.setHashtag(projectDao.getHashtag(pj_num));
            //프로젝트 좋아요 유무
            getContactRes.setPj_like(projectProvider.checkPjLike(pj_num, user_id));
            //프로젝트 해시태그 불러오기
            getContactRes.setHashtag(projectProvider.getHashtag(pj_num));
            //프로젝트 모집중, 마감임박, 마감 표시
            getContactRes.setPj_recruit(recruit(getContactRes.getPj_daysub()));
            return getContactRes;
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 조회수 증가 메서드
     * @param pj_num
     * @param user_id
     * @throws BaseException
     * @author 한규범
     */
    public void plusViews(int pj_num, String user_id) throws BaseException{
        try {
            projectDao.plusViews(pj_num, user_id);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }


}
