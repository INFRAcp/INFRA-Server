package com.example.demo.src.project;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.project.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;

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
        if(search.length() < 2){
            throw new BaseException(SEARCH_LENGTH_ERROR);
        }

        try {
            List<GetProjectRes> getProjectRes = projectDao.getProjectsBySearch(search);
            return getProjectRes;
        }catch (Exception exception){
            throw  new BaseException(DATABASE_ERROR);
        }
    }

    //프로젝트 키워드 조회
    public List<GetPjKeywordRes> getPj_keywords() throws BaseException{
        try{
            List<GetPjKeywordRes> getPj_keywordRes = projectDao.getPj_keywords();
            return getPj_keywordRes;
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    //버리는 카드
    public List<GetPjKeywordRes> getPj_keywordsBysearch(String search) throws BaseException{
        try{
            List<GetPjKeywordRes> getPj_keywordRes = projectDao.getPj_keywordsBysearch(search);
            return getPj_keywordRes;
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //유저가 찜한 프로젝트 조회
    public List<PostPjLikeRes> like(PostPjLikeReq postPj_likeReq) throws BaseException{
        try {
            List<PostPjLikeRes> postPj_likeRes = projectDao.getPj_num(postPj_likeReq);
            return postPj_likeRes;
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //프로젝트 null 값 체크
    private BaseResponse<PostPjRegisterRes> postPjNullCheck(@RequestBody PostPjRegisterReq postPjRegisterReq) {
        if(postPjRegisterReq.getPj_header()==null){
            return new BaseResponse<>(POST_PROJECT_EMPTY_HEADER);
        }
        if(postPjRegisterReq.getPj_field()==null){
            return new BaseResponse<>(POST_PROJECT_EMPTY_FIELD);
        }
        if(postPjRegisterReq.getPj_content()==null){
            return new BaseResponse<>(POST_PROJECT_EMPTY_CONTENT);
        }
        if(postPjRegisterReq.getPj_name()==null){
            return new BaseResponse<>(POST_PROJECT_EMPTY_NAME);
        }
        if(postPjRegisterReq.getPj_subField()==null){
            return new BaseResponse<>(POST_PROJECT_EMPTY_SUBFIELD);
        }
        if(postPjRegisterReq.getPj_progress()==null){
            return new BaseResponse<>(POST_PROJECT_EMPTY_PROGRESS);
        }
        if(postPjRegisterReq.getPj_endTerm()==null){
            return new BaseResponse<>(POST_PROJECT_EMPTY_END_TERM);
        }
        if(postPjRegisterReq.getPj_startTerm()==null){
            return new BaseResponse<>(POST_PROJECT_EMPTY_START_TERM);
        }
        if(postPjRegisterReq.getPj_deadline()==null){
            return new BaseResponse<>(POST_PROJECT_EMPTY_DEADLINE);
        }
        if(postPjRegisterReq.getPj_totalPerson()==0){
            return new BaseResponse<>(POST_PROJECT_EMPTY_TOTAL_PERSON);
        }
        return null;
    }

    public List<PostPjParticipateRes> getTeam(PostPjParticipateReq postPj_participateReq) throws BaseException{
        try{
            List<PostPjParticipateRes> postPj_participateRes = projectDao.getTeam(postPj_participateReq);
            return postPj_participateRes;
        }catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<PostPjInquiryRes> proInquiry(PostPjInquiryReq postPj_inquiryReq) throws BaseException {
        try{
            List<PostPjInquiryRes> postPj_inquiryRes = projectDao.proInquiry(postPj_inquiryReq);
            return postPj_inquiryRes;
        }catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }


}
