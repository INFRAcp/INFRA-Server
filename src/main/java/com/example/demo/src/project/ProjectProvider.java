package com.example.demo.src.project;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.project.model.GetProjectRes;
import com.example.demo.utils.JwtService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ProjectProvider {

    private final ProjectDao projectDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ProjectProvider(ProjectDao projectDao, JwtService jwtService){
        this.projectDao = projectDao;
        this.jwtService = jwtService;
    }

    //Project들의 정보 조회
    public List<GetProjectRes> getProjects() throws BaseException {
        try{
            List<GetProjectRes> getProjectRes = projectDao.getProjects();
            return getProjectRes;
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetProjectRes> getProjectsByKeyword(String search) throws BaseException{
        try {
            List<GetProjectRes> getProjectRes = projectDao.getProjectsBySearch(search);
            return getProjectRes;
        }catch (Exception exception){
            throw  new BaseException(DATABASE_ERROR);
        }
    }
}
