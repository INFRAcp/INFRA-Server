package com.example.demo.src.help.qa;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.help.qa.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController

@RequestMapping("/qa")

public class QaController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final QaProvider qaProvider;
    @Autowired
    private final QaService qaService;
    @Autowired
    private final JwtService jwtService;

    public QaController(QaProvider qaProvider, QaService qaService, JwtService jwtService) {
        this.qaProvider = qaProvider;
        this.qaService = qaService;
        this.jwtService = jwtService;
    }

    /*
    * [GET]
    * 모든 질문 조회 API
    *
    * [GET] /? user=
    * 해당 user(id)를 갖는 질문 조회 API
    * */

    @ResponseBody
    @GetMapping("")

    public BaseResponse<List<GetQaRes>> getQa(@RequestParam(required = false) String user){
        try {
            // Query String (user_id) 가 없을 경우 -> 전체 질문을 가져옴
            if (user == null){
                List<GetQaRes> getQaRes = qaProvider.getQa();
                return new BaseResponse<>(getQaRes);
            }
            // Query String (user_id) 가 있을 경우 -> User_id에 맞는 질문을 가져옴
            // jwt
            String userIdByJwt = jwtService.getUserId();
            if(!user.equals(userIdByJwt)) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetQaRes> getQaRes = qaProvider.getQaByUser_id(user);
            return new BaseResponse<>(getQaRes);
        }
        catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /*
    * [DELETE] /:qa_num
    * 해당 qa_num을 갖는 질문 삭제 API
    * */

    @ResponseBody
    @DeleteMapping("/{qa_num}")

    public BaseResponse<String> deleteQa(@PathVariable("qa_num") int qa_num){
        try {
            qaService.deleteQa(qa_num);

            String result = Integer.toString(qa_num) + "번 질문이 삭제되었습니다.";
            return new BaseResponse<>(result);
        }
        catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /*
    * [PATCH] /modify/:qa_num
    * 해당 qa_num을 갖는 질문 수정 API
    * */

    @ResponseBody
    @PatchMapping("/modify/{num}")

    public BaseResponse<GetQaRes> modifyQa(@PathVariable("num") int num, @RequestBody Qa qa){
        try {
            // jwt
            String userIdByJwt = jwtService.getUserId();
            GetQaRes getQaRes = qaProvider.getQaByQaNum(num);

            if(!getQaRes.getUser_id().equals(userIdByJwt)) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            PatchQaReq patchQaReq = new PatchQaReq(num, qa.getQa_q());
            qaService.modifyQa(patchQaReq);

            // 반영된 이후 getQaRes를 받아옴
            GetQaRes getQaRes2 = qaProvider.getQaByQaNum(num);
            return new BaseResponse<>(getQaRes2);
        }
        catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /*
    * [POST]
    * 질문 등록 API
    * */

    @ResponseBody
    @PostMapping("")

    public BaseResponse<String> uploadQa(@RequestBody PostQaReq postQaReq){
        try {
            // jwt
            String userIdByJwt = jwtService.getUserId();
            if(!postQaReq.getUser_id().equals(userIdByJwt)) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            qaService.uploadQa(postQaReq);

            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /*
     * [PATCH] /del/:qa_num
     * 해당 qa_num을 갖는 질문 삭제 API
     * */

    @ResponseBody
    @PatchMapping("/del/{num}")

    public BaseResponse<GetQaRes> deleteQa2(@PathVariable("num") int num){
        try {
            // jwt
            String userIdByJwt = jwtService.getUserId();
            GetQaRes getQaRes = qaProvider.getQaByQaNum(num);

            if(!getQaRes.getUser_id().equals(userIdByJwt)) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            qaService.modifyQa2(num);

            // 반영된 이후 getQaRes를 받아옴
            GetQaRes getQaRes2 = qaProvider.getQaByQaNum(num);
            return new BaseResponse<>(getQaRes2);
        }
        catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


}
