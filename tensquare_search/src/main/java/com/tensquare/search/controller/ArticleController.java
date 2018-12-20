package com.tensquare.search.controller;

import com.tensquare.search.pojo.Article;
import com.tensquare.search.service.ArticleService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/article")
@CrossOrigin
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @RequestMapping(value = "",method = RequestMethod.POST)
    public Result add(@RequestBody Article article){
        articleService.add(article);
        return new Result(true, StatusCode.OK, "添加成功");
    }

    @RequestMapping(value = "/search/{keywords}/{page}/{size}",method = RequestMethod.GET)
    public Result search(@PathVariable String keywords,@PathVariable int page,@PathVariable int size){
        Page<Article> pagelist = articleService.search(keywords, page, size);
        PageResult pageResult = new PageResult(pagelist.getTotalElements(), pagelist.getContent());
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }
}
