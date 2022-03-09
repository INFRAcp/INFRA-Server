package com.example.demo.src.project;

import com.example.demo.config.BaseException;
import com.example.demo.src.project.model.*;
import com.example.demo.utils.jwt.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

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
     * @return List 제목, 분야, 이름, 진행, 모집마감일, 전체인원, 모집인원, (모집, 마감임박), 마감 남은 일수
     * @author 한규범, 윤성식
     */
    public List<GetProjectRes> getProjects(String user_id) throws BaseException {
        try {
            List<GetProjectRes> getProjectRes = projectDao.getProjects(user_id);
            return getProjectRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 프로젝트 전체, 검색 조회
     * @param search
     * @return List 제목, 분야, 이름, 진행, 모집마감일, 전체인원, 모집인원, (모집, 마감임박), 마감 남은 일수
     * @author 한규범, 윤성식
     */
    public List<GetProjectRes> getProjectsByKeyword(String search, String user_id) throws BaseException {
        if (search.length() < 2) {
            throw new BaseException(SEARCH_LENGTH_ERROR);
        }

        try {
            List<GetProjectRes> getProjectRes = projectDao.getProjectsBySearch(search, user_id);
            return getProjectRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 유저가 찜한 프로젝트 조회
     * @param postPj_likeReq
     * @return List 프로젝트 번호, 제목, 조회수, 분야, 이름, 세부분야, 진행상황, 모집마감일, 총 모집인원, 현재 모집인원, 게시일
     * @author 한규범
     */
    public List<GetPjLikeRes> like(String user_id) throws BaseException {
        try {
            List<GetPjLikeRes> postPj_likeRes = projectDao.getPj_num(user_id);
            return postPj_likeRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 프로젝트에 참여한 팀원들 조회
     * @param pj_num
     * @return List 유저 닉네임, 유저 사진
     * @author 윤성식
     */
    public List<GetPjParticipateRes> getTeam(int pj_num) throws BaseException {
        try {
            List<GetPjParticipateRes> postPj_participateRes = projectDao.getTeam(pj_num);
            return postPj_participateRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 유저가 조회했던 프로젝트 조회
     * @param user_id
     * @return List 프로젝트 번호, 프로젝트 제목, 조회수, 프로젝트 분야, 이름, 세부분야, 진행, 마감일, 전체인원, 모집 중인 인원, 프로젝트 등록 시간
     * @author 한규범
     */
    public List<GetPjInquiryRes> proInquiry(String user_id) throws BaseException {
        try {
            List<GetPjInquiryRes> postPj_inquiryRes = projectDao.proInquiry(user_id);
            return postPj_inquiryRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 본인이 지원한 프로젝트 신청 현황
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

    /**
     * 팀원 평가 조회
     * @param passiveUser_id
     * @return List <GetEvalRes>
     * @throws BaseException
     * @author shinhyeon
     */
    public List<GetEvalRes> getEval(String passiveUser_id) throws BaseException {
        try {
            List<GetEvalRes> getEvalRes = projectDao.getEval(passiveUser_id);
            return getEvalRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 평가하는 인원의 승인 상태 조회
     * @param user_id
     * @param pj_num
     * @return String
     * @throws BaseException
     * @author shinhyeon
     */
    public String getPjInviteStatus1(String user_id, Integer pj_num) throws BaseException {
        try {
            String pj_inviteStatus = projectDao.getPjInviteStatus1(user_id, pj_num);
            return pj_inviteStatus;
        } catch (Exception exception) {
            throw new BaseException(PROJECT_EVALUATE_AUTHORITY);
        }
    }

    /**
     * 평가받는 인원의 승인 상태 조회
     * @param passiveUser_id
     * @param pj_num
     * @return String
     * @throws BaseException
     * @author shinhyeon
     */
    public String getPjInviteStatus2(String passiveUser_id, Integer pj_num) throws BaseException {
        try {
            String pj_inviteStatus = projectDao.getPjInviteStatus2(passiveUser_id, pj_num);
            return pj_inviteStatus;
        } catch (Exception exception) {
            throw new BaseException(PROJECT_MEMBER);
        }
    }

    /**
     * 팀원 평가 존재 유무
     * @param user_id
     * @param passiveUser_id
     * @param pj_num
     * @return Integer
     * @throws BaseException
     * @author shinhyeon
     */
    public Integer getEvalCheck(String user_id, String passiveUser_id, Integer pj_num) throws BaseException {
        try {
            Integer evalCheck = projectDao.getEvalCheck(user_id, passiveUser_id, pj_num);
            return evalCheck;
        } catch (Exception exception) {
            throw new BaseException(PROJECT_EVALUATE);
        }
    }

    /**
     * 프로젝트 팀장 조회
     * @param pj_num
     * @return String
     * @author shinhyeon
     */
    public String getTeamLeader(Integer pj_num){
        String teamLeader = projectDao.getTeamLeader(pj_num);
        return teamLeader;
    }

    /**
     * 카테고리 이름을 통한 번호 반환
     * @param pj_categoryName
     * @return
     * @throws BaseException
     * @author 한규범
     */
    public String getPjCategoryNum(String pj_categoryName) throws BaseException {
        try {
            return projectDao.getPjCategoryNum(pj_categoryName);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 세부 카테고리 이름을 통한 번호 반환
     * @param pj_subCategoryName
     * @return
     * @throws BaseException
     * @author 한규범
     */
    public String getPjSubCategoryNum(String pj_subCategoryName) throws BaseException {
        try {
            return projectDao.getPjsubCategoryNum(pj_subCategoryName);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 유저 등급 조회
     * @param user_id
     * @return float
     * @throws BaseException
     * @author shinhyeon
     */
    public float getGrade(String user_id) throws BaseException {
        try{
            return projectDao.getGrade(user_id);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 프로젝트 찜여부 반환 메서드
     * @param pj_num
     * @param user_id
     * @return int 형 찜했으면 1, 안했으면 0
     * @throws BaseException
     * @author 한규범
     */
    public int checkPjLike(int pj_num, String user_id) throws BaseException{
        try{
            return projectDao.checkPjLike(pj_num, user_id);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public String[] getHashtag(int pj_num) throws BaseException{
        try {
            return projectDao.getHashtag(pj_num);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    /**
     * 프로젝트 사진 경로 조회
     * @param pj_num
     * @author shinhyeon
     * @return List<String>
     */
    public List<String> getPjPhoto(int pj_num) {
        List<String> photos = projectDao.getPjPhoto(pj_num);
        if(photos.isEmpty()) photos.add("https://infra-infra-bucket.s3.ap-northeast-2.amazonaws.com/pjphoto/infra_project.png");
        return photos;
    }

    /**
     * 인기 프로젝트 조회 (지금 핫한 프로젝트)
     * @param user_id
     * @return List<GetHotProjectRes>
     * @throws BaseException
     * @author shinhyeon
     */
    public List<GetHotProjectRes> getProjectsBy1DayViews(String user_id) throws BaseException {
        try {
            List<GetHotProjectRes> getHotProjectRes = projectDao.getProjectsBy1DayViews(user_id);
            return getHotProjectRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 프로젝트 추천 조회 (이런 프로젝트는 어떠세요?)
     * @param user_id
     * @return List<GetHotProjectRes>
     * @throws BaseException
     * @author shinhyeon
     */
    public List<GetHotProjectRes> getRecommendProjects(String user_id) throws BaseException {
        // 관심분야 설정 여부 확인
        int user_interestCategory = projectDao.checkInterestCategory(user_id);
        if(user_interestCategory == 0) throw new BaseException(EMPTY_INTEREST_CATEGORY);

        try {
            List<GetHotProjectRes> getHotProjectRes = projectDao.getRecommendProjects(user_id);
            return getHotProjectRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
