package com.tensquare.controller;

import com.tensquare.pojo.Label;
import com.tensquare.service.LabelService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/label")
@CrossOrigin //开启跨域访问
public class LabelController {

    @Autowired
    private LabelService labelService;

    //多条件查询
    @RequestMapping(value = "/search",method = RequestMethod.POST)
    public Result search(@RequestBody Map map){
        List<Label> list = labelService.findSearch(map);
        return new Result(true,StatusCode.OK,"查询成功",list);
    }

    //多条件查询加分页
    @RequestMapping(value = "/search/{page}/{size}",method = RequestMethod.POST)
    public Result search(@RequestBody Map map,@PathVariable int page,@PathVariable int size){
        Page<Label> pageList = labelService.findSearch(map,page,size);
        PageResult pageResult = new PageResult(pageList.getTotalElements(), pageList.getContent());
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Result findAll(){
        // int num = 1/0;
        List<Label> list = labelService.findAll();
        return new Result(true, StatusCode.OK,"查询成功",list);
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public Result findById(@PathVariable String id){
        System.out.println("No.2");
        Label label = labelService.findById(id);
        return new Result(true, StatusCode.OK, "查询成功", label);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody Label label){
        labelService.add(label);
        return new Result(true, StatusCode.OK, "添加成功");
    }

    @RequestMapping(value="/{id}",method = RequestMethod.PUT)
    public Result update(@RequestBody Label label,@PathVariable String id){
        label.setId(id);
        labelService.update(label);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    public Result deleteById(@PathVariable String id){
        labelService.deleteById(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }
}
