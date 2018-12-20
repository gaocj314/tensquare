package com.tensquare.webmagic.pipeline;

import com.tensquare.webmagic.dao.ArticleDao;
import com.tensquare.webmagic.pojo.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import util.IdWorker;

@Component
public class ArticleDbPipeline implements Pipeline {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private ArticleDao articleDao;

    private String channelId;

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        String title = resultItems.get("title");
        String content = resultItems.get("content");

        Article article = new Article();
        article.setId(String.valueOf(idWorker.nextId()));
        article.setTitle(title);
        article.setContent(content);
        article.setChannelid(channelId);
        articleDao.save(article);
    }
}
