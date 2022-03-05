package com.example.demo.src.project;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.project.model.*;
import com.example.demo.src.s3.S3Service;
import com.example.demo.utils.jwt.JwtService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/project")
public class ProjectController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ProjectProvider projectProvider;
    @Autowired
    private final ProjectService projectService;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final S3Service s3Service;

    public ProjectController(ProjectProvider projectProvider, ProjectService projectService, JwtService jwtService, S3Service s3Service) {
        this.projectProvider = projectProvider;
        this.projectService = projectService;
        this.jwtService = jwtService;
        this.s3Service = s3Service;
    }

    /**
     * 프로젝트 전체, 검색 조회
     *
     * @param search
     * @return List 제목, 분야, 이름, 진행, 모집마감일, 전체인원, 모집인원, (모집, 마감임박), 마감 남은 일수, 사진
     * @author 한규범, 윤성식, shinhyeon
     */
    @ResponseBody
    @GetMapping("/inquiry")
    public BaseResponse<List<GetProjectRes>> getProjects(@RequestParam(required = false) String search, String user_id) throws BaseException {
            jwtService.JwtEffectiveness(user_id, jwtService.getUserId());
            if (search == null) {
                List<GetProjectRes> getProjectRes = projectProvider.getProjects(user_id);
                projectService.recruit(getProjectRes);

                for (int i = 0; i < getProjectRes.size(); i++) {
                    getProjectRes.get(i).setPj_like(projectProvider.checkPjLike(getProjectRes.get(i).getPj_num(), user_id));
                    getProjectRes.get(i).setHashtag(projectProvider.getHashtag(getProjectRes.get(i).getPj_num()));
                }

                // 프로젝트 사진 조회
                for (int i = 0; i < getProjectRes.size(); i++) {
                    List<String> photos = projectProvider.getPjPhoto(getProjectRes.get(i).getPj_num());
                    getProjectRes.get(i).setPj_photo(photos);
                }
                return new BaseResponse<>(getProjectRes);
            }
            // 키워드 있는 검색
            List<GetProjectRes> getProjectRes = projectProvider.getProjectsByKeyword(search, user_id);
            projectService.recruit(getProjectRes);

            for (int i = 0; i < getProjectRes.size(); i++) {
                getProjectRes.get(i).setPj_like(projectProvider.checkPjLike(getProjectRes.get(i).getPj_num(), user_id));
                getProjectRes.get(i).setHashtag(projectProvider.getHashtag(getProjectRes.get(i).getPj_num()));
            }

            // 프로젝트 사진 조회
            for (int i = 0; i < getProjectRes.size(); i++) {
                List<String> photos = projectProvider.getPjPhoto(getProjectRes.get(i).getPj_num());
                getProjectRes.get(i).setPj_photo(photos);
            }
            return new BaseResponse<>(getProjectRes);
    }

    /**
     * @param getProjectRes
     * @author 한규범
     */
    public void recruit(List<GetProjectRes> getProjectRes) {
        for (int i = 0; i < getProjectRes.size(); i++) {
            if (getProjectRes.get(i).getPj_daysub() <= 2 && getProjectRes.get(i).getPj_daysub() >= 0) {
                getProjectRes.get(i).setPj_recruit("마감임박");
            }
        }
    }

    /**
     * 프로젝트 키워드 조회
     *
     * @param search
     * @return List 프로젝트 번호, 키워드
     * @author 한규범, 윤성식
     */
    @ResponseBody
    @GetMapping("/keyword")
    public BaseResponse<List<GetPjKeywordRes>> getPj_keywords(@RequestParam(required = false) String user_id, String search) throws BaseException{
            jwtService.JwtEffectiveness(user_id, jwtService.getUserId());
            if (search == null) {
                List<GetPjKeywordRes> getPj_keywordRes = projectProvider.getPj_keywords();
                return new BaseResponse<>(getPj_keywordRes);
            }
            List<GetPjKeywordRes> getPj_keywordRes = projectProvider.getPj_keywordsBysearch(search);
            return new BaseResponse<>(getPj_keywordRes);
    }

    /**
     * 유저가 찜한 프로젝트 조회
     *
     * @param postPj_likeReq
     * @return List 프로젝트 번호, 제목, 조회수, 분야, 이름, 세부분야, 진행상황, 모집마감일, 총 모집인원, 현재 모집인원, 게시일
     * @author 한규범
     */
    @ResponseBody
    @PostMapping("/like-pj")
    public BaseResponse<List<PostPjLikeRes>> like(@RequestBody PostPjLikeReq postPj_likeReq) throws BaseException{
            jwtService.JwtEffectiveness(postPj_likeReq.getUser_id(), jwtService.getUserId());

            List<PostPjLikeRes> postPj_likeRes = projectProvider.like(postPj_likeReq);
            // 프로젝트 사진 조회
            for (int i = 0; i < postPj_likeRes.size(); i++) {
                List<String> photos = projectProvider.getPjPhoto(postPj_likeRes.get(i).getPj_num());
                postPj_likeRes.get(i).setPj_photo(photos);
            }
            return new BaseResponse<>(postPj_likeRes);
    }

    /**
     * 유저가 조회했던 프로젝트 조회
     *
     * @param postPj_inquiryReq
     * @return List 프로젝트 번호, 프로젝트 제목, 조회수, 프로젝트 분야, 이름, 세부분야, 진행, 마감일, 전체인원, 모집 중인 인원, 프로젝트 등록 시간
     * @author 한규범
     */
    @ResponseBody
    @PostMapping("/project-inquiry")
    public BaseResponse<List<PostPjInquiryRes>> proInquiry(@RequestBody PostPjInquiryReq postPj_inquiryReq) throws BaseException{
            jwtService.JwtEffectiveness(postPj_inquiryReq.getUser_id(), jwtService.getUserId());

            List<PostPjInquiryRes> postPj_inquiryRes = projectProvider.proInquiry(postPj_inquiryReq);
            // 프로젝트 사진 조회
            for (int i = 0; i < postPj_inquiryRes.size(); i++) {
                List<String> photos = projectProvider.getPjPhoto(postPj_inquiryRes.get(i).getPj_num());
                postPj_inquiryRes.get(i).setPj_photo(photos);
            }
            return new BaseResponse<>(postPj_inquiryRes);
    }

    /**
     * 프로젝트에 참여한 팀원들 조회
     *
     * @param postPj_participateReq
     * @return List 유저 닉네임, 유저 사진
     * @author 윤성식
     */
    @ResponseBody
    @PostMapping("/team")
    public BaseResponse<List<PostPjParticipateRes>> getTeam(@RequestBody PostPjParticipateReq postPj_participateReq, String user_id) throws BaseException{
            jwtService.JwtEffectiveness(user_id, jwtService.getUserId());

            List<PostPjParticipateRes> postPj_participateRes = projectProvider.getTeam(postPj_participateReq);
            if (postPj_participateRes == null) {
                throw new BaseException(POST_PROJECT_GETTEAM_NULL);
            } else {
                return new BaseResponse<>(postPj_participateRes);
            }
    }

    /**
     * 프로젝트 등록
     * 다중 파일 업르드 (form-data<image, json>)
     *
     * @param MultipartFiles
     * @return PostPjRegisterRes 프로젝트 이름
     * @author 한규범 강신현(s3)
     */
    @ResponseBody
    @PostMapping("/registration")
    public BaseResponse<PostPjRegisterRes> pjRegistration(@RequestParam("jsonList") String jsonList, @RequestPart(value = "images", required = false) MultipartFile[] MultipartFiles) throws IOException, BaseException{
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            PostPjRegisterReq postPjRegisterReq = objectMapper.readValue(jsonList, new TypeReference<PostPjRegisterReq>() {
            });

            jwtService.JwtEffectiveness(postPjRegisterReq.getUser_id(), jwtService.getUserId());
            PostPjRegisterRes postPjRegisterRes = projectService.registrationPj(postPjRegisterReq);

            if(MultipartFiles != null)
            {
                for(int i = 0; i < MultipartFiles.length; i++) { // 다중 이미지 파일
                    // s3에 업로드
                    int pj_num = postPjRegisterReq.getPj_num();
                    String s3path = "pjphoto/pj_num : " + Integer.toString(pj_num);
                    String imgPath = s3Service.uploadPrphoto(MultipartFiles[i], s3path);
                    // db에 반영 (Pj_photo)
                    s3Service.uploadPjPhoto(imgPath, pj_num);
                }
            }
            return new BaseResponse<>(postPjRegisterRes);
    }

    /**
     * 프로젝트 수정
     *
     * @param patchPjModifyReq
     * @return PatchPjModifyRes 프로젝트 이름
     * @author 한규범
     */
    @ResponseBody
    @PatchMapping("/modify")
    public BaseResponse<PatchPjModifyRes> pjModify(@RequestBody PatchPjModifyReq patchPjModifyReq) throws BaseException{
            jwtService.JwtEffectiveness(patchPjModifyReq.getUser_id(), jwtService.getUserId());
            PatchPjModifyRes patchPjModifyRes = projectService.pjModify(patchPjModifyReq);
            return new BaseResponse<>(patchPjModifyRes);
    }

    /**
     * 프로젝트 삭제
     *
     * @param delPjDelReq
     * @return DelPjDelRes 결과 메시지
     * @author 한규범
     */
    @ResponseBody
    @DeleteMapping("/del")
    public BaseResponse<DelPjDelRes> pjDel(@RequestBody DelPjDelReq delPjDelReq) throws BaseException{
            jwtService.JwtEffectiveness(delPjDelReq.getUser_id(), jwtService.getUserId());
            DelPjDelRes delpjDelRes = projectService.pjDel(delPjDelReq);
            return new BaseResponse<>(delpjDelRes);
    }


    /**
     * 프로젝트 지원
     *
     * @param postPjApplyReq
     * @return PostPjApplyRes 완료 메시지
     * @author 한규범
     */
    @ResponseBody
    @PostMapping("/apply")
    public BaseResponse<PostPjApplyRes> pjApply(@RequestBody PostPjApplyReq postPjApplyReq) throws BaseException{
            PostPjApplyRes postPjApplyRes = projectService.pjApply(postPjApplyReq);
            projectService.rejectCheck(postPjApplyRes);
            projectService.coincideCheck(postPjApplyRes);
            return new BaseResponse<>(postPjApplyRes);

    }

    /**
     * 프로젝트 신청한 유저 승인, 거절 / 팀원 강퇴
     *
     * @param patchPjMemberReq
     * @return PatchPjApproveRes 완료 메시지
     * @author shinhyeon
     */
    @ResponseBody
    @PatchMapping("/member")
    public BaseResponse<PatchPjMemberRes> pjAcceptRequest(@RequestBody PatchPjMemberReq patchPjMemberReq) throws BaseException{
        if (patchPjMemberReq.getUser_id() == null || patchPjMemberReq.getPj_num() == null || patchPjMemberReq.getPj_inviteStatus() == null) {
            return new BaseResponse<>(REQUEST_EMPTY);
        }
            String teamLeader = projectProvider.getTeamLeader(patchPjMemberReq.getPj_num());
            jwtService.JwtEffectiveness(teamLeader, jwtService.getUserId());

            if (patchPjMemberReq.getPj_inviteStatus().equals("강퇴")) {
                PatchPjMemberRes patchPjMemberRes = projectService.pjKickOut(patchPjMemberReq, jwtService.getUserId());
                return new BaseResponse<>(patchPjMemberRes);
            }

            PatchPjMemberRes patchPjMemberRes = projectService.pjAcceptRequest(patchPjMemberReq, jwtService.getUserId());
            return new BaseResponse<>(patchPjMemberRes);

    }


    /**
     * 프로젝트 신청 현황
     *
     * @param pj_num
     * @return List 유저ID, 유저 평점, 유저 사진, 프로젝트 번호
     * @author 윤성식
     */
    @ResponseBody
    @GetMapping("/apply-list")
    public BaseResponse<List<GetApplyListRes>> pjApplyList(@RequestParam(required = false) String pj_num, String user_id) throws BaseException{
            jwtService.JwtEffectiveness(user_id, jwtService.getUserId());
            List<GetApplyListRes> getApplyListRes = projectProvider.pjApplyList(pj_num);
            if (getApplyListRes == null) {
                throw new BaseException(GET_PROJECT_APPLY_LIST_NULL);
            }
            return new BaseResponse<>(getApplyListRes);
    }

    /**
     * 본인이 지원한 프로젝트 신청 현황
     *
     * @param postUserApplyReq
     * @return List 프로젝트 번호, 참여 상태, 프로젝트 이름, 조회수, 프로젝트 제목
     * @author 윤성식
     */
    @ResponseBody
    @PostMapping("/apply-mylist")
    public BaseResponse<List<PostUserApplyRes>> userApply(@RequestBody PostUserApplyReq postUserApplyReq) throws BaseException{
            jwtService.JwtEffectiveness(postUserApplyReq.getUser_id(), jwtService.getUserId());

            List<PostUserApplyRes> postUserApplyRes = projectProvider.getUserApply(postUserApplyReq);
            return new BaseResponse<>(postUserApplyRes);
    }

    /**
     * 프로젝트 찜 등록
     *
     * @param postLikeRegisterReq
     * @return 등록 완료된 메세지
     * @author 윤성식
     */
    @ResponseBody
    @PostMapping("/like")
    public BaseResponse<PostLikeRegisterRes> likeRegister(@RequestBody PostLikeRegisterReq postLikeRegisterReq) throws BaseException{
            jwtService.JwtEffectiveness(postLikeRegisterReq.getUser_id(), jwtService.getUserId());
            PostLikeRegisterRes postLikeRegisterRes = projectService.likeRegister(postLikeRegisterReq);
            return new BaseResponse<>(postLikeRegisterRes);
    }

    /**
     * 프로젝트 찜 삭제
     *
     * @param postLikeRegisterReq
     * @return 찜 삭제된 메세지
     * @author 윤성식
     */
    @ResponseBody
    @DeleteMapping("/like-del")
    public BaseResponse<PostLikeRegisterRes> likeDel(@RequestBody PostLikeRegisterReq postLikeRegisterReq) throws BaseException{
            jwtService.JwtEffectiveness(postLikeRegisterReq.getUser_id(), jwtService.getUserId());
            PostLikeRegisterRes postLikeRegisterRes = projectService.likeDel(postLikeRegisterReq);
            return new BaseResponse<>(postLikeRegisterRes);
    }


    /**
     * [GET] /project/evaluate?passiveUser_id=
     * 팀원 평가 조회 API
     *
     * @param passiveUser_id
     * @return List <평가한 id, 평가 받은 id, 프로젝트 num, 의견, 책임감, 역량, 팀워크, 리더쉽>
     * @author shinhyeon
     */

    @ResponseBody
    @GetMapping("/evaluate")
    public BaseResponse<List<GetEvalRes>> getEval(@RequestParam String passiveUser_id) throws BaseException{
            // Query String (user_id) 가 받은 평가들만 조회
            jwtService.JwtEffectiveness(passiveUser_id, jwtService.getUserId());
            List<GetEvalRes> getEvalRes = projectProvider.getEval(passiveUser_id);
            return new BaseResponse<>(getEvalRes);
    }

    /**
     * [POST] /project/evaluate
     * 팀원 평가 등록 API
     *
     * @param postEvalReq
     * @return String
     * @author shinhyeon
     */

    @ResponseBody
    @PostMapping("/evaluate")
    public BaseResponse<String> uploadEval(@RequestBody PostEvalReq postEvalReq) throws BaseException{
        if (postEvalReq.getUser_id() == null || postEvalReq.getPassiveUser_id() == null || postEvalReq.getPj_num() == null ||
                postEvalReq.getOpinion() == null || postEvalReq.getResponsibility() == null || postEvalReq.getAbility() == null ||
                postEvalReq.getTeamwork() == null || postEvalReq.getLeadership() == null) {
            return new BaseResponse<>(POST_PROJECT_EVALUATE_EMPTY);
        }

            // jwt (평가하는 user_id 와 jwt의 id 를 비교)
            jwtService.JwtEffectiveness(postEvalReq.getUser_id(), jwtService.getUserId());

            // 팀원 평가 등록
            projectService.uploadEval(postEvalReq);

            // 팀원 등급 등록(or 최신화)
            float grade = (float) ((postEvalReq.getResponsibility() + postEvalReq.getAbility() + postEvalReq.getTeamwork() + postEvalReq.getLeadership()) / 4.0);
            projectService.uploadGrade(postEvalReq.getPassiveUser_id(), grade);

            return new BaseResponse<>(SUCCESS);

    }

    /**
     * [POST] /project/evaluate/modify
     * 팀원 평가 수정 API
     *
     * @param patchEvalReq
     * @return String
     * @author shinhyeon
     */

    @ResponseBody
    @PatchMapping("/evaluate/modify")
    public BaseResponse<String> modifyEval(@RequestBody PatchEvalReq patchEvalReq) throws BaseException{
        if (patchEvalReq.getUser_id() == null || patchEvalReq.getPassiveUser_id() == null || patchEvalReq.getPj_num() == null ||
                patchEvalReq.getOpinion() == null || patchEvalReq.getResponsibility() == null || patchEvalReq.getAbility() == null ||
                patchEvalReq.getTeamwork() == null || patchEvalReq.getLeadership() == null) {
            return new BaseResponse<>(POST_PROJECT_EVALUATE_EMPTY);
        }

            // jwt
            jwtService.JwtEffectiveness(patchEvalReq.getUser_id(), jwtService.getUserId());

            // 팀원 평가 수정
            projectService.modifyEval(patchEvalReq);

            // 팀원 등급 등록(or 최신화)
            float grade = (float) ((patchEvalReq.getResponsibility() + patchEvalReq.getAbility() + patchEvalReq.getTeamwork() + patchEvalReq.getLeadership()) / 4.0);
            projectService.uploadGrade(patchEvalReq.getPassiveUser_id(), grade);

            return new BaseResponse<>(SUCCESS);

    }

    /**
     * [POST] /project/evaluate/del
     * 팀원 평가 삭제 API
     *
     * @param patchEvalDelReq
     * @return String
     * @author shinhyeon
     */

    @ResponseBody
    @PatchMapping("/evaluate/del")
    public BaseResponse<String> delEval(@RequestBody PatchEvalDelReq patchEvalDelReq) throws BaseException{
        if (patchEvalDelReq.getUser_id() == null || patchEvalDelReq.getPassiveUser_id() == null || patchEvalDelReq.getPj_num() == null) {
            return new BaseResponse<>(POST_PROJECT_EVALUATE_EMPTY);
        }
            // jwt
            jwtService.JwtEffectiveness(patchEvalDelReq.getUser_id(), jwtService.getUserId());

            // 팀원 평가 삭제
            projectService.delEval(patchEvalDelReq);

            return new BaseResponse<>(SUCCESS);

    }

    /**
     * 프로젝트 하나 접속
     * @param pj_num
     * @param user_id
     * @author 한규범
     */
    @ResponseBody
    @GetMapping("/contact")
    public BaseResponse<GetContactRes> pjContact(@RequestParam(required = false) int pj_num, String user_id) throws BaseException{
            jwtService.JwtEffectiveness(user_id, jwtService.getUserId());
            GetContactRes getContactRes = projectService.pjContact(pj_num, user_id);
            projectService.plusViews(pj_num, user_id);

            return new BaseResponse<>(getContactRes);
    }
}
