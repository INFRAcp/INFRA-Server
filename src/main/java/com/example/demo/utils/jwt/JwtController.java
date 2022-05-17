package com.example.demo.utils.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jwt")
public class JwtController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final JwtService jwtService;

    @Autowired
    private final JwtDao jwtDao;

    public JwtController(JwtService jwtService, JwtDao jwtDao){
        this.jwtService = jwtService;
        this.jwtDao = jwtDao;
    }


}
