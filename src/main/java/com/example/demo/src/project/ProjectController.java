package com.example.demo.src.project;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.project.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    public ProjectController(ProjectProvider projectProvider, ProjectService projectService, JwtService jwtService) {
        this.projectProvider = projectProvider;
        this.projectService = projectService;
        this.jwtService = jwtService;
    }


    //프로젝트 전체 조회
    @ResponseBody
    @GetMapping("/inquiry")
    public BaseResponse<List<GetProjectRes>> getProjects(@RequestParam(required = false) String search){
        try{
            if(search == null){
                List<GetProjectRes> getProjectRes = projectProvider.getProjects();

                for(int i=0; i<getProjectRes.size(); i++){
                    if(getProjectRes.get(i).getPj_DaySub() <= 2 && getProjectRes.get(i).getPj_DaySub() >= 0){
                        getProjectRes.get(i).setPj_recruit("마감임박");
                    }
                }

            return new BaseResponse<>(getProjectRes);
            }
            List<GetProjectRes> getProjectRes = projectProvider.getProjectsByKeyword(search);
            return new BaseResponse<>(getProjectRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //프로젝트 키워드 조회
    @ResponseBody
    @GetMapping("/keyword")
    public BaseResponse<List<GetPjKeywordRes>> getPj_keywords(@RequestParam(required = false) String search){
        try{
            if(search == null){
                List<GetPjKeywordRes> getPj_keywordRes = projectProvider.getPj_keywords();
                return new BaseResponse<>(getPj_keywordRes);
            }
            List<GetPjKeywordRes> getPj_keywordRes = projectProvider.getPj_keywordsBysearch(search);
            return new BaseResponse<>(getPj_keywordRes);
        }catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //유저가 찜한 프로젝트 조회
    @ResponseBody
    @PostMapping("/likePj")
    public BaseResponse<List<PostPjLikeRes>> like(@RequestBody PostPjLikeReq postPj_likeReq){
        try{
            List<PostPjLikeRes> postPj_likeRes = projectProvider.like(postPj_likeReq);
            return new BaseResponse<>(postPj_likeRes);
        }catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    //유저가 조회했던 프로젝트 조회
    @ResponseBody
    @PostMapping("/project-inquiry")
    public BaseResponse<List<PostPjInquiryRes>> proInquiry(@RequestBody PostPjInquiryReq postPj_inquiryReq){
        try{
            List<PostPjInquiryRes> postPj_inquiryRes = projectProvider.proInquiry(postPj_inquiryReq);
            return new BaseResponse<>(postPj_inquiryRes);
        }catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    //프로젝트에 참여한 팀원들 조회
    @ResponseBody
    @PostMapping("/team")
    public BaseResponse<List<PostPjParticipateRes>> getTeam(@RequestBody PostPjParticipateReq postPj_participateReq){
        try{
            List<PostPjParticipateRes> postPj_participateRes = projectProvider.getTeam(postPj_participateReq);
            return new BaseResponse<>(postPj_participateRes);
        }catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    //프로젝트 등록
    @ResponseBody
    @PostMapping("/registration")
    public BaseResponse<PostPjRegisterRes> pjRegistration(@RequestBody PostPjRegisterReq postPjRegisterReq){
        try{
            PjDateCheck(postPjRegisterReq.getPj_deadline(), postPjRegisterReq.getPj_startTerm(), postPjRegisterReq.getPj_endTerm());
            PjNullCheck(postPjRegisterReq.getPj_header(), postPjRegisterReq.getPj_field(), postPjRegisterReq.getPj_content(), postPjRegisterReq.getPj_name(), postPjRegisterReq.getPj_subField(), postPjRegisterReq.getPj_progress(), postPjRegisterReq.getPj_endTerm(), postPjRegisterReq.getPj_startTerm(), postPjRegisterReq.getPj_deadline(), postPjRegisterReq.getPj_totalPerson());
            PjKeywordCheck(postPjRegisterReq.getKeyword());
            PostPjRegisterRes postPjRegisterRes = projectService.registrationPj(postPjRegisterReq);
            return new BaseResponse<>(postPjRegisterRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //프로젝트 기한 오류 확인
    private void PjDateCheck(LocalDate pj_deadline, LocalDate pj_startTerm, LocalDate pj_endTerm) throws BaseException{
        if (pj_deadline.isBefore(pj_startTerm)){
            throw new BaseException(POST_PROJECT_DEADLINE_BEFORE_START);
        }
        if (pj_endTerm.isBefore(pj_startTerm)){
            throw new BaseException(POST_PROJECT_END_BEFORE_START);
        }
    }

    //프로젝트 null 값 확인
    private void PjNullCheck(String pj_header, String pj_field, String pj_content, String pj_name, String pj_subField, String pj_progress, LocalDate pj_endTerm, LocalDate pj_startTerm, LocalDate pj_deadline, int pj_totalPerson) throws BaseException{
        if(pj_header==null){
            throw new BaseException(POST_PROJECT_EMPTY_HEADER);
        }
        if(pj_field==null){
            throw new BaseException(POST_PROJECT_EMPTY_FIELD);
        }
        if(pj_content==null){
            throw new BaseException(POST_PROJECT_EMPTY_CONTENT);
        }
        if(pj_name==null){
            throw new BaseException(POST_PROJECT_EMPTY_NAME);
        }
        if(pj_subField==null){
            throw new BaseException(POST_PROJECT_EMPTY_SUBFIELD);
        }
        if(pj_progress==null){
            throw new BaseException(POST_PROJECT_EMPTY_PROGRESS);
        }
        if(pj_endTerm==null){
            throw new BaseException(POST_PROJECT_EMPTY_END_TERM);
        }
        if(pj_startTerm==null){
            throw new BaseException(POST_PROJECT_EMPTY_START_TERM);
        }
        if(pj_deadline==null){
            throw new BaseException(POST_PROJECT_EMPTY_DEADLINE);
        }
        if(pj_totalPerson==0){
            throw new BaseException(POST_PROJECT_EMPTY_TOTAL_PERSON);
        }
    }

    //키워드 값 확인 프로젝트 5글자, 4개 제한
    private void PjKeywordCheck(String [] keyword) throws BaseException{
        if(keyword.length > 4){
            throw new BaseException(POST_PROJECT_KEYWORD_CNT_EXCEED);
        }
        for(int j=0; j<keyword.length; j++){
            if(keyword[j].length() > 5){
                throw new BaseException(POST_PROJECT_KEYWORD_EXCEED);
            }
        }
    }


    //프로젝트 수정
    @ResponseBody
    @PatchMapping("/modify")
    public BaseResponse<PatchPjModifyRes> pjModify(@RequestBody PatchPjModifyReq patchPjModifyReq){
        try {
            PjDateCheck(patchPjModifyReq.getPj_deadline(), patchPjModifyReq.getPj_startTerm(), patchPjModifyReq.getPj_endTerm());
            PjNullCheck(patchPjModifyReq.getPj_header(), patchPjModifyReq.getPj_field(), patchPjModifyReq.getPj_content(), patchPjModifyReq.getPj_name(), patchPjModifyReq.getPj_subField(), patchPjModifyReq.getPj_progress(), patchPjModifyReq.getPj_endTerm(), patchPjModifyReq.getPj_startTerm(), patchPjModifyReq.getPj_deadline(), patchPjModifyReq.getPj_totalPerson());
            PjKeywordCheck(patchPjModifyReq.getKeyword());
            PatchPjModifyRes patchPjModifyRes = projectService.pjModify(patchPjModifyReq);
            return new BaseResponse<>(patchPjModifyRes);
        }catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //프로젝트 삭제
    @ResponseBody
    @DeleteMapping("/del")
    public BaseResponse<DelPjDelRes> pjDel(@RequestBody DelPjDelReq delPjDelReq){
        try {
            DelPjDelRes delpjDelRes = projectService.pjDel(delPjDelReq);
            return new BaseResponse<>(delpjDelRes);
        }catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //프로젝트 지원
    @ResponseBody
    @PostMapping("/apply")
    public BaseResponse<PostPjApplyRes> pjApply(@RequestBody PostPjApplyReq postPjApplyReq){
        try {
                PostPjApplyRes postPjApplyRes = projectService.pjApply(postPjApplyReq);
                if (postPjApplyRes.getComment().equals("중복"))
                    throw new BaseException(POST_PROJECT_COINCIDE_CHECK);
                else
                    return new BaseResponse<>(postPjApplyRes);
        }catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //프로젝트신청한 유저 승인
    @ResponseBody
    @PatchMapping("/approve")
    public BaseResponse<PatchPjApproveRes> pjApprove(@RequestBody PatchPjApproveReq patchPjApproveReq){
        try{
            PatchPjApproveRes patchPjApproveRes = projectService.pjApprove(patchPjApproveReq);
            return new BaseResponse<>(patchPjApproveRes);
        }catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    //프로젝트 신청 현황
    @ResponseBody
    @GetMapping("/apply-list")
    public BaseResponse<List<GetApplyListRes>> pjApplyList(@RequestParam(required = false) String pj_num) {
        try {
            List<GetApplyListRes> getApplyListRes = projectProvider.pjApplyList(pj_num);
            return new BaseResponse<>(getApplyListRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //본인이 지원한 프로젝트 신청 현황
    @ResponseBody
    @PostMapping("/apply-mylist")
    public BaseResponse<List<PostUserApplyRes>> userApply(@RequestBody PostUserApplyReq postUserApplyReq){
        try{
            List<PostUserApplyRes> postUserApplyRes = projectProvider.getUserApply(postUserApplyReq);
            return new BaseResponse<>(postUserApplyRes);
        }catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
