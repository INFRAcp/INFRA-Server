package com.example.demo.src.project;

import com.example.demo.src.project.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class ProjectDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //프로젝트 조회
    public List<GetProjectRes> getProjects() {

        String getProjectQuery = "select Project.pj_num, User_id, pj_views, pj_header, pj_field, pj_content, pj_name, pj_subField, pj_progress, pj_endTerm,pj_startTerm, pj_deadline, pj_totalPerson,pj_recruitPerson, pj_time, DATEDIFF(pj_deadline,now()) from Project where pj_status = '등록'";
        return this.jdbcTemplate.query(getProjectQuery,
                (rs, rowNum) -> new GetProjectRes(
                        rs.getString("pj_header"),
                        rs.getString("pj_field"),
                        rs.getString("pj_name"),
                        rs.getString("pj_progress"),
                        rs.getString("pj_deadline"),
                        rs.getInt("pj_totalPerson"),
                        rs.getInt("pj_recruitPerson"),
                        "모집중",
                        rs.getInt("DATEDIFF(pj_deadline,now())")
                ));
    }

    //검색 프로젝트 조회
    public List<GetProjectRes> getProjectsBySearch(String search) {
        String getProjectsBySearchQuery = "select distinct pj_header, pj_field, pj_name, pj_progress, pj_deadline, pj_totalPerson, pj_recruitPerson, DATEDIFF(pj_deadline,now()) from Project, Pj_keyword where pj_name like ? or pj_content like ? or keyword like ? or pj_subfield like ?";
        String getProjectsBySearchParams = '%' + search + '%';

        return this.jdbcTemplate.query(getProjectsBySearchQuery,
                (rs, rowNum) -> new GetProjectRes(
                        rs.getString("pj_header"),
                        rs.getString("pj_field"),
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
                getProjectsBySearchParams);
    }

    //키워드 조회
    public List<GetPjKeywordRes> getPj_keywords() {
        String getProjectQuery = "select Project.pj_num, keyword from Pj_keyword, Project where Project.pj_num = Pj_keyword.pj_num";
        return this.jdbcTemplate.query(getProjectQuery,
                (rs, rowNum) -> new GetPjKeywordRes(
                        rs.getInt("pj_num"),
                        rs.getString("keyword")
                )
        );
    }

    //버리는 카드
    public List<GetPjKeywordRes> getPj_keywordsBysearch(String search) {
        String getProjectsBySearchQuery = "select Project.pj_num, keyword from Project, Pj_keyword where Project.pj_num = Pj_keyword.pj_num and pj_name like ? or pj_content like ? or keyword like ? or pj_subfield like ?";

        String getProjectsBySearchParams = '%' + search + '%';

        return this.jdbcTemplate.query(getProjectsBySearchQuery,
                (rs, rowNum) -> new GetPjKeywordRes(
                        rs.getInt("pj_num"),
                        rs.getString("keyword")),
                getProjectsBySearchParams,
                getProjectsBySearchParams,
                getProjectsBySearchParams,
                getProjectsBySearchParams);
    }

    //유저가 찜한 프로젝트 조회
    public List<PostPjLikeRes> getPj_num(PostPjLikeReq postPj_likeReq) {
        String getPj_numQuery = "select Project.pj_num, pj_header, pj_views, pj_field, pj_name, pj_subField, pj_progress, pj_deadline, pj_totalPerson, pj_recruitPerson, pj_time from Project where pj_num in (select pj_num from Pj_like where user_id= ?)";
        String getParams = postPj_likeReq.getUser_id();
        return this.jdbcTemplate.query(getPj_numQuery,
                (rs, rowNum) -> new PostPjLikeRes(
                        rs.getInt("pj_num"),
                        rs.getString("pj_header"),
                        rs.getInt("pj_views"),
                        rs.getString("pj_field"),
                        rs.getString("pj_name"),
                        rs.getString("pj_subField"),
                        rs.getString("pj_progress"),
                        rs.getString("pj_deadline"),
                        rs.getInt("pj_totalPerson"),
                        rs.getInt("pj_recruitPerson"),
                        rs.getString("pj_time")),
                getParams
        );
    }

    //프로젝트에 참여한 팀원들 조회
    public List<PostPjParticipateRes> getTeam(PostPjParticipateReq postPj_participateReq) {
        String getTeam_Query = "select User_nickname, User_prPhoto from User where User_id in (select User_id from Pj_request where pj_status = '승인완료' and pj_num = ?)";
        Integer getParams = postPj_participateReq.getPj_num();
        return this.jdbcTemplate.query(getTeam_Query,
                (rs, rowNum) -> new PostPjParticipateRes(
                        rs.getString("user_nickname"),
                        rs.getString("user_prPhoto")),
                getParams
        );
    }

    //유저가 조회했던 프로젝트 조회
    public List<PostPjInquiryRes> proInquiry(PostPjInquiryReq postPj_inquiryReq) {
        String getPj_inquiryQuery = "select pj_num, pj_header, pj_views, pj_field, pj_name, pj_subField, pj_progress, pj_deadline, pj_totalPerson, pj_recruitPerson, pj_time from Project where pj_num in (select pj_num from Pj_inquiry where user_id = ?)";
        String Pj_inquiryParams = postPj_inquiryReq.getUser_id();
        return this.jdbcTemplate.query(getPj_inquiryQuery,
                (rs, rowNum) -> new PostPjInquiryRes(
                        rs.getInt("pj_num"),
                        rs.getString("pj_header"),
                        rs.getInt("pj_views"),
                        rs.getString("pj_field"),
                        rs.getString("pj_name"),
                        rs.getString("pj_subField"),
                        rs.getString("pj_progress"),
                        rs.getString("pj_deadline"),
                        rs.getInt("pj_totalPerson"),
                        rs.getInt("pj_recruitPerson"),
                        rs.getString("pj_time")),
                Pj_inquiryParams
        );
    }

    //프로젝트 등록
    public String pjRegistration(PostPjRegisterReq postPjRegisterReq) {
        String Pj_numQuery = "SELECT pj_num FROM Project ORDER BY pj_num DESC LIMIT 1";
        postPjRegisterReq.setPj_num(this.jdbcTemplate.queryForObject(Pj_numQuery, int.class) + 1);

        String Pj_timeQuery = "SELECT now()";
        postPjRegisterReq.setPj_time(this.jdbcTemplate.queryForObject(Pj_timeQuery, Timestamp.class));

        String registrationPjQuery = "insert into Project(pj_num, user_id, pj_views, pj_header, pj_field,    pj_content, pj_name, pj_subField, pj_progress, pj_endTerm,      pj_startTerm, pj_deadline, pj_totalPerson, pj_recruitPerson, pj_time) VALUES (?,?,?,?,?  ,?,?,?,?,?   ,?,?,?,?,?)";
        Object[] registrationParms = new Object[]
                {postPjRegisterReq.getPj_num(),
                        postPjRegisterReq.getUser_id(),
                        postPjRegisterReq.getPj_views(),
                        postPjRegisterReq.getPj_header(),
                        postPjRegisterReq.getPj_field(),
                        postPjRegisterReq.getPj_content(),
                        postPjRegisterReq.getPj_name(),
                        postPjRegisterReq.getPj_subField(),
                        postPjRegisterReq.getPj_progress(),
                        postPjRegisterReq.getPj_endTerm(),
                        postPjRegisterReq.getPj_startTerm(),
                        postPjRegisterReq.getPj_deadline(),
                        postPjRegisterReq.getPj_totalPerson(),
                        postPjRegisterReq.getPj_recruitPerson(),
                        postPjRegisterReq.getPj_time()};
        this.jdbcTemplate.update(registrationPjQuery, registrationParms);

        for (int i = 0; i < postPjRegisterReq.getKeyword().length; i++) {
            String insertKeywordQuery = "INSERT INTO Pj_keyword (pj_num, keyword) VALUES(?,?)";
            this.jdbcTemplate.update(insertKeywordQuery, postPjRegisterReq.getPj_num(), postPjRegisterReq.getKeyword()[i]);
        }

        String lastInsertPjnameQuery = postPjRegisterReq.getPj_name();
        return lastInsertPjnameQuery;
    }

    //프로젝트 수정
    public String pjModify(PatchPjModifyReq patchPjModifyReq) {
        String pjModifyQuery = "update Project set pj_header = ?, pj_field = ?, pj_content = ?, pj_name = ?, pj_subField = ?, pj_progress = ?, pj_startTerm = ?, pj_endTerm = ?, pj_deadline = ?, pj_totalPerson = ? where pj_num = ? ";
        Object[] pjModifyParms = new Object[]{
                patchPjModifyReq.getPj_header(),
                patchPjModifyReq.getPj_field(),
                patchPjModifyReq.getPj_content(),
                patchPjModifyReq.getPj_name(),
                patchPjModifyReq.getPj_subField(),
                patchPjModifyReq.getPj_progress(),
                patchPjModifyReq.getPj_startTerm(),
                patchPjModifyReq.getPj_endTerm(),
                patchPjModifyReq.getPj_deadline(),
                patchPjModifyReq.getPj_totalPerson(),
                patchPjModifyReq.getPj_num()
        };
        this.jdbcTemplate.update(pjModifyQuery, pjModifyParms);

        String deleteKeywordQuery = "delete from Pj_keyword where pj_num = ?";
        this.jdbcTemplate.update(deleteKeywordQuery, patchPjModifyReq.getPj_num());

        for (int i = 0; i < patchPjModifyReq.getKeyword().length; i++) {
            String insertKeywordQuery = "INSERT into Pj_keyword (pj_num, keyword) VALUES (?,?)";
            this.jdbcTemplate.update(insertKeywordQuery, patchPjModifyReq.getPj_num(), patchPjModifyReq.getKeyword()[i]);
        }

        return patchPjModifyReq.getPj_name();
    }

    public String pjDel(DelPjDelReq getPjDelReq) {

        String pjDelQuery = "update Project set pj_status = '삭제' where pj_num = ? ";
        this.jdbcTemplate.update(pjDelQuery, getPjDelReq.getPj_num());

        return "삭제가 완료되었습니다.";
    }

    //프로젝트 지원
    public String pjApply(PostPjApplyReq postPjApplyReq) {
        String pjApplyCoincideCheckQuery = "Select Count(*) from Pj_request where pj_num = ? and user_id = ?";

        if (this.jdbcTemplate.queryForObject(pjApplyCoincideCheckQuery, int.class, postPjApplyReq.getPj_num(), postPjApplyReq.getUser_id()) == 1) {
            return "중복";
        } else {
            String pjApplyQuery = "insert into Pj_request (user_id, pj_num, pj_inviteStatus) VALUES (?,?,'신청')";
            this.jdbcTemplate.update(pjApplyQuery, postPjApplyReq.getUser_id(), postPjApplyReq.getPj_num());
            return "신청이 완료되었습니다.";
        }
    }

    //프로젝트 신청한 유저 승인
    public String pjApprove(PatchPjApproveReq patchPjApproveReq) {
        String pjApproveQuery = "update Pj_request set pj_inviteStatus = '승인완료' where user_id = ? and pj_num = ? and pj_inviteStatus = '신청'";
        Object[] pjApproveParams = new Object[]{
                patchPjApproveReq.getUser_id(),
                patchPjApproveReq.getPj_num()
        };
        this.jdbcTemplate.update(pjApproveQuery, pjApproveParams);

        return "승인완료";
    }

    //본인이 지원한 프로젝트 신청 현황
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

    //특정 프로젝트 리스트 조회
    public List<GetApplyListRes> pjApplyList(String pj_num) {
        String pjApplyListQuery = "select User.user_id, user_nickname, user_grade, user_prPhoto from User, Pj_request where User.user_id = Pj_request.user_id and pj_num = ?";
        return this.jdbcTemplate.query(pjApplyListQuery,
                (rs, rowNum) -> new GetApplyListRes(
                        rs.getString("user_id"),
                        rs.getString("user_nickname"),
                        rs.getString("user_grade"),
                        rs.getString("user_prPhoto")),
                pj_num);
    }

    /**
     * 팀원 평가 조회
     *
     * @param user_id
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
     * 질문 등록
     *
     * @param PostEvalReq
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
     * @param PostEvalReq
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
     * @param PostEvalReq
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

    /**
     * 팀원 평가 수정
     *
     * @param PatchEvalReq
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
     * @param PatchEvalReq
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

        return this.jdbcTemplate.queryForObject(getEvalCheckQuery, (Object[]) getEvalCheckParms,Integer.class);
    }
}
