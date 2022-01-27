package com.example.demo.src.help.report;

import com.example.demo.config.BaseException;
import com.example.demo.src.help.report.model.GetReportReq;
import com.example.demo.src.help.report.model.GetReportRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import static com.example.demo.config.BaseResponseStatus.*;

@Service

public class ReportProvider {
    private final ReportDao reportDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ReportProvider(ReportDao reportDao, JwtService jwtService) {
        this.reportDao = reportDao;
        this.jwtService = jwtService;
    }


    /**
     * 특정 사용자가 신고했던 목록 조회 API
     * @param getReportReq
     * @return
     * @throws BaseException
     */
    public List<GetReportRes> getReports(GetReportReq getReportReq) throws BaseException {
        try {
            List<GetReportRes> getReportRes = reportDao.getReports(getReportReq);
            return getReportRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
