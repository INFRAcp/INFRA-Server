package com.example.demo.src.help.report;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.help.report.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/report")

public class ReportController {
    // 동작에 있어서 필요한 요쇼들 불러오기
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ReportProvider reportProvider;
    @Autowired
    private final ReportService reportService;
    @Autowired
    private final JwtService jwtService;

    public ReportController(ReportProvider reportProvider, ReportService reportService, JwtService jwtService) {
        this.reportProvider = reportProvider;
        this.reportService = reportService;
        this.jwtService = jwtService;
    }


    // [POST] 새로운 신고 등록하기
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostReportRes> createReport(@RequestBody PostReportReq postReportReq) {
        if (postReportReq.getUser_id() == null || postReportReq.getReportedUser_id() == null || postReportReq.getRp_category() == null
                || postReportReq.getRp_field() == null || postReportReq.getRp_opinion() == null) {
            return new BaseResponse<>(POST_REPORTS_EMPTY_INFO);
        }
        try {
            PostReportRes postReportRes = reportService.createReport(postReportReq);
            return new BaseResponse<>(postReportRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // [POST] 특정 사용자가 신고했던 목록 조회
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<PostReportUserRes>> getReports(@RequestBody PostReportUserReq postReportUserReq) {
        try {
            List<PostReportUserRes> postReportUserRes = reportProvider.getReports(postReportUserReq);
            return new BaseResponse<>(postReportUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // [Delete] 특정 사용자의 특정 신고글 삭제
    @ResponseBody
    @DeleteMapping("")
    public BaseResponse<String> deleteReport(@RequestBody PostReportDelReq postReportDelReq) {
        if (postReportDelReq.getUser_id() == null || postReportDelReq.getReportedUser_id() == null){
            return new BaseResponse<>(POST_REPORTS_DELETE_ERROR);
        }
        try {
            reportService.deleteReport(postReportDelReq);
            String result = "신고가 철회되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
