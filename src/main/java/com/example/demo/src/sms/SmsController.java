package com.example.demo.src.sms;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.sms.model.PostSmsCheckReq;
import com.example.demo.src.sms.model.PostSmsCheckRes;
import com.example.demo.src.sms.model.PostSmsReq;
import com.example.demo.src.sms.model.PostSmsRes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/sms")
public class SmsController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private SmsService smsService;
    public SmsController(SmsService smsService) {
        this.smsService = smsService;
    }

    //문자발송
    @PostMapping("/send")
    public BaseResponse<PostSmsRes> smsSend(
            @RequestBody PostSmsReq request)
            throws NoSuchAlgorithmException, URISyntaxException, UnsupportedEncodingException, InvalidKeyException, JsonProcessingException, BaseException {
        try {
            if(smsService.phoneCheck(request.getRecipientPhoneNumber()) == false){
                throw new BaseException(POST_SMS_PHONECHECK_ERROE);
            }
            int certification = (int) ((Math.random() * (99999 - 10000 + 1)) + 10000);
            request.setContent("인프라 회원가입 \n 인증번호 : "+Integer.toString(certification));
            PostSmsRes data = smsService.sendSms(request.getRecipientPhoneNumber(), request.getContent());

            data.setCertifyValue(certification);

            return new BaseResponse<>(data);
        }catch (BaseException exception){
            return new BaseResponse((exception.getStatus()));
        }
    }


}
