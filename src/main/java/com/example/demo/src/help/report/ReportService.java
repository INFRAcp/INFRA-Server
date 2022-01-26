package com.example.demo.src.help.report;

import com.example.demo.config.BaseException;
import com.example.demo.src.help.report.model.PostReportDelReq;
import com.example.demo.src.help.report.model.PostReportReq;
import com.example.demo.src.help.report.model.PostReportRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;


@Service
public class ReportService {
    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log 처리부분: Log를 기록하기 위해 필요한 함수입니다.

    private final ReportDao reportDao;
    private final ReportProvider reportProvider;
    private final JwtService jwtService;


    @Autowired
    public ReportService(ReportDao reportDao, ReportProvider reportProvider, JwtService jwtService) {
        this.reportDao = reportDao;
        this.reportProvider = reportProvider;
        this.jwtService = jwtService;
    }

    // [POST] 새로운 신고 등록하기
    public PostReportRes createReport(PostReportReq postReportReq) throws BaseException {
        try {
            reportDao.createReport(postReportReq);
            String user_id = postReportReq.getUser_id();
            return new PostReportRes(user_id);
        } catch (Exception exception) { // DB에 이상이 있는 경우
            logger.error(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // [PATCH] 특정 사용자의 특정 신고글 삭제
    public int deleteReport(PostReportDelReq postReportDelReq) throws BaseException {
        try {
            return reportDao.deleteReport(postReportDelReq);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}