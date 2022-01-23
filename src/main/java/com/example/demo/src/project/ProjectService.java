package com.example.demo.src.project;

import com.example.demo.config.BaseException;
import com.example.demo.src.project.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ProjectService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProjectDao projectDao;
    private final ProjectProvider projectProvider;
    private final JwtService jwtService;

    @Autowired
    public ProjectService(ProjectDao projectDao, ProjectProvider projectProvider, JwtService jwtService){
        this.projectDao = projectDao;
        this.projectProvider = projectProvider;
        this.jwtService = jwtService;
    }

    //프로젝트 등록
    public PostPjRegisterRes registrationPj(PostPjRegisterReq postPjRegisterReq) throws BaseException{
        try{
            String pjRegisterSucese = projectDao.pjRegistration(postPjRegisterReq);
            return new PostPjRegisterRes(pjRegisterSucese);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //프로젝트 수정
    public PatchPjModifyRes pjModify(PatchPjModifyReq patchPjModifyReq)throws BaseException{
        try {
            String PjModify = projectDao.pjModify(patchPjModifyReq);
            return new PatchPjModifyRes(PjModify);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //프로젝트 삭제
    public GetpjDelRes pjDel(GetPjDelReq getPjDelReq) throws BaseException{
        try {
            String pjDel = projectDao.pjDel(getPjDelReq);
            return new GetpjDelRes(pjDel);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
