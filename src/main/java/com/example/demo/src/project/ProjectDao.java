package com.example.demo.src.project;

import com.example.demo.config.BaseException;
import com.example.demo.src.project.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ProjectDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 프로젝트 전체, 검색 조회
     *
     * @return List 제목, 분야, 이름, 진행, 모집마감일, 전체인원, 모집인원, (모집, 마감임박), 마감 남은 일수
     * @author 한규범, 윤성식
     */
    public List<GetProjectRes> getProjects() {
        String getProjectQuery = "select Project.pj_num, user_id, pj_views, pj_header, pj_categoryName, pj_content, pj_name, pj_subCategoryNum, pj_progress, pj_endTerm,pj_startTerm, pj_deadline, pj_totalPerson,pj_recruitPerson, pj_time, DATEDIFF(pj_deadline,now()) " +
                "from Project, Pj_category " +
                "where pj_status = '등록' and Project.pj_categoryNum = Pj_category.pj_categoryNum";
        return this.jdbcTemplate.query(getProjectQuery,
                (rs, rowNum) -> new GetProjectRes(
                        rs.getInt("pj_num"),
                        rs.getString("pj_header"),
                        rs.getString("pj_categoryName"),
                        rs.getString("pj_name"),
                        rs.getString("pj_progress"),
                        rs.getString("pj_deadline"),
                        rs.getInt("pj_totalPerson"),
                        rs.getInt("pj_recruitPerson"),
                        "모집중",
                        rs.getInt("DATEDIFF(pj_deadline,now())")
                ));
    }


    /**
     * 프로젝트 전체, 검색 조회
     *
     * @param search
     * @return List 제목, 분야,ㅂ 이름, 진행, 모집마감일, 전체인원, 모집인원, (모집, 마감임박), 마감 남은 일수
     * @author 한규범, 윤성식
     */
    public List<GetProjectRes> getProjectsBySearch(String search) {
        String getProjectsBySearchQuery = "select distinct Project.pj_num, pj_header, Project.pj_categoryNum, pj_name, pj_name, pj_progress, pj_deadline, pj_totalPerson,pj_recruitPerson, DATEDIFF(pj_deadline,now()) " +
                "from Project, Pj_hashtag, Pj_category, Pj_subCategory " +

                "where pj_status = '등록' " +
                "and (Project.pj_num = Pj_hashtag.pj_num and hashtag like ?) " +
                "or (Project.pj_categoryNum = Pj_category.pj_categoryNum and pj_categoryName like ?) " +
                "or (Project.pj_subCategoryNum = Pj_subCategory.pj_subCategoryNum and pj_subCategoryName like ?) " +
                "or pj_name like ? or pj_content like ?";

        String getProjectsBySearchParams = '%' + search + '%';

        return this.jdbcTemplate.query(getProjectsBySearchQuery,
                (rs, rowNum) -> new GetProjectRes(
                        rs.getInt("pj_num"),
                        rs.getString("pj_header"),
                        rs.getString("pj_categoryNum"),
                        rs.getString("pj_name"),
                        rs.getString("pj_progress"),
                        rs.getString("pj_deadline"),
                        rs.getInt("pj_totalPerson"),
                        rs.getInt("pj_recruitPerson"),
                        "모집중",
                        rs.getInt("DATEDIFF(pj_deadline,now())")),
                getProjectsBySearchParams,
                getProjectsBySearchParams,
                getProjectsBySearchParams,
                getProjectsBySearchParams,
                getProjectsBySearchParams);
    }

    /**
     * 프로젝트 키워드 조회
     *
     * @return List 프로젝트 번호, 키워드
     * @author 한규범, 윤성식
     */
    public List<GetPjKeywordRes> getPj_keywords() {
        String getProjectQuery = "select Project.pj_num, hashtag from Pj_hashtag, Project where Project.pj_num = Pj_hashtag.pj_num";
        return this.jdbcTemplate.query(getProjectQuery,
                (rs, rowNum) -> new GetPjKeywordRes(
                        rs.getInt("pj_num"),
                        rs.getString("hashtag")
                )
        );
    }

    /**
     * 프로젝트 키워드 조회
     *
     * @param search
     * @return List 프로젝트 번호, 키워드
     * @author 한규범, 윤성식
     */
    public List<GetPjKeywordRes> getPj_keywordsBysearch(String search) {
        String getProjectsBySearchQuery = "select Project.pj_num, hashtag from Project, Pj_hashtag " +
                "where Project.pj_num = Pj_hashtag.pj_num and pj_name like ? or pj_content like ? or hashtag like ?";

        String getProjectsBySearchParams = '%' + search + '%';

        return this.jdbcTemplate.query(getProjectsBySearchQuery,
                (rs, rowNum) -> new GetPjKeywordRes(
                        rs.getInt("pj_num"),
                        rs.getString("hashtag")),
                getProjectsBySearchParams,
                getProjectsBySearchParams,
                getProjectsBySearchParams);
    }

    /**
     * 유저가 찜한 프로젝트 조회
     *
     * @param postPj_likeReq
     * @return List 프로젝트 번호, 제목, 조회수, 분야, 이름, 세부분야, 진행상황, 모집마감일, 총 모집인원, 현재 모집인원, 게시일
     * @author 한규범
     */
    public List<PostPjLikeRes> getPj_num(PostPjLikeReq postPj_likeReq) {
        String getPj_numQuery = "select Project.pj_num, pj_header, pj_views, pj_categoryName, pj_name, pj_subCategoryNum, pj_progress, pj_deadline, pj_totalPerson, pj_recruitPerson, pj_time " +
                "from Project, Pj_category " +
                "where Project.pj_categoryNum = Pj_category.pj_categoryNum and pj_num in (select pj_num from Pj_like where user_id= ?)";
        String getParams = postPj_likeReq.getUser_id();
        return this.jdbcTemplate.query(getPj_numQuery,
                (rs, rowNum) -> new PostPjLikeRes(
                        rs.getInt("pj_num"),
                        rs.getString("pj_header"),
                        rs.getInt("pj_views"),
                        rs.getString("pj_categoryName"),
                        rs.getString("pj_name"),
                        rs.getString("pj_subCategoryNum"),
                        rs.getString("pj_progress"),
                        rs.getString("pj_deadline"),
                        rs.getInt("pj_totalPerson"),
                        rs.getInt("pj_recruitPerson"),
                        rs.getString("pj_time")),
                getParams
        );
    }

    /**
     * 프로젝트에 참여한 팀원들 조회
     *
     * @param postPj_participateReq
     * @return List 유저 닉네임, 유저 사진
     * @author 윤성식
     */
    public List<PostPjParticipateRes> getTeam(PostPjParticipateReq postPj_participateReq) {
        String getTeam_Query = "select user_nickname, user_prPhoto " +
                "from User " +
                "where user_id in (select user_id from Pj_request where pj_inviteStatus = '승인완료' and pj_num = ?)";
        Integer getParams = postPj_participateReq.getPj_num();
        return this.jdbcTemplate.query(getTeam_Query,
                (rs, rowNum) -> new PostPjParticipateRes(
                        rs.getString("user_nickname"),
                        rs.getString("user_prPhoto")),
                getParams
        );
    }

    /**
     * 유저가 조회했던 프로젝트 조회
     *
     * @param postPj_inquiryReq
     * @return List 프로젝트 번호, 프로젝트 제목, 조회수, 프로젝트 분야, 이름, 세부분야, 진행, 마감일, 전체인원, 모집 중인 인원, 프로젝트 등록 시간
     * @author 한규범
     */
    public List<PostPjInquiryRes> proInquiry(PostPjInquiryReq postPj_inquiryReq) {
        String getPj_inquiryQuery = "select pj_num, pj_header, pj_views, pj_categoryName, pj_name, pj_subCategoryNum, pj_progress, pj_deadline, pj_totalPerson, pj_recruitPerson, pj_time from Project, Pj_category where Project.pj_categoryNum = Pj_category.pj_categoryNum and pj_num in (select pj_num from Pj_inquiry where user_id = ?)";
        String Pj_inquiryParams = postPj_inquiryReq.getUser_id();
        return this.jdbcTemplate.query(getPj_inquiryQuery,
                (rs, rowNum) -> new PostPjInquiryRes(
                        rs.getInt("pj_num"),
                        rs.getString("pj_header"),
                        rs.getInt("pj_views"),
                        rs.getString("pj_categoryName"),
                        rs.getString("pj_name"),
                        rs.getString("pj_subCategoryNum"),
                        rs.getString("pj_progress"),
                        rs.getString("pj_deadline"),
                        rs.getInt("pj_totalPerson"),
                        rs.getInt("pj_recruitPerson"),
                        rs.getString("pj_time")),
                Pj_inquiryParams
        );
    }

    /**
     * 프로젝트 등록
     *
     * @param postPjRegisterReq
     * @return PostPjRegisterRes 프로젝트 이름
     * @author 한규범
     */
    public String pjRegistration(PostPjRegisterReq postPjRegisterReq) {

        String registrationPjQuery = "insert into Project(user_id, pj_views, pj_header, pj_categoryNum,    pj_content, pj_name, pj_subCategoryNum, pj_progress, pj_endTerm,      pj_startTerm, pj_deadline, pj_totalPerson, pj_recruitPerson) VALUES (?,?,?,?  ,?,?,?,?,?   ,?,?,?,?)";
        Object[] registrationParms = new Object[]
                {postPjRegisterReq.getUser_id(),
                        postPjRegisterReq.getPj_views(),
                        postPjRegisterReq.getPj_header(),
                        postPjRegisterReq.getPj_categoryNum(),
                        postPjRegisterReq.getPj_content(),
                        postPjRegisterReq.getPj_name(),
                        postPjRegisterReq.getPj_subCategoryNum(),
                        postPjRegisterReq.getPj_progress(),
                        postPjRegisterReq.getPj_endTerm(),
                        postPjRegisterReq.getPj_startTerm(),
                        postPjRegisterReq.getPj_deadline(),
                        postPjRegisterReq.getPj_totalPerson(),
                        postPjRegisterReq.getPj_recruitPerson()};
        this.jdbcTemplate.update(registrationPjQuery, registrationParms);

        for (int i = 0; i < postPjRegisterReq.getHashtag().length; i++) {
            String insertKeywordQuery = "INSERT INTO Pj_hashtag (pj_num, hashtag) VALUES(?,?)";
            this.jdbcTemplate.update(insertKeywordQuery, postPjRegisterReq.getPj_num(), postPjRegisterReq.getHashtag()[i]);
        }

        String lastInsertPjnameQuery = postPjRegisterReq.getPj_name();
        return lastInsertPjnameQuery;
    }

    /**
     * 프로젝트 수정
     *
     * @param patchPjModifyReq
     * @return PatchPjModifyRes 프로젝트 이름
     * @author 한규범
     */
    public String pjModify(PatchPjModifyReq patchPjModifyReq) {
        String pjModifyQuery = "update Project set pj_header = ?, pj_categoryNum = ?, pj_content = ?, pj_name = ?, pj_subCategoryNum = ?, pj_progress = ?, pj_startTerm = ?, pj_endTerm = ?, pj_deadline = ?, pj_totalPerson = ? where pj_num = ? ";
        Object[] pjModifyParms = new Object[]{
                patchPjModifyReq.getPj_header(),
                patchPjModifyReq.getPj_categoryNum(),
                patchPjModifyReq.getPj_content(),
                patchPjModifyReq.getPj_name(),
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

        return patchPjModifyReq.getPj_name();
    }

    /**
     * 프로젝트 삭제
     *
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
        String pjApplyRejectCheckQuery = "select pj_inviteStatus from Pj_request where pj_num = ? and user_id = ?";

        String comment = this.jdbcTemplate.queryForObject(pjApplyRejectCheckQuery, String.class, postPjApplyReq.getPj_num(), postPjApplyReq.getUser_id());

        if (comment.equals("거절")) {
            return "거절";
        } else if (this.jdbcTemplate.queryForObject(pjApplyCoincideCheckQuery, int.class, postPjApplyReq.getPj_num(), postPjApplyReq.getUser_id()) == 1) {
            return "중복";
        } else {
            String pjApplyQuery = "insert into Pj_request (user_id, pj_num, pj_inviteStatus) VALUES (?,?,'신청')";
            this.jdbcTemplate.update(pjApplyQuery, postPjApplyReq.getUser_id(), postPjApplyReq.getPj_num());
            return "신청이 완료되었습니다.";
        }
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
        String getApplyQuery = "select Pj_request.pj_num, pj_inviteStatus, pj_name, pj_views, pj_header from Pj_request, Project where Pj_request.pj_num = Project.pj_num and Pj_request.user_id = ?";
        String getApplyParams = postUserApplyReq.getUser_id();
        return this.jdbcTemplate.query(getApplyQuery,
                (rs, rowNum) -> new PostUserApplyRes(
                        rs.getInt("pj_num"),
                        rs.getString("pj_inviteStatus"),
                        rs.getString("pj_name"),
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

    /**
     * 프로젝트 찜 등록
     *
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
     * 프로젝트 찜 삭제
     *
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
     *
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
     *
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
}
