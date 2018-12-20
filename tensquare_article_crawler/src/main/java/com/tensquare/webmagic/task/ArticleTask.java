package com.tensquare.webmagic.task;

import com.tensquare.webmagic.pipeline.ArticleDbPipeline;
import com.tensquare.webmagic.process.ArticleProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.scheduler.RedisScheduler;

@Component
public class ArticleTask {

    @Autowired
    private ArticleProcessor articleProcessor;

    @Autowired
    private RedisScheduler redisScheduler;

    @Autowired
    private ArticleDbPipeline articleDbPipeline;

    @Scheduled(cron = "50 39 11 * * ?")
    public void aiTask(){
        articleDbPipeline.setChannelId("ai");
        Spider.create(articleProcessor)
                .addUrl("https://www.csdn.net/nav/ai")
                // .addPipeline(new ConsolePipeline())
                .addPipeline(articleDbPipeline)
                .setScheduler(redisScheduler)
                .run();
    }

    @Scheduled(cron = "50 41 11 * * ?")
    public void dbTask(){
        articleDbPipeline.setChannelId("db");
        Spider.create(articleProcessor)
                .addUrl("https://www.csdn.net/nav/db")
                // .addPipeline(new ConsolePipeline())
                .addPipeline(articleDbPipeline)
                .setScheduler(redisScheduler)
                .run();
    }

    @Scheduled(cron = "30 43 11 * * ?")
    public void webTask(){
        articleDbPipeline.setChannelId("web");
        Spider.create(articleProcessor)
                .addUrl("https://www.csdn.net/nav/web")
                // .addPipeline(new ConsolePipeline())
                .addPipeline(articleDbPipeline)
                .setScheduler(redisScheduler)
                .run();
    }

}
