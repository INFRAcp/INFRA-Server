package com.example.demo.src.sms;

import com.example.demo.src.sms.model.PostSmsRequest;
import com.example.demo.src.sms.model.PostMessageModel;
import com.example.demo.src.sms.model.PostSmsRes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SmsService {

    private final String serviceId = "ncp:sms:kr:280044915694:infra-sms";
    private final String accessKey = "JUARhMRAzpFrstZqd775";
    private final String secretKey = "lgiOpHwGvTW5HjcIaCKkhATBHFB3qeRuWzaBOrZZ";

    private SmsDao smsDao;

    @Autowired
    public SmsService(SmsDao smsDao){
        this.smsDao = smsDao;
    }

    public PostSmsRes sendSms(String recipientPhoneNumber, String content) throws JsonProcessingException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, URISyntaxException {

        Long time = System.currentTimeMillis();

        List<PostMessageModel> messages = new ArrayList<>();
        messages.add(new PostMessageModel(recipientPhoneNumber, content));

        PostSmsRequest smsRequest = new PostSmsRequest("SMS", "COMM", "82", "0318488224", "인증", messages);

        smsRequest.setContent(Integer.toString((int)(Math.random() * (99999 - 10000 + 1)) + 10000));

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(smsRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time.toString());
        headers.set("x-ncp-iam-access-key", this.accessKey);
        String sig = makeSignature(time); // 암호화
        headers.set("x-ncp-apigw-signature-v2", sig);

        HttpEntity<String> body = new HttpEntity<>(jsonBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        PostSmsRes smsResponse = restTemplate.postForObject(
                new URI("https://sens.apigw.ntruss.com/sms/v2/services/"+this.serviceId+"/messages"), body, PostSmsRes.class);
        return smsResponse;
    }

    public String makeSignature(Long time) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {

        String space = " ";
        String newLine = "\n";
        String method = "POST";
        String url = "/sms/v2/services/"+ this.serviceId+"/messages";
        String timestamp = time.toString();
        String accessKey = this.accessKey;
        String secretKey = this.secretKey;

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.encodeBase64String(rawHmac);

        return encodeBase64String;
    }

    /**
     *
     * @param recipientPhoneNumber
     * @author 한규범
     */
    public boolean phoneCheck(String recipientPhoneNumber) {
        if(smsDao.phoneCheck(recipientPhoneNumber) == true){
            return true;
        }else{
            return false;
        }
    }
}
