package com.example.demo.src.project;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.project.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Repository
public class ProjectDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //프로젝트 조회
    public List<GetProjectRes> getProjects() {
        String getProjectQuery = "select Project.pj_num, User_id, pj_views, pj_header, pj_field, pj_content, pj_name, pj_subField, pj_progress, pj_endTerm,pj_startTerm, pj_deadline, pj_totalPerson,pj_recruitPerson, pj_time from Project";
        return this.jdbcTemplate.query(getProjectQuery,
                (rs, rowNum) -> new GetProjectRes(
                        rs.getString("pj_header"),
                        rs.getString("pj_field"),
                        rs.getString("pj_name"),
                        rs.getString("pj_progress"),
                        rs.getString("pj_deadline"),
                        rs.getInt("pj_totalPerson"),
                        rs.getInt("pj_recruitPerson")
                )
        );
    }

    //검색 프로젝트 조회
    public List<GetProjectRes> getProjectsBySearch(String search) {
        String getProjectsBySearchQuery = "select distinct pj_header, pj_field, pj_name, pj_progress, pj_deadline, pj_totalPerson, pj_recruitPerson from Project, Pj_keyword where pj_name like ? or pj_content like ? or keyword like ? or pj_subfield like ?";
        String getProjectsBySearchParams = '%' + search + '%';

        return this.jdbcTemplate.query(getProjectsBySearchQuery,
                (rs, rowNum) -> new GetProjectRes(
                        rs.getString("pj_header"),
                        rs.getString("pj_field"),
                        rs.getString("pj_name"),
                        rs.getString("pj_progress"),
                        rs.getString("pj_deadline"),
                        rs.getInt("pj_totalPerson"),
                        rs.getInt("pj_recruitPerson")),
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
        String getTeam_Query = "select User_nickname, User_prPhoto from User where User_id in (select User_id from Pj_request where pj_inviteStatus = '승인완료' and pj_num = ?)";
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
        postPjRegisterReq.setPj_num(this.jdbcTemplate.queryForObject(Pj_numQuery, int.class)+1);

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

        for(int i=0; i<postPjRegisterReq.getKeyword().length; i++){
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

        for(int i=0; i<patchPjModifyReq.getKeyword().length; i++){
            String insertKeywordQuery = "INSERT into Pj_keyword (pj_num, keyword) VALUES (?,?)";
            this.jdbcTemplate.update(insertKeywordQuery,patchPjModifyReq.getPj_num(), patchPjModifyReq.getKeyword()[i]);
        }


        return patchPjModifyReq.getPj_name();
    }

    //프로젝트 삭제
    public String pjDel(DelPjDelReq getPjDelReq) {
//        String pjKeowrdDelQuery = "delete from Pj_keyword where pj_num = ?";
//        this.jdbcTemplate.update(pjKeowrdDelQuery, getPjDelReq.getPj_num());

        String pjDelQuery = "update Project set pj_status = '삭제' where pj_num = ? ";
        this.jdbcTemplate.update(pjDelQuery, getPjDelReq.getPj_num());

        return "삭제가 완료되었습니다.";
    }

    //프로젝트 지원
    public String pjApply(PostPjApplyReq postPjApplyReq) {
        String pjApplyCoincideCheckQuery = "Select Count(*) from Pj_request where pj_num = ? and user_id = ?";

        if(this.jdbcTemplate.queryForObject(pjApplyCoincideCheckQuery, int.class, postPjApplyReq.getPj_num(), postPjApplyReq.getUser_id()) == 1){
            return "중복";
        }else{
            String pjApplyQuery = "insert into Pj_request (user_id, pj_num, pj_inviteStatus) VALUES (?,?,'신청')";
            this.jdbcTemplate.update(pjApplyQuery, postPjApplyReq.getUser_id(), postPjApplyReq.getPj_num());
            return "신청이 완료되었습니다.";
        }
    }

    //특정 프로젝트 리스트 조회
    public List<GetApplyListRes> pjApplyList(String pj_num) {
        String pjApplyListQuery = "select User.user_id, user_nickname, user_grade, user_prPhoto from User, Pj_request where User.user_id = Pj_request.user_id and pj_num = ?";
        return this.jdbcTemplate.query(pjApplyListQuery,
                (rs,rowNum) -> new GetApplyListRes(
                        rs.getString("user_id"),
                        rs.getString("user_nickname"),
                        rs.getString("user_grade"),
                        rs.getString("user_prPhoto")),
                pj_num);
    }

}
