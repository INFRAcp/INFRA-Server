package com.example.demo.src.project;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.project.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ProjectProvider {

    private final ProjectDao projectDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ProjectProvider(ProjectDao projectDao, JwtService jwtService){
        this.projectDao = projectDao;
        this.jwtService = jwtService;
    }

    //Project들의 정보 조회
    public List<GetProjectRes> getProjects() throws BaseException {
        try{
            List<GetProjectRes> getProjectRes = projectDao.getProjects();
            return getProjectRes;
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    //검색 프로젝트 정보 조회
    public List<GetProjectRes> getProjectsByKeyword(String search) throws BaseException{
        try {
            List<GetProjectRes> getProjectRes = projectDao.getProjectsBySearch(search);
            return getProjectRes;
        }catch (Exception exception){
            throw  new BaseException(DATABASE_ERROR);
        }
    }

    //프로젝트 키워드 조회
    public List<GetPj_keywordRes> getPj_keywords() throws BaseException{
        try{
            List<GetPj_keywordRes> getPj_keywordRes = projectDao.getPj_keywords();
            return getPj_keywordRes;
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    //버리는 카드
    public List<GetPj_keywordRes> getPj_keywordsBysearch(String search) throws BaseException{
        try{
            List<GetPj_keywordRes> getPj_keywordRes = projectDao.getPj_keywordsBysearch(search);
            return getPj_keywordRes;
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //유저가 찜한 프로젝트 조회
    public PostPj_likeRes like(PostPj_likeReq postPj_likeReq) throws BaseException{
        Project project = projectDao.getPj_num(postPj_likeReq);
        int pj_num = project.getPj_num();
        String pj_name = project.getPj_name();
        String pj_header = project.getPj_header();
        String pj_field = project.getPj_field();
        String pj_subField = project.getPj_subField();
        String pj_getPj_progress = project.getPj_progress();
        String pj_deadline = project.getPj_deadline();
        int pj_total_person = project.getPj_total_person();
        int pj_recruit_person = project.getPj_recruit_person();
        int pj_views = project.getPj_views();
        String pj_time = project.getPj_time();
        return new PostPj_likeRes(
                pj_num,
                pj_header,
                pj_views,
                pj_field,
                pj_name,
                pj_subField,
                pj_getPj_progress,
                pj_deadline,
                pj_total_person,
                pj_recruit_person,
                pj_time
        );

    }
}
