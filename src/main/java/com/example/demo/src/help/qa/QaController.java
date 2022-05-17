package com.example.demo.src.help.qa;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.help.qa.model.GetQaRes;
import com.example.demo.src.help.qa.model.PatchAnswerReq;
import com.example.demo.src.help.qa.model.PatchQaReq;
import com.example.demo.src.help.qa.model.PostQaReq;
import com.example.demo.utils.jwt.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.QA_REQUEST_BODY_EMPTY;
import static com.example.demo.config.BaseResponseStatus.SUCCESS;

@RestController
@RequestMapping("/qa")
public class QaController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final QaProvider qaProvider;
    private final QaService qaService;
    private final JwtService jwtService;

    @Autowired
    public QaController(QaProvider qaProvider, QaService qaService, JwtService jwtService) {
        this.qaProvider = qaProvider;
        this.qaService = qaService;
        this.jwtService = jwtService;
    }

    /**
     * [GET] /qa?user_id=
     * 모든 질문 조회 API
     *
     * @param user_id
     * @return List<질문 번호, 아이디, 질문, 답변, 질문한 시간, 답변한 시간, status>
     * @author shinhyeon
     */
    @GetMapping("")
    public BaseResponse<List<GetQaRes>> getQa(@RequestParam(required = false) String user_id) throws BaseException {
        // user_id가 없을 경우 -> 전체 질문을 가져옴
        if (user_id == null) {
            List<GetQaRes> getQaRes = qaProvider.getQaAll();
            return new BaseResponse<>(getQaRes);
        }

        // user_id가 있을 경우 -> User_id에 맞는 질문을 가져옴
        String userIdByJwt = jwtService.getUserId();
        jwtService.JwtEffectiveness(user_id, userIdByJwt);

        List<GetQaRes> getQaRes = qaProvider.getQaByUser_id(user_id);
        return new BaseResponse<>(getQaRes);
    }

    /**
     * [PATCH] qa/modify/:qa_num
     * 해당 qa_num을 갖는 질문 수정 API
     *
     * @param qa_num
     * @param patchQaReq
     * @return 질문 번호, 아이디, 질문, 답변, 질문한 시간, 답변한 시간, status
     */
    @PatchMapping("/modify/{qa_num}")
    public BaseResponse<GetQaRes> modifyQa(@PathVariable("qa_num") int qa_num, @RequestBody @Valid PatchQaReq patchQaReq, BindingResult bindingResult) throws BaseException {
        if (bindingResult.hasErrors()) {
            throw new BaseException(QA_REQUEST_BODY_EMPTY);
        }

        String userIdByJwt = jwtService.getUserId();
        qaService.modifyQa(userIdByJwt, qa_num, patchQaReq);

        // 반영된 이후 getQaRes를 받아옴
        GetQaRes getQaRes = qaProvider.getQaByQaNum(qa_num);
        return new BaseResponse<>(getQaRes);
    }

    /**
     * [POST] /qa
     * 질문 등록 API
     *
     * @param postQaReq - user_id, qa_q
     * @return
     * @author shinhyeon
     */
    @PostMapping("")
    public BaseResponse<String> createQa(@RequestBody @Valid PostQaReq postQaReq, BindingResult bindingResult) throws BaseException {
        if (bindingResult.hasErrors()) {
            throw new BaseException(QA_REQUEST_BODY_EMPTY);
        }

        String userIdByJwt = jwtService.getUserId();
        jwtService.JwtEffectiveness(postQaReq.getUser_id(), userIdByJwt);

        qaService.createQa(postQaReq);

        return new BaseResponse<>(SUCCESS);
    }


    /**
     * [PATCH] /del/:qa_num
     * 해당 qa_num을 갖는 질문 삭제 API
     *
     * @param qa_num
     * @return
     * @author shinhyeon, yunhee
     */
    @DeleteMapping("/del/{qa_num}")
    public BaseResponse<GetQaRes> deleteQa(@PathVariable("qa_num") int qa_num) throws BaseException {
        String userIdByJwt = jwtService.getUserId();
        String qaUserId = qaProvider.getUserIdByQaNum(qa_num);

        jwtService.JwtEffectiveness(qaUserId, userIdByJwt);

        qaService.deleteQa(qa_num);

        return new BaseResponse("성공적으로 질문을 삭제했습니다.");
    }

    /**
     * [PATCH] qa/answer/:qa_num
     * 해당 qa_num을 갖는 질문 답변 API
     *
     * @param qa_num
     * @param patchAnswerReq
     * @return GetQaRes
     */
    @PatchMapping("/answer/{qa_num}")
    public BaseResponse<GetQaRes> answerQa(@PathVariable("qa_num") int qa_num, @RequestBody @Valid PatchAnswerReq patchAnswerReq,
                                           BindingResult bindingResult) throws BaseException {
        if (bindingResult.hasErrors()) {
            throw new BaseException(QA_REQUEST_BODY_EMPTY);
        }

        String userIdByJwt = jwtService.getUserId();

        // 관리자만 답변을 달 수 있음 (현재는 ye5ni로 판별을 하지만 추후 관리자 user_id가 정해지면 변경할 예정임)
        jwtService.JwtEffectiveness("ye5ni", userIdByJwt);

        qaService.answerQa(qa_num, patchAnswerReq);

        // 반영된 이후 getQaRes를 받아옴
        GetQaRes getQaRes = qaProvider.getQaByQaNum(qa_num);
        return new BaseResponse<>(getQaRes);
    }

}