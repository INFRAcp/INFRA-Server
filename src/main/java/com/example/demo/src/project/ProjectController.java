package com.example.demo.src.project;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.project.model.*;
import com.example.demo.src.s3.S3Service;
import com.example.demo.utils.JwtService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    public BaseResponse<List<GetProjectRes>> getProjects(@RequestParam(required = false) String search, String user_id) {
        try {
            if (search == null) {
                projectService.userIdJwt(user_id, jwtService.getUserId());
                List<GetProjectRes> getProjectRes = projectProvider.getProjects();
                projectService.recruit(getProjectRes);
                // 프로젝트 사진 조회
                for(int i=0;i<getProjectRes.size();i++)
                {
                    List<String> photos = projectProvider.getPjPhoto(getProjectRes.get(i).getPj_num());
                    getProjectRes.get(i).setPj_photo(photos);
                }
                return new BaseResponse<>(getProjectRes);
            }
            projectService.userIdJwt(user_id, jwtService.getUserId());
            List<GetProjectRes> getProjectRes = projectProvider.getProjectsByKeyword(search);
            projectService.recruit(getProjectRes);
            // 프로젝트 사진 조회
            for(int i=0;i<getProjectRes.size();i++)
            {
                List<String> photos = projectProvider.getPjPhoto(getProjectRes.get(i).getPj_num());
                getProjectRes.get(i).setPj_photo(photos);
            }
            return new BaseResponse<>(getProjectRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
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
    public BaseResponse<List<GetPjKeywordRes>> getPj_keywords(@RequestParam(required = false) String search) {
        try {
            if (search == null) {
                List<GetPjKeywordRes> getPj_keywordRes = projectProvider.getPj_keywords();
                return new BaseResponse<>(getPj_keywordRes);
            }
            List<GetPjKeywordRes> getPj_keywordRes = projectProvider.getPj_keywordsBysearch(search);
            return new BaseResponse<>(getPj_keywordRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저가 찜한 프로젝트 조회
     *
     * @param postPj_likeReq
     * @return List 프로젝트 번호, 제목, 조회수, 분야, 이름, 세부분야, 진행상황, 모집마감일, 총 모집인원, 현재 모집인원, 게시일
     * @author 한규범
     */
    @ResponseBody
    @PostMapping("/likePj")
    public BaseResponse<List<PostPjLikeRes>> like(@RequestBody PostPjLikeReq postPj_likeReq) {
        try {
            projectService.userIdJwt(postPj_likeReq.getUser_id(), jwtService.getUserId());

            List<PostPjLikeRes> postPj_likeRes = projectProvider.like(postPj_likeReq);
            // projectProvider.getPjPhoto(postPj_likeRes); // 프로젝트 사진 조회
            return new BaseResponse<>(postPj_likeRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
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
    public BaseResponse<List<PostPjInquiryRes>> proInquiry(@RequestBody PostPjInquiryReq postPj_inquiryReq) {
        try {
            List<PostPjInquiryRes> postPj_inquiryRes = projectProvider.proInquiry(postPj_inquiryReq);
            return new BaseResponse<>(postPj_inquiryRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
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
    public BaseResponse<List<PostPjParticipateRes>> getTeam(@RequestBody PostPjParticipateReq postPj_participateReq) {
        try {
            List<PostPjParticipateRes> postPj_participateRes = projectProvider.getTeam(postPj_participateReq);
            if(postPj_participateRes == null){
                throw new BaseException(POST_PROJECT_GETTEAM_NULL);
            }
            else {
                return new BaseResponse<>(postPj_participateRes);
            }
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 프로젝트 등록
     * 다중 파일 업르드 (form-data<image, json>)
     * @param postPjRegisterReq
     * @return PostPjRegisterRes 프로젝트 이름
     * @author 한규범 강신현(s3)
     */
    @ResponseBody
    @PostMapping("/registration")
    public BaseResponse<PostPjRegisterRes> pjRegistration(@RequestParam("jsonList") String jsonList, @RequestPart("images") MultipartFile[] MultipartFiles) throws IOException {
        try {
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            PostPjRegisterReq postPjRegisterReq = objectMapper.readValue(jsonList, new TypeReference<PostPjRegisterReq>() {});

            projectService.userIdJwt(postPjRegisterReq.getUser_id(), jwtService.getUserId());
            projectService.PjDateCheck(postPjRegisterReq.getPj_deadline(), postPjRegisterReq.getPj_startTerm(), postPjRegisterReq.getPj_endTerm());
            projectService.PjNullCheck(postPjRegisterReq.getPj_header(), postPjRegisterReq.getPj_categoryName(), postPjRegisterReq.getPj_content(), postPjRegisterReq.getPj_name(), postPjRegisterReq.getPj_subCategoryName(), postPjRegisterReq.getPj_progress(), postPjRegisterReq.getPj_endTerm(), postPjRegisterReq.getPj_startTerm(), postPjRegisterReq.getPj_deadline(), postPjRegisterReq.getPj_totalPerson());
            projectService.PjKeywordCheck(postPjRegisterReq.getHashtag());
            postPjRegisterReq.setPj_categoryNum(projectProvider.getPjCategoryNum(postPjRegisterReq.getPj_categoryName()));
            postPjRegisterReq.setPj_subCategoryNum(projectProvider.getPjSubCategoryNum(postPjRegisterReq.getPj_subCategoryName()));
            PostPjRegisterRes postPjRegisterRes = projectService.registrationPj(postPjRegisterReq);

            for(int i=0;i<MultipartFiles.length;i++){ // 다중 이미지 파일
                // s3에 업로드
                int pj_num = postPjRegisterReq.getPj_num();
                String s3path = "pjphoto/pj_num : " + Integer.toString(pj_num);
                String imgPath = s3Service.uploadPrphoto(MultipartFiles[i], s3path);
                // db에 반영 (Pj_photo)
                s3Service.uploadPjPhoto(imgPath, pj_num);
            }

            return new BaseResponse<>(postPjRegisterRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
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
    public BaseResponse<PatchPjModifyRes> pjModify(@RequestBody PatchPjModifyReq patchPjModifyReq) {
        try {
            projectService.PjDateCheck(patchPjModifyReq.getPj_deadline(), patchPjModifyReq.getPj_startTerm(), patchPjModifyReq.getPj_endTerm());
            projectService.PjNullCheck(patchPjModifyReq.getPj_header(), patchPjModifyReq.getPj_categoryNum(), patchPjModifyReq.getPj_content(), patchPjModifyReq.getPj_name(), patchPjModifyReq.getPj_subCategoryNum(), patchPjModifyReq.getPj_progress(), patchPjModifyReq.getPj_endTerm(), patchPjModifyReq.getPj_startTerm(), patchPjModifyReq.getPj_deadline(), patchPjModifyReq.getPj_totalPerson());
            projectService.PjKeywordCheck(patchPjModifyReq.getHashtag());
            PatchPjModifyRes patchPjModifyRes = projectService.pjModify(patchPjModifyReq);
            return new BaseResponse<>(patchPjModifyRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
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
    public BaseResponse<DelPjDelRes> pjDel(@RequestBody DelPjDelReq delPjDelReq) {
        try {
            DelPjDelRes delpjDelRes = projectService.pjDel(delPjDelReq);
            return new BaseResponse<>(delpjDelRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
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
    public BaseResponse<PostPjApplyRes> pjApply(@RequestBody PostPjApplyReq postPjApplyReq) {
        try {
            PostPjApplyRes postPjApplyRes = projectService.pjApply(postPjApplyReq);
            projectService.rejectCheck(postPjApplyRes);
            projectService.coincideCheck(postPjApplyRes);
            return new BaseResponse<>(postPjApplyRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
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
    public BaseResponse<PatchPjMemberRes> pjAcceptRequest(@RequestBody PatchPjMemberReq patchPjMemberReq) {
        if (patchPjMemberReq.getUser_id() == null || patchPjMemberReq.getPj_num() == null || patchPjMemberReq.getPj_inviteStatus() == null) {
            return new BaseResponse<>(REQUEST_EMPTY);
        }
        try {
            // jwt
            String userIdByJwt = jwtService.getUserId();

            if (patchPjMemberReq.getPj_inviteStatus().equals("강퇴")) {
                PatchPjMemberRes patchPjMemberRes = projectService.pjKickOut(patchPjMemberReq, userIdByJwt);
                return new BaseResponse<>(patchPjMemberRes);
            }

            PatchPjMemberRes patchPjMemberRes = projectService.pjAcceptRequest(patchPjMemberReq, userIdByJwt);
            return new BaseResponse<>(patchPjMemberRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
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
    public BaseResponse<List<GetApplyListRes>> pjApplyList(@RequestParam(required = false) String pj_num) {
        try {
            List<GetApplyListRes> getApplyListRes = projectProvider.pjApplyList(pj_num);
            if(getApplyListRes == null){
                throw new BaseException(GET_PROJECT_APPLY_LIST_NULL);
            }
            return new BaseResponse<>(getApplyListRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
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
    public BaseResponse<List<PostUserApplyRes>> userApply(@RequestBody PostUserApplyReq postUserApplyReq) {
        try {
            List<PostUserApplyRes> postUserApplyRes = projectProvider.getUserApply(postUserApplyReq);
            return new BaseResponse<>(postUserApplyRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
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
    public BaseResponse<PostLikeRegisterRes> likeRegister(@RequestBody PostLikeRegisterReq postLikeRegisterReq) {
        try {
            PostLikeRegisterRes postLikeRegisterRes = projectService.likeRegister(postLikeRegisterReq);
            return new BaseResponse<>(postLikeRegisterRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
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
    public BaseResponse<PostLikeRegisterRes> likeDel(@RequestBody PostLikeRegisterReq postLikeRegisterReq) {
        try {
            PostLikeRegisterRes postLikeRegisterRes = projectService.likeDel(postLikeRegisterReq);
            return new BaseResponse<>(postLikeRegisterRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
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
    public BaseResponse<List<GetEvalRes>> getEval(@RequestParam String passiveUser_id) {
        try {
            // Query String (user_id) 가 받은 평가들만 조회
            // jwt
            String userIdByJwt = jwtService.getUserId();
            if (!passiveUser_id.equals(userIdByJwt)) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<GetEvalRes> getEvalRes = projectProvider.getEval(passiveUser_id);
            return new BaseResponse<>(getEvalRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
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
    public BaseResponse<String> uploadEval(@RequestBody PostEvalReq postEvalReq) {
        if (postEvalReq.getUser_id() == null || postEvalReq.getPassiveUser_id() == null || postEvalReq.getPj_num() == null ||
                postEvalReq.getOpinion() == null || postEvalReq.getResponsibility() == null || postEvalReq.getAbility() == null ||
                postEvalReq.getTeamwork() == null || postEvalReq.getLeadership() == null) {
            return new BaseResponse<>(POST_PROJECT_EVALUATE_EMPTY);
        }

        try {
            // jwt (평가하는 user_id 와 jwt의 id 를 비교)
            String userIdByJwt = jwtService.getUserId();
            if (!postEvalReq.getUser_id().equals(userIdByJwt)) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            // 팀원 평가 등록
            projectService.uploadEval(postEvalReq);

            // 팀원 등급 등록(or 최신화)
            float grade = (float) ((postEvalReq.getResponsibility() + postEvalReq.getAbility() + postEvalReq.getTeamwork() + postEvalReq.getLeadership()) / 4.0);
            projectService.uploadGrade(postEvalReq.getPassiveUser_id(), grade);

            return new BaseResponse<>(SUCCESS);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
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
    public BaseResponse<String> modifyEval(@RequestBody PatchEvalReq patchEvalReq) {
        if (patchEvalReq.getUser_id() == null || patchEvalReq.getPassiveUser_id() == null || patchEvalReq.getPj_num() == null ||
                patchEvalReq.getOpinion() == null || patchEvalReq.getResponsibility() == null || patchEvalReq.getAbility() == null ||
                patchEvalReq.getTeamwork() == null || patchEvalReq.getLeadership() == null) {
            return new BaseResponse<>(POST_PROJECT_EVALUATE_EMPTY);
        }

        try {
            // jwt
            String userIdByJwt = jwtService.getUserId();

            if (!patchEvalReq.getUser_id().equals(userIdByJwt)) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            // 팀원 평가 수정
            projectService.modifyEval(patchEvalReq);

            // 팀원 등급 등록(or 최신화)
            float grade = (float) ((patchEvalReq.getResponsibility() + patchEvalReq.getAbility() + patchEvalReq.getTeamwork() + patchEvalReq.getLeadership()) / 4.0);
            projectService.uploadGrade(patchEvalReq.getPassiveUser_id(), grade);

            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
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
    public BaseResponse<String> delEval(@RequestBody PatchEvalDelReq patchEvalDelReq) {
        if (patchEvalDelReq.getUser_id() == null || patchEvalDelReq.getPassiveUser_id() == null || patchEvalDelReq.getPj_num() == null) {
            return new BaseResponse<>(POST_PROJECT_EVALUATE_EMPTY);
        }
        try {
            // jwt
            String userIdByJwt = jwtService.getUserId();

            if (!patchEvalDelReq.getUser_id().equals(userIdByJwt)) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            // 팀원 평가 삭제
            projectService.delEval(patchEvalDelReq);

            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
