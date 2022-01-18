package com.example.demo.src.project;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.project.model.GetProjectRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("/project")
public class ProjectController {
//    final Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    @Autowired
//    private final ProjectProvider projectProvider;
//    @Autowired
//    private final ProjectService projectService;
//    @Autowired
//    private final JwtService jwtService;
//
//    public ProjectController(ProjectProvider projectProvider, ProjectService projectService, JwtService jwtService) {
//        this.projectProvider = projectProvider;
//        this.projectService = projectService;
//        this.jwtService = jwtService;
//    }


    //프로젝트 전체 조회
    @ResponseBody
    @GetMapping("/inquiry")
    public BaseResponse<List<GetProjectRes>> getProjects(@RequestParam(required = false) String search){
        try{
            if(search == null){
                List<GetProjectRes> getProjectRes = projectProvider.getProjects();
                return new BaseResponse<>(getProjectRes);
            }
            List<GetProjectRes> getProjectRes = projectProvider.getProjectsByKeyword(search);
            return new BaseResponse<>(getProjectRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }




}
