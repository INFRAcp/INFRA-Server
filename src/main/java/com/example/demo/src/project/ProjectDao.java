package com.example.demo.src.project;

import com.example.demo.config.BaseException;
import com.example.demo.src.project.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Repository
public class ProjectDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 프로젝트 목록 조회, 검색조회
     * @return List 제목, 분야, 진행, 모집마감일, 전체인원, 모집인원, (모집, 마감임박), 마감 남은 일수
     * @author 한규범, 윤성식
     */
    public List<GetProjectRes> getProjects(String user_id) {
        String getProjectQuery = "select Project.pj_num, user_id, pj_views, pj_header, pj_categoryName, pj_content, pj_subCategoryNum, pj_progress, pj_endTerm,pj_startTerm, pj_deadline, pj_totalPerson,pj_recruitPerson, pj_time, DATEDIFF(pj_deadline,now()) " +
                "from Project, Pj_category " +
                "where pj_status = '등록' and Project.pj_categoryNum = Pj_category.pj_categoryNum";
        return this.jdbcTemplate.query(getProjectQuery,
                (rs, rowNum) -> new GetProjectRes(
                        user_id,
                        rs.getInt("pj_num"),
                        rs.getString("pj_header"),
                        rs.getString("pj_categoryName"),
                        rs.getString("pj_progress"),
                        rs.getString("pj_deadline"),
                        rs.getInt("pj_totalPerson"),
                        rs.getInt("pj_recruitPerson"),
                        "모집중",
                        rs.getInt("DATEDIFF(pj_deadline,now())"),
                        0,
                        null,
                        null
                ));
    }


    /**
     * 프로젝트 전체, 검색 조회
     * @param search
     * @return List 제목, 분야, 진행, 모집마감일, 전체인원, 모집인원, (모집, 마감임박), 마감 남은 일수
     * @author 한규범, 윤성식
     */
    public List<GetProjectRes> getProjectsBySearch(String search, String user_id) {
        String getProjectsBySearchQuery = "select distinct Project.pj_num, pj_header, Project.pj_categoryNum, pj_progress, pj_deadline, pj_totalPerson,pj_recruitPerson, DATEDIFF(pj_deadline,now()) " +
                "from Project, Pj_hashtag, Pj_category, Pj_subCategory " +

                "where pj_status = '등록' " +
                "and (Project.pj_num = Pj_hashtag.pj_num and hashtag like ?) " +
                "or (Project.pj_categoryNum = Pj_category.pj_categoryNum and pj_categoryName like ?) " +
                "or (Project.pj_subCategoryNum = Pj_subCategory.pj_subCategoryNum and pj_subCategoryName like ?) " +
                "or pj_content like ?";

        String getProjectsBySearchParams = '%' + search + '%';

        return this.jdbcTemplate.query(getProjectsBySearchQuery,
                (rs, rowNum) -> new GetProjectRes(
                        user_id,
                        rs.getInt("pj_num"),
                        rs.getString("pj_header"),
                        rs.getString("pj_categoryNum"),
                        rs.getString("pj_progress"),
                        rs.getString("pj_deadline"),
                        rs.getInt("pj_totalPerson"),
                        rs.getInt("pj_recruitPerson"),
                        "모집중",
                        rs.getInt("DATEDIFF(pj_deadline,now())"),
                        0,
                        null,
                        null),
                getProjectsBySearchParams,
                getProjectsBySearchParams,
                getProjectsBySearchParams,
                getProjectsBySearchParams);
    }


    /**
     * 유저가 스크랩한 프로젝트 목록 조회
     * @param user_id
     * @return List 프로젝트 번호, 제목, 조회수, 분야, 세부분야, 진행상황, 모집마감일, 총 모집인원, 현재 모집인원, 게시일
     * @author 한규범
     */
    public List<GetPjLikeRes> getPj_num(String user_id) {
        String getPj_numQuery = "select Project.pj_num, pj_header, pj_views, pj_categoryName, pj_subCategoryNum, pj_progress, pj_deadline, pj_totalPerson, pj_recruitPerson, pj_time, DATEDIFF(pj_deadline,now()) " +
                "from Project, Pj_category " +
                "where Project.pj_categoryNum = Pj_category.pj_categoryNum and pj_num in (select pj_num from Pj_like where user_id= ?)";
        String getParams = user_id;
        return this.jdbcTemplate.query(getPj_numQuery,
                (rs, rowNum) -> new GetPjLikeRes(
                        rs.getInt("pj_num"),
                        rs.getString("pj_header"),
                        rs.getInt("pj_views"),
                        rs.getString("pj_categoryName"),
                        rs.getString("pj_subCategoryNum"),
                        rs.getString("pj_progress"),
                        rs.getString("pj_deadline"),
                        rs.getInt("pj_totalPerson"),
                        rs.getInt("pj_recruitPerson"),
                        rs.getString("pj_time"),
                        null,
                        null,
                        rs.getInt("DATEDIFF(pj_deadline,now())"),
                        0,
                        null),

                getParams
        );
    }

    /**
     * 프로젝트에 참여한 팀원들 조회
     * @param pj_num
     * @return List 유저 닉네임, 유저 사진
     * @author 윤성식
     */
    public List<GetPjParticipateRes> getTeam(int pj_num) {
        String getTeamCheckQuery = "select count(*) from Pj_request where pj_inviteStatus = '승인완료' and pj_num = ?";

        if(this.jdbcTemplate.queryForObject(getTeamCheckQuery, int.class, pj_num) == 0){
            return null;
        }
        else {
            String getTeam_Query = "select user_id, user_nickname, user_prPhoto " +
                    "from User " +
                    "where user_id in (select user_id from Pj_request where pj_inviteStatus = '승인완료' and pj_num = ?)";
            Integer getParams = pj_num;
            return this.jdbcTemplate.query(getTeam_Query,
                    (rs, rowNum) -> new GetPjParticipateRes(
                            rs.getString("user_id"),
                            rs.getString("user_nickname"),
                            rs.getString("user_prPhoto")),
                    getParams
            );
        }
    }

    /**
     * 유저가 열람한 프로젝트 목록 조회
     * @param user_id
     * @return List 프로젝트 번호, 프로젝트 제목, 조회수, 프로젝트 분야, 세부분야, 진행, 마감일, 전체인원, 모집 중인 인원, 프로젝트 등록 시간
     * @author 한규범
     */
    public List<GetPjInquiryRes> proInquiry(String user_id) {
        String getPj_inquiryQuery = "select pj_num, pj_header, pj_views, pj_categoryName, pj_subCategoryNum, pj_progress, pj_deadline, pj_totalPerson, pj_recruitPerson, pj_time, DATEDIFF(pj_deadline,now()) from Project, Pj_category where Project.pj_categoryNum = Pj_category.pj_categoryNum and pj_num in (select pj_num from Pj_inquiry where user_id = ?)";
        String Pj_inquiryParams = user_id;
        return this.jdbcTemplate.query(getPj_inquiryQuery,
                (rs, rowNum) -> new GetPjInquiryRes(
                        rs.getInt("pj_num"),
                        rs.getString("pj_header"),
                        rs.getInt("pj_views"),
                        rs.getString("pj_categoryName"),
                        rs.getInt("pj_subCategoryNum"),
                        rs.getString("pj_progress"),
                        rs.getString("pj_deadline"),
                        rs.getInt("pj_totalPerson"),
                        rs.getInt("pj_recruitPerson"),
                        rs.getString("pj_time"),
                        null,
                        null,
                        rs.getInt("DATEDIFF(pj_deadline,now())"),
                        0,
                        null),
                Pj_inquiryParams
        );
    }

    /**
     * 프로젝트 등록
     * @param postPjRegisterReq
     * @return PostPjRegisterRes 프로젝트 제목
     * @author 한규범
     */
    public String pjRegistration(PostPjRegisterReq postPjRegisterReq) {

        String registrationPjQuery = "insert into Project(user_id, pj_views, pj_header, pj_categoryNum,    pj_content, pj_subCategoryNum, pj_progress, pj_endTerm,      pj_startTerm, pj_deadline, pj_totalPerson, pj_recruitPerson) VALUES (?,?,?,?  ,?,?,?,?   ,?,?,?,?)";
        Object[] registrationParms = new Object[]
                {postPjRegisterReq.getUser_id(),
                        postPjRegisterReq.getPj_views(),
                        postPjRegisterReq.getPj_header(),
                        postPjRegisterReq.getPj_categoryNum(),
                        postPjRegisterReq.getPj_content(),
                        postPjRegisterReq.getPj_subCategoryNum(),
                        postPjRegisterReq.getPj_progress(),
                        postPjRegisterReq.getPj_endTerm(),
                        postPjRegisterReq.getPj_startTerm(),
                        postPjRegisterReq.getPj_deadline(),
                        postPjRegisterReq.getPj_totalPerson(),
                        postPjRegisterReq.getPj_recruitPerson()};
        this.jdbcTemplate.update(registrationPjQuery, registrationParms);

        String getPjNumQuery = "SELECT pj_num FROM Project ORDER BY pj_num DESC LIMIT 1";
        postPjRegisterReq.setPj_num(this.jdbcTemplate.queryForObject(getPjNumQuery, int.class));



        for (int i = 0; i < postPjRegisterReq.getHashtag().length; i++) {
            String insertKeywordQuery = "INSERT INTO Pj_hashtag (pj_num, hashtag) VALUES(?,?)";
            this.jdbcTemplate.update(insertKeywordQuery, postPjRegisterReq.getPj_num(), postPjRegisterReq.getHashtag()[i]);
        }

        String lastInsertPjnameQuery = postPjRegisterReq.getPj_header();
        return lastInsertPjnameQuery;
    }

    /**
     * 프로젝트 수정
     * @param patchPjModifyReq
     * @return PatchPjModifyRes 프로젝트 제목
     * @author 한규범
     */
    public String pjModify(PatchPjModifyReq patchPjModifyReq) {
        String pjModifyQuery = "update Project set pj_header = ?, pj_categoryNum = ?, pj_content = ?, pj_subCategoryNum = ?, pj_progress = ?, pj_startTerm = ?, pj_endTerm = ?, pj_deadline = ?, pj_totalPerson = ? where pj_num = ? ";
        Object[] pjModifyParms = new Object[]{
                patchPjModifyReq.getPj_header(),
                patchPjModifyReq.getPj_categoryNum(),
                patchPjModifyReq.getPj_content(),
                patchPjModifyReq.getPj_subCategoryNum(),
                patchPjModifyReq.getPj_progress(),
                patchPjModifyReq.getPj_startTerm(),
                patchPjModifyReq.getPj_endTerm(),
                patchPjModifyReq.getPj_deadline(),
                patchPjModifyReq.getPj_totalPerson(),
                patchPjModifyReq.getPj_num()
        };
        this.jdbcTemplate.update(pjModifyQuery, pjModifyParms);

        String deleteKeywordQuery = "delete from Pj_hashtag where pj_num = ?";
        this.jdbcTemplate.update(deleteKeywordQuery, patchPjModifyReq.getPj_num());

        for (int i = 0; i < patchPjModifyReq.getHashtag().length; i++) {
            String insertKeywordQuery = "INSERT into Pj_hashtag (pj_num, hashtag) VALUES (?,?)";
            this.jdbcTemplate.update(insertKeywordQuery, patchPjModifyReq.getPj_num(), patchPjModifyReq.getHashtag()[i]);
        }

        return patchPjModifyReq.getPj_header();
    }

    /**
     * 프로젝트 삭제
     * @param delPjDelReq
     * @return DelPjDelRes 결과 메시지
     * @author 한규범
     */
    public String pjDel(DelPjDelReq delPjDelReq) {

        String pjDelQuery = "update Project set pj_status = '삭제' where pj_num = ? ";
        this.jdbcTemplate.update(pjDelQuery, delPjDelReq.getPj_num());

        return "삭제가 완료되었습니다.";
    }

    /**
     * 프로젝트 지원
     *
     * @param postPjApplyReq
     * @return PostPjApplyRes 완료 메시지
     * @author 한규범
     */
    public String pjApply(PostPjApplyReq postPjApplyReq) {
        String pjApplyCoincideCheckQuery = "Select Count(*) from Pj_request where pj_num = ? and user_id = ?";
        int checkCount = this.jdbcTemplate.queryForObject(pjApplyCoincideCheckQuery, int.class, postPjApplyReq.getPj_num(), postPjApplyReq.getUser_id());
        if(checkCount >= 1){
            String pjApplyRejectCheckQuery = "select pj_inviteStatus from Pj_request where pj_num = ? and user_id = ?";

            String comment = this.jdbcTemplate.queryForObject(pjApplyRejectCheckQuery, String.class, postPjApplyReq.getPj_num(), postPjApplyReq.getUser_id());

            if (comment.equals("거절")) {
                return "거절";
            } else if (comment.equals("승인완료") || comment.equals("신청")) {
                return "중복";
            }

        }else{
            String pjApplyQuery = "insert into Pj_request (user_id, pj_num, pj_inviteStatus) VALUES (?,?,'신청')";
            this.jdbcTemplate.update(pjApplyQuery, postPjApplyReq.getUser_id(), postPjApplyReq.getPj_num());
            return "신청이 완료되었습니다.";
        }
        return null;
    }

    /**
     * 프로젝트 신청한 유저 승인
     *
     * @param patchPjMemberReq
     * @return PatchPjApproveRes 완료 메시지
     * @author 윤성식
     */
    public String pjApprove(PatchPjMemberReq patchPjMemberReq) {
        String pjApproveQuery = "update Pj_request set pj_inviteStatus = '승인완료' where user_id = ? and pj_num = ? and pj_inviteStatus = '신청'";
        Object[] pjApproveParams = new Object[]{
                patchPjMemberReq.getUser_id(),
                patchPjMemberReq.getPj_num()
        };
        this.jdbcTemplate.update(pjApproveQuery, pjApproveParams);

        return "승인완료";
    }

    /**
     * 프로젝트 신청한 유저 거절
     *
     * @param patchPjMemberReq
     * @return PatchPjApproveRes 완료 메시지
     * @author shinhyeon
     */
    public String pjReject(PatchPjMemberReq patchPjMemberReq) {
        String pjRejectQuery = "update Pj_request set pj_inviteStatus = '거절' where user_id = ? and pj_num = ? and pj_inviteStatus = '신청'";
        Object[] pjRejectParams = new Object[]{
                patchPjMemberReq.getUser_id(),
                patchPjMemberReq.getPj_num()
        };
        this.jdbcTemplate.update(pjRejectQuery, pjRejectParams);

        return "거절";
    }

    /**
     * 프로젝트 팀원 강퇴
     *
     * @param patchPjMemberReq
     * @return PatchPjApproveRes 완료 메시지
     * @author shinhyeon
     */
    public String pjKickOut(PatchPjMemberReq patchPjMemberReq) {
        String pjRejectQuery = "update Pj_request set pj_inviteStatus = '강퇴' where user_id = ? and pj_num = ? and pj_inviteStatus = '승인완료'";
        Object[] pjRejectParams = new Object[]{
                patchPjMemberReq.getUser_id(),
                patchPjMemberReq.getPj_num()
        };
        this.jdbcTemplate.update(pjRejectQuery, pjRejectParams);

        return "강퇴";
    }

    /**
     * 본인이 지원한 프로젝트 신청 현황
     *
     * @param postUserApplyReq
     * @return List 프로젝트 번호, 참여 상태, 프로젝트 이름, 조회수, 프로젝트 제목
     * @author 윤성식
     */
    public List<PostUserApplyRes> getUserApply(PostUserApplyReq postUserApplyReq) {
        String getApplyQuery = "select Pj_request.pj_num, pj_inviteStatus, pj_views, pj_header from Pj_request, Project where Pj_request.pj_num = Project.pj_num and Pj_request.user_id = ?";
        String getApplyParams = postUserApplyReq.getUser_id();
        return this.jdbcTemplate.query(getApplyQuery,
                (rs, rowNum) -> new PostUserApplyRes(
                        rs.getInt("pj_num"),
                        rs.getString("pj_inviteStatus"),
                        rs.getInt("pj_views"),
                        rs.getString("pj_header")),
                getApplyParams
        );
    }


    /**
     * 프로젝트 신청 현황
     *
     * @param pj_num
     * @return List 유저ID, 유저 평점, 유저 사진, 프로젝트 번호
     * @author 윤성식
     */
    public List<GetApplyListRes> pjApplyList(String pj_num) {
        String pjCountApplyListQuery = "select count(*) from Pj_request where pj_num = ?";
        if(this.jdbcTemplate.queryForObject(pjCountApplyListQuery, int.class, pj_num) == 0){
            return null;
        }
        else {
            String pjApplyListQuery = "select User.user_id, user_nickname, user_grade, user_prPhoto, pj_inviteStatus from User, Pj_request where User.user_id = Pj_request.user_id and pj_num = ?";
            return this.jdbcTemplate.query(pjApplyListQuery,
                    (rs, rowNum) -> new GetApplyListRes(
                            rs.getString("user_id"),
                            rs.getString("user_nickname"),
                            rs.getString("user_grade"),
                            rs.getString("user_prPhoto"),
                            rs.getString("pj_inviteStatus")),
                    pj_num);
        }
    }

    /**
     * 프로젝트 스크랩 등록
     * @param postLikeRegisterReq
     * @return 등록 완료된 메세지
     * @author 윤성식
     */
    public String likeRegister(PostLikeRegisterReq postLikeRegisterReq) {
        String likeRegisterQuery = "INSERT into Pj_like (user_id, pj_num) VALUES (?,?)";
        this.jdbcTemplate.update(likeRegisterQuery, postLikeRegisterReq.getUser_id(), postLikeRegisterReq.getPj_num());

        return "찜 등록완료";
    }

    /**
     * 프로젝트 스크랩 삭제
     * @param postLikeRegisterReq
     * @return 찜 삭제된 메세지
     * @author 윤성식
     */
    public String likeDel(PostLikeRegisterReq postLikeRegisterReq) {
        String likeDelQuery = "delete from Pj_like where user_id = ? and pj_num = ?";
        this.jdbcTemplate.update(likeDelQuery, postLikeRegisterReq.getUser_id(), postLikeRegisterReq.getPj_num());

        return "찜 삭제";
    }
    /**
     * 팀원 평가 조회
     *
     * @param passiveUser_id
     * @return List <평가한 id, 평가 받은 id, 프로젝트 num, 의견, 책임감, 역량, 팀워크, 리더쉽>
     * @author shinhyeon
     */
    public List<GetEvalRes> getEval(String passiveUser_id) {
        String getEvalQuery = "select * from Pj_evaluate where passiveUser_id = ? order By pj_num";

        return this.jdbcTemplate.query(getEvalQuery,
                (rs, rowNum) -> new GetEvalRes(
                        rs.getString("user_id"),
                        rs.getString("passiveUser_id"),
                        rs.getInt("pj_num"),
                        rs.getString("opinion"),
                        rs.getFloat("responsibility"),
                        rs.getFloat("ability"),
                        rs.getFloat("teamwork"),
                        rs.getFloat("leadership")
                ),
                passiveUser_id
        );
    }

    /**
     * 팀원 평가 등록
     *
     * @param postEvalReq
     * @return int
     * @author shinhyeon
     */
    public int uploadEval(PostEvalReq postEvalReq) {
        String uploadEvalQuery = "insert into Pj_evaluate (user_id, passiveUser_id, pj_num, opinion, responsibility, ability, teamwork, leadership) VALUES (?,?,?,?,?,?,?,?)";
        Object[] uploadEvalParms = new Object[]{
                postEvalReq.getUser_id(),
                postEvalReq.getPassiveUser_id(),
                postEvalReq.getPj_num(),
                postEvalReq.getOpinion(),
                postEvalReq.getResponsibility(),
                postEvalReq.getAbility(),
                postEvalReq.getTeamwork(),
                postEvalReq.getLeadership()
        };

        return this.jdbcTemplate.update(uploadEvalQuery, uploadEvalParms);
    }

    /**
     * 평가하는 인원의 승인 상태 조회
     *
     * @param user_id, pj_num
     * @return String
     * @author shinhyeon
     */
    public String getPjInviteStatus1(String user_id, Integer pj_num) {
        String getPjInviteStatusQuery = "select pj_inviteStatus from Pj_request where user_id = ? and pj_num = ?";
        Object[] getPjInviteStatusParms = new Object[]{
                user_id,
                pj_num
        };

        return this.jdbcTemplate.queryForObject(getPjInviteStatusQuery, getPjInviteStatusParms, String.class);
    }

    /**
     * 평가받는 인원의 승인 상태 조회
     *
     * @param passiveUser_id
     * @return String
     * @author shinhyeon
     */
    public String getPjInviteStatus2(String passiveUser_id, Integer pj_num) {
        String getPjInviteStatusQuery = "select pj_inviteStatus from Pj_request where user_id = ? and pj_num = ?";
        Object[] getPjInviteStatusParms = new Object[]{
                passiveUser_id,
                pj_num
        };

        return this.jdbcTemplate.queryForObject(getPjInviteStatusQuery, getPjInviteStatusParms, String.class);
    }

    public void uploadGrade(String user_id, float user_grade) {
        String uploadGradeQuery = "UPDATE User SET user_grade = ? WHERE user_id = ?";
        Object[] uploadGradeParams = new Object[]{
                user_grade,
                user_id
        };

        this.jdbcTemplate.update(uploadGradeQuery, uploadGradeParams);
    }

    /**
     * 팀원 평가 수정
     *
     * @param patchEvalReq
     * @return x
     * @author shinhyeon
     */
    public void modifyEval(PatchEvalReq patchEvalReq) {
        String modifyEvalQuery = "update Pj_evaluate set opinion = ?, responsibility = ?, ability = ?, teamwork = ?, leadership = ? where user_id = ? and passiveUser_id = ? and pj_num = ?";
        Object[] modifyEvalParms = new Object[]{
                patchEvalReq.getOpinion(),
                patchEvalReq.getResponsibility(),
                patchEvalReq.getAbility(),
                patchEvalReq.getTeamwork(),
                patchEvalReq.getLeadership(),
                patchEvalReq.getUser_id(),
                patchEvalReq.getPassiveUser_id(),
                patchEvalReq.getPj_num()
        };

        this.jdbcTemplate.update(modifyEvalQuery, modifyEvalParms);
    }

    /**
     * 팀원 평가 삭제
     *
     * @param patchEvalDelReq
     * @return x
     * @author shinhyeon
     */
    public void delEval(PatchEvalDelReq patchEvalDelReq) {
        String delEvalQuery = "update Pj_evaluate set status = ? where user_id = ? and passiveUser_id = ? and pj_num = ?";
        Object[] delEvalParms = new Object[]{
                "삭제",
                patchEvalDelReq.getUser_id(),
                patchEvalDelReq.getPassiveUser_id(),
                patchEvalDelReq.getPj_num()
        };

        this.jdbcTemplate.update(delEvalQuery, delEvalParms);

    }

    /**
     * 평가 존재 유무
     *
     * @param user_id
     * @param passiveUser_id
     * @param pj_num
     * @return Integer
     * @author shinhyeon
     */
    public Integer getEvalCheck(String user_id, String passiveUser_id, Integer pj_num) {
        String getEvalCheckQuery = "SELECT 1 FROM Pj_evaluate WHERE user_id = ? and passiveUser_id = ? and pj_num = ? LIMIT 1";
        Object getEvalCheckParms = new Object[]{
                user_id,
                passiveUser_id,
                pj_num
        };

        return this.jdbcTemplate.queryForObject(getEvalCheckQuery, (Object[]) getEvalCheckParms, Integer.class);
    }

    /**
     * 프로젝트 팀장 조회
     *
     * @param pj_num
     * @return String
     * @author shinhyeon
     */
    public String getTeamLeader(Integer pj_num) {
        String getTeamLeaderQuery = "SELECT user_id FROM Project WHERE pj_num=?";
        return this.jdbcTemplate.queryForObject(getTeamLeaderQuery, new Integer[]{pj_num}, String.class);
    }

    /**
     * 카테고리 이름을 통한 번호 반환
     * @param pj_categoryName
     * @return
     * @throws BaseException
     * @author 한규범
     */
    public String getPjCategoryNum(String pj_categoryName) {
        String getPjCategoryNumQuery = "SELECT pj_categoryNum FROM Pj_category WHERE pj_categoryName = ?";
        return this.jdbcTemplate.queryForObject(getPjCategoryNumQuery, String.class, pj_categoryName);
    }

    /**
     * 세부 카테고리 이름을 통한 번호 반환
     * @param pj_subCategoryName
     * @return
     * @throws BaseException
     * @author 한규범
     */
    public String getPjsubCategoryNum(String pj_subCategoryName) {
        String getPjSubCategoryNumQuery = "SELECT pj_subCategoryNum FROM Pj_subCategory WHERE pj_subCategoryName = ?";
        return this.jdbcTemplate.queryForObject(getPjSubCategoryNumQuery, String.class, pj_subCategoryName);
    }

    /**
     * 프로젝트 찜여부 반환 메서드
     * @param pj_num
     * @param user_id
     * @return int 형 찜했으면 1, 안했으면 0
     * @throws BaseException
     * @author 한규범
     */
    public int checkPjLike(int pj_num, String user_id) {
        String checkPjLikeQuery="SELECT count(*) FROM Pj_like WHERE pj_num = ? and user_id = ?";
        return this.jdbcTemplate.queryForObject(checkPjLikeQuery,int.class, pj_num, user_id);
    }

    /**
     * 해시태그 반환하는 메서드
     * @param pj_num
     * @return
     * @author 한규범
     */
    public String[] getHashtag(int pj_num) {
        String nullcheckHashtag = "SELECT count(*) FROM Pj_hashtag WHERE pj_num = ?";
        int cnt = this.jdbcTemplate.queryForObject(nullcheckHashtag, int.class, pj_num);
        if (cnt > 0) {
            String getHashtagQuery = "SELECT hashtag FROM Pj_hashtag WHERE pj_num = ?";
            List<String> hashTag = this.jdbcTemplate.queryForList(getHashtagQuery, String.class, pj_num);
            String hashtag[] = hashTag.toArray(new String[hashTag.size()]); //형변환

            return hashtag;
        }
        return null;
    }

    /**
     * 유저 등급 조회
     *
     * @param user_id
     * @return float
     * @qathor shinhyeon
     */
    public float getGrade(String user_id) {
        String getGradeQuery = "SELECT user_grade FROM User WHERE user_id = ?";
        return this.jdbcTemplate.queryForObject(getGradeQuery, Float.class, user_id);
    }

    /**
     * 프로젝트 사진 경로 조회
     * @param pj_num
     * @return List<String>
     * @qathor shinhyeon
     */
    public List<String> getPjPhoto(int pj_num) {
        String getPjPhotoQuery = "SELECT pjPhoto FROM Pj_photo WHERE pj_num = ?";
        return this.jdbcTemplate.queryForList(getPjPhotoQuery, String.class, pj_num);
    }

    /**
     * 프로젝트 하나 접속
     * @param pj_num
     * @param user_id
     * @return
     * @author 한규범
     */
    public GetContactRes pjContact(int pj_num, String user_id) {
        String pjContactQuery = "SELECT Project.user_id, user_nickname, user_prPhoto, pj_header, pj_views, pj_categoryName, pj_subCategoryName, pj_content, pj_progress, pj_endTerm, pj_startTerm, pj_deadline, pj_totalPerson, pj_recruitPerson," +
                "(SELECT count(*) FROM Project, Pj_like WHERE Project.pj_num = Pj_like.pj_num and Project.pj_num = ?) as CNT, DATEDIFF(pj_deadline,now()) as DAY " +
                "FROM User, Project, Pj_subCategory, Pj_category " +
                "WHERE Project.user_id = User.user_id and Pj_subCategory.pj_categoryNum = Pj_category.pj_categoryNum and Project.pj_categoryNum = Pj_category.pj_categoryNum and Project.pj_subCategoryNum = Pj_subCategory.pj_subCategoryNum and pj_num = ?";

        return this.jdbcTemplate.queryForObject(pjContactQuery,
                (rs, rowNum) -> GetContactRes.builder().user_id(rs.getString("user_id")).
                        pj_num(pj_num).
                        pj_views(rs.getInt("pj_views")).
                        pj_categoryName(rs.getString("pj_categoryName")).
                        pj_subCategoryName(rs.getString("pj_subCategoryName")).
                        pj_content(rs.getString("pj_content")).
                        pj_header(rs.getString("pj_header")).
                        pj_progress(rs.getString("pj_progress")).
                        pj_endTerm(rs.getString("pj_endTerm")).
                        pj_startTerm(rs.getString("pj_startTerm")).
                        pj_deadline(rs.getString("pj_deadline")).
                        pj_totalPerson(rs.getString("pj_totalPerson")).
                        pj_recruitPerson(rs.getString("pj_recruitPerson")).
                        user_nickname(rs.getString("user_nickname")).
                        user_prPhoto(rs.getString("user_prPhoto")).
                        hashtag(null).
                        pjLikeCount(rs.getInt("CNT")).
                        pj_daysub(rs.getInt("DAY")).build(), pj_num, pj_num);

    }

    /**
     * 조회수 증가 메서드
     * @param pj_num
     * @param user_id
     * @author 한규범
     */
    public void plusViews(int pj_num, String user_id) {
        String plusViewsCheckQuery = "SELECT count(*) FROM Pj_inquiry WHERE user_id = ? and pj_num = ?";
        int timeCount = this.jdbcTemplate.queryForObject(plusViewsCheckQuery, int.class, user_id, pj_num);
        if(timeCount==0){ //처음 조회
            String inputViews = "insert into Pj_inquiry (user_id, pj_num, pj_inquiryTime) VALUES (?,?,DEFAULT)";
            this.jdbcTemplate.update(inputViews, user_id, pj_num);
        } else if (timeCount==1) { //두번 이상 조회
            String plusViewsQuery = "SELECT pj_inquiryTime FROM Pj_inquiry WHERE user_id = ? and pj_num = ?";
            String time = this.jdbcTemplate.queryForObject(plusViewsQuery, String.class, user_id, pj_num);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            LocalDateTime ldTime = LocalDateTime.parse(time, formatter);
            ldTime = ldTime.plusMinutes(30);
            LocalDateTime now = LocalDateTime.now();

            if(ldTime.isBefore(now)){ // 30분이 경과된 경우
                String plusViews = "SELECT pj_views FROM Project WHERE pj_num = ?";
                int views = this.jdbcTemplate.queryForObject(plusViews, int.class, pj_num);
                views++;

                String plusPjViews = "UPDATE Project SET pj_views = ? WHERE pj_num = ?";
                this.jdbcTemplate.update(plusPjViews, views, pj_num);

                String viewtimeUpdate = "UPDATE Pj_inquiry SET pj_inquiryTime = Default WHERE pj_num = ? and user_id = ?";
                this.jdbcTemplate.update(viewtimeUpdate, pj_num, user_id);
            }

        }
    }

    /**
     * 인기 프로젝트 조회 (지금 핫한 프로젝트)
     * @param user_id
     * @return List<GetHotProjectRes>
     * @author shinhyeon
     */
    public List<GetHotProjectRes> getProjectsBy1DayViews(String user_id) {
        String getHotProjectsQuery = "SELECT Project.pj_num, user_id, pj_views, pj_header, pj_views, pj_categoryName, pj_subCategoryName, pj_content, pj_progress, pj_endTerm,pj_startTerm, pj_deadline, pj_totalPerson,pj_recruitPerson, pj_time, DATEDIFF(pj_deadline,now()) " +
                "FROM Project, Pj_category, Pj_subCategory " +
                "WHERE pj_status = '등록' AND Project.pj_categoryNum = Pj_category.pj_categoryNum AND Project.pj_categoryNum = Pj_subCategory.pj_categoryNum AND Project.pj_subCategoryNum = Pj_subCategory.pj_subCategoryNum";

        List<GetHotProjectRes> getHotProjectRes = this.jdbcTemplate.query(getHotProjectsQuery,
                (rs, rowNum) -> new GetHotProjectRes(
                        user_id,
                        rs.getInt("pj_num"),
                        rs.getString("pj_header"),
                        rs.getInt("pj_views"),
                        0,
                        rs.getString("pj_categoryName"),
                        rs.getString("pj_subCategoryName"),
                        rs.getString("pj_progress"),
                        rs.getString("pj_deadline"),
                        rs.getInt("pj_totalPerson"),
                        rs.getInt("pj_recruitPerson"),
                        "모집중",
                        rs.getInt("DATEDIFF(pj_deadline,now())"),
                        0,
                        null,
                        null
                ));


        for(int i=0;i<getHotProjectRes.size();i++)
        {
            // pj_views_1day(하루 동안 조회수) 값 계산하여 대입
            String plusViewsQuery = "SELECT pj_inquiryTime FROM Pj_inquiry WHERE pj_num = ?";
            List<String> time = this.jdbcTemplate.queryForList(plusViewsQuery, String.class, getHotProjectRes.get(i).getPj_num());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            int views_1day = 0;
            for(int j=0;j<time.size();j++)
            {
                LocalDateTime ldTime = LocalDateTime.parse(time.get(j), formatter);
                ldTime = ldTime.plusDays(1);
                LocalDateTime now = LocalDateTime.now();

                if(ldTime.isAfter(now)){ // 조회한지 하루가 안된 경우
                    views_1day ++;
                }
            }
            getHotProjectRes.get(i).setPj_views_1day(views_1day);
        }

        // pj_views_1day 기준으로 내림차순 정렬
        Collections.sort(getHotProjectRes, (c1, c2) ->  c2.getPj_views_1day() - c1.getPj_views_1day());

        return getHotProjectRes;
    }

    /**
     * 프로젝트 추천 조회 (이런 프로젝트는 어떠세요?)
     * @param user_id
     * @return List<GetHotProjectRes>
     * @author shinhyeon
     */
    public List<GetHotProjectRes> getRecommendProjects(String user_id) {
        // 관심 카테고리 가져오기
        String getUserInterestCategory = "SELECT user_interestCategory FROM User_interest WHERE user_id = ? ";
        int user_interestCategory = this.jdbcTemplate.queryForObject(getUserInterestCategory, int.class, user_id);

        // (관심 카테고리와 일치하는) 추천 프로젝트 가져오기
        String getRecommendProjectsQuery = "SELECT Project.pj_num, user_id, pj_views, pj_header, pj_views, pj_categoryName, pj_subCategoryName, pj_content, pj_progress, pj_endTerm,pj_startTerm, pj_deadline, pj_totalPerson,pj_recruitPerson, pj_time, DATEDIFF(pj_deadline,now()) " +
                "FROM Project, Pj_category, Pj_subCategory " +
                "WHERE pj_status = '등록' AND Project.pj_categoryNum = Pj_category.pj_categoryNum AND Project.pj_categoryNum = Pj_subCategory.pj_categoryNum AND Project.pj_subCategoryNum = Pj_subCategory.pj_subCategoryNum AND Project.pj_categoryNum = ? ";

        return this.jdbcTemplate.query(getRecommendProjectsQuery,
                (rs, rowNum) -> new GetHotProjectRes(
                        user_id,
                        rs.getInt("pj_num"),
                        rs.getString("pj_header"),
                        rs.getInt("pj_views"),
                        0,
                        rs.getString("pj_categoryName"),
                        rs.getString("pj_subCategoryName"),
                        rs.getString("pj_progress"),
                        rs.getString("pj_deadline"),
                        rs.getInt("pj_totalPerson"),
                        rs.getInt("pj_recruitPerson"),
                        "모집중",
                        rs.getInt("DATEDIFF(pj_deadline,now())"),
                        0,
                        null,
                        null
                ), user_interestCategory);
    }

    /**
     * 관심분야 설정 여부 확인
     * @param user_id
     * @return int
     * @author shinhyeon
     */
    public int checkInterestCategory(String user_id) {
        String checkInterestCategoryQuery = "select count(*) from User_interest where user_id = ? ";
        return this.jdbcTemplate.queryForObject(checkInterestCategoryQuery, int.class, user_id);
    }

    public List<GetMyPjInquiryRes> getMyPjInquiry(String user_id) {
        String getProjectQuery = "select Project.pj_num, pj_header, pj_categoryName, pj_subCategoryNum, pj_progress, pj_deadline, pj_totalPerson, pj_recruitPerson, DATEDIFF(pj_deadline,now()) " +
                "from Project, Pj_category " +
                "where pj_status = '등록' and Project.pj_categoryNum = Pj_category.pj_categoryNum and user_id = ? ";
        return this.jdbcTemplate.query(getProjectQuery,
                (rs, rowNum) -> new GetMyPjInquiryRes(
                        rs.getInt("pj_num"),
                        rs.getString("pj_header"),
                        rs.getString("pj_categoryName"),
                        rs.getInt("pj_subCategoryNum"),
                        rs.getString("pj_progress"),
                        rs.getString("pj_deadline"),
                        rs.getInt("pj_totalPerson"),
                        rs.getInt("pj_recruitPerson"),
                        null,
                        "모집중",
                        rs.getInt("DATEDIFF(pj_deadline,now())"),
                        null
                ), user_id);
    }
}
