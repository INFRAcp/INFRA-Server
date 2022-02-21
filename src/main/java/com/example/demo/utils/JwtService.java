package com.example.demo.utils;


import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.user.UserDao;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static com.example.demo.config.BaseResponseStatus.EMPTY_JWT;
import static com.example.demo.config.BaseResponseStatus.INVALID_JWT;

@Service
public class JwtService {

    private final long ACCESS_TOKEN_VALID_TIME = 1 * 60 * 500L;   // 30초
    private final long REFRESH_TOKEN_VALID_TIME = 1 * 60 * 800L; // 50초
//            = 60 * 60 * 24 * 7 * 1000L;   // 1주

    private final JwtDao jwtDao;

    @Autowired
    public JwtService(JwtDao jwtDao){
        this.jwtDao = jwtDao;
    }

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
                    .parseClaimsJws(accessToken); // 파싱 및 검증, 실패 시 에러
        } catch (Exception ignored) {
            //엑세스 토큰 만료
            try {
                String refreshTokenIdx = resolveRefreshToken();
                if(refreshTokenIdx == null || refreshTokenIdx.length() ==0){
                    throw new BaseException(EMPTY_JWT);
                }else{
                    //리프레시토큰 가져오기
                    String refreshToken = jwtDao.getRefreshToken(refreshTokenIdx);
                    claims = Jwts.parser()
                            .setSigningKey(Secret.JWT_REFRESH_SECRET_KEY)
                            .parseClaimsJws(refreshToken);  // 파싱 및 검증, 실패 시 에러
                    return "재발급";
                }
            }catch (Exception ignored2){
                //리프레시 토큰 만료될 경우
                return "만료";
            }

        }

        // 3. userId 추출
        return claims.getBody().get("userId", String.class);  // jwt 에서 userId를 추출
    }

}