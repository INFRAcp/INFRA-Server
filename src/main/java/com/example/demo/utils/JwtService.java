package com.example.demo.utils;


import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static com.example.demo.config.BaseResponseStatus.EMPTY_JWT;
import static com.example.demo.config.BaseResponseStatus.INVALID_JWT;

@Service
public class JwtService {

    private final long ACCESS_TOKEN_VALID_TIME = 1 * 60 * 1000L;   // 1분
    private final long REFRESH_TOKEN_VALID_TIME = 60 * 60 * 24 * 7 * 1000L;   // 1주

    /*
    Access JWT 생성
    @param userIdx
    @return String
     */
    public String createAccessJwt(String userId) {
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("type", "jwt")
                .claim("userId", userId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_VALID_TIME))
                .signWith(SignatureAlgorithm.HS256, Secret.JWT_ACCESS_SECRET_KEY)
                .compact();
    }

    /**
     * Refresh JWT 생성
     * @param userId
     * @return
     * @author 규범
     */
    public String createRefreshJwt(String userId){
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("type", "jwt")
                .claim("userId", userId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_VALID_TIME))
                .signWith(SignatureAlgorithm.HS256, Secret.JWT_REFRESH_SECRET_KEY)
                .compact();
    }

    /*
    Header에서 X-ACCESS-TOKEN 으로 JWT 추출
    @return String
     */
    public String resolveAccessToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("X-ACCESS-TOKEN");
    }

    public String resolveRefreshToken(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("X-REFRESH-TOKEN");
    }

    /*
    JWT에서 userId 추출
    @return int
    @throws BaseException
     */
    public String getUserId() throws BaseException {

        //ACCESS 만료


        //1. JWT 추출
        String accessToken = resolveAccessToken();
        if (accessToken == null || accessToken.length() == 0) {
            throw new BaseException(EMPTY_JWT);
        }

        // 2. JWT parsing
        Jws<Claims> claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(Secret.JWT_ACCESS_SECRET_KEY)
                    .parseClaimsJws(accessToken);
        } catch (Exception ignored) {
            throw new BaseException(INVALID_JWT);
        }

        // 3. userId 추출
        return claims.getBody().get("userId", String.class);  // jwt 에서 userId를 추출
    }

}