package com.example.demo.src.project;

import com.example.demo.config.BaseException;
import com.example.demo.src.project.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.SEARCH_LENGTH_ERROR;

@Service
public class ProjectProvider {

    private final ProjectDao projectDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ProjectProvider(ProjectDao projectDao, JwtService jwtService) {
        this.projectDao = projectDao;
        this.jwtService = jwtService;
    }

    /**
     * 프로젝트 전체, 검색 조회
     *
     * @return List 제목, 분야, 이름, 진행, 모집마감일, 전체인원, 모집인원, (모집, 마감임박), 마감 남은 일수
     * @author 한규범, 윤성식
     */
    public List<GetProjectRes> getProjects() throws BaseException {
        try {
            List<GetProjectRes> getProjectRes = projectDao.getProjects();
            return getProjectRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 프로젝트 전체, 검색 조회
     *
     * @param search
     * @return List 제목, 분야, 이름, 진행, 모집마감일, 전체인원, 모집인원, (모집, 마감임박), 마감 남은 일수
     * @author 한규범, 윤성식
     */
    public List<GetProjectRes> getProjectsByKeyword(String search) throws BaseException {
        if (search.length() < 2) {
            throw new BaseException(SEARCH_LENGTH_ERROR);
        }

        try {
            List<GetProjectRes> getProjectRes = projectDao.getProjectsBySearch(search);
            return getProjectRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 프로젝트 키워드 조회
     *
     * @return List 프로젝트 번호, 키워드
     * @author 한규범, 윤성식
     */
    public List<GetPjKeywordRes> getPj_keywords() throws BaseException {
        try {
            List<GetPjKeywordRes> getPj_keywordRes = projectDao.getPj_keywords();
            return getPj_keywordRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 프로젝트 키워드 조회
     *
     * @param search
     * @return List 프로젝트 번호, 키워드
     * @author 한규범, 윤성식
     */
    public List<GetPjKeywordRes> getPj_keywordsBysearch(String search) throws BaseException {
        try {
            List<GetPjKeywordRes> getPj_keywordRes = projectDao.getPj_keywordsBysearch(search);
            return getPj_keywordRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 유저가 찜한 프로젝트 조회
     *
     * @param postPj_likeReq
     * @return List 프로젝트 번호, 제목, 조회수, 분야, 이름, 세부분야, 진행상황, 모집마감일, 총 모집인원, 현재 모집인원, 게시일
     * @author 한규범
     */
    public List<PostPjLikeRes> like(PostPjLikeReq postPj_likeReq) throws BaseException {
        try {
            List<PostPjLikeRes> postPj_likeRes = projectDao.getPj_num(postPj_likeReq);
            return postPj_likeRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 프로젝트에 참여한 팀원들 조회
     *
     * @param postPj_participateReq
     * @return List 유저 닉네임, 유저 사진
     * @author 윤성식
     */
    public List<PostPjParticipateRes> getTeam(PostPjParticipateReq postPj_participateReq) throws BaseException {
        try {
            List<PostPjParticipateRes> postPj_participateRes = projectDao.getTeam(postPj_participateReq);
            return postPj_participateRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 유저가 조회했던 프로젝트 조회
     *
     * @param postPj_inquiryReq
     * @return List 프로젝트 번호, 프로젝트 제목, 조회수, 프로젝트 분야, 이름, 세부분야, 진행, 마감일, 전체인원, 모집 중인 인원, 프로젝트 등록 시간
     * @author 한규범
     */
    public List<PostPjInquiryRes> proInquiry(PostPjInquiryReq postPj_inquiryReq) throws BaseException {
        try {
            List<PostPjInquiryRes> postPj_inquiryRes = projectDao.proInquiry(postPj_inquiryReq);
            return postPj_inquiryRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 본인이 지원한 프로젝트 신청 현황
     *
     * @param postUserApplyReq
     * @return List 프로젝트 번호, 참여 상태, 프로젝트 이름, 조회수, 프로젝트 제목
     * @author 윤성식
     */
    public List<PostUserApplyRes> getUserApply(PostUserApplyReq postUserApplyReq) throws BaseException {
        try {
            List<PostUserApplyRes> postUserApplyRes = projectDao.getUserApply(postUserApplyReq);
            return postUserApplyRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 프로젝트 신청 현황
     *
     * @param pj_num
     * @return List 유저ID, 유저 평점, 유저 사진, 프로젝트 번호
     * @author 윤성식
     */
    public List<GetApplyListRes> pjApplyList(String pj_num) throws BaseException {
        try {
            List<GetApplyListRes> getApplyListRes = projectDao.pjApplyList(pj_num);
            return getApplyListRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

//    public List<GetProjectRes> getPjCategory(List<GetProjectRes> getProjectRes) throws BaseException{
//        try {
//            List<GetProjectRes> getProjectResList = projectDao.getPjCategory(getProjectRes);
//            return getProjectResList;
//        }catch (Exception exception){
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
}
