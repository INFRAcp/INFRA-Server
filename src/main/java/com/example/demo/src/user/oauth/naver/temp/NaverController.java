package com.example.demo.src.user.oauth.naver.temp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/user/naver")
public class NaverController {

    @GetMapping("")
    @ResponseBody
    public String naverLogin(@RequestParam String code, @RequestParam String state) {
        /**인가코드 발급**/
        // RestTemplate 인스턴스 생성
        RestTemplate rt = new RestTemplate();

        // url을 통해 httpHeaders() 클래스 생성
        HttpHeaders accessTokenHeaders = new HttpHeaders();
        accessTokenHeaders.add("Content-type", "application/x-www-form-urlencoded");

        // 인가코드를 발급받기 위해 필요한 정보들
        MultiValueMap<String, String> accessTokenParams = new LinkedMultiValueMap<>();
        accessTokenParams.add("grant_type", "authorization_code");
        accessTokenParams.add("client_id", "qTEVZNsX5FudEPGlJxn1");
        accessTokenParams.add("client_secret", "JxjdS8mCqP");
        accessTokenParams.add("code", code);    // 응답으로 받은 코드
        accessTokenParams.add("state", state); // 응답으로 받은 상태

        System.out.println("code : " + code);   // 응답코드 출력

        /** access token 발급 **/
        HttpEntity<MultiValueMap<String, String>> accessTokenRequest = new HttpEntity<>(accessTokenParams, accessTokenHeaders);

        ResponseEntity<String> accessTokenResponse = rt.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                accessTokenRequest,
                String.class
        );
        System.out.println("accessToken : " + accessTokenResponse.getBody());    // access token 출력

        /** 발급받은 access token을 활용해 회원정보 API 호출 **/
        // 이전에 받았던 Access Token 응답
        ObjectMapper objectMapper = new ObjectMapper();

        // json -> 객체로 매핑하기 위해 NaverOauthParams 클래스 생성
        NaverOauthParams naverOauthParams = null;
        try {
            naverOauthParams = objectMapper.readValue(accessTokenResponse.getBody(), NaverOauthParams.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // header를 생성해서 access token을 넣어줍니다.
        HttpHeaders profileRequestHeader = new HttpHeaders();
        profileRequestHeader.add("Authorization", "Bearer " + naverOauthParams.getAccess_token());

        HttpEntity<HttpHeaders> profileHttpEntity = new HttpEntity<>(profileRequestHeader);

        // profile api로 생성해둔 헤더를 담아서 요청을 보냅니다.
        ResponseEntity<String> profileResponse = rt.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST,
                profileHttpEntity,
                String.class
        );

        return "profile response : " + profileResponse.getBody();
    }
}
