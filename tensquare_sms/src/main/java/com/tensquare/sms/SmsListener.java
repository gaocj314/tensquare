package com.tensquare.sms;

import com.aliyuncs.exceptions.ClientException;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.*;
@Component
@RabbitListener(queues = "sms")
public class SmsListener {

    @Autowired
    private SmsUtil smsUtil;

    @Value("${aliyun.sms.template_code}")
    private String template_code;

    @Value("${aliyun.sms.sign_name}")
    private String sign_name;

    @RabbitHandler
    public void getSms(Map map){
        //取出数据，调用阿里云短信服务
        System.out.println("mobile:"+map.get("mobile"));
        System.out.println("code:"+map.get("code"));
        //{"code":"321311"}
        try {
            smsUtil.sendSms(map.get("mobile")+"",template_code,sign_name,"{\"code\":\""+map.get("code")+"\"}");
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
