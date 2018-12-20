package com.tensquare.ai.controller;

import com.tensquare.ai.service.CnnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Autowired
    private CnnService cnnService;

    @RequestMapping(value="/textclassify",method = RequestMethod.POST)
    public Map textClassify( @RequestBody Map<String,String> content){
        return cnnService.textClassify(content.get("content"));
    }

}
