package com.example.demo.src.help.qa;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.help.qa.model.GetQaRes;
import com.example.demo.src.help.qa.model.PatchQaReq;
import com.example.demo.src.help.qa.model.PostQaReq;
import com.example.demo.src.help.qa.model.Qa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("/qa")

public class QaController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final QaProvider qaProvider;
    @Autowired
    private final QaService qaService;

    public QaController(QaProvider qaProvider, QaService qaService) {
        this.qaProvider = qaProvider;
        this.qaService = qaService;
    }

    /*
    * [GET]
    * 모든 질문 조회 API
    *
    * [GET] /? UserId=
    * 해당 User id를 갖는 질문 조회 API
    * */

    @ResponseBody
    @GetMapping("")

    public BaseResponse<List<GetQaRes>> getQa(@RequestParam(required = false) String User_id){
        try {
            // Query String (User_id) 가 없을 경우 -> 전체 질문을 가져옴
            if (User_id == null){
                List<GetQaRes> getQaRes = qaProvider.getQa();
                return new BaseResponse<>(getQaRes);
            }
            // Query String (User_id) 가 있을 경우 -> User_id에 맞는 질문을 가져옴
            List<GetQaRes> getQaRes = qaProvider.getQaByUser_id(User_id);
            return new BaseResponse<>(getQaRes);
        }
        catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /*
    * [DELETE] /:QA_num
    * 해당 QA_num을 갖는 질문 삭제 API
    * */

    @ResponseBody
    @DeleteMapping("/{QA_num}")

    public BaseResponse<String> deleteQa(@PathVariable("QA_num") int QA_num){
        try {
            qaService.deleteQa(QA_num);

            String result = Integer.toString(QA_num) + "번 질문이 삭제되었습니다.";
            return new BaseResponse<>(result);
        }
        catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /*
    * [PATCH] /:QA_num
    * 해당 QA_num을 갖는 질문 수정 API
    * */

    @ResponseBody
    @PatchMapping("/{QA_num}")

    public BaseResponse<List<GetQaRes>> modifyQa(@PathVariable("QA_num") int QA_num, @RequestBody Qa qa){
        try {
            PatchQaReq patchQaReq = new PatchQaReq(QA_num, qa.getQA_q());
            qaService.modifyQa(patchQaReq);

            List<GetQaRes> getQaRes = qaProvider.getQaByQaNum(QA_num);
            return new BaseResponse<>(getQaRes);
        }
        catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /*
    * [POST]
    * 질문 등록 API
    * */

//    @RequestBody
//    @PostMapping("")
//
//    public BaseResponse<List<GetQaRes>> uploadQa(@RequestBody PostQaReq postQaReq){
//
//    }


}
