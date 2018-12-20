package com.tensquare.user.task;

import com.tensquare.user.pipeline.UserDbPipeline;
import com.tensquare.user.process.UserProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.RedisScheduler;

@Component
public class UserTask {

    @Autowired
    private UserProcessor userProcessor;

    @Autowired
    private RedisScheduler redisScheduler;

    @Autowired
    private UserDbPipeline userDbPipeline;

    @Scheduled(cron = "35 25 12 * * ?")
    public void aiTask(){
        Spider.create(userProcessor)
                .addUrl("https://www.csdn.net/nav/ai")
                // .addPipeline(new ConsolePipeline())
                .addPipeline(userDbPipeline)
                .setScheduler(redisScheduler)
                .run();
    }


}
