package com.example.demo;

import com.example.demo.src.user.UserProvider;
import com.example.demo.src.user.UserService;
import com.example.demo.src.user.model.PostLoginReq;
import com.example.demo.utils.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    UserProvider userProvider;

    @MockBean
    JwtService jwtService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private WebApplicationContext ctx;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysExpect(status().isOk())
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @Transactional
    void 회원가입() {

    }

    @Test
    void 로그인() throws Exception {
        PostLoginReq postLoginReq = new PostLoginReq("kang", "0000");
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders
                .post("/user/log-in")
                .content(mapper.writeValueAsString(postLoginReq))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ApiResponse res = mapper.readValue(contentAsString, ApiResponse.class);
        System.out.println(">>>>>>>>>>>>>>>>>>>>");
        System.out.println(contentAsString);
        System.out.println(res.toString());
    }

    @Test
    void id_중복체크() throws Exception {
        String successCaseId = "infra2010";
        Object obj = mockMvc.perform(get("/user/valid-id/" + successCaseId))
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andDo(print());
    }
}

