package com.example.demo.src.project;

import com.example.demo.src.project.model.GetProjectRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;
import com.example.demo.src.project.model.*;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.sql.Timestamp;
import java.time.LocalTime;
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
        String getProjectQuery = "select Project.pj_num, User_id, pj_views, pj_header, pj_field, pj_content, pj_name, pj_subField, pj_progress, pj_end_term,pj_start_term, pj_deadline, pj_total_person,pj_recruit_person, pj_time from Project";
        return this.jdbcTemplate.query(getProjectQuery,
                (rs, rowNum) -> new GetProjectRes(
                        rs.getString("pj_header"),
                        rs.getString("pj_field"),
                        rs.getString("pj_name"),
                        rs.getString("pj_progress"),
                        rs.getString("pj_deadline"),
                        rs.getInt("pj_total_person"),
                        rs.getInt("pj_recruit_person")
                )
        );
    }

    //검색 프로젝트 조회
    public List<GetProjectRes> getProjectsBySearch(String search) {
        String getProjectsBySearchQuery = "select distinct pj_header, pj_field, pj_name, pj_progress, pj_deadline, pj_total_person, pj_recruit_person from Project, Pj_keyword where pj_name like ? or pj_content like ? or keyword like ? or pj_subfield like ?";
        String getProjectsBySearchParams = '%' + search + '%';

        return this.jdbcTemplate.query(getProjectsBySearchQuery,
                (rs, rowNum) -> new GetProjectRes(
                        rs.getString("pj_header"),
                        rs.getString("pj_field"),
                        rs.getString("pj_name"),
                        rs.getString("pj_progress"),
                        rs.getString("pj_deadline"),
                        rs.getInt("pj_total_person"),
                        rs.getInt("pj_recruit_person")),
                getProjectsBySearchParams,
                getProjectsBySearchParams,
                getProjectsBySearchParams,
                getProjectsBySearchParams);
    }

    //키워드 조회
    public List<GetPj_keywordRes> getPj_keywords() {
        String getProjectQuery = "select Project.pj_num, keyword from Pj_keyword, Project where Project.pj_num = Pj_keyword.pj_num";
        return this.jdbcTemplate.query(getProjectQuery,
                (rs, rowNum) -> new GetPj_keywordRes(
                        rs.getInt("pj_num"),
                        rs.getString("keyword")
                )
        );
    }

    //버리는 카드
    public List<GetPj_keywordRes> getPj_keywordsBysearch(String search) {
        String getProjectsBySearchQuery = "select Project.pj_num, keyword from Project, Pj_keyword where Project.pj_num = Pj_keyword.pj_num and pj_name like ? or pj_content like ? or keyword like ? or pj_subfield like ?";

        String getProjectsBySearchParams = '%' + search + '%';

        return this.jdbcTemplate.query(getProjectsBySearchQuery,
                (rs, rowNum) -> new GetPj_keywordRes(
                        rs.getInt("pj_num"),
                        rs.getString("keyword")),
                getProjectsBySearchParams,
                getProjectsBySearchParams,
                getProjectsBySearchParams,
                getProjectsBySearchParams);
    }

    //유저가 찜한 프로젝트 조회
    public List<PostPj_likeRes> getPj_num(PostPj_likeReq postPj_likeReq) {
        String getPj_numQuery = "select Project.pj_num, pj_header, pj_views, pj_field, pj_name, pj_subField, pj_progress, pj_deadline, pj_total_person, pj_recruit_person, pj_time from Project where pj_num in (select pj_num from Pj_like where User_id= ?)";
        String getParams = postPj_likeReq.getUser_id();
        return this.jdbcTemplate.query(getPj_numQuery,
                (rs, rowNum) -> new PostPj_likeRes(
                        rs.getInt("pj_num"),
                        rs.getString("pj_header"),
                        rs.getInt("pj_views"),
                        rs.getString("pj_field"),
                        rs.getString("pj_name"),
                        rs.getString("pj_subField"),
                        rs.getString("pj_progress"),
                        rs.getString("pj_deadline"),
                        rs.getInt("pj_total_person"),
                        rs.getInt("pj_recruit_person"),
                        rs.getString("pj_time")),
                getParams
        );
    }
    //프로젝트에 참여한 팀원들 조회
    public List<PostPj_participateRes> getTeam(PostPj_participateReq postPj_participateReq) {
        String getTeam_Query = "select User_nickname, User_pr_photo from User where User_id in (select User_id from Pj_request where pj_status = '승인완료' and pj_num = ?)";
        Integer getParams = postPj_participateReq.getPj_num();
        return this.jdbcTemplate.query(getTeam_Query,
                (rs, rowNum) -> new PostPj_participateRes(
                        rs.getString("User_nickname"),
                        rs.getString("User_pr_photo")),
                getParams
                );
    }

    //프로젝트 등록
    public String registrationPj(PostPjRegisterReq postPjRegisterReq) {
        String Pj_numQuery = "SELECT pj_num FROM Project ORDER BY pj_num DESC LIMIT 1";
        postPjRegisterReq.setPj_num(this.jdbcTemplate.queryForObject(Pj_numQuery, int.class)+1);

        String Pj_timeQuery = "SELECT now()";
        postPjRegisterReq.setPj_time(this.jdbcTemplate.queryForObject(Pj_timeQuery, Timestamp.class));

        String registrationPjQuery = "insert into Project(pj_num, User_id, pj_views, pj_header, pj_field, pj_content, pj_name, pj_subField, pj_progress, pj_end_term, pj_start_term, pj_deadline, pj_total_person, pj_recruit_person, pj_time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
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
                postPjRegisterReq.getPj_end_term(),
                postPjRegisterReq.getPj_start_term(),
                postPjRegisterReq.getPj_deadline(),
                postPjRegisterReq.getPj_total_person(),
                postPjRegisterReq.getPj_recruit_person(),
                postPjRegisterReq.getPj_time()};
        this.jdbcTemplate.update(registrationPjQuery, registrationParms);

        if(postPjRegisterReq.getKeyword1() != null){
            String registrationPjKeywordQuery = "insert into Pj_keyword(pj_num, keyword) VALUES (?,?)";
            Object[] registrationKeywordParms = new Object[]{
                    postPjRegisterReq.getPj_num(),
                    postPjRegisterReq.getKeyword1()
            };
            this.jdbcTemplate.update(registrationPjKeywordQuery, registrationKeywordParms);
        }
        if(postPjRegisterReq.getKeyword2() != null){
            String registrationPjKeywordQuery = "insert into Pj_keyword(pj_num, keyword) VALUES (?,?)";
            Object[] registrationKeywordParms = new Object[]{
                    postPjRegisterReq.getPj_num(),
                    postPjRegisterReq.getKeyword2()
            };
            this.jdbcTemplate.update(registrationPjKeywordQuery, registrationKeywordParms);
        }
        if(postPjRegisterReq.getKeyword3() != null){
            String registrationPjKeywordQuery = "insert into Pj_keyword(pj_num, keyword) VALUES (?,?)";
            Object[] registrationKeywordParms = new Object[]{
                    postPjRegisterReq.getPj_num(),
                    postPjRegisterReq.getKeyword3()
            };
            this.jdbcTemplate.update(registrationPjKeywordQuery, registrationKeywordParms);
        }
        if(postPjRegisterReq.getKeyword4() != null){
            String registrationPjKeywordQuery = "insert into Pj_keyword(pj_num, keyword) VALUES (?,?)";
            Object[] registrationKeywordParms = new Object[]{
                    postPjRegisterReq.getPj_num(),
                    postPjRegisterReq.getKeyword4()
            };
            this.jdbcTemplate.update(registrationPjKeywordQuery, registrationKeywordParms);
        }


        String lastInsertPjnameQuery = postPjRegisterReq.getPj_name();
        return lastInsertPjnameQuery;
//        return this.jdbcTemplate.queryForObject(lastInsertPjnameQuery, String.class);
    }
}
