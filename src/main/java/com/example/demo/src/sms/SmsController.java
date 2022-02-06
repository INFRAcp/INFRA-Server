package com.example.demo.src.sms;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.sms.model.PostSmsReq;
import com.example.demo.src.sms.model.PostSmsRes;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/sms")
public class SmsController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SmsService smsService;

    public SmsController(SmsService smsService) {
        this.smsService = smsService;
    }

    //문자발송

    /**
     * @param postSmsReq
     * @return
     * @throws NoSuchAlgorithmException
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     * @throws InvalidKeyException
     * @throws JsonProcessingException
     * @throws BaseException
     * @author 한규범
     */
    @PostMapping("/send")
    public BaseResponse<PostSmsRes> smsSend(
            @RequestBody PostSmsReq postSmsReq)
            throws NoSuchAlgorithmException, URISyntaxException, UnsupportedEncodingException, InvalidKeyException, JsonProcessingException, BaseException {
        try {

            if(smsService.phoneCheck(postSmsReq.getRecipientPhoneNumber()) == false){
                throw new BaseException(DUPLICATED_PHONE);
            }
            if(Pattern.matches("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", postSmsReq.getRecipientPhoneNumber()) == false){
                throw new BaseException(POST_SMS_PHONEFORM_ERROE);
            }

            int certification = (int) ((Math.random() * (99999 - 10000 + 1)) + 10000);


            postSmsReq.setRecipientPhoneNumber(postSmsReq.getRecipientPhoneNumber().replaceAll("-",""));

            postSmsReq.setContent("인프라 회원가입 \n인증번호 : "+(certification));
            PostSmsRes data = smsService.sendSms(postSmsReq.getRecipientPhoneNumber(), postSmsReq.getContent());

            data.setCertifyValue(certification);

            return new BaseResponse<>(data);
        }catch (BaseException exception){
            return new BaseResponse((exception.getStatus()));
        }
    }


}
