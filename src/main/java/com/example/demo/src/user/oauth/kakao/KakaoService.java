package com.example.demo.src.user.oauth.kakao;

import com.example.demo.config.BaseException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Component
@Service
public class KakaoService {
    // value annotaion
    @Value("${spring.kakao.client_id}")
    private String client_id;
    @Value("${spring.kakao.redirect_uri}")
    private String redirect_uri;
    @Value("${spring.kakao.client_secret}")
    private String client_secret;

    private final KakaoDao kakaoDao;

    public KakaoService(KakaoDao kakaoDao){
        this.kakaoDao = kakaoDao;
    }

    /**
     * 카카오 로그인 API
     * @param authorize_code
     * @return
     * @throws UnsupportedEncodingException
     * @author yewon
     */
    // access_token 발급 받기
    public String getAccessToken(String authorize_code) throws UnsupportedEncodingException {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";  // api 요청 URL

        try {
            URL url = new URL(reqURL);   // 위에서 선언한 reqURL (카카오 open API 요청)
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();  // url을 통해 HttpURLConnection 클래스 생성
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);     //POST 요청을 위해 기본값이 false인 setDoOutput을 true로

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송 (request parameters)
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code"); // string으로 고정되어있는 grant_type
            sb.append("&client_id=" + this.client_id);  // TODO REST_API_KEY 입력
            sb.append("&redirect_uri=" + this.redirect_uri); // TODO 인가코드 받은 redirect_uri 입력
            sb.append("&client_secret=" + this.client_secret); // TODO client_secret 키 입력
            sb.append("&code=" + authorize_code);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리에 포함된 클래스로 JSON 파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result); // JSON Parser를 사용해서 String 값을 JSON 객체로 만들어줌
            // object, array 등 총 5가지 종류의 class 가 있음. - element는 부모클래스로서 기본적으로 이 형태로 받아오게 됨.

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token : " + access_Token);
            System.out.println("refresh_token : " + refresh_Token);

            br.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return access_Token;
    }

    // 사용자 정보 가져오기
    public HashMap<String, Object> getUserInfo(String access_Token) {

        HashMap<String, Object> userInfo = new HashMap<>();
        String reqURL = "https://kapi.kakao.com/v2/user/me";

        //access_token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + access_Token);     //전송할 header 작성, access_token전송

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line + "\n";
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리로 JSON파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

//            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
            JsonObject kakao_account = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

//            String nickname = properties.getAsJsonObject().get("nickname").getAsString();
            String email = kakao_account.getAsJsonObject().get("email").getAsString();

            //userInfo.put("nickname : ", nickname);
            //userInfo.put("email : ",email);
//            System.out.println("nickname : " + nickname);
            System.out.println("email : " + email);

            kakaoDao.insertInfo(email);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return userInfo;
    }

    /**
     * 카카오 로그아웃 API
     * @param access_Token
     * @author yewon
     */
    public static void kakaoLogout(String access_Token) {
        String reqURL = "https://kapi.kakao.com/v1/user/logout";
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + access_Token);

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String result = "";
            String line = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println(result);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 카카오 세션 만료 API
     * @param access_Token
     * @author yewon
     */
    public static void kakaoSessionOut(String access_Token) {
        String reqURL = "https://kapi.kakao.com/v1/user/unlink";
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + access_Token);

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String result = "";
            String line = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println(result);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 카카오 회원탈퇴 API
     *
     * @param user_email
     * @return
     * @throws BaseException
     * @author yewon
     */
    @Transactional
    public void delUser(String user_email) throws BaseException {
        try {
            kakaoDao.delUser(user_email);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
