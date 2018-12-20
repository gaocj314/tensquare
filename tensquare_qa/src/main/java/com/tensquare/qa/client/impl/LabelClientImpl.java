package com.tensquare.qa.client.impl;

import com.tensquare.qa.client.LabelClient;
import entity.Result;
import entity.StatusCode;
import org.springframework.stereotype.Component;

@Component
public class LabelClientImpl implements LabelClient {
    @Override
    public Result findById(String id) {
        /**
         * 1.可以直接报出服务器繁忙
         * 2.可以处理业务,如此服务异常关闭后,可获取redis中信息,返回
         */
        System.out.println("熔断器执行了");
        return new Result(false, StatusCode.REMOTEERROR, "远程调用失败");
    }
}
