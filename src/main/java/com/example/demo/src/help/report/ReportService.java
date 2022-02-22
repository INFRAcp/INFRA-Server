package com.example.demo.src.help.report;

import com.example.demo.config.BaseException;
import com.example.demo.src.help.report.model.PatchReportReq;
import com.example.demo.src.help.report.model.PostReportReq;
import com.example.demo.src.help.report.model.PostReportRes;
import com.example.demo.utils.jwt.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;


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


    /**
     * 새로운 신고 등록 API
     *
     * @param postReportReq
     * @return
     * @throws BaseException
     * @author yewon
     */
    @Transactional
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

    /**
     * 특정 사용자의 특정 신고글 삭제 API
     *
     * @param patchReportReq
     * @return
     * @throws BaseException
     * @author yewon
     */
    @Transactional
    public int deleteReport(PatchReportReq patchReportReq) throws BaseException {
        try {
            return reportDao.deleteReport(patchReportReq);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}